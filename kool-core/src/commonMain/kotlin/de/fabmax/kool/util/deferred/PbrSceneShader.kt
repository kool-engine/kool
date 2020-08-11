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

    var sceneCamera: Camera? = cfg.sceneCamera
        set(value) {
            field = value
            deferredCameraNode?.sceneCam = value
        }

    private var depthSampler: TextureSampler? = null
    private var positionAoSampler: TextureSampler? = null
    private var normalRoughnessSampler: TextureSampler? = null
    private var albedoMetalSampler: TextureSampler? = null
    private var emissiveSampler: TextureSampler? = null

    var depth: Texture? = cfg.depth
        set(value) {
            field = value
            depthSampler?.texture = value
        }
    var positionAo: Texture? = cfg.positionAo
        set(value) {
            field = value
            positionAoSampler?.texture = value
        }
    var normalRoughness: Texture? = cfg.normalRoughness
        set(value) {
            field = value
            normalRoughnessSampler?.texture = value
        }
    var albedoMetal: Texture? = cfg.albedoMetal
        set(value) {
            field = value
            albedoMetalSampler?.texture = value
        }
    var emissive: Texture? = cfg.emissive
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
    private var irradianceMapSampler: CubeMapSampler? = null
    private var reflectionMapSampler: CubeMapSampler? = null
    private var brdfLutSampler: TextureSampler? = null

    var irradianceMap: CubeMapTexture? = cfg.environmentMaps?.irradianceMap
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap: CubeMapTexture? = cfg.environmentMaps?.reflectionMap
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut: Texture? = cfg.environmentMaps?.brdfLut
        set(value) {
            field = value
            brdfLutSampler?.texture = value
        }

    // Screen space AO and Reflection maps
    private var ssaoSampler: TextureSampler? = null
    var scrSpcAmbientOcclusionMap: Texture? = null
        set(value) {
            field = value
            ssaoSampler?.texture = value
        }
    private var ssrSampler: TextureSampler? = null
    var scrSpcReflectionMap: Texture? = null
        set(value) {
            field = value
            ssrSampler?.texture = value
        }

    // Shadow Mapping
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val depthSamplers = Array<TextureSampler?>(shadowMaps.size) { null }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.depthTest = DepthCompareOp.ALWAYS
        builder.blendMode = BlendMode.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        deferredCameraNode = model.findNode("deferredCam")
        deferredCameraNode?.let { it.sceneCam = sceneCamera }

        depthSampler = model.findNode<TextureNode>("depth")?.sampler
        depthSampler?.let { it.texture = depth }
        positionAoSampler = model.findNode<TextureNode>("positionAo")?.sampler
        positionAoSampler?.let { it.texture = positionAo }
        normalRoughnessSampler = model.findNode<TextureNode>("normalRoughness")?.sampler
        normalRoughnessSampler?.let { it.texture = normalRoughness }
        albedoMetalSampler = model.findNode<TextureNode>("albedoMetal")?.sampler
        albedoMetalSampler?.let { it.texture = albedoMetal }
        emissiveSampler = model.findNode<TextureNode>("emissive")?.sampler
        emissiveSampler?.let { it.texture = emissive }

        uAmbient = model.findNode("uAmbient")
        uAmbient?.uniform?.value?.set(ambient)

        irradianceMapSampler = model.findNode<CubeMapNode>("irradianceMap")?.sampler
        irradianceMapSampler?.let { it.texture = irradianceMap }
        reflectionMapSampler = model.findNode<CubeMapNode>("reflectionMap")?.sampler
        reflectionMapSampler?.let { it.texture = reflectionMap }
        brdfLutSampler = model.findNode<TextureNode>("brdfLut")?.sampler
        brdfLutSampler?.let { it.texture = brdfLut }

        ssaoSampler = model.findNode<TextureNode>("ssaoMap")?.sampler
        ssaoSampler?.let { it.texture = scrSpcAmbientOcclusionMap }
        ssrSampler = model.findNode<TextureNode>("ssrMap")?.sampler
        ssrSampler?.let { it.texture = scrSpcReflectionMap }

        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<TextureNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
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

                val posAoTex = textureNode("positionAo")
                val mrtDeMultiplex = addNode(DeferredPbrShader.MrtDeMultiplexNode(stage)).apply {
                    inPositionAo = textureSamplerNode(posAoTex, coord).outColor
                    inNormalRough = textureSamplerNode(textureNode("normalRoughness"), coord).outColor
                    inAlbedoMetallic = textureSamplerNode(textureNode("albedoMetal"), coord).outColor
                    if (cfg.isWithEmissive) {
                        inEmissive = textureSamplerNode(textureNode("emissive"), coord).outColor
                    }
                }

                addNode(DiscardClearNode(stage)).apply { inViewPos = mrtDeMultiplex.outViewPos }

                val defCam = addNode(DeferredCameraNode(stage))
                val worldPos = vec3TransformNode(mrtDeMultiplex.outViewPos, defCam.outInvViewMat, 1f).outVec3
                val worldNrm = vec3TransformNode(mrtDeMultiplex.outViewNormal, defCam.outInvViewMat, 0f).outVec3

                var lightNode: LightNode? = null
                if (cfg.maxLights > 0) {
                    lightNode = multiLightNode(cfg.maxLights)
                    cfg.shadowMaps.forEachIndexed { i, map ->
                        lightNode.inShaodwFacs[i] = when (map) {
                            is CascadedShadowMap -> deferredCascadedShadowMapNode(map, "depthMap_$i", mrtDeMultiplex.outViewPos, worldPos).outShadowFac
                            is SimpleShadowMap -> deferredSimpleShadowMapNode(map, "depthMap_$i", worldPos).outShadowFac
                            else -> ShaderNodeIoVar(ModelVar1fConst(1f))
                        }
                    }
                }

                val reflMap: CubeMapNode?
                val brdfLut: TextureNode?
                val irrSampler: CubeMapSamplerNode?
                if (cfg.isImageBasedLighting) {
                    val irrMap = cubeMapNode("irradianceMap")
                    irrSampler = cubeMapSamplerNode(irrMap, worldNrm)
                    reflMap = cubeMapNode("reflectionMap")
                    brdfLut = textureNode("brdfLut")
                } else {
                    irrSampler = null
                    reflMap = null
                    brdfLut = null
                }

                val mat = pbrMaterialNode(lightNode, reflMap, brdfLut).apply {
                    lightBacksides = cfg.lightBacksides
                    inFragPos = worldPos
                    inNormal = worldNrm
                    inViewDir = viewDirNode(defCam.outCamPos, worldPos).output

                    inIrradiance = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output

                    inAlbedo = mrtDeMultiplex.outAlbedo
                    inEmissive = mrtDeMultiplex.outEmissive
                    inMetallic = mrtDeMultiplex.outMetallic
                    inRoughness = mrtDeMultiplex.outRoughness

                    var aoFactor = mrtDeMultiplex.outAo
                    if (cfg.isScrSpcAmbientOcclusion) {
                        val aoMap = textureNode("ssaoMap")
                        val aoNode = addNode(AoMapSampleNode(aoMap, graph))
                        aoNode.inViewport = defCam.outViewport
                        aoFactor = multiplyNode(aoFactor, aoNode.outAo).output
                    }
                    inAmbientOccl = aoFactor

                    if (cfg.isScrSpcReflections) {
                        val ssr = textureSamplerNode(textureNode("ssrMap"), coord).outColor
                        inReflectionColor = gammaNode(ssr).outColor
                        inReflectionWeight = splitNode(ssr, "a").output
                    }
                }

                colorOutput(mat.outColor)
                val depthSampler = textureSamplerNode(textureNode("depth"), coord)
                depthOutput(depthSampler.outColor)
            }
        }
    }

    class DeferredPbrConfig {
        var sceneCamera: Camera? = null

        var isImageBasedLighting = false
        var isScrSpcAmbientOcclusion = false
        var isScrSpcReflections = false
        var isWithEmissive = false

        var maxLights = 4
        val shadowMaps = mutableListOf<ShadowMap>()
        var lightBacksides = false

        var depth: Texture? = null
        var positionAo: Texture? = null
        var normalRoughness: Texture? = null
        var albedoMetal: Texture? = null
        var emissive: Texture? = null

        var environmentMaps: EnvironmentMaps? = null

        fun useMrtPass(mrtPass: DeferredMrtPass) {
            sceneCamera = mrtPass.camera
            depth = mrtPass.depthTexture
            positionAo = mrtPass.positionAo
            normalRoughness = mrtPass.normalRoughness
            albedoMetal = mrtPass.albedoMetal
            isWithEmissive = mrtPass.withEmissive
            if (isWithEmissive) {
                emissive = mrtPass.emissive
            }
        }

        fun useImageBasedLighting(environmentMaps: EnvironmentMaps?) {
            this.environmentMaps = environmentMaps
            isImageBasedLighting = environmentMaps != null
        }
    }
}