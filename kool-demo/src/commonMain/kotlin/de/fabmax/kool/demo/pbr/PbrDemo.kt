package de.fabmax.kool.demo.pbr

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.loadTexture2d
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.MipMapping
import de.fabmax.kool.pipeline.SamplerSettings
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.get
import de.fabmax.kool.util.set
import kotlinx.coroutines.launch

/**
 * @author fabmax
 */

class PbrDemo : DemoScene("PBR Materials") {

    private lateinit var skybox: Skybox.Cube

    private val loadedHdris = Array<EnvironmentMap?>(hdriTextures.size) { null }

    private val sphereProto = SphereProto()
    private val pbrContent = listOf(
        PbrMaterialContent(sphereProto, mainScene),
        ColorGridContent(sphereProto),
        RoughnesMetalGridContent(sphereProto)
    )
    private val selectedContentIdx = mutableStateOf(0)
    private val selectedHdriIdx = mutableStateOf(0)
    private val selectedLightIdx = mutableStateOf(0)

    private val selectedContent: PbrContent get() = pbrContent[selectedContentIdx.value]
    private val selectedLightSetup: LightSetup get() = lightSetups[selectedLightIdx.value]

    private val isAutoRotate = mutableStateOf(true).onChange { _, new ->
        pbrContent.forEach { c -> c.autoRotate = new }
    }

    override fun lateInit(ctx: KoolContext) {
        mainScene.onRelease {
            loadedHdris.forEach { it?.release() }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        selectedLightSetup.setup(this)

        orbitCamera {
            // let the camera slowly rotate around vertical axis
            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * 2f
                }
            }
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            setZoom(20.0)
        }

        coroutineScope.launch {
            val maps = loadHdri(selectedHdriIdx.value)
            skybox = Skybox.cube(maps.reflectionMap, 1f)
            this@setupMainScene += skybox

            pbrContent.forEach {
                addNode(it.createContent(this@setupMainScene, maps, ctx))
            }
            selectedContent.show()
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        val comboW = UiSizes.baseSize * 3.5f
        MenuRow {
            Text("Scene") { labelStyle(Grow.Std) }
            ComboBox {
                modifier
                    .width(comboW)
                    .items(pbrContent)
                    .selectedIndex(selectedContentIdx.use())
                    .onItemSelected {
                        selectedContent.hide()
                        selectedContentIdx.set(it)
                        selectedContent.show()
                    }
            }
        }
        with(selectedContent) { createContentMenu() }

        Text("View") { sectionTitleStyle() }
        MenuRow {
            Text("Environment") { labelStyle(Grow.Std) }
            ComboBox {
                modifier
                    .width(comboW)
                    .items(hdriTextures)
                    .selectedIndex(selectedHdriIdx.use())
                    .onItemSelected {
                        selectedHdriIdx.set(it)
                        mainScene.coroutineScope.launch { updateHdri(it) }
                    }
            }
        }
        MenuRow {
            Text("Discrete lights") { labelStyle(Grow.Std) }
            ComboBox {
                modifier
                    .width(comboW)
                    .items(lightSetups)
                    .selectedIndex(selectedLightIdx.use())
                    .onItemSelected {
                        selectedLightIdx.set(it)
                        selectedLightSetup.setup(mainScene)
                    }
            }
        }
        LabeledSwitch("Auto rotate view", isAutoRotate)
    }

    private suspend fun updateHdri(idx: Int) {
        val envMap = loadHdri(idx)
        skybox.skyboxShader.setSingleSky(envMap.reflectionMap)
        pbrContent.forEach { it.updateEnvironmentMap(envMap) }
    }

    private suspend fun loadHdri(idx: Int): EnvironmentMap {
        val loaded = loadedHdris[idx]
        if (loaded != null) {
            return loaded
        }

        val rgbe = Assets.loadTexture2d(
            assetPath = hdriTextures[idx].hdriPath,
            format = TexFormat.RGBA,
            mipMapping = MipMapping.Off,
            samplerSettings = SamplerSettings().nearest()
        ).getOrThrow()
        val maps = EnvironmentMap.fromHdriTexture(rgbe)
        loadedHdris[idx] = maps
        return maps
    }

