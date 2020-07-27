package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.*
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

    // Screen space AO and Reflection maps
    private var ssaoSampler: TextureSampler? = null
    var scrSpcAmbientOcclusionMap: Texture? = cfg.scrSpcAmbientOcclusionMap
        set(value) {
            field = value
            ssaoSampler?.texture = value
        }
    private var ssrSampler: TextureSampler? = null
    var scrSpcReflectionMap: Texture? = cfg.scrSpcReflectionMap
        set(value) {
            field = value
            ssrSampler?.texture = value
        }
    private var ssrNoiseSampler: TextureSampler? = null
    var scrSpcReflectionNoise: Texture? = cfg.scrSpcReflectionNoise
        set(value) {
            field = value
            ssrSampler?.texture = value
        }
    private var uMaxIterations: PushConstantNode1i? = null
    var scrSpcReflectionIterations = 24
        set(value) {
            field = value
            uMaxIterations?.uniform?.value = value
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
        ssrNoiseSampler = model.findNode<TextureNode>("ssrNoiseTex")?.sampler
        ssrNoiseSampler?.let { it.texture = scrSpcReflectionNoise }
        uMaxIterations = model.findNode("uMaxIterations")
        uMaxIterations?.uniform?.value = scrSpcReflectionIterations

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
        fun generateScrSpcReflectionNoiseTex(): Texture {
            val sz = 64
            val buf = createUint8Buffer(sz * sz * 4)
            val rand = Random(0x1deadb0b)
            val vec = MutableVec3f()
            for (i in 0 until (sz * sz)) {
                do {
                    vec.set(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), rand.randomF(-1f, 1f))
                } while (vec.length() > 1f)
                vec.norm().scale(0.25f)
                buf[i * 4 + 0] = ((vec.x + 1f) * 127.5f).toByte()
                buf[i * 4 + 1] = ((vec.y + 1f) * 127.5f).toByte()
                buf[i * 4 + 2] = ((vec.z + 1f) * 127.5f).toByte()
                buf[i * 4 + 3] = rand.randomI(0..255).toByte()
            }
            val data = BufferedTextureData(buf, sz, sz, TexFormat.RGBA)
            val texProps = TextureProps(TexFormat.RGBA, AddressMode.REPEAT, AddressMode.REPEAT, minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST)
            return Texture("ssr_noise_tex", texProps) { data }
        }

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
                        val ssrNoiseTex = textureNode("ssrNoiseTex")
                        val noise = noiseTextureSamplerNode(ssrNoiseTex, constVec2i(Vec2i(64, 64))).outNoise
                        val sceneColorTex = textureNode("ssrMap")
                        val viewPos = mrtDeMultiplex.outViewPos
                        val viewDir = normalizeNode(viewPos).output
                        val rayOffset = splitNode(noise, "a").output
                        val rayDir = reflectNode(viewDir, normalizeNode(mrtDeMultiplex.outViewNormal).output).outDirection

                        val rayDirNoise = vecFromColorNode(splitNode(noise, "xyz").output).output
                        val rayDirMod = multiplyNode(rayDirNoise, mrtDeMultiplex.outRoughness).output
                        val roughRayDir = normalizeNode(addNode(rayDir, rayDirMod).output).output

                        val rayTraceNode = addNode(ScreenSpaceRayTraceNode(posAoTex, stage)).apply {
                            inProjMat = defCam.outProjMat
                            inRayOrigin = viewPos
                            inRayDirection = roughRayDir
                            inRayOffset = rayOffset

                            maxIterations = pushConstantNode1i("uMaxIterations").output
                        }
                        inReflectionColor = textureSamplerNode(sceneColorTex, rayTraceNode.outSamplePos).outColor
                        inReflectionWeight = rayTraceNode.outSampleWeight
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

        var irradianceMap: CubeMapTexture? = null
        var reflectionMap: CubeMapTexture? = null
        var brdfLut: Texture? = null

        var scrSpcAmbientOcclusionMap: Texture? = null
        var scrSpcReflectionMap: Texture? = null
        var scrSpcReflectionNoise: Texture? = null

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

        fun useScreenSpaceAmbientOcclusion(ssaoMap: Texture?) {
            this.scrSpcAmbientOcclusionMap = ssaoMap
            isScrSpcAmbientOcclusion = true
        }

        fun useScreenSpaceReflections(ssrMap: Texture?, generateNoiseTex: Boolean) {
            this.scrSpcReflectionMap = ssrMap
            isScrSpcReflections = true
            if (generateNoiseTex) {
                scrSpcReflectionNoise = generateScrSpcReflectionNoiseTex()
            }
        }

        fun useImageBasedLighting(environmentMaps: EnvironmentMaps) {
            useImageBasedLighting(environmentMaps.irradianceMap, environmentMaps.reflectionMap, environmentMaps.brdfLut)
        }

        fun useImageBasedLighting(irradianceMap: CubeMapTexture?, reflectionMap: CubeMapTexture?, brdfLut: Texture?) {
            this.irradianceMap = irradianceMap
            this.reflectionMap = reflectionMap
            this.brdfLut = brdfLut
            isImageBasedLighting = irradianceMap != null && reflectionMap != null && brdfLut != null
        }
    }
}