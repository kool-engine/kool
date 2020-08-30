package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.ControlUiBuilder
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.UniformBufferMvp
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.UnlitMaterialConfig
import de.fabmax.kool.pipeline.shading.UnlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.deferred.deferredPbrShader
import kotlinx.coroutines.delay
import kotlin.math.pow

class AtmosphereDemo : DemoScene("Atmosphere") {

    private val sunColor = Color.WHITE
    private val sun = Light().apply {
        setDirectional(Vec3f.NEG_Z_AXIS)
        setColor(sunColor, 5f)
    }
    private var sunIntensity = 1f

    private var time = 0.5f
    private var moonTime = 0f
    private var animateTime = true
    private var timeSlider: Slider? = null

    private lateinit var mainCamera: PerspectiveCamera
    private val shadows = mutableListOf<SimpleShadowMap>()
    private val atmoShader = AtmosphericScatteringShader()
    private val earthTransform = Group("earth")
    private val camTransform = EarthCamTransform(earthRadius)

    private lateinit var menuContainer: UiContainer
    private lateinit var loadingLabel: Label

    private val textures = mutableMapOf<String, Texture>()
    private var loadingComplete = false
    private var sceneSetup = false

    private lateinit var opticalDepthLutPass: OpticalDepthLutPass

    override fun lateInit(ctx: KoolContext) {
        camTransform.apply {
            mainScene.registerDragHandler(this)
            +mainCamera
        }

        ctx.assetMgr.launch {
            delay(500)
            loadTex(texMilkyway, "milkyway-dark.jpg")
            loadTex(texSun, "sun.png")
            loadTex(texSunBg, "sun_bg.png")
            loadTex(texMoon, "moon.jpg")
            loadTex(EarthShader.texEarthDay, "earth_day.jpg")
            loadTex(EarthShader.texEarthNight, "earth_night.jpg")
            loadTex(EarthShader.texEarthNrm, "earth_nrm.jpg")
            loadTex(EarthShader.texEarthHeight, "earth_height.jpg")
            loadingLabel.text = "Initializing Scene..."
            delay(100)
            loadingComplete = true
        }
    }

    private suspend fun AssetManager.loadTex(key: String, path: String) {
        loadingLabel.text = "Loading texture \"$key\"..."
        textures[key] = loadAndPrepareTexture(path)
    }

    override fun setupMainScene(ctx: KoolContext) = scene {
        mainCamera = camera as PerspectiveCamera
        opticalDepthLutPass = OpticalDepthLutPass()

        lighting.lights.clear()
        lighting.lights += sun
        shadows += SimpleShadowMap(this, 0)

        val defCfg = DeferredPipelineConfig().apply {
            isWithEmissive = true
            isWithAmbientOcclusion = false
            isWithScreenSpaceReflections = false
            maxGlobalLights = 1
            shadowMaps = shadows
        }
        val deferredPipeline = DeferredPipeline(this, defCfg)
        deferredPipeline.pbrPass.sceneShader.ambient = Color(0.05f, 0.05f, 0.05f).toLinear()
        +deferredPipeline.renderOutput

        addOffscreenPass(opticalDepthLutPass)

        atmoShader.apply {
            opticalDepthLut = opticalDepthLutPass.colorTexture
            sceneColor = deferredPipeline.pbrPass.colorTexture
            scenePos = deferredPipeline.mrtPass.positionAo
            surfaceRadius = earthRadius
            atmosphereRadius = 6500f / kmPerUnit

            scatteringCoeffs = Vec3f(0.75f, 1.05f, 1.25f)
            rayleighCoeffs = Vec3f(0.5f, 0.5f, 1f)
            scatteringCoeffStrength = 2.0f
        }

        shadows.forEach { shadow ->
            shadow.drawNode = deferredPipeline.contentGroup
            shadow.shadowBounds = earthTransform.bounds
            //shadow.shadowBounds = deferredPipeline.contentGroup.bounds
        }

        onUpdate += { ev ->
            if (loadingComplete) {
                if (!sceneSetup) {
                    sceneSetup = true

                    menuContainer.isVisible = true
                    loadingLabel.isVisible = false

                    deferredPipeline.contentGroup.setupContent()
                    setupSkybox()
                    updateSun()
                    +mesh(listOf(Attribute.POSITIONS)) {
                        generate {
                            icoSphere {
                                steps = 3
                                radius = earthRadius + 12
                            }
                        }
                        shader = atmoShader
                    }
                }

                mainCamera.apply {
                    val h = globalPos.length() - earthRadius
                    position.set(Vec3f.ZERO)
                    lookAt.set(Vec3f.NEG_Z_AXIS)
                    clipNear = (h * 0.5f).clamp(0.003f, 5f)
                    clipFar = clipNear * 1000f
                }

                if (animateTime) {
                    val dt = ev.deltaT / 120
                    // setting time slider value results in timer slider's onChange function being called which also sets time
                    timeSlider?.value = (time + dt) % 1f
                    moonTime = (moonTime + dt / moonT)
                }
            }
        }

        onDispose += {
            textures.values.forEach { it.dispose() }
        }
    }