    private class Hdri(val hdriPath: String, val name: String) {
        override fun toString() = name
    }

    private class LightSetup(val name: String, val setup: Scene.() -> Unit) {
        override fun toString() = name
    }

    abstract class PbrContent(val name: String) {
        var content: Node? = null
        var autoRotate = true

        fun show() {
            content?.isVisible = true
        }

        fun hide() {
            content?.isVisible = false
        }

        override fun toString() = name

        abstract fun UiScope.createContentMenu()
        abstract fun createContent(scene: Scene, envMap: EnvironmentMap, ctx: KoolContext): Node
        abstract fun updateEnvironmentMap(envMap: EnvironmentMap)
    }

    class SphereProto {
        val detailSphere = IndexedVertexList(VertexLayouts.PositionNormalTexCoordTangent)
        val parallaxSphere = IndexedVertexList(VertexLayouts.PositionNormalTexCoordTangent)
        val simpleSphere = IndexedVertexList(VertexLayouts.PositionNormal)

        init {
            MeshBuilder(detailSphere).apply {
                vertexCustomizer = {
                    val uv = it.texCoord.get(MutableVec2f())
                    uv.x *= 4
                    uv.y *= 2
                    it.texCoord.set(uv)
                }
                uvSphere {
                    steps = 700
                    radius = 7f
                }
            }

            MeshBuilder(parallaxSphere).apply {
                vertexCustomizer = {
                    val uv = it.texCoord.get(MutableVec2f())
                    uv.x *= 4
                    uv.y *= 2
                    it.texCoord.set(uv)
                }
                uvSphere {
                    steps = 250
                    radius = 7f
                }
            }

            MeshBuilder(simpleSphere).apply {
                uvSphere {
                    steps = 100
                    radius = 1f
                }
            }
        }
    }

    companion object {
        private val hdriTextures = listOf(
            Hdri("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png", "South Africa"),
            Hdri("${DemoLoader.hdriPath}/circus_arena_1k.rgbe.png", "Circus"),
            Hdri("${DemoLoader.hdriPath}/newport_loft.rgbe.png", "Loft"),
            Hdri("${DemoLoader.hdriPath}/shanghai_bund_1k.rgbe.png", "Shanghai"),
            Hdri("${DemoLoader.hdriPath}/mossy_forest_1k.rgbe.png", "Mossy forest")
        )

        private const val lightStrength = 250f
        private const val lightExtent = 10f
        private val lightSetups = listOf(
                LightSetup("None") { lighting.clear() },

                LightSetup("Front x1") {
                    val light1 = Light.Point().setup(Vec3f(0f, 0f, lightExtent * 1.5f)).setColor(Color.WHITE, lightStrength * 2f)
                    lighting.clear()
                    lighting.addLight(light1)
                },

                LightSetup("Front x4") {
                    val light1 = Light.Point().setup(Vec3f(lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light2 = Light.Point().setup(Vec3f(-lightExtent, -lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light3 = Light.Point().setup(Vec3f(-lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light4 = Light.Point().setup(Vec3f(lightExtent, -lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    lighting.clear()
                    lighting.addLight(light1)
                    lighting.addLight(light2)
                    lighting.addLight(light3)
                    lighting.addLight(light4)
                },

                LightSetup("Top x1") {
                    val light1 = Light.Point().setup(Vec3f(0f, lightExtent * 1.5f, 0f)).setColor(Color.WHITE, lightStrength * 2f)
                    lighting.clear()
                    lighting.addLight(light1)
                },

                LightSetup("Top x4") {
                    val light1 = Light.Point().setup(Vec3f(lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light2 = Light.Point().setup(Vec3f(-lightExtent, lightExtent, -lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light3 = Light.Point().setup(Vec3f(-lightExtent, lightExtent, lightExtent)).setColor(Color.WHITE, lightStrength)
                    val light4 = Light.Point().setup(Vec3f(lightExtent, lightExtent, -lightExtent)).setColor(Color.WHITE, lightStrength)
                    lighting.clear()
                    lighting.addLight(light1)
                    lighting.addLight(light2)
                    lighting.addLight(light3)
                    lighting.addLight(light4)
                }
        )
    }
}