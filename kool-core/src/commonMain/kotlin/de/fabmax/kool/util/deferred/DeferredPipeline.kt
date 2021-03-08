package de.fabmax.kool.util.deferred

import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Scene
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
    val shadowMaps: List<ShadowMap>

    val contentGroup: Group
        get() = mrtPass.content
    val renderOutput: Mesh

    private val noSsrMap = SingleColorTexture(Color(0f, 0f, 0f, 0f))

    private val maxGlobalLights = cfg.maxGlobalLights
    private val isAoAvailable = cfg.isWithAmbientOcclusion
    private val isssrAvailable = cfg.isWithScreenSpaceReflections

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
    var isSsrEnabled = isssrAvailable
        set(value) {
            field = value && isssrAvailable
            updateEnabled()
        }
    var reflectionMapSize = 0.7f

    init {
        mrtPass = DeferredMrtPass(scene, cfg.isWithEmissive)

        aoPipeline = if (cfg.isWithAmbientOcclusion) AoPipeline.createDeferred(scene, mrtPass) else null
        shadowMaps = cfg.shadowMaps ?: createShadowMapsFromSceneLights()

        var sceneShader = cfg.pbrSceneShader
        if (sceneShader == null) {
            val lightingCfg = PbrSceneShader.DeferredPbrConfig().apply {
                isWithEmissive = cfg.isWithEmissive
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
        pbrPass.sceneShader.scrSpcAmbientOcclusionMap = aoPipeline?.aoMap

        if (cfg.isWithScreenSpaceReflections) {
            reflectionPass = ReflectionPass(mrtPass, pbrPass, cfg.baseReflectionStep)
            reflectionDenoisePass = ReflectionDenoisePass(reflectionPass, mrtPass.positionAo)
            pbrPass.sceneShader.scrSpcReflectionMap = reflectionDenoisePass.colorTexture
            scene.addOffscreenPass(reflectionPass)
            scene.addOffscreenPass(reflectionDenoisePass)
        } else {
            reflectionPass = null
            reflectionDenoisePass = null
        }

        renderOutput = pbrPass.createOutputQuad()

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
        }

        scene.onDispose += {
            noSsrMap.dispose()
        }
    }

    private fun updateEnabled() {
        shadowMaps.forEach { it.isShadowMapEnabled = isEnabled }
        aoPipeline?.isEnabled = isEnabled && isAoEnabled
        mrtPass.isEnabled = isEnabled
        pbrPass.isEnabled = isEnabled
        reflectionPass?.isEnabled = isEnabled && isSsrEnabled
        reflectionDenoisePass?.isEnabled = isEnabled && isSsrEnabled
        pbrPass.sceneShader.scrSpcReflectionMap = if (isSsrEnabled) reflectionDenoisePass?.colorTexture else noSsrMap
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
    var isWithEmissive = false
    var isWithAmbientOcclusion = true
    var isWithScreenSpaceReflections = true
    var isWithImageBasedLighting = false

    var maxGlobalLights = 4
    var lightBacksides = false
    var baseReflectionStep = 0.1f
    var environmentMaps: EnvironmentMaps? = null
    var shadowMaps: List<ShadowMap>? = null

    var pbrSceneShader: PbrSceneShader? = null

    fun useShadowMaps(shadowMaps: List<ShadowMap>?) {
        this.shadowMaps = shadowMaps
    }

    fun useImageBasedLighting(environmentMaps: EnvironmentMaps?) {
        this.environmentMaps = environmentMaps
        isWithImageBasedLighting = environmentMaps != null
    }
}
