package de.fabmax.kool.demo.pbr

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.pbrMapGen.BrdfLutPass
import de.fabmax.kool.util.pbrMapGen.IrradianceMapPass
import de.fabmax.kool.util.pbrMapGen.ReflectionMapPass
import de.fabmax.kool.util.uiFont

/**
 * @author fabmax
 */


fun pbrDemoScene(ctx: KoolContext): List<Scene> {
    return PbrDemo(ctx).scenes
}

class PbrDemo(val ctx: KoolContext) {
    val scenes = mutableListOf<Scene>()

    private val contentScene: Scene

    private var irradianceMapPass: IrradianceMapPass? = null
    private var reflectionMapPass: ReflectionMapPass? = null
    private var brdfLut: BrdfLutPass? = null

    private val lightCycler = Cycler(lightSetups).apply { index = 2 }
    private val hdriCycler = Cycler(hdriTextures)
    private val loadedHdris = Array<Texture?>(hdriTextures.size) { null }

    private val pbrContentCycler = Cycler(listOf(PbrMaterialContent(), ColorGridContent(), RoughnesMetalGridContent()))

    init {
        val nextHdriKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "Next environment map", { it.isPressed }) {
            hdriCycler.next()
            updateHdri(hdriCycler.index)
        }
        val prevHdriKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "Prev environment map", { it.isPressed }) {
            hdriCycler.prev()
            updateHdri(hdriCycler.index)
        }

        contentScene = setupScene()
        contentScene.onDispose += {
            ctx.inputMgr.removeKeyListener(nextHdriKeyListener)
            ctx.inputMgr.removeKeyListener(prevHdriKeyListener)

            loadedHdris.forEach { it?.dispose() }
            irradianceMapPass?.dispose(ctx)
            reflectionMapPass?.dispose(ctx)
            brdfLut?.dispose(ctx)
        }

        scenes += contentScene
        scenes += pbrMenu()
    }

    private fun setupScene() = scene {
        mainRenderPass.clearColor = null
        lightCycler.current.setup(this)

        +orbitInputTransform {
            +camera
            // let the camera slowly rotate around vertical axis
            onUpdate += { ctx -> verticalRotation += ctx.deltaT * 2f }
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 20.0
        }

        loadHdri(hdriCycler.index) { tex ->
            val irrMapPass = IrradianceMapPass(this, tex)
            val reflMapPass = ReflectionMapPass(this, tex)
            val brdfLutPass = BrdfLutPass(this)
            irradianceMapPass = irrMapPass
            reflectionMapPass = reflMapPass
            brdfLut = brdfLutPass

            this += Skybox(reflMapPass.colorTextureCube, 1.25f)

            pbrContentCycler.forEach {
                +it.createContent(this, irrMapPass.colorTextureCube, reflMapPass.colorTextureCube, brdfLutPass.colorTexture, ctx)
            }
            pbrContentCycler.current.show()
        }
    }

    private fun pbrMenu() = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-480f), zero())
            layoutSpec.setSize(dps(250f), dps(360f), full())

            // environment map selection
            var y = -35f
            +label("env-lbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                text = "Environment"
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 30f
            val envLabel = button("selected-env") {
                layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                text = hdriCycler.current.name

                onClick += { _, _, _ ->
                    text = hdriCycler.next().name
                    updateHdri(hdriCycler.index)
                }
            }
            +envLabel
            +button("env-left") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    envLabel.text = hdriCycler.prev().name
                    updateHdri(hdriCycler.index)
                }
            }
            +button("env-right") {
                layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    envLabel.text = hdriCycler.next().name
                    updateHdri(hdriCycler.index)
                }
            }

            // light mode selection
            y -= 40f
            +label("light-lbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                text = "Lighting"
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 30f
            +toggleButton("env-lighting") {
                layoutSpec.setOrigin(pcs(8f), dps(y), zero())
                layoutSpec.setSize(pcs(84f), dps(35f), full())
                text = "Image Based"
                isEnabled = true
                onClick += { _, _, _ ->
                    pbrContentCycler.forEach { it.setUseImageBasedLighting(isEnabled) }
                }
            }
            y -= 30f
            val lightLabel = button("selected-light") {
                layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                text = lightCycler.current.name

                onClick += { _, _, _ ->
                    text = lightCycler.next().name
                    lightCycler.current.setup(contentScene)
                }
            }
            +lightLabel
            +button("light-left") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    lightLabel.text = lightCycler.prev().name
                    lightCycler.current.setup(contentScene)
                }
            }
            +button("light-right") {
                layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    lightLabel.text = lightCycler.next().name
                    lightCycler.current.setup(contentScene)
                }
            }

            y -= 40f
            // content selection
            +label("content-lbl") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                text = "Scene Content"
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 30f
            val contentLabel = button("selected-content") {
                layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                text = pbrContentCycler.current.name

                onClick += { _, _, _ ->
                    pbrContentCycler.current.hide()
                    pbrContentCycler.next().show()
                    text = pbrContentCycler.current.name
                }
            }
            +contentLabel
            +button("content-left") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    pbrContentCycler.current.hide()
                    pbrContentCycler.prev().show()
                    contentLabel.text = pbrContentCycler.current.name
                }
            }
            +button("content-right") {
                layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    pbrContentCycler.current.hide()
                    pbrContentCycler.next().show()
                    contentLabel.text = pbrContentCycler.current.name
                }
            }

            y -= 10f
            pbrContentCycler.forEach { it.createMenu(this, smallFont, y) }
            pbrContentCycler.current.show()
        }
    }

    private fun updateHdri(idx: Int) {
        loadHdri(idx) { tex ->
            irradianceMapPass?.let {
                it.hdriTexture = tex
                it.update()
            }
            reflectionMapPass?.let {
                it.hdriTexture = tex
                it.update()
            }
        }
    }

    private fun loadHdri(idx: Int, recv: (Texture) -> Unit) {
        val tex = loadedHdris[idx]
        if (tex == null) {
            ctx.assetMgr.loadAndPrepareTexture(hdriTextures[idx].hdriPath, hdriTexProps) {
                loadedHdris[idx] = it
                recv(it)
            }
        } else {
            recv(tex)
        }
    }

    private class EnvironmentMap(val hdriPath: String, val name: String)

    private class LightSetup(val name: String, val setup: Scene.() -> Unit)

    abstract class PbrContent(val name: String) {
        var content: TransformGroup? = null
        var ui: UiContainer? = null

        fun show() {
            content?.isVisible = true
            ui?.isVisible = true
        }

        fun hide() {
            content?.isVisible = false
            ui?.isVisible = false
        }

        abstract fun createMenu(parent: UiContainer, smallFont: Font, yPos: Float)
        abstract fun setUseImageBasedLighting(enabled: Boolean)
        abstract fun createContent(scene: Scene, irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture, ctx: KoolContext): TransformGroup
    }

    companion object {
        // HDRIs are encoded as RGBE images, use nearest sampling to not mess up the exponent
        private val hdriTexProps = TextureProps(
                minFilter = FilterMethod.NEAREST,
                magFilter = FilterMethod.NEAREST,
                mipMapping = true)

        private val hdriPath = Demo.getProperty("pbrDemo.envMaps", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/hdri")
        private val hdriTextures = listOf(
                EnvironmentMap("${hdriPath}/circus_arena_1k.rgbe.png", "Circus"),
                EnvironmentMap("${hdriPath}/newport_loft.rgbe.png", "Loft"),
                EnvironmentMap("${hdriPath}/spruit_sunrise_1k.rgbe.png", "Sunrise"),
                EnvironmentMap("${hdriPath}/shanghai_bund_1k.rgbe.png", "Shanghai")
        )

        private const val lightStrength = 250f
        private const val lightExtent = 10f
        private val lightSetups = listOf(
                LightSetup("Off") { lighting.lights.clear() },

                LightSetup("Front x1") {
                    val light1 = Light().setPoint(Vec3f(0f, 0f, lightExtent * 1.5f)).setColor(Color.WHITE, lightStrength * 2f)
                    lighting.lights.clear()
                    lighting.lights.add(light1)
                },

                LightSetup("Front x4") {
                    val light1 = Light().setPoint(Vec3f(lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light2 = Light().setPoint(Vec3f(-lightExtent, -lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light3 = Light().setPoint(Vec3f(-lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light4 = Light().setPoint(Vec3f(lightExtent, -lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    lighting.lights.clear()
                    lighting.lights.add(light1)
                    lighting.lights.add(light2)
                    lighting.lights.add(light3)
                    lighting.lights.add(light4)
                },

                LightSetup("Top x1") {
                    val light1 = Light().setPoint(Vec3f(0f, lightExtent * 1.5f, 0f)).setColor(Color.WHITE, lightStrength * 2f)
                    lighting.lights.clear()
                    lighting.lights.add(light1)
                },

                LightSetup("Top x4") {
                    val light1 = Light().setPoint(Vec3f(lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light2 = Light().setPoint(Vec3f(-lightExtent, lightExtent, -lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light3 = Light().setPoint(Vec3f(-lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light4 = Light().setPoint(Vec3f(lightExtent, lightExtent, -lightExtent)).setColor(Color.WHITE, lightStrength)
                    lighting.lights.clear()
                    lighting.lights.add(light1)
                    lighting.lights.add(light2)
                    lighting.lights.add(light3)
                    lighting.lights.add(light4)
                }
        )
    }
}