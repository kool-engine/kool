package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.demo.building.BuildingDemo
import de.fabmax.kool.demo.pbr.PbrDemo
import de.fabmax.kool.demo.procedural.ProceduralDemo
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.DebugOverlay

/**
 * @author fabmax
 */

fun demo(startScene: String? = null, ctx: KoolContext = createDefaultContext()) {
    val assetsBaseDir = Demo.getProperty("assetsBaseDir", "")
    if (assetsBaseDir.isNotEmpty()) {
        ctx.assetMgr.assetsBaseDir = assetsBaseDir
    }

    // launch demo
    Demo(ctx, startScene)
}

class Demo(ctx: KoolContext, startScene: String? = null) {

    private val dbgOverlay = DebugOverlay(ctx, DebugOverlay.Position.LOWER_RIGHT)
    private val newScenes = mutableListOf<Scene>()
    private val currentScenes = mutableListOf<Scene>()

    private val defaultScene = DemoEntry("glTF Models") { GltfDemo() }

    private val demos = mutableMapOf(
            "proceduralDemo" to DemoEntry("Procedural Geometry") { ProceduralDemo() },
            "gltfDemo" to DemoEntry("glTF Models") { GltfDemo() },
            "deferredDemo" to DemoEntry("Deferred Shading") { DeferredDemo() },
            "aoDemo" to DemoEntry("Ambient Occlusion") { AoDemo() },
            "ssrDemo" to DemoEntry("Reflections") { MultiLightDemo() },
            "pbrDemo" to DemoEntry("PBR Materials") { PbrDemo() },
            "treeDemo" to DemoEntry("Procedural Tree") { TreeDemo() },
            "instanceDemo" to DemoEntry("Instanced Drawing") { InstanceDemo() },
            "simplificationDemo" to DemoEntry("Simplification") { SimplificationDemo() },

            "helloWorldDemo" to DemoEntry("Hello World", true) { HelloWorldDemo() },
            "helloGltfDemo" to DemoEntry("Hello glTF", true) { HelloGltfDemo() },

            "buildingDemo" to DemoEntry("Procedural Building", true) { BuildingDemo() },
            "atmoTest" to DemoEntry("atmoTest", true) { AtmosphereTest() }
    )

    init {
        ctx.scenes += dbgOverlay.ui
        ctx.scenes += demoOverlay(ctx)
        ctx.onRender += this::onRender

        val initScene = (demos[startScene] ?: defaultScene).sceneLoader(ctx)
        newScenes += initScene.setupScenes(ctx)

        ctx.run()
    }

    private fun onRender(ctx: KoolContext) {
        if (newScenes.isNotEmpty()) {
            currentScenes.forEach { s ->
                ctx.scenes -= s
                s.dispose(ctx)
            }
            currentScenes.clear()

            // new scenes have to be inserted in front, so that demo menu is rendered after it
            newScenes.forEachIndexed { i, s ->
                ctx.scenes.add(i, s)
                currentScenes.add(s)
            }
            newScenes.clear()
        }
    }

    private fun demoOverlay(ctx: KoolContext): Scene = uiScene(ctx.screenDpi, "demo-overlay") {
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi(::BlurredComponentUi)
        }
        content.ui.setCustom(BlankComponentUi())

        +drawerMenu("menu", "Demos") {
            +container("demos") {
                ui.setCustom(BlankComponentUi())
                layoutSpec.setOrigin(zero(), dps(45f, true), zero())
                layoutSpec.setSize(full(), pcs(100f, true) - dps(110f, true), full())

                //+ScrollHandler(this)

                var y = -30f
                demos.values.filter { !it.isHidden }.forEach { demo ->
                    +button(demo.label) {
                        layoutSpec.setOrigin(zero(), dps(y, true), zero())
                        layoutSpec.setSize(pcs(100f, true), dps(30f, true), full())
                        textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                        y -= 35f

                        onClick += { _,_,_ ->
                            val demoScene = demo.sceneLoader(ctx)
                            newScenes += demoScene.setupScenes(ctx)
                            isOpen = false
                        }
                    }
                }
            }

            +toggleButton("showDbg") {
                layoutSpec.setOrigin(zero(), dps(10f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), full())
                text = "Debug Info"
                isEnabled = dbgOverlay.ui.isVisible

                onStateChange += { dbgOverlay.ui.isVisible = isEnabled }
            }
        }
    }

    private class DemoEntry(val label: String, val isHidden: Boolean = false, val sceneLoader: (KoolContext) -> DemoScene)

    companion object {
        val demoProps = mutableMapOf<String, Any>()

        val envMapBasePath: String
            get() = getProperty("pbrDemo.envMaps", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/hdri")

        val pbrBasePath: String
            get() = getProperty("pbrDemo.materials", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/materials")

        val modelBasePath: String
            get() = getProperty("pbrDemo.models", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/models")

        fun setProperty(key: String, value: Any) {
            demoProps[key] = value
        }

        inline fun <reified T> getProperty(key: String, default: T): T {
            return demoProps[key] as? T ?: default
        }
    }
}

class Cycler<T>(elements: List<T>) : List<T> by elements {

    constructor(vararg elements: T) : this(listOf(*elements))

    var index = 0

    val current: T
        get() = get(index)

    fun next(): T {
        index = (index + 1) % size
        return current
    }

    fun prev(): T {
        index = (index + size - 1 + size) % size
        return current
    }
}

abstract class DemoScene(val name: String) {
    val scenes = mutableListOf<Scene>()

    lateinit var mainScene: Scene
    var menuScene: Scene? = null

    open fun setupScenes(ctx: KoolContext): List<Scene> {
        mainScene = setupMainScene(ctx)
        scenes += mainScene
        menuScene = setupMenu(ctx)
        menuScene?.let { scenes += it }
        lateInit(ctx)
        return scenes
    }

    abstract fun setupMainScene(ctx: KoolContext): Scene

    open fun setupMenu(ctx: KoolContext): Scene? {
        return null
    }

    open fun lateInit(ctx: KoolContext) { }
}
