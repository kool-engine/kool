package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.TextureSampler2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
open class PbrSceneShader(cfg: DeferredPbrConfig, model: ShaderModel = defaultDeferredPbrModel(cfg)) : ModeledShader(model) {

    private var deferredCameraNode: DeferredCameraNode? = null
    var sceneCamera: Camera? = null
        set(value) {
            field = value
            deferredCameraNode?.sceneCam = value
        }

    val depth = Texture2dInput("depth")
    val positionFlags = Texture2dInput("positionFlags")
    val normalRoughness = Texture2dInput("normalRoughness")
    val albedoMetal = Texture2dInput("albedoMetal")
    val emissiveAo = Texture2dInput("emissiveAo")

    // Lighting props
    val ambient = ColorInput("uAmbient", Color(0.03f, 0.03f, 0.03f, 1f))
    val ambientShadowFactor = FloatInput("uAmbientShadowFactor", cfg.ambientShadowFactor)

    // Image based lighting maps
    val environmentMapOrientation = Mat3fInput("uEnvMapOri")
    val irradianceMap = TextureCubeInput("irradianceMap", cfg.environmentMaps?.irradianceMap)
    val reflectionMap = TextureCubeInput("reflectionMap", cfg.environmentMaps?.reflectionMap)
    val brdfLut = Texture2dInput("brdfLut")

    // Screen space AO and Reflection maps
    val scrSpcAmbientOcclusionMap = Texture2dInput("ssaoMap")
    val scrSpcReflectionMap = Texture2dInput("ssrMap")

    // Shadow Mapping
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val depthSamplers = Array<TextureSampler2d?>(shadowMaps.size) { null }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    fun setMaterialInput(materialPass: MaterialPass) {
        sceneCamera = materialPass.camera
        depth(materialPass.depthTexture)
        positionFlags(materialPass.positionFlags)
        normalRoughness(materialPass.normalRoughness)
        albedoMetal(materialPass.albedoMetal)
        emissiveAo(materialPass.emissiveAo)
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        brdfLut(ctx.defaultPbrBrdfLut)
        builder.depthTest = DepthCompareOp.ALWAYS
        builder.blendMode = BlendMode.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        deferredCameraNode = model.findNode("deferredCam")
        deferredCameraNode?.let { it.sceneCam = sceneCamera }

        depth.connect(model)
        positionFlags.connect(model)
        normalRoughness.connect(model)
        albedoMetal.connect(model)
        emissiveAo.connect(model)

        ambient.connect(model)

        irradianceMap.connect(model)
        reflectionMap.connect(model)
        brdfLut.connect(model)
        environmentMapOrientation.connect(model)

        scrSpcAmbientOcclusionMap.connect(model)
        scrSpcReflectionMap.connect(model)

        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<Texture2dNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
            ambientShadowFactor.connect(model)
        }
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultDeferredPbrModel(cfg: DeferredPbrConfig) = ShaderModel("defaultDeferredPbrModel()").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val coord = ifTexCoords.output

                val posFlagsTex = texture2dNode("positionFlags")
                val mrtDeMultiplex = addNode(DeferredPbrShader.MrtDeMultiplexNode(stage)).apply {
                    inPositionFlags = texture2dSamplerNode(posFlagsTex, coord).outColor
                    inNormalRough = texture2dSamplerNode(texture2dNode("normalRoughness"), coord).outColor
                    inAlbedoMetallic = texture2dSamplerNode(texture2dNode("albedoMetal"), coord).outColor
                    inEmissiveAo = texture2dSamplerNode(texture2dNode("emissiveAo"), coord).outColor
                }

                addNode(DiscardClearNode(stage)).apply { inViewPos = mrtDeMultiplex.outViewPos }

                val defCam = addNode(DeferredCameraNode(stage))
                val worldPos = vec3TransformNode(mrtDeMultiplex.outViewPos, defCam.outInvViewMat, 1f).outVec3
                val worldNrm = vec3TransformNode(mrtDeMultiplex.outViewNormal, defCam.outInvViewMat, 0f).outVec3

