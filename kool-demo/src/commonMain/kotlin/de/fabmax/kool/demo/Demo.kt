package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.demo.atmosphere.AtmosphereDemo
import de.fabmax.kool.demo.pbr.PbrDemo
import de.fabmax.kool.demo.physics.collision.CollisionDemo
import de.fabmax.kool.demo.physics.joints.JointsDemo
import de.fabmax.kool.demo.physics.vehicle.VehicleDemo
import de.fabmax.kool.demo.procedural.ProceduralDemo
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.DebugOverlay

/**
 * @author fabmax
 */

fun demo(startScene: String? = null, ctx: KoolContext = createDefaultContext(), extraScenes: List<DemoEntry>? = null) {
    val assetsBaseDir = Demo.getProperty("assetsBaseDir", "")
    if (assetsBaseDir.isNotEmpty()) {
        ctx.assetMgr.assetsBaseDir = assetsBaseDir
    }

    // launch demo
    var demo = startScene
    if (demo != null) {
        demo = demo.toLowerCase()
        if (demo.endsWith("demo")) {
            demo = demo.substring(0, demo.length - 4)
        }
    }
    Demo(ctx, demo, extraScenes)
}

class Demo(ctx: KoolContext, startScene: String? = null, extraScenes: List<DemoEntry>?) {

    private val dbgOverlay = DebugOverlay(ctx, DebugOverlay.Position.LOWER_RIGHT)
    private val newScenes = mutableListOf<Scene>()
    private val currentScenes = mutableListOf<Scene>()

    private val defaultScene = DemoEntry("glTF Models") { GltfDemo() }

    private val demos = mutableMapOf(
        "phys-vehicle" to DemoEntry("Physics - Vehicle") { VehicleDemo() },
        "phys-joints" to DemoEntry("Physics - Joints") { JointsDemo() },
        "physics" to DemoEntry("Physics - Collision") { CollisionDemo() },
        "atmosphere" to DemoEntry("Atmospheric Scattering") { AtmosphereDemo() },
        "procedural" to DemoEntry("Procedural Geometry") { ProceduralDemo() },
        "gltf" to DemoEntry("glTF Models") { GltfDemo() },
        "deferred" to DemoEntry("Deferred Shading") { DeferredDemo() },
        "ao" to DemoEntry("Ambient Occlusion") { AoDemo() },
        "ssr" to DemoEntry("Reflections") { MultiLightDemo() },
        "pbr" to DemoEntry("PBR Materials") { PbrDemo() },
        "tree" to DemoEntry("Procedural Tree") { TreeDemo() },
        "instance" to DemoEntry("Instanced Drawing") { InstanceDemo() },
        "simplification" to DemoEntry("Simplification") { SimplificationDemo() },

        "helloworld" to DemoEntry("Hello World", true) { HelloWorldDemo() },
        "hellogltf" to DemoEntry("Hello glTF", true) { HelloGltfDemo() },
    )

    init {
        extraScenes?.let {
            it.forEach { demo ->
                demos[demo.label.toLowerCase()] = demo
            }
        }

        // load physics module early - in js, for some reason wasm file cannot be loaded if this happens later on
        Physics.loadPhysics()

        dbgOverlay.ui.isVisible = getProperty("dbgOverlay.isVisible", true)

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

    companion object {
        val demoProps = mutableMapOf<String, Any>()

        val awsBaseUrl: String
            get() = getProperty("awsBaseUrl", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com")

        val envMapBasePath: String
            get() = getProperty("pbrDemo.envMaps", "$awsBaseUrl/hdri")

        val pbrBasePath: String
            get() = getProperty("pbrDemo.materials", "$awsBaseUrl/materials")

        val modelBasePath: String
            get() = getProperty("pbrDemo.models", "$awsBaseUrl/models")

        fun setProperty(key: String, value: Any) {
            demoProps[key] = value
        }

        inline fun <reified T> getProperty(key: String, default: T): T {
            return demoProps[key] as? T ?: default
        }
    }
}

class DemoEntry(val label: String, val isHidden: Boolean = false, val sceneLoader: (KoolContext) -> DemoScene)

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
