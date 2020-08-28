package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.ControlUiBuilder
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.Slider
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import kotlin.math.max

class AtmosphereDemo : DemoScene("Atmosphere") {

    private val kmPerUnit = 100f
    private val earthRadius = 6000f / kmPerUnit

    private val sunColor = Color.MD_AMBER.mix(Color.WHITE, 0.85f).toLinear()
    private val sun = Light().apply {
        setDirectional(Vec3f.NEG_Z_AXIS)
        setColor(sunColor, 5f)
    }
    private var sunIntensity = 1f

    private var time = 0.5f
    private var animateTime = true
    private var timeSlider: Slider? = null

    private val shadows = mutableListOf<SimpleShadowMap>()
    private val atmoShader = AtmosphericScatteringShader()
    private val earthTransform = Group("earth")
    private val camTransform = EarthCamTransform(earthRadius)

    override fun lateInit(ctx: KoolContext) {
        camTransform.apply {
            mainScene.registerDragHandler(this)

            val cam = mainScene.camera as PerspectiveCamera
            +cam
            onUpdate += { ev ->
                cam.apply {
                    val h = globalPos.length() - earthRadius
                    position.set(Vec3f.ZERO)
                    lookAt.set(Vec3f.NEG_Z_AXIS)
                    clipNear = max(0.003f, h * 0.5f)
                    clipFar = clipNear * 1000f
                }

                if (animateTime) {
                    timeSlider?.value = (time + ev.deltaT / 300) % 1f
                }
            }
        }
        updateSun()
    }

    override fun setupMainScene(ctx: KoolContext) = scene {
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
        deferredPipeline.contentGroup.setupContent()
        +deferredPipeline.renderOutput
        shadows.forEach { shadow ->
            shadow.drawNode = deferredPipeline.contentGroup
            shadow.shadowBounds = deferredPipeline.contentGroup.bounds
        }
        atmoShader.apply {
            sceneColor = deferredPipeline.pbrPass.colorTexture
            scenePos = deferredPipeline.mrtPass.positionAo
            planetRadius = earthRadius
            atmosphereRadius = 6500f / kmPerUnit

            scatteringCoeffs = Vec3f(0.75f, 1.05f, 1.30f)
            rayleighCoeffs = Vec3f(0.5f, 0.5f, 1f)
            scatteringCoeffStrength = 2f
        }

        +group {
            isFrustumChecked = false
            +Skybox.sphere(Texture("milkyway-dark.jpg"), hdriInput = false)
            // milky way is wildly tilted (no idea in which direction...)
            rotate(-60f, Vec3f.X_AXIS)
        }

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
                val earthShader = EarthShader().also { shader = it }

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
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
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
                atmoShader.atmosphereRadius = earthRadius + value / kmPerUnit
            }
            sliderWithValueSmall("Falloff:", atmoShader.densityFalloff, 0f, 15f, 2, widthLabel = 24f) {
                atmoShader.densityFalloff = value
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
        val angle = 180f
        val lightDir = MutableVec3f(0f, 0f, 1f).rotate(angle, Vec3f.Y_AXIS)
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

    private fun ControlUiBuilder.colorSlider(label: String, color: Color, initialValue: Float, min: Float, max: Float, onChange: Slider.() -> Unit): Slider {
        val slider = sliderWithValueSmall(label, initialValue, min, max, widthLabel = 10f, onChange = onChange)
        slider.knobColor.setCustom(color)
        slider.trackColorHighlighted.setCustom(color.mix(Color.BLACK, 0.5f))
        return slider
    }
}