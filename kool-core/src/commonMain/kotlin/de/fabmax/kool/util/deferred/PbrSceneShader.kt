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

    private var positionAoSampler: TextureSampler? = null
    private var normalRoughnessSampler: TextureSampler? = null
    private var albedoMetalSampler: TextureSampler? = null

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

    var irradianceMap: CubeMapTexture? = cfg.irradianceMap
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap: CubeMapTexture? = cfg.reflectionMap
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut: Texture? = cfg.brdfLut
        set(value) {
            field = value
            brdfLutSampler?.texture = value
        }

    // Screen space ambient occlusion map
    private var ssaoSampler: TextureSampler? = null
    var scrSpcAmbientOcclusionMap: Texture? = cfg.scrSpcAmbientOcclusionMap
        set(value) {
            field = value
            ssaoSampler?.texture = value
        }

    // Shadow Mapping
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val depthSamplers = Array<TextureSampler?>(shadowMaps.size) { null }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.depthTest = DepthCompareOp.DISABLED
        return super.createPipeline(mesh, builder, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline) {
        deferredCameraNode = model.findNode("deferredCam")
        deferredCameraNode?.let { it.sceneCam = sceneCamera }

        positionAoSampler = model.findNode<TextureNode>("positionAo")?.sampler
        positionAoSampler?.let { it.texture = positionAo }
        normalRoughnessSampler = model.findNode<TextureNode>("normalRoughness")?.sampler
        normalRoughnessSampler?.let { it.texture = normalRoughness }
        albedoMetalSampler = model.findNode<TextureNode>("albedoMetal")?.sampler
        albedoMetalSampler?.let { it.texture = albedoMetal }

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

        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<TextureNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
        }
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
                val mrtDeMultiplex = addNode(DeferredPbrShader.MrtDeMultiplexNode(stage)).apply {
                    inPositionAo = textureSamplerNode(textureNode("positionAo"), coord).outColor
                    inNormalRough = textureSamplerNode(textureNode("normalRoughness"), coord).outColor
                    inAlbedoMetallic = textureSamplerNode(textureNode("albedoMetal"), coord).outColor
                }

                addNode(DiscardClearNode(stage)).apply { inViewPos = mrtDeMultiplex.outViewPos }

                val defCam = addNode(DeferredCameraNode(stage))
                val worldPos = vec3TransformNode(mrtDeMultiplex.outViewPos, defCam.outInvViewMat, 1f).outVec3
                val worldNrm = vec3TransformNode(mrtDeMultiplex.outViewNormal, defCam.outInvViewMat, 0f).outVec3

                val lightNode = multiLightNode(cfg.maxLights)
                cfg.shadowMaps.forEachIndexed { i, map ->
                    lightNode.inShaodwFacs[i] = when (map) {
                        is CascadedShadowMap -> deferredCascadedShadoweMapNode(map, "depthMap_$i", mrtDeMultiplex.outViewPos, worldPos).outShadowFac
                        is SimpleShadowMap -> deferredSimpleShadoweMapNode(map, "depthMap_$i", worldPos).outShadowFac
                        else -> ShaderNodeIoVar(ModelVar1fConst(1f))
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
                    inCamPos = defCam.outCamPos

                    inIrradiance = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output

                    inAlbedo = mrtDeMultiplex.outAlbedo
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
                }

                colorOutput(mat.outColor)
            }
        }
    }

    class DeferredPbrConfig {
        var sceneCamera: Camera? = null

        var isImageBasedLighting = false
        var isScrSpcAmbientOcclusion = false

        var maxLights = 4
        val shadowMaps = mutableListOf<ShadowMap>()
        var lightBacksides = false

        var positionAo: Texture? = null
        var normalRoughness: Texture? = null
        var albedoMetal: Texture? = null

        var irradianceMap: CubeMapTexture? = null
        var reflectionMap: CubeMapTexture? = null
        var brdfLut: Texture? = null

        var scrSpcAmbientOcclusionMap: Texture? = null

        fun useScreenSpaceAmbientOcclusion(ssaoMap: Texture?) {
            this.scrSpcAmbientOcclusionMap = ssaoMap
            isScrSpcAmbientOcclusion = true
        }

        fun useImageBasedLighting(irradianceMap: CubeMapTexture?, reflectionMap: CubeMapTexture?, brdfLut: Texture?) {
            this.irradianceMap = irradianceMap
            this.reflectionMap = reflectionMap
            this.brdfLut = brdfLut
            isImageBasedLighting = irradianceMap != null && reflectionMap != null && brdfLut != null
        }
    }
}