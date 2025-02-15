package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BloomPass
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.min
import kotlin.math.roundToInt

class DeferredPipeline(val scene: Scene, val cfg: DeferredPipelineConfig) {

    var renderResolution = 1f
        set(value) {
            field = value.clamp(0.1f, 2f)
        }

    val passes: List<DeferredPasses>
    var activePassIndex = 0
        private set(value) {
            field = value % passes.size
        }

    val activePass: DeferredPasses
        get() = passes[activePassIndex]
    val inactivePass: DeferredPasses
        get() = passes[(activePassIndex + 1) % passes.size]

    val onSwap = mutableListOf<DeferredPassSwapListener>()
    val onConfigChange = mutableListOf<(DeferredPipeline) -> Unit>()

    val lightingPassShader: PbrSceneShader
    val aoPipeline: AoPipeline.DeferredAoPipeline?
    val reflections: Reflections?
    //val bloomPass: BloomPass?
    val shadowMaps: List<ShadowMap>

    val sceneContent = Node()
    val lightingPassContent = Node()

    val dynamicPointLights = DeferredPointLights(true)
    val staticPointLights = DeferredPointLights(false)
    val spotLights: List<DeferredSpotLights>
        get() = mutSpotLights
    private val mutSpotLights = mutableListOf<DeferredSpotLights>()

    val noSsrMap = SingleColorTexture(Color(0f, 0f, 0f, 0f))

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
    var aoMapSize: Float
        get() = aoPipeline?.mapSize ?: 0f
        set(value) { aoPipeline?.mapSize = value }

    var isSsrEnabled = isSsrAvailable
        set(value) {
            field = value && isSsrAvailable
            updateEnabled()
        }
    var reflectionMapSize: Float
        get() = reflections?.mapSize ?: 0f
        set(value) { reflections?.mapSize = value }

    var isBloomEnabled = isBloomAvailable
        set(value) {
            field = value && isBloomAvailable
            updateEnabled()
        }
    var bloomStrength: Float
        get() = passes[0].bloomPass?.strength ?: 0f
        set(value) {
            passes[0].bloomPass?.strength = value
            passes[1].bloomPass?.strength = value
        }
    var bloomRadius: Float
        get() = passes[0].bloomPass?.radius ?: 0f
        set(value) {
            passes[0].bloomPass?.radius = value
            passes[1].bloomPass?.radius = value
        }
    var bloomThreshold: Float
        get() = passes[0].bloomPass?.threshold ?: 0f
        set(value) {
            passes[0].bloomPass?.threshold = value
            passes[1].bloomPass?.threshold = value
        }

    init {
        passes = createPasses()

        shadowMaps = cfg.shadowMaps ?: createShadowMapsFromSceneLights()
        lightingPassShader = cfg.pbrSceneShader ?: PbrSceneShader(PbrSceneShader.DeferredPbrConfig().apply {
            isScrSpcAmbientOcclusion = cfg.isWithAmbientOcclusion
            isScrSpcReflections = cfg.isWithScreenSpaceReflections
            lightingConfig.apply {
                maxNumberOfLights = cfg.maxGlobalLights
                addShadowMaps(this@DeferredPipeline.shadowMaps)
            }
            useImageBasedLighting(cfg.environmentMap)
        })

        setupLightingPassContent()

        if (cfg.isWithAmbientOcclusion) {
            aoPipeline = AoPipeline.createDeferred(this)
            passes.forEach { it.lightingPass.dependsOn(aoPipeline.denoisePass) }
            lightingPassShader.scrSpcAmbientOcclusionMap = aoPipeline.aoMap
            onSwap += aoPipeline
        } else {
            aoPipeline = null
        }

        if (cfg.isWithScreenSpaceReflections) {
            reflections = Reflections(cfg)
            scene.addOffscreenPass(reflections.reflectionPass)
            scene.addOffscreenPass(reflections.denoisePass)
            passes.forEach { it.lightingPass.dependsOn(reflections.denoisePass) }
            lightingPassShader.scrSpcReflectionMap = reflections.denoisePass.colorTexture
            onSwap += reflections
        } else {
            reflections = null
        }

        if (cfg.isWithBloom) {
            passes.forEach { pass ->
                val bloom = BloomPass(pass.lightingPass.colorTexture!!)
                scene.addComputePass(bloom)
                bloom.dependsOn(pass.lightingPass)
                pass.bloomPass = bloom
            }
        }

        // make sure scene content is updated from scene.onUpdate, although sceneContent group is not a direct child of scene
        scene.onUpdate += { ev ->
            sceneContent.update(ev)
            lightingPassContent.update(ev)
        }
        scene.onRenderScene += { onRenderScene() }
        scene.onRelease {
            noSsrMap.release()
            passes.forEach { pass ->
                scene.removeOffscreenPass(pass.materialPass)
                scene.removeOffscreenPass(pass.lightingPass)
                pass.materialPass.release()
                pass.lightingPass.release()
                pass.extraPasses.forEach {
                    scene.removeOffscreenPass(it)
                    it.release()
                }
            }
        }
    }