                var lightNode: MultiLightNode? = null
                if (cfg.maxLights > 0) {
                    lightNode = multiLightNode(worldPos, cfg.maxLights)
                    cfg.shadowMaps.forEachIndexed { i, map ->
                        lightNode.inShadowFacs[i] = when (map) {
                            is CascadedShadowMap -> deferredCascadedShadowMapNode(map, "depthMap_$i", mrtDeMultiplex.outViewPos, worldPos, worldNrm).outShadowFac
                            is SimpleShadowMap -> deferredSimpleShadowMapNode(map, "depthMap_$i", worldPos, worldNrm).outShadowFac
                            else -> ShaderNodeIoVar(ModelVar1fConst(1f))
                        }
                    }
                }

                val reflMap: TextureCubeNode?
                val brdfLut: Texture2dNode?
                val irrSampler: TextureCubeSamplerNode?
                val envMapOri: UniformMat3fNode?
                if (cfg.isImageBasedLighting) {
                    envMapOri = uniformMat3fNode("uEnvMapOri")
                    val irrDirection = vec3TransformNode(worldNrm, envMapOri.output).outVec3
                    val irrMap = textureCubeNode("irradianceMap")
                    irrSampler = textureCubeSamplerNode(irrMap, irrDirection)
                    reflMap = textureCubeNode("reflectionMap")
                    brdfLut = texture2dNode("brdfLut")
                } else {
                    irrSampler = null
                    reflMap = null
                    brdfLut = null
                    envMapOri = null
                }

                val mat = pbrMaterialNode(reflMap, brdfLut).apply {
                    inFragPos = worldPos
                    inNormal = worldNrm
                    inViewDir = viewDirNode(defCam.outCamPos, worldPos).output
                    envMapOri?.let { inReflectionMapOrientation = it.output }

                    val avgShadow: ShaderNodeIoVar
                    if (lightNode != null) {
                        inLightCount = lightNode.outLightCount
                        inFragToLight = lightNode.outFragToLightDirection
                        inRadiance = lightNode.outRadiance
                        avgShadow = lightNode.outAvgShadowFac
                    } else {
                        avgShadow = constFloat(0f)
                    }

                    val irr = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output
                    val ambientShadowFac = pushConstantNode1f("uAmbientShadowFactor").output
                    val shadowStr = multiplyNode(subtractNode(constFloat(1f), avgShadow).output, ambientShadowFac)
                    val ambientStr = subtractNode(constFloat(1f), shadowStr.output).output
                    inIrradiance = multiplyNode(irr, ambientStr).output
                    inReflectionStrength = ambientStr

                    inAlbedo = mrtDeMultiplex.outAlbedo
                    inEmissive = mrtDeMultiplex.outEmissive
                    inMetallic = mrtDeMultiplex.outMetallic
                    inRoughness = mrtDeMultiplex.outRoughness
                    inAlwaysLit = mrtDeMultiplex.outLightBacksides

                    var aoFactor = mrtDeMultiplex.outAo
                    if (cfg.isScrSpcAmbientOcclusion) {
                        val aoMap = texture2dNode("ssaoMap")
                        val aoNode = addNode(AoMapSampleNode(aoMap, graph))
                        aoNode.inViewport = defCam.outViewport
                        aoFactor = multiplyNode(aoFactor, aoNode.outAo).output
                    }
                    inAmbientOccl = aoFactor

                    if (cfg.isScrSpcReflections) {
                        val ssr = texture2dSamplerNode(texture2dNode("ssrMap"), coord).outColor
                        inReflectionColor = gammaNode(ssr).outColor
                        inReflectionWeight = splitNode(ssr, "a").output
                    }
                }

                colorOutput(mat.outColor)
                val depthSampler = texture2dSamplerNode(texture2dNode("depth"), coord)
                depthOutput(depthSampler.outColor)
            }
        }
    }

    class DeferredPbrConfig {
        var isImageBasedLighting = false
        var isScrSpcAmbientOcclusion = false
        var isScrSpcReflections = false

        var maxLights = 4
        val shadowMaps = mutableListOf<ShadowMap>()
        var environmentMaps: EnvironmentMaps? = null
        var ambientShadowFactor = 0f

        fun useImageBasedLighting(environmentMaps: EnvironmentMaps?) {
            this.environmentMaps = environmentMaps
            isImageBasedLighting = environmentMaps != null
        }
    }
}