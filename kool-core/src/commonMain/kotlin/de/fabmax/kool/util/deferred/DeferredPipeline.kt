package de.fabmax.kool.util.deferred

import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.min
import kotlin.math.roundToInt

class DeferredPipeline(val scene: Scene, cfg: DeferredPipelineConfig) {

    val mrtPass: DeferredMrtPass
    val pbrPass: PbrLightingPass

    val aoPipeline: AoPipeline.DeferredAoPipeline?
    val reflectionPass: ReflectionPass?
    val reflectionDenoisePass: ReflectionDenoisePass?
    val bloom: Bloom?
    val shadowMaps: List<ShadowMap>

    val contentGroup: Group
        get() = mrtPass.content
    val renderOutput: Mesh

    val outputShader: DeferredOutputShader

    private val noSsrMap = SingleColorTexture(Color(0f, 0f, 0f, 0f))
    private val noBloomMap = SingleColorTexture(Color(0f, 0f, 0f, 0f))

    private val maxGlobalLights = cfg.maxGlobalLights
    private val isAoAvailable = cfg.isWithAmbientOcclusion
    private val isSsrAvailable = cfg.isWithScreenSpaceReflections
    private val isBloomAvailable = cfg.isWithBloom

    var isEnabled = true
        set(value) {
            field = value
            updateEnabled()
        }
    var isAoEnabled = isAoAvailable
        set(value) {
            field = value && isAoAvailable
            updateEnabled()
        }
    var isSsrEnabled = isSsrAvailable
        set(value) {
            field = value && isSsrAvailable
            updateEnabled()
        }
    var reflectionMapSize = 0.7f

    var isBloomEnabled = isBloomAvailable
        set(value) {
            field = value && isBloomAvailable
            updateEnabled()
        }

    var bloomStrength: Float
        get() = outputShader.bloomStrength.value
        set(value) { outputShader.bloomStrength(value) }
    var bloomScale: Float
        get() = bloom?.blurPass?.bloomScale ?: 0f
        set(value) { bloom?.blurPass?.bloomScale = value }
    var bloomMapSize: Int
        get() = bloom?.desiredMapHeight ?: 0
        set(value) { bloom?.desiredMapHeight = value }

    init {
        mrtPass = DeferredMrtPass(scene, cfg.isWithExtendedMaterials)

        aoPipeline = if (cfg.isWithAmbientOcclusion) AoPipeline.createDeferred(scene, mrtPass) else null
        shadowMaps = cfg.shadowMaps ?: createShadowMapsFromSceneLights()

        var sceneShader = cfg.pbrSceneShader
        if (sceneShader == null) {
            val lightingCfg = PbrSceneShader.DeferredPbrConfig().apply {
                isWithEmissive = cfg.isWithExtendedMaterials
                isScrSpcAmbientOcclusion = cfg.isWithAmbientOcclusion
                isScrSpcReflections = cfg.isWithScreenSpaceReflections
                maxLights = cfg.maxGlobalLights
                shadowMaps += this@DeferredPipeline.shadowMaps
                useImageBasedLighting(cfg.environmentMaps)
            }
            sceneShader = PbrSceneShader(lightingCfg)
        }
        sceneShader.setMrtMaps(mrtPass)

        pbrPass = PbrLightingPass(scene, mrtPass, sceneShader)
        pbrPass.sceneShader.scrSpcAmbientOcclusionMap(aoPipeline?.aoMap)

        if (cfg.isWithScreenSpaceReflections) {
            reflectionPass = ReflectionPass(mrtPass, pbrPass, cfg.baseReflectionStep)
            reflectionDenoisePass = ReflectionDenoisePass(reflectionPass, mrtPass.positionAo)
            pbrPass.sceneShader.scrSpcReflectionMap(reflectionDenoisePass.colorTexture)
            scene.addOffscreenPass(reflectionPass)
            scene.addOffscreenPass(reflectionDenoisePass)
        } else {
            reflectionPass = null
            reflectionDenoisePass = null
        }

        if (cfg.isWithBloom) {
            bloom = Bloom(cfg, pbrPass)
            scene.addOffscreenPass(bloom.thresholdPass)
            scene.addOffscreenPass(bloom.blurPass)

        } else {
            bloom = null
        }

        renderOutput = createOutputQuad(cfg)
        outputShader = renderOutput.shader as DeferredOutputShader

        scene.onRenderScene += { ctx ->
            val vpW = scene.mainRenderPass.viewport.width
            val vpH = scene.mainRenderPass.viewport.height

            if (vpW > 0 && vpH > 0 && (vpW != mrtPass.width || vpH != mrtPass.height)) {
                mrtPass.resize(vpW, vpH, ctx)
                pbrPass.resize(vpW, vpH, ctx)
            }

            reflectionPass?.let { rp ->
                val reflMapW = (vpW * reflectionMapSize).roundToInt()
                val reflMapH = (vpH * reflectionMapSize).roundToInt()
                if (reflMapW > 0 && reflMapH > 0 && (reflMapW != rp.width || reflMapH != rp.height)) {
                    rp.resize(reflMapW, reflMapH, ctx)
                    reflectionDenoisePass?.resize(reflMapW, reflMapH, ctx)
                }
            }

            bloom?.checkSize(vpW, vpH, ctx)
        }

        scene.onDispose += {
            noSsrMap.dispose()
            noBloomMap.dispose()
        }
    }