    fun createDefaultOutputQuad(): Mesh {
        val outputShader = DeferredOutputShader(cfg, this)
        passes[0].lightingPass.onAfterPass { outputShader.setDeferredInput(passes[0]) }
        passes[1].lightingPass.onAfterPass { outputShader.setDeferredInput(passes[1]) }

        onConfigChange += {
            outputShader.isBloomEnabled = isBloomEnabled
        }

        return Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS).apply {
            generateFullscreenQuad(true)
            shader = outputShader
        }
    }

    private fun createPasses(): List<DeferredPasses> {
        val matPass0 = MaterialPass(this, "0")
        val lightPass0 = PbrLightingPass(this, "0", matPass0)

        val matPass1 = MaterialPass(this, "1")
        val lightPass1 = PbrLightingPass(this, "1", matPass1)

        scene.addOffscreenPass(matPass0)
        scene.addOffscreenPass(lightPass0)
        scene.addOffscreenPass(matPass1)
        scene.addOffscreenPass(lightPass1)

        return listOf(DeferredPasses(matPass0, lightPass0), DeferredPasses(matPass1, lightPass1))
    }

    private fun setupLightingPassContent() {
        lightingPassContent.apply {
            addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
                generateFullscreenQuad()
                shader = lightingPassShader
            }
            addNode(dynamicPointLights.mesh)
            addNode(staticPointLights.mesh)
        }
    }

    private fun onRenderScene() {
        if (isEnabled) {
            swapPasses()

            val vpW = (scene.mainRenderPass.viewport.width * renderResolution).roundToInt()
            val vpH = (scene.mainRenderPass.viewport.height * renderResolution).roundToInt()

            activePass.checkSize(vpW, vpH)
            reflections?.checkSize(vpW, vpH)
            aoPipeline?.checkSize(vpW, vpH)
        }
    }

    private fun swapPasses() {
        activePassIndex++

        val prev = inactivePass
        val current = activePass
        prev.isEnabled = false
        current.isEnabled = true

        lightingPassShader.setMaterialInput(current.materialPass)
        dynamicPointLights.lightShader.setMaterialInput(current.materialPass)
        staticPointLights.lightShader.setMaterialInput(current.materialPass)
        for (i in spotLights.indices) {
            spotLights[i].lightShader.setMaterialInput(current.materialPass)
        }

        for (i in onSwap.indices) {
            onSwap[i].onSwap(prev, current)
        }
    }

    fun createSpotLights(maxSpotAngle: AngleF): DeferredSpotLights {
        val lights = DeferredSpotLights(maxSpotAngle)
        lightingPassContent += lights.mesh
        mutSpotLights += lights
        return lights
    }

    private fun updateEnabled() {
        shadowMaps.forEach { it.isShadowMapEnabled = isEnabled }
        aoPipeline?.isEnabled = isEnabled && isAoEnabled
        passes.forEach { it.isEnabled = isEnabled }
        reflections?.isEnabled = isEnabled && isSsrEnabled
        lightingPassShader.scrSpcReflectionMap = if (isSsrEnabled) reflections?.reflectionMap else noSsrMap

        onConfigChange.forEach { it(this) }
    }

    private fun createShadowMapsFromSceneLights(): List<ShadowMap> {
        val shadows = mutableListOf<ShadowMap>()
        for (i in 0 until min(maxGlobalLights, scene.lighting.lights.size)) {
            val light = scene.lighting.lights[i]
            val shadowMap: ShadowMap? = when (light) {
                is Light.Directional -> CascadedShadowMap(scene, light, drawNode = sceneContent)
                is Light.Spot -> SimpleShadowMap(scene, light, drawNode = sceneContent)
                is Light.Point -> {
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
    var isWithAmbientOcclusion = false
    var isWithScreenSpaceReflections = false
    var isWithImageBasedLighting = false
    var isWithBloom = false
    var isWithVignette = false
    var isWithChromaticAberration = false

    var maxGlobalLights = 4
    var baseReflectionStep = 0.1f
    var bloomKernelSize = 8
    var bloomAvgDownSampling = true
    var environmentMap: EnvironmentMap? = null
    var shadowMaps: List<ShadowMap>? = null

    var pbrSceneShader: PbrSceneShader? = null

    var outputDepthTest = DepthCompareOp.LESS

    fun useShadowMaps(shadowMaps: List<ShadowMap>?) {
        this.shadowMaps = shadowMaps
    }

    fun useImageBasedLighting(environmentMap: EnvironmentMap?) {
        this.environmentMap = environmentMap
        isWithImageBasedLighting = environmentMap != null
    }
}
