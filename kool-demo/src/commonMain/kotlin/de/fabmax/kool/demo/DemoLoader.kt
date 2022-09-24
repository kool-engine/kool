package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.DebugOverlay

/**
 * @author fabmax
 */

fun demo(startScene: String? = null, ctx: KoolContext = createDefaultContext()) {
    val assetsBaseDir = DemoLoader.getProperty("assetsBaseDir", "")
    if (assetsBaseDir.isNotEmpty()) {
        ctx.assetMgr.assetsBaseDir = assetsBaseDir
    }

    // launch demo
    var demo = startScene
    if (demo != null) {
        demo = demo.lowercase()
        if (demo.endsWith("demo")) {
            demo = demo.substring(0, demo.length - 4)
        }
    }
    DemoLoader(ctx, demo)
}

class DemoLoader(ctx: KoolContext, startScene: String? = null) {

    val dbgOverlay = DebugOverlay(DebugOverlay.Position.LOWER_RIGHT)

    private val loadingScreen = LoadingScreen(ctx)
    private var currentDemo: Pair<String, DemoScene>? = null
    private var switchDemo: Demos.Entry? = null

    init {
        // load physics module early - in js, for some reason wasm file cannot be loaded if this happens later on
        Physics.loadPhysics()

        val menu = DemoMenu(this)

        ctx.scenes += dbgOverlay.ui
        ctx.scenes += menu.ui
        ctx.onRender += this::onRender

        val loadScene = startScene ?: ctx.assetMgr.storage.loadString("selectedScene") ?: Demos.defaultDemo
        val loadDemo = Demos.demos[loadScene] ?: Demos.demos[Demos.defaultDemo]!!
        switchDemo = loadDemo

        ctx.run()
    }

    fun loadDemo(demo: Demos.Entry) {
        if (demo.id != currentDemo?.first) {
            switchDemo = demo
        }
    }

    private fun onRender(ctx: KoolContext) {
        switchDemo?.let { newDemo ->
            ctx.assetMgr.storage.storeString("selectedScene", newDemo.id)
            // release old demo
            currentDemo?.let { demo ->
                demo.second.scenes.forEach {
                    ctx.scenes -= it
                    it.dispose(ctx)
                }
                demo.second.dispose(ctx)
            }
            ctx.scenes.add(0, loadingScreen)

            // set new demo
            currentDemo = newDemo.id to newDemo.newInstance(ctx).also { it.loadingScreen = loadingScreen }
            switchDemo = null
        }

        currentDemo?.second?.let {
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

    companion object {
        val demoProps = mutableMapOf<String, Any>()

        val assetStorageBase: String
            get() = getProperty("assets.base", "https://kool.blob.core.windows.net/kool-demo")

        val hdriPath: String
            get() = getProperty("assets.hdri", "$assetStorageBase/hdri")

        val materialPath: String
            get() = getProperty("assets.materials", "$assetStorageBase/materials")

        val modelPath: String
            get() = getProperty("assets.models", "$assetStorageBase/models")

        val heightMapPath: String
            get() = getProperty("assets.heightmaps", "$assetStorageBase/heightmaps")

        val soundPath: String
            get() = getProperty("sounds", "$assetStorageBase/sounds")

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

    suspend fun showLoadText(text: String, delayFrames: Int = 1) {
        loadingScreen?.let { ls ->
            ls.loadingText2.text = text
            ls.ctx.delayFrames(delayFrames)
        }
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
