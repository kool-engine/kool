package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.logE

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
class DeferredLightShader(cfg: Config) : ModeledShader(shaderModel(cfg)) {

    private var deferredCameraNode: DeferredCameraNode? = null

    var sceneCamera: Camera? = cfg.sceneCamera
        set(value) {
            field = value
            deferredCameraNode?.sceneCam = value
        }

    private var positionAoSampler: TextureSampler2d? = null
    private var normalRoughnessSampler: TextureSampler2d? = null
    private var albedoMetalSampler: TextureSampler2d? = null

    var positionAo: Texture2d? = cfg.positionAo
        set(value) {
            field = value
            positionAoSampler?.texture = value
        }
    var normalRoughness: Texture2d? = cfg.normalRoughness
        set(value) {
            field = value
            normalRoughnessSampler?.texture = value
        }
    var albedoMetal: Texture2d? = cfg.albedoMetal
        set(value) {
            field = value
            albedoMetalSampler?.texture = value
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
        positionAoSampler = model.findNode<Texture2dNode>("positionAo")?.sampler
        positionAoSampler?.let { it.texture = positionAo }
        normalRoughnessSampler = model.findNode<Texture2dNode>("normalRoughness")?.sampler
        normalRoughnessSampler?.let { it.texture = normalRoughness }
        albedoMetalSampler = model.findNode<Texture2dNode>("albedoMetal")?.sampler
        albedoMetalSampler?.let { it.texture = albedoMetal }
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        val LIGHT_POS = Attribute("aLightPos", GlslType.VEC_4F)
        val LIGHT_DIR = Attribute("aLightDir", GlslType.VEC_4F)
        val LIGHT_DATA = Attribute("aLightData", GlslType.VEC_4F)

        fun shaderModel(cfg: Config) = ShaderModel("point light shader model").apply {
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
                ifLightDir = if (cfg.lightType == Light.Type.POINT) {
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
                }

                // discard fragment if it contains background / clear color
                addNode(DiscardClearNode(stage)).apply { inViewPos = mrtDeMultiplex.outViewPos }

                val defCam = addNode(DeferredCameraNode(stage))
                val worldPos = vec3TransformNode(mrtDeMultiplex.outViewPos, defCam.outInvViewMat, 1f).outVec3
                val worldNrm = vec3TransformNode(mrtDeMultiplex.outViewNormal, defCam.outInvViewMat, 0f).outVec3

                val mat = pbrLightNode().apply {
                    lightBacksides = cfg.lightBacksides
                    inFragPos = worldPos
                    inNormal = worldNrm
                    inCamPos = defCam.outCamPos

                    inAlbedo = mrtDeMultiplex.outAlbedo
                    inMetallic = mrtDeMultiplex.outMetallic
                    inRoughness = mrtDeMultiplex.outRoughness

                    if (cfg.lightType == Light.Type.SPOT) {
                        val spot = addNode(SingleSpotLightNode(stage)).apply {
                            isReducedSoi = true
                            inLightPos = ifLightPos.output
                            inLightColor = ifLightColor.output
                            inMaxIntensity = ifLightData.output
                            inLightDir = ifLightDir!!.output
                            inFragPos = worldPos
                        }
                        inFragToLight = spot.outFragToLightDirection
                        inRadiance = spot.outRadiance
                    } else {
                        val point = addNode(SinglePointLightNode(stage)).apply {
                            isReducedSoi = true
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

    class Config {
        var lightType = Light.Type.POINT
            set(value) {
                field = if (value == Light.Type.DIRECTIONAL) {
                    logE { "Directional lights are not supported for deferred lights, use global lighting instead." }
                    Light.Type.POINT
                } else {
                    value
                }
            }

        var sceneCamera: Camera? = null

        var lightBacksides = false

        var positionAo: Texture2d? = null
        var normalRoughness: Texture2d? = null
        var albedoMetal: Texture2d? = null
    }
}