    fun setBloomBrightnessThresholds(lower: Float, upper: Float) {
        bloom?.lowerThreshold = lower
        bloom?.upperThreshold = upper
    }

    private fun createOutputQuad(cfg: DeferredPipelineConfig) = textureMesh {
        isFrustumChecked = false
        generate {
            rect {
                mirrorTexCoordsY()
            }
        }
        shader = DeferredOutputShader(cfg, pbrPass.colorTexture!!, mrtPass.depthTexture!!, bloom?.bloomMap)
    }

    private fun updateEnabled() {
        shadowMaps.forEach { it.isShadowMapEnabled = isEnabled }
        aoPipeline?.isEnabled = isEnabled && isAoEnabled
        mrtPass.isEnabled = isEnabled
        pbrPass.isEnabled = isEnabled
        reflectionPass?.isEnabled = isEnabled && isSsrEnabled
        reflectionDenoisePass?.isEnabled = isEnabled && isSsrEnabled
        pbrPass.sceneShader.scrSpcReflectionMap(if (isSsrEnabled) reflectionDenoisePass?.colorTexture else noSsrMap)
        bloom?.isEnabled = isEnabled && isBloomEnabled
        outputShader.bloomMap(if (isBloomEnabled) bloom?.bloomMap else noBloomMap)
    }

    private fun createShadowMapsFromSceneLights(): List<ShadowMap> {
        val shadows = mutableListOf<ShadowMap>()
        for (i in 0 until min(maxGlobalLights, scene.lighting.lights.size)) {
            val light = scene.lighting.lights[i]
            val shadowMap: ShadowMap? = when (light.type) {
                Light.Type.DIRECTIONAL -> CascadedShadowMap(scene, i, drawNode = mrtPass.content)
                Light.Type.SPOT -> SimpleShadowMap(scene, i, drawNode = mrtPass.content)
                Light.Type.POINT -> {
                    logW { "Point light shadow maps not yet supported" }
                    null
                }
            }
            shadowMap?.let { shadows += shadowMap }
        }
        return shadows
    }
}

class DeferredPipelineConfig {
    var isWithExtendedMaterials = false
    var isWithAmbientOcclusion = false
    var isWithScreenSpaceReflections = false
    var isWithImageBasedLighting = false
    var isWithBloom = false
    var isWithVignette = false
    var isWithChromaticAberration = false

    var maxGlobalLights = 4
    var lightBacksides = false
    var baseReflectionStep = 0.1f
    var bloomKernelSize = 8
    var bloomAvgDownSampling = true
    var environmentMaps: EnvironmentMaps? = null
    var shadowMaps: List<ShadowMap>? = null

    var pbrSceneShader: PbrSceneShader? = null

    var outputDepthTest = DepthCompareOp.LESS

    fun useShadowMaps(shadowMaps: List<ShadowMap>?) {
        this.shadowMaps = shadowMaps
    }

    fun useImageBasedLighting(environmentMaps: EnvironmentMaps?) {
        this.environmentMaps = environmentMaps
        isWithImageBasedLighting = environmentMaps != null
    }
}
