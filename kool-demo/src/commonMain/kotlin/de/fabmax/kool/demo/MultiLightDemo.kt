package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

fun multiLightDemo(ctx: KoolContext): List<Scene> {
    return MultiLightDemo(ctx).scenes
}

class MultiLightDemo(ctx: KoolContext) {
    val scenes = mutableListOf<Scene>()

    private val mainScene: Scene
    private val lights = listOf(
            LightMesh(Color.MD_CYAN.toLinear()),
            LightMesh(Color.MD_RED.toLinear()),
            LightMesh(Color.MD_AMBER.toLinear()),
            LightMesh(Color.MD_GREEN.toLinear()))
    private val depthPasses = mutableListOf<ShadowMapPass>()

    private var lightCount = 4
    private var isColoredLights = true
    private var isSpotLights = true
    private var isRandomSpots = true
    private var lightPower = 400f

    private val colorCycler = Cycler(matColors)

    private var modelShader: PbrShader? = null

    init {
        mainScene = mainScene(ctx)
        scenes += mainScene
        scenes += menu(ctx)

        lights.forEach {
            val pass = ShadowMapPass(mainScene, it.light)
            depthPasses += pass
            ctx.offscreenPasses += pass.offscreenPass
        }
    }

    private fun mainScene(ctx: KoolContext) = scene {
        +orbitInputTransform {
            +camera
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 20f
            translation.set(0f, 2f, 0f)
            setMouseRotation(20f, -30f)
        }

        lighting.lights.clear()
        lights.forEach { +it }
        updateLighting()

        ctx.loadModel("bunny.kmfz", 0.05f, Vec3f(0f, 3.75f, 0f)) {
            +it

            val cfg = PbrShader.PbrConfig().apply {
                albedoSource = Albedo.STATIC_ALBEDO
                isReceivingShadows = true
            }
            modelShader = PbrShader(cfg).apply {
                albedo = colorCycler.current.linColor
                roughness = 0.1f
                metallic = 0f

                onCreated += {
                    depthMaps?.let { maps ->
                        depthPasses.forEachIndexed { i, pass ->
                            if (i < maps.size) {
                                maps[i] = pass.offscreenPass.impl.depthTexture
                            }
                        }
                    }
                }
            }
            it.pipelineLoader = modelShader
        }

        +textureMesh(isNormalMapped = true) {
            generate {
                rect {
                    rotate(-90f, Vec3f.X_AXIS)
                    size.set(40f, 40f)
                    origin.set(-size.x / 2, -size.y / 2, 0f)
                    fullTexCoords(1.5f)
                }
            }

            val cfg = PbrShader.PbrConfig().apply {
                albedoSource = Albedo.TEXTURE_ALBEDO
                isNormalMapped = true
                isRoughnessMapped = true
                isReceivingShadows = true
            }
            pipelineLoader = PbrShader(cfg).apply {
                val basePath = Demo.getProperty("pbrDemo.materials", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/materials")
                albedoMap = Texture { it.loadTextureData("$basePath/woodfloor/WoodFlooringMahoganyAfricanSanded001_COL_2K.jpg") }
                normalMap = Texture { it.loadTextureData("$basePath/woodfloor/WoodFlooringMahoganyAfricanSanded001_NRM_2K.jpg") }
                roughnessMap = Texture { it.loadTextureData("$basePath/woodfloor/WoodFlooringMahoganyAfricanSanded001_REFL_2K.jpg") }
                metallic = 0f

                onCreated += {
                    depthMaps?.let { maps ->
                        depthPasses.forEachIndexed { i, pass ->
                            if (i < maps.size) {
                                maps[i] = pass.offscreenPass.impl.depthTexture
                            }
                        }
                    }
                }
            }
        }
    }

    private fun menu(ctx: KoolContext) = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-480f), zero())
            layoutSpec.setSize(dps(250f), dps(340f), full())

            // environment map selection
            var y = -40f
            +label("lights") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                text = "Lights"
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +toggleButton("colorToggle") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                text = "Colored Lights:"
                isEnabled = isColoredLights
                onClick += { _, _, _ ->
                    isColoredLights = isEnabled
                    updateLighting()
                }
            }
            y -= 35f
            +toggleButton("spotToggle") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                text = "Spot Lights:"
                isEnabled = isSpotLights
                onClick += { _, _, _ ->
                    isSpotLights = isEnabled
                    updateLighting()
                }
            }
            y -= 35f
            +toggleButton("randomToggle") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                text = "Randomize Spots:"
                isEnabled = isRandomSpots
                onClick += { _, _, _ ->
                    isRandomSpots = isEnabled
                    updateLighting()
                }
            }
            y -= 35f
            +label("lightCntLbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                text = "Lights:"
            }
            val btnLightCnt = button("lightCnt") {
                layoutSpec.setOrigin(pcs(45f), dps(y), zero())
                layoutSpec.setSize(pcs(40f), dps(35f), full())
                text = "$lightCount"

                onClick += { _, _, _ ->
                    lightCount++
                    if (lightCount > 4) { lightCount = 1 }
                    text = "$lightCount"
                    updateLighting()
                }
            }
            +btnLightCnt
            +button("decLightCnt") {
                layoutSpec.setOrigin(pcs(35f), dps(y), zero())
                layoutSpec.setSize(pcs(10f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    lightCount--
                    if (lightCount < 1) { lightCount = 4 }
                    btnLightCnt.text = "$lightCount"
                    updateLighting()
                }
            }
            +button("incLightCnt") {
                layoutSpec.setOrigin(pcs(85f), dps(y), zero())
                layoutSpec.setSize(pcs(10f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    lightCount++
                    if (lightCount > 4) { lightCount = 1 }
                    btnLightCnt.text = "$lightCount"
                    updateLighting()
                }
            }
            y -= 35f
            +label("lightPowerLbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                text = "Power:"
            }
            +slider("lightPowerSlider") {
                layoutSpec.setOrigin(pcs(30f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                value = lightPower / 10f

                onValueChanged += {
                    lightPower = value * 10f
                    updateLighting()
                }
            }

            y -= 40f
            +label("material") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                text = "Material"
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +label("roughnessLbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                text = "Rough:"
            }
            +slider("roughhnessSlider") {
                layoutSpec.setOrigin(pcs(30f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                value = 10f

                onValueChanged += {
                    modelShader?.roughness = value / 100f
                }
            }
            y -= 35f
            +label("colorLbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                text = "Color:"
            }
            val matLabel = button("selected-color") {
                layoutSpec.setOrigin(pcs(45f), dps(y), zero())
                layoutSpec.setSize(pcs(40f), dps(35f), full())
                text = colorCycler.current.name

                onClick += { _, _, _ ->
                    text = colorCycler.next().name
                    modelShader?.albedo = colorCycler.current.linColor
                }
            }
            +matLabel
            +button("color-left") {
                layoutSpec.setOrigin(pcs(35f), dps(y), zero())
                layoutSpec.setSize(pcs(10f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    matLabel.text = colorCycler.prev().name
                    modelShader?.albedo = colorCycler.current.linColor
                }
            }
            +button("color-right") {
                layoutSpec.setOrigin(pcs(85f), dps(y), zero())
                layoutSpec.setSize(pcs(10f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    matLabel.text = colorCycler.next().name
                    modelShader?.albedo = colorCycler.current.linColor
                }
            }
        }
    }

    private fun updateLighting() {
        lights.forEach { it.disable() }

        var pos = 0f
        val step = 360f / lightCount
        for (i in 0 until min(lightCount, lights.size)) {
            lights[i].setup(pos, isSpotLights, isColoredLights, lightPower)
            lights[i].enable()
            pos += step
        }
    }

    private inner class LightMesh(val color: Color) : TransformGroup() {
        val light = Light()

        private val meshPos = MutableVec3f()
        private val rotOff = randomF(0f, 3f)

        init {
            val lightMesh = colorMesh {
                generate {
                    color = this@LightMesh.color.toSrgb()
                    sphere {
                        radius = 0.2f
                    }
                }
                pipelineLoader = ModeledShader.VertexColor()
            }
            +lightMesh

            onPreRender += { ctx ->
                setIdentity()
                rotate(ctx.time.toFloat() * -10f, Vec3f.Y_AXIS)
                translate(meshPos)
                light.position.set(lightMesh.globalCenter)
                light.direction.set(0f, 2f, 0f).subtract(lightMesh.globalCenter).norm()

                if (isRandomSpots) {
                    val r = cos(ctx.time / 15 + rotOff).toFloat()
                    light.direction.rotate(r * 20f, Vec3f.X_AXIS)
                    light.direction.rotate(r * 20f, Vec3f.Z_AXIS)
                    light.spotAngle = 50f - r * 10f
                }
            }
        }

        fun setup(angPos: Float, isSpot: Boolean, isColored: Boolean, power: Float) {
            val x = cos(angPos.toRad()) * 10f
            val z = sin(angPos.toRad()) * 10f
            meshPos.set(x, 9f, z)
            val dir = MutableVec3f(meshPos).scale(-1f).norm()
            val color = if (isColored) { this.color } else { Color.WHITE }

            if (isSpot) {
                light.setSpot(meshPos, dir, 50f).setColor(color, power)
            } else {
                light.setPoint(meshPos).setColor(color, power)
            }
        }

        fun enable() {
            isVisible = true
            scene?.lighting?.lights?.apply {
                if (!contains(light)) {
                    add(light)
                }
            }
        }

        fun disable() {
            isVisible = false
            scene?.lighting?.lights?.remove(light)
        }
    }

    private fun KoolContext.loadModel(path: String, scale: Float, translation: Vec3f, recv: (Mesh) -> Unit) {
        assetMgr.loadModel(path) { model ->
            if (model != null) {
                val mesh = model.meshes[0].toMesh()
                mesh.geometry.forEach {
                    it.position.scale(scale).add(translation)
                }
                recv(mesh)
            }
        }
    }

    private data class MatColor(val name: String, val linColor: Color)

    companion object {
        private val matColors = listOf(
                MatColor("White", Color.WHITE.toLinear()),
                MatColor("Red", Color.MD_RED.toLinear()),
                MatColor("Pink", Color.MD_PINK.toLinear()),
                MatColor("Purple", Color.MD_PURPLE.toLinear()),
                MatColor("Deep Purple", Color.MD_DEEP_PURPLE.toLinear()),
                MatColor("Indigo", Color.MD_INDIGO.toLinear()),
                MatColor("Blue", Color.MD_BLUE.toLinear()),
                MatColor("Cyan", Color.MD_CYAN.toLinear()),
                MatColor("Teal", Color.MD_TEAL.toLinear()),
                MatColor("Green", Color.MD_GREEN.toLinear()),
                MatColor("Light Green", Color.MD_LIGHT_GREEN.toLinear()),
                MatColor("Lime", Color.MD_LIME.toLinear()),
                MatColor("Yellow", Color.MD_YELLOW.toLinear()),
                MatColor("Amber", Color.MD_AMBER.toLinear()),
                MatColor("Orange", Color.MD_ORANGE.toLinear()),
                MatColor("Deep Orange", Color.MD_DEEP_ORANGE.toLinear()),
                MatColor("Brown", Color.MD_BROWN.toLinear()),
                MatColor("Grey", Color.MD_GREY.toLinear()),
                MatColor("Blue Grey", Color.MD_BLUE_GREY.toLinear()),
                MatColor("Almost Black", Color(0.1f, 0.1f, 0.1f).toLinear())
        )
    }
}