    private fun Scene.setupSkybox() {
        +group {
            isFrustumChecked = false
            +Skybox.sphere(textures[texMilkyway]!!, hdriInput = false)
            // milky way is wildly tilted (no idea in which direction...)
            rotate(-60f, Vec3f.X_AXIS)
        }

        +textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(3f, 3f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    origin.z = -10f
                }
            }
            shader = skyboxShader(textures[texSunBg], 0.75f)
        }
        +textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(1f, 1f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    origin.z = -10f
                }

            }
            shader = skyboxShader(textures[texSun], 1f)
        }
    }

    private fun skyboxShader(texture: Texture?, maxAlpha: Float): UnlitShader {
        val unlitCfg = UnlitMaterialConfig().apply {
            alphaMode = AlphaModeBlend()
            useColorMap(texture, maxAlpha < 1f)
            color = Color.WHITE.withAlpha(maxAlpha)
        }
        val unlitModel = UnlitShader.defaultUnlitModel(unlitCfg).apply {
            vertexStage {
                val mvp = findNodeByType<UniformBufferMvp>()!!
                positionOutput = addNode(Skybox.SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
            }
        }
        return UnlitShader(unlitCfg, unlitModel).apply {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.NO_CULLING
                builder.depthTest = DepthCompareOp.LESS_EQUAL
            }
        }
    }

    private fun Group.setupContent() {
        +earthTransform.apply {
            +camTransform

            +textureMesh(isNormalMapped = true) {
                generate {
                    icoSphere {
                        steps = 7
                        radius = earthRadius
                    }
                }
                val earthShader = EarthShader(textures).also { shader = it }

                onUpdate += {
                    val dirToSun = MutableVec3f(sun.direction).scale(-1f)

                    earthShader.uDirToSun?.value?.let { uSunDir ->
                        uSunDir.set(dirToSun)
                        toLocalCoords(uSunDir, 0f)
                    }

                    atmoShader.dirToSun = dirToSun
                }
            }

            onUpdate += {
                setIdentity()
                // earth rotation axis is tilted by ~20°
                rotate(-20f, Vec3f.X_AXIS)
                // rotate according to time
                rotate(time * 360, Vec3f.Y_AXIS)
            }
        }

        +group {
            isFrustumChecked = false
            +Moon()

            onUpdate += {
                setIdentity()
                rotate(moonInclination, Vec3f.X_AXIS)
                rotate(360f * moonTime, Vec3f.Y_AXIS)
                translate(0f, 0f, moonDist)
            }
        }
    }

    private inner class Moon : Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS), "moon") {
        init {
            isFrustumChecked = false
            generate {
                rotate(180f, Vec3f.Y_AXIS)
                icoSphere {
                    steps = 4
                    radius = moonRadius
                }
            }
            shader = deferredPbrShader {
                useAlbedoMap(textures[texMoon])
                roughness = 0.7f
            }
        }

        override fun collectDrawCommands(updateEvent: RenderPass.UpdateEvent) {
            val rpCam = updateEvent.camera

            if (rpCam is PerspectiveCamera) {
                // Use modified camera clip values when rendering moon. This can cause artifacts but works in
                // most situations and is better than moon being completely clipped away

                val clipN = rpCam.clipNear
                val clipF = rpCam.clipFar
                val d = globalCenter.distance(rpCam.globalPos) + moonRadius
                val customClip = d > clipF

                if (customClip) {
                    rpCam.clipFar = d
                    rpCam.clipNear = d / 1000f
                    rpCam.updateCamera(updateEvent.ctx, updateEvent.viewport)
                }
                super.collectDrawCommands(updateEvent)
                if (customClip) {
                    rpCam.clipNear = clipN
                    rpCam.clipFar = clipF
                    rpCam.updateCamera(updateEvent.ctx, updateEvent.viewport)
                }

            } else {
                super.collectDrawCommands(updateEvent)
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        uiRoot.apply {
            loadingLabel = label("Loading...") {
                layoutSpec.setOrigin(zero(), zero(), zero())
                layoutSpec.setSize(pcs(100f), pcs(100f), full())
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            +loadingLabel
        }

        image(opticalDepthLutPass.colorTexture).apply {
            aspectRatio = 1f
            relativeWidth = 0.25f
        }

        this@AtmosphereDemo.menuContainer = menuContainer
        menuContainer.isVisible = false
        menuWidth = 380f
        section("Scattering") {
            colorSlider("R:", Color.RED, atmoShader.scatteringCoeffs.x, 0f, 2f) { updateScatteringCoeffs(x = value) }
            colorSlider("G:", Color.GREEN, atmoShader.scatteringCoeffs.y, 0f, 2f) { updateScatteringCoeffs(y = value) }
            colorSlider("B:", Color.BLUE, atmoShader.scatteringCoeffs.z, 0f, 2f) { updateScatteringCoeffs(z = value) }
            gap(8f)
            sliderWithValueSmall("Pow:", atmoShader.scatteringCoeffPow, 1f, 20f, 2, widthLabel = 10f) {
                atmoShader.scatteringCoeffPow = value
            }
            sliderWithValueSmall("Str:", atmoShader.scatteringCoeffStrength, 0.01f, 5f, 2, widthLabel = 10f) {
                atmoShader.scatteringCoeffStrength = value
            }
        }

        section("Rayleigh") {
            colorSlider("R:", Color.RED, atmoShader.rayleighCoeffs.x, 0f, 4f) { updateRayleighCoeffs(x = value) }
            colorSlider("G:", Color.GREEN, atmoShader.rayleighCoeffs.y, 0f, 4f) { updateRayleighCoeffs(y = value) }
            colorSlider("B:", Color.BLUE, atmoShader.rayleighCoeffs.z, 0f, 4f) { updateRayleighCoeffs(z = value) }
            gap(8f)
            sliderWithValueSmall("Str:", atmoShader.rayleighStrength, 0f, 2f, widthLabel = 10f) { atmoShader.rayleighStrength = value }
        }
        section("Mie") {
            sliderWithValueSmall("g:", atmoShader.mieG, 0.5f, 0.999f, 3, widthLabel = 10f) { atmoShader.mieG = value }
            sliderWithValueSmall("Str:", atmoShader.mieStrength, 0f, 2f, widthLabel = 10f) { atmoShader.mieStrength = value }
        }

        section("Atmosphere") {
            val thickFmt: (Float) -> String = { "${it.toString(0)} km" }
            sliderWithValueSmall("Thickness:", (atmoShader.atmosphereRadius - earthRadius) * kmPerUnit, 10f, 1000f, textFormat = thickFmt, widthLabel = 24f) {
                updateAtmosphereThickness(value)
            }
            sliderWithValueSmall("Falloff:", opticalDepthLutPass.densityFalloff, 0f, 15f, 2, widthLabel = 24f) {
                opticalDepthLutPass.densityFalloff = value
            }
        }

        section("View") {
            sliderWithValueSmall("Sun:", atmoShader.sunIntensity.x, 0.1f, 5f, 2, widthLabel = 24f) {
                sunIntensity = value
                updateSun()
            }
            val timeFmt: (Float) -> String = {
                val t = it * 24
                val h = t.toInt()
                val m = ((t % 1f) * 60).toInt()
                val m0 = if (m < 10) "0" else ""
                "$h:$m0$m"
            }
            timeSlider = sliderWithValueSmall("Time:", time, 0f, 1f, textFormat = timeFmt, widthLabel = 24f) {
                time = value
                updateSun()
            }
            toggleButton("Animate Time", animateTime) { animateTime = isEnabled }
            textWithValue("Camera Height:", "").apply {
                onUpdate += {
                    val h = (mainScene.camera.globalPos.distance(Vec3f.ZERO) - earthRadius) * kmPerUnit
                    text = "${h.toString(1)} km"
                }
            }
        }
    }

    private fun updateSun() {
        val lightDir = MutableVec3f(0f, 0f, -1f)
        atmoShader.dirToSun = lightDir
        atmoShader.sunIntensity = MutableVec3f(sunColor.r, sunColor.g, sunColor.b).scale(sunIntensity)

        mainScene.lighting.lights[0].apply {
            setDirectional(MutableVec3f(lightDir).scale(-1f))
            setColor(sunColor, sunIntensity * 5)
        }
    }

    private fun updateRayleighCoeffs(x: Float = atmoShader.rayleighCoeffs.x, y: Float = atmoShader.rayleighCoeffs.y, z: Float = atmoShader.rayleighCoeffs.z) {
        atmoShader.rayleighCoeffs = Vec3f(x, y, z)
    }

    private fun updateScatteringCoeffs(x: Float = atmoShader.scatteringCoeffs.x, y: Float = atmoShader.scatteringCoeffs.y, z: Float = atmoShader.scatteringCoeffs.z) {
        atmoShader.scatteringCoeffs = Vec3f(x, y, z)
    }

    private fun updateAtmosphereThickness(newThickness: Float) {
        val atmoRadius = earthRadius + newThickness / kmPerUnit
        atmoShader.atmosphereRadius = atmoRadius
        opticalDepthLutPass.atmosphereRadius = atmoRadius
    }

    private fun ControlUiBuilder.colorSlider(label: String, color: Color, initialValue: Float, min: Float, max: Float, onChange: Slider.() -> Unit): Slider {
        val slider = sliderWithValueSmall(label, initialValue, min, max, widthLabel = 10f, onChange = onChange)
        slider.knobColor.setCustom(color)
        slider.trackColorHighlighted.setCustom(color.mix(Color.BLACK, 0.5f))
        return slider
    }

    companion object {
        private const val kmPerUnit = 100f
        private const val earthRadius = 6000f / kmPerUnit

        private const val moonRadius = 1750f / kmPerUnit
        private const val moonDistScale = 0.25f
        private const val moonDist = 384400 / kmPerUnit * moonDistScale
        private const val moonInclination = 5.145f

        // scaled moon orbital period (according to kepler's 3rd law)
        private val keplerC = (moonDist / moonDistScale).pow(3) / 27.32f.pow(2)
        private val moonT = moonDist.pow(3) / keplerC

        private const val texMilkyway = "Milkyway"
        private const val texSun = "Sun"
        private const val texSunBg = "Sun Background"
        private const val texMoon = "Moon"
    }
}