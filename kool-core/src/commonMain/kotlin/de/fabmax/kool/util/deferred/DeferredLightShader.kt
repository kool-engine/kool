package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
class DeferredLightShader(lightType: Light.Type) : ModeledShader(shaderModel(lightType)) {

    private var deferredCameraNode: DeferredCameraNode? = null
    var sceneCamera: Camera? = null
        set(value) {
            field = value
            deferredCameraNode?.sceneCam = value
        }

    val positionAo = Texture2dInput("positionAo")
    val normalRoughness = Texture2dInput("normalRoughness")
    val albedoMetal = Texture2dInput("albedoMetal")
    val emissiveMat = Texture2dInput("emissiveMat")

    fun setMaterialInput(materialPass: MaterialPass) {
        sceneCamera = materialPass.camera
        positionAo(materialPass.positionAo)
        normalRoughness(materialPass.normalRoughness)
        albedoMetal(materialPass.albedoMetal)
        emissiveMat(materialPass.emissive?: SingleColorTexture(Color.BLACK.withAlpha(0f)))
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.blendMode = BlendMode.BLEND_ADDITIVE
        builder.cullMethod = CullMethod.CULL_FRONT_FACES
        builder.depthTest = DepthCompareOp.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        deferredCameraNode = model.findNode("deferredCam")
        deferredCameraNode?.let { it.sceneCam = sceneCamera }
        positionAo.connect(model)
        normalRoughness.connect(model)
        albedoMetal.connect(model)
        emissiveMat.connect(model)
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        val LIGHT_POS = Attribute("aLightPos", GlslType.VEC_4F)
        val LIGHT_DIR = Attribute("aLightDir", GlslType.VEC_4F)
        val LIGHT_DATA = Attribute("aLightData", GlslType.VEC_4F)

        fun shaderModel(lightType: Light.Type) = ShaderModel("point light shader model").apply {
            val ifFragCoords: StageInterfaceNode
            val ifLightColor: StageInterfaceNode
            val ifLightPos: StageInterfaceNode
            val ifLightData: StageInterfaceNode
            val ifLightDir: StageInterfaceNode?

            vertexStage {
                val instMvp = instanceAttrModelMat().output
                val mvpMat = multiplyNode(premultipliedMvpNode().outMvpMat, instMvp).output
                positionOutput = vec4TransformNode(attrPositions().output, mvpMat).outVec4
                ifFragCoords = stageInterfaceNode("ifFragCoords", positionOutput)

                ifLightColor = stageInterfaceNode("ifLightColor", instanceAttributeNode(Attribute.COLORS).output, true)
                ifLightPos = stageInterfaceNode("ifLightPos", instanceAttributeNode(LIGHT_POS).output, true)
                ifLightData = stageInterfaceNode("ifLightData", instanceAttributeNode(LIGHT_DATA).output, true)
                ifLightDir = if (lightType == Light.Type.POINT) {
                    null
                } else {
                    stageInterfaceNode("ifLightDir", instanceAttributeNode(LIGHT_DIR).output, true)
                }
            }
            fragmentStage {
                val xyPos = divideNode(splitNode(ifFragCoords.output, "xy").output, splitNode(ifFragCoords.output, "w").output).output
                val texPos = addNode(multiplyNode(xyPos, 0.5f).output, ShaderNodeIoVar(ModelVar1fConst(0.5f))).output

                val coord = texPos
                val mrtDeMultiplex = addNode(DeferredPbrShader.MrtDeMultiplexNode(stage)).apply {
                    inPositionAo = texture2dSamplerNode(texture2dNode("positionAo"), coord).outColor
                    inNormalRough = texture2dSamplerNode(texture2dNode("normalRoughness"), coord).outColor
                    inAlbedoMetallic = texture2dSamplerNode(texture2dNode("albedoMetal"), coord).outColor
                    inEmissiveMat = texture2dSamplerNode(texture2dNode("emissiveMat"), coord).outColor
                }

                // discard fragment if it contains background / clear color
                addNode(DiscardClearNode(stage)).apply { inViewPos = mrtDeMultiplex.outViewPos }

                val defCam = addNode(DeferredCameraNode(stage))
                val worldPos = vec3TransformNode(mrtDeMultiplex.outViewPos, defCam.outInvViewMat, 1f).outVec3
                val worldNrm = vec3TransformNode(mrtDeMultiplex.outViewNormal, defCam.outInvViewMat, 0f).outVec3

                val mat = pbrLightNode().apply {
                    inFragPos = worldPos
                    inNormal = worldNrm
                    inCamPos = defCam.outCamPos

                    inAlbedo = mrtDeMultiplex.outAlbedo
                    inMetallic = mrtDeMultiplex.outMetallic
                    inRoughness = mrtDeMultiplex.outRoughness
                    inAlwaysLit = mrtDeMultiplex.outLightBacksides

                    if (lightType == Light.Type.SPOT) {
                        val spot = addNode(SingleSpotLightNode(stage)).apply {
                            isReducedSoi = true
                            inShadowFac = mrtDeMultiplex.outAo
                            inLightPos = ifLightPos.output
                            inLightColor = ifLightColor.output
                            inMaxIntensity = splitNode(ifLightData.output, "x").output
                            inSpotCoreRatio = splitNode(ifLightData.output, "y").output
                            inLightDir = ifLightDir!!.output
                            inFragPos = worldPos
                        }
                        inFragToLight = spot.outFragToLightDirection
                        inRadiance = spot.outRadiance
                    } else {
                        val point = addNode(SinglePointLightNode(stage)).apply {
                            isReducedSoi = true
                            inShadowFac = mrtDeMultiplex.outAo
                            inLightPos = ifLightPos.output
                            inLightColor = ifLightColor.output
                            inMaxIntensity = ifLightData.output
                            inFragPos = worldPos
                        }
                        inFragToLight = point.outFragToLightDirection
                        inRadiance = point.outRadiance
                    }
                }

                colorOutput(mat.outColor)
            }
        }
    }
}