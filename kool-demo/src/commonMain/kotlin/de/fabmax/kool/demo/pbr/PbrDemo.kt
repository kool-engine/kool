package de.fabmax.kool.demo.pbr

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.UiContainer
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps

/**
 * @author fabmax
 */

class PbrDemo : DemoScene("PBR Materials") {

    private lateinit var skybox: Mesh
    private lateinit var envMaps: EnvironmentMaps

    private val lightCycler = Cycler(lightSetups)
    private val hdriCycler = Cycler(hdriTextures)
    private val loadedHdris = Array<Texture2d?>(hdriTextures.size) { null }

    private val sphereProto = SphereProto()
    private val pbrContentCycler = Cycler(listOf(
            PbrMaterialContent(sphereProto), ColorGridContent(sphereProto), RoughnesMetalGridContent(sphereProto)))

    private var autoRotate = true
        set(value) {
            field = value
            pbrContentCycler.forEach { it.autoRotate = value }
        }

    override fun lateInit(ctx: KoolContext) {
        val nextHdriKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "Next environment map", { it.isPressed }) {
            hdriCycler.next()
            updateHdri(hdriCycler.index, ctx)
        }
        val prevHdriKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "Prev environment map", { it.isPressed }) {
            hdriCycler.prev()
            updateHdri(hdriCycler.index, ctx)
        }
        mainScene.onDispose += {
            ctx.inputMgr.removeKeyListener(nextHdriKeyListener)
            ctx.inputMgr.removeKeyListener(prevHdriKeyListener)

            loadedHdris.forEach { it?.dispose() }
            envMaps.dispose()
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        lightCycler.current.setup(this)

        +orbitInputTransform {
            +camera
            // let the camera slowly rotate around vertical axis
            onUpdate += {
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 2f
                }
            }
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 20.0
        }

        loadHdri(hdriCycler.index, ctx) { hdri ->
            envMaps = EnvironmentHelper.hdriEnvironment(this, hdri, false)
            skybox = Skybox.cube(envMaps.reflectionMap, 1f)
            this += skybox

            pbrContentCycler.forEach {
                +it.createContent(this, envMaps, ctx)
            }
            pbrContentCycler.current.show()
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        menuHeight = 440f

        section("Environment") {
            cycler(null, hdriCycler) { _, _ -> updateHdri(hdriCycler.index, ctx) }
        }
        section("Image Based Lighting") {
            toggleButton("IBL Enabled", true) {
                pbrContentCycler.forEach { it.setUseImageBasedLighting(isEnabled) }
            }
        }
        section("Discrete Lighting") {
            cycler(null, lightCycler) { light, _ -> light.setup(mainScene) }
        }
        section("Scene Content") {
            cycler(null, pbrContentCycler) { content, prevContent ->
                prevContent.hide()
                content.show()
            }
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }

            menuY += 25f
            pbrContentCycler.forEach { it.createMenu(menuContainer, smallFont, menuY) }
            pbrContentCycler.current.show()
        }
    }

    private fun updateHdri(idx: Int, ctx: KoolContext) {
        loadHdri(idx, ctx) { tex ->
            envMaps.let { oldEnvMap -> ctx.runDelayed(1) { oldEnvMap.dispose() } }
            envMaps = EnvironmentHelper.hdriEnvironment(mainScene, tex, false)
            (skybox.shader as Skybox.SkyboxCubeShader).environmentMap = envMaps.reflectionMap
            pbrContentCycler.forEach { it.updateEnvironmentMap(envMaps) }
        }
    }

    private fun loadHdri(idx: Int, ctx: KoolContext, recv: (Texture2d) -> Unit) {
        val tex = loadedHdris[idx]
        if (tex == null) {
            ctx.assetMgr.launch {
                val loadedTex = loadAndPrepareTexture(hdriTextures[idx].hdriPath, hdriTexProps)
                loadedHdris[idx] = loadedTex
                recv(loadedTex)
            }
        } else {
            recv(tex)
        }
    }

    private class Hdri(val hdriPath: String, val name: String) {
        override fun toString() = name
    }

    private class LightSetup(val name: String, val setup: Scene.() -> Unit) {
        override fun toString() = name
    }

    abstract class PbrContent(val name: String) {
        var content: Group? = null
        var ui: UiContainer? = null
        var autoRotate = true

        fun show() {
            content?.isVisible = true
            ui?.isVisible = true
        }

        fun hide() {
            content?.isVisible = false
            ui?.isVisible = false
        }

        override fun toString() = name

        abstract fun createMenu(parent: UiContainer, smallFont: Font, yPos: Float)
        abstract fun setUseImageBasedLighting(enabled: Boolean)
        abstract fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Group
        abstract fun updateEnvironmentMap(envMaps: EnvironmentMaps)
    }

    class SphereProto {
        val detailSphere = IndexedVertexList(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, Attribute.NORMALS, Attribute.TANGENTS)
        val simpleSphere = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)

        init {
            MeshBuilder(detailSphere).apply {
                vertexModFun = {
                    texCoord.x *= 4
                    texCoord.y *= 2
                }
                uvSphere {
                    steps = 700
                    radius = 7f
                }
            }
            detailSphere.generateTangents()

            MeshBuilder(simpleSphere).apply {
                uvSphere {
                    steps = 100
                    radius = 1f
                }
            }
        }
    }

    companion object {
        // HDRIs are encoded as RGBE images, use nearest sampling to not mess up the exponent
        private val hdriTexProps = TextureProps(
                minFilter = FilterMethod.NEAREST,
                magFilter = FilterMethod.NEAREST,
                mipMapping = false,
                maxAnisotropy = 1)

        private val hdriTextures = listOf(
                Hdri("${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", "South Africa"),
                Hdri("${Demo.envMapBasePath}/circus_arena_1k.rgbe.png", "Circus"),
                Hdri("${Demo.envMapBasePath}/newport_loft.rgbe.png", "Loft"),
                Hdri("${Demo.envMapBasePath}/shanghai_bund_1k.rgbe.png", "Shanghai"),
                Hdri("${Demo.envMapBasePath}/mossy_forest_1k.rgbe.png", "Mossy Forest")
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