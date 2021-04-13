package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.demo.atmosphere.AtmosphereDemo
import de.fabmax.kool.demo.pbr.PbrDemo
import de.fabmax.kool.demo.physics.collision.CollisionDemo
import de.fabmax.kool.demo.physics.joints.JointsDemo
import de.fabmax.kool.demo.physics.manyvehicles.ManyVehiclesDemo
import de.fabmax.kool.demo.physics.ragdoll.RagdollDemo
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
    private val loadingScreen = LoadingScreen(ctx)
    private var currentDemo: DemoScene? = null
    private var switchDemo: DemoScene? = null
        set(value) {
            field = value
            value?.loadingScreen = loadingScreen
        }

    private val defaultScene = DemoEntry("Physics - Vehicle") { VehicleDemo() }

    private val demos = mutableMapOf(
        "phys-ragdoll" to DemoEntry("Physics - Ragdoll") { RagdollDemo() },
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
        "manyvehicles" to DemoEntry("Many Vehicles", true) { ManyVehiclesDemo() },
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

        switchDemo = (demos[startScene] ?: defaultScene).newInstance(ctx)

        ctx.run()
    }

    private fun onRender(ctx: KoolContext) {
        switchDemo?.let { newDemo ->
            // release old demo
            currentDemo?.let { demo ->
                demo.scenes.forEach {
                    ctx.scenes -= it
                    it.dispose(ctx)
                }
                demo.dispose(ctx)
            }
            ctx.scenes.add(0, loadingScreen)

            // set new demo
            currentDemo = newDemo
            switchDemo = null
        }

        currentDemo?.let {
            if (it.demoState != DemoScene.State.RUNNING) {
                it.checkDemoState(ctx)
                if (it.demoState == DemoScene.State.RUNNING) {
                    // demo setup complete -> add scenes
                    ctx.scenes -= loadingScreen
                    it.scenes.forEachIndexed { i, s -> ctx.scenes.add(i, s) }
                }
            }
        }
    }

    private fun demoOverlay(ctx: KoolContext): Scene = uiScene(ctx.screenDpi, "demo-overlay") {
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi(::SimpleComponentUi)
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
                            switchDemo = demo.newInstance(ctx)
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

        val soundsBasePath: String
            get() = getProperty("pbrDemo.models", "$awsBaseUrl/sounds")

        fun setProperty(key: String, value: Any) {
            demoProps[key] = value
        }

        inline fun <reified T> getProperty(key: String, default: T): T {
            return demoProps[key] as? T ?: default
        }
    }
}

class DemoEntry(val label: String, val isHidden: Boolean = false, val newInstance: (KoolContext) -> DemoScene)

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
    var demoState = State.NEW
    var isLoading = false
    var isLoaded = false

    val mainScene = Scene(name)
    var menuScene: Scene? = null
    val scenes = mutableListOf(mainScene)

    var loadingScreen: LoadingScreen? = null
        set(value) {
            field = value
            value?.loadingText1?.text = "Loading $name"
            value?.loadingText2?.text = ""
        }

    protected fun showLoadText(text: String) {
        loadingScreen?.loadingText2?.text = text
    }

    fun checkDemoState(ctx: KoolContext) {
        if (demoState == State.NEW) {
            // load resources (async from AssetManager CoroutineScope)
            demoState = State.LOADING
            ctx.assetMgr.launch {
                loadResources(ctx)
                demoState = State.SETUP
            }
        }

        if (demoState == State.SETUP) {
            // setup scene after required resources are loaded, blocking in main thread
            setupScenes(ctx)
            demoState = State.RUNNING
        }
    }

    private fun setupScenes(ctx: KoolContext) {
        mainScene.setupMainScene(ctx)
        menuScene = setupMenu(ctx)
        menuScene?.let { scenes += it }
        lateInit(ctx)
    }

    open suspend fun AssetManager.loadResources(ctx: KoolContext) { }

    abstract fun Scene.setupMainScene(ctx: KoolContext)

    open fun setupMenu(ctx: KoolContext): Scene? {
        return null
    }

    open fun lateInit(ctx: KoolContext) { }

    open fun dispose(ctx: KoolContext) { }

    enum class State {
        NEW,
        LOADING,
        SETUP,
        RUNNING
    }
}
