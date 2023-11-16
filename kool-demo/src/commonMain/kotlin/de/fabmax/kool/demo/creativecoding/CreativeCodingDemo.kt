package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.LabeledSwitch
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

class CreativeCodingDemo : DemoScene("Creative Coding") {

    private val isAutoRotate = mutableStateOf(true)

    private val contents = mutableListOf<CreativeContent>()
    private val contentIndex = mutableStateOf(3)
    private val content: CreativeContent
        get() = contents[contentIndex.value]
    private val contentGroup = Node()

    private val hdri by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val light = mainScene.lighting.singleDirectionalLight {
            setup(Vec3f(-1f, -0.5f, -1f))
            setColor(MdColor.AMBER tone 100, 3f)
        }

        val resources = Resources(hdri, listOf(CascadedShadowMap(mainScene, light, 2000f, nearOffset = -200f)))

        contents += Circles()
        contents += Hexagons(resources)
        contents += LargeSpheres(resources)
        contents += SmallSpheres(resources)
        contents += PlanarOrbits(resources)

        addNode(contentGroup)
        selectContent(contentIndex.value)

        setupCamera()
        skybox(hdri, lod = 2f)

        onRelease {
            contents.forEach { it.release() }
        }
    }

    private fun selectContent(newContentIndex: Int) {
        // remove old content
        contentGroup -= content

        // add new content
        contentIndex.set(newContentIndex)
        contentGroup += content
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        LabeledSwitch("Auto rotate view:", isAutoRotate)

        MenuRow {
            Text("Scene:") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.largeGap)
            }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .items(contents)
                    .selectedIndex(contentIndex.use())
                    .onItemSelected {
                        selectContent(it)
                        // somewhat ugly: make sure the remembered() settings values are reset when switching
                        // between scenes
                        this@menuSurface.clearMemory()
                    }
            }
        }

        divider()

        with(content) {
            settingsMenu()
        }
    }

    private fun Scene.setupCamera() {
        orbitCamera {
            minZoom = 100.0
            maxZoom = 1000.0
            zoom = 400.0
            rightDragMethod = OrbitInputTransform.DragMethod.NONE

            camera.setClipRange(2f, 2000f)
            setMouseRotation(0f, -10f)

            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * -3f
                    contentGroup.transform.rotate((Time.deltaT * 3f).deg, Vec3f.Y_AXIS)
                }
            }
        }
    }

    class Resources(
        val imageEnv: EnvironmentMaps,
        val shadowMaps: List<ShadowMap>
    )

    companion object {
        val txtFormatInt: (Float) -> String = { "${it.roundToInt()}" }

        private val backgroundGradient = ColorGradient(
            0f to (MdColor.GREY toneLin 200),
            0.1f to (MdColor.GREY toneLin 200),
            0.95f to (MdColor.BLUE_GREY toneLin 800),
            1f to (MdColor.BLUE_GREY toneLin 800)
        )
    }
}