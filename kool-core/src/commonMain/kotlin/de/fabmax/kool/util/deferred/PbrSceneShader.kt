package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.ibl.EnvironmentMaps

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
class PbrSceneShader(cfg: DeferredPbrConfig, model: ShaderModel = defaultDeferredPbrModel(cfg)) : ModeledShader(model) {

    private var deferredCameraNode: DeferredCameraNode? = null

    var sceneCamera: Camera? = null
        set(value) {
            field = value
            deferredCameraNode?.sceneCam = value
        }

    private var depthSampler: TextureSampler2d? = null
    private var positionAoSampler: TextureSampler2d? = null
    private var normalRoughnessSampler: TextureSampler2d? = null
    private var albedoMetalSampler: TextureSampler2d? = null
    private var emissiveSampler: TextureSampler2d? = null

    var depth: Texture2d? = null
        set(value) {
            field = value
            depthSampler?.texture = value
        }
    var positionAo: Texture2d? = null
        set(value) {
            field = value
            positionAoSampler?.texture = value
        }
    var normalRoughness: Texture2d? = null
        set(value) {
            field = value
            normalRoughnessSampler?.texture = value
        }
    var albedoMetal: Texture2d? = null
        set(value) {
            field = value
            albedoMetalSampler?.texture = value
        }
    var emissive: Texture2d? = null
        set(value) {
            field = value
            emissiveSampler?.texture = value
        }

    // Lighting props
    private var uAmbient: PushConstantNodeColor? = null
    var ambient = Color(0.03f, 0.03f, 0.03f, 1f)
        set(value) {
            field = value
            uAmbient?.uniform?.value?.set(value)
        }

    // Image based lighting maps
    private var irradianceMapSampler: TextureSamplerCube? = null
    private var reflectionMapSampler: TextureSamplerCube? = null
    private var brdfLutSampler: TextureSampler2d? = null

    var irradianceMap: TextureCube? = cfg.environmentMaps?.irradianceMap
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap: TextureCube? = cfg.environmentMaps?.reflectionMap
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut: Texture2d? = cfg.environmentMaps?.brdfLut
        set(value) {
            field = value
            brdfLutSampler?.texture = value
        }

    // Screen space AO and Reflection maps
    private var ssaoSampler: TextureSampler2d? = null
    var scrSpcAmbientOcclusionMap: Texture2d? = null
        set(value) {
            field = value
            ssaoSampler?.texture = value
        }
    private var ssrSampler: TextureSampler2d? = null
    var scrSpcReflectionMap: Texture2d? = null
        set(value) {
            field = value
            ssrSampler?.texture = value
        }

    // Shadow Mapping
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val depthSamplers = Array<TextureSampler2d?>(shadowMaps.size) { null }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    private var uAmbientShadowFactor: Uniform1f? = null
    var ambientShadowFactor = cfg.ambientShadowFactor
        set(value) {
            field = value
            uAmbientShadowFactor?.value = value
        }

    fun setMrtMaps(mrtPass: DeferredMrtPass) {
        sceneCamera = mrtPass.camera
        depth = mrtPass.depthTexture
        positionAo = mrtPass.positionAo
        normalRoughness = mrtPass.normalRoughness
        albedoMetal = mrtPass.albedoMetal
        emissive = mrtPass.emissive
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.depthTest = DepthCompareOp.ALWAYS
        builder.blendMode = BlendMode.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        deferredCameraNode = model.findNode("deferredCam")
        deferredCameraNode?.let { it.sceneCam = sceneCamera }

        depthSampler = model.findNode<Texture2dNode>("depth")?.sampler
        depthSampler?.let { it.texture = depth }
        positionAoSampler = model.findNode<Texture2dNode>("positionAo")?.sampler
        positionAoSampler?.let { it.texture = positionAo }
        normalRoughnessSampler = model.findNode<Texture2dNode>("normalRoughness")?.sampler
        normalRoughnessSampler?.let { it.texture = normalRoughness }
        albedoMetalSampler = model.findNode<Texture2dNode>("albedoMetal")?.sampler
        albedoMetalSampler?.let { it.texture = albedoMetal }
        emissiveSampler = model.findNode<Texture2dNode>("emissive")?.sampler
        emissiveSampler?.let { it.texture = emissive }

        uAmbient = model.findNode("uAmbient")
        uAmbient?.uniform?.value?.set(ambient)

        irradianceMapSampler = model.findNode<TextureCubeNode>("irradianceMap")?.sampler
        irradianceMapSampler?.let { it.texture = irradianceMap }
        reflectionMapSampler = model.findNode<TextureCubeNode>("reflectionMap")?.sampler
        reflectionMapSampler?.let { it.texture = reflectionMap }
        brdfLutSampler = model.findNode<Texture2dNode>("brdfLut")?.sampler
        brdfLutSampler?.let { it.texture = brdfLut }

        ssaoSampler = model.findNode<Texture2dNode>("ssaoMap")?.sampler
        ssaoSampler?.let { it.texture = scrSpcAmbientOcclusionMap }
        ssrSampler = model.findNode<Texture2dNode>("ssrMap")?.sampler
        ssrSampler?.let { it.texture = scrSpcReflectionMap }

        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<Texture2dNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
            uAmbientShadowFactor = model.findNode<PushConstantNode1f>("uAmbientShadowFactor")?.uniform
            uAmbientShadowFactor?.value = ambientShadowFactor
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

                val posAoTex = texture2dNode("positionAo")
                val mrtDeMultiplex = addNode(DeferredPbrShader.MrtDeMultiplexNode(stage)).apply {
                    inPositionAo = texture2dSamplerNode(posAoTex, coord).outColor
                    inNormalRough = texture2dSamplerNode(texture2dNode("normalRoughness"), coord).outColor
                    inAlbedoMetallic = texture2dSamplerNode(texture2dNode("albedoMetal"), coord).outColor
                    if (cfg.isWithEmissive) {
                        inEmissiveMat = texture2dSamplerNode(texture2dNode("emissive"), coord).outColor
                    }
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
                if (cfg.isImageBasedLighting) {
                    val irrMap = textureCubeNode("irradianceMap")
                    irrSampler = textureCubeSamplerNode(irrMap, worldNrm)
                    reflMap = textureCubeNode("reflectionMap")
                    brdfLut = texture2dNode("brdfLut")
                } else {
                    irrSampler = null
                    reflMap = null
                    brdfLut = null
                }

                val mat = pbrMaterialNode(reflMap, brdfLut).apply {
                    inFragPos = worldPos
                    inNormal = worldNrm
                    inViewDir = viewDirNode(defCam.outCamPos, worldPos).output

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
        var isWithEmissive = false

        var maxLights = 4
        val shadowMaps = mutableListOf<ShadowMap>()
        var lightBacksides = false
        var environmentMaps: EnvironmentMaps? = null
        var ambientShadowFactor = 0f

        fun useImageBasedLighting(environmentMaps: EnvironmentMaps?) {
            this.environmentMaps = environmentMaps
            isImageBasedLighting = environmentMaps != null
        }
    }
}