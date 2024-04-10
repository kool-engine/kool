package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.util.DebugOverlay
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logI

/**
 * @author fabmax
 */

fun demo(startScene: String? = null, ctx: KoolContext) {
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
    val menu = DemoMenu(this)

    private val loadingScreen = LoadingScreen(ctx)
    private var currentDemo: Pair<String, DemoScene>? = null
    private var switchDemo: Demos.Entry? = null

    private var initShownMenu = false
    private var shouldAutoHideMenu = 2.5f

    val activeDemo: DemoScene?
        get() = currentDemo?.second

    init {
        Settings.loadSettings()

        ctx.scenes += dbgOverlay.ui
        ctx.scenes += menu.ui
        ctx.onRender += this::onRender

        val loadScene = startScene ?: Settings.selectedDemo.value

        launchOnMainThread {
            val loadDemo = Demos.demos[loadScene] ?: Demos.demos[Demos.defaultDemo]!!
            switchDemo = loadDemo
        }
    }

    fun loadDemo(demo: Demos.Entry) {
        if (demo.id != currentDemo?.first) {
            switchDemo = demo
        }
    }

    private fun onRender(ctx: KoolContext) {
        applySettings(ctx)

        switchDemo?.let { newDemo ->
            switchContentScene(newDemo, ctx)
            switchDemo = null
        }

        currentDemo?.second?.let {
            if (it.demoState != DemoScene.State.RUNNING) {
                it.checkDemoState(this, ctx)
                if (it.demoState == DemoScene.State.RUNNING) {
                    // demo setup complete -> add scenes
                    ctx.scenes -= loadingScreen
                    it.scenes.forEachIndexed { i, s -> ctx.scenes.stageAdd(s, i) }
                }

            } else {
                // demo fully loaded
                if (shouldAutoHideMenu > 0f) {
                    shouldAutoHideMenu -= Time.deltaT
                    if (Settings.showMenuOnStartup.value) {
                        if (!initShownMenu) {
                            menu.isExpanded = true
                            initShownMenu = true
                        }
                        val ptr = PointerInput.primaryPointer
                        if (shouldAutoHideMenu <= 0f && (!ptr.isValid || ptr.x > UiSizes.menuWidth.px)) {
                            menu.isExpanded = false
                        }
                    }
                }
            }
        }
    }

    private fun switchContentScene(newDemo: Demos.Entry, ctx: KoolContext) {
        launchOnMainThread {

            logI { "Loaded demo ${newDemo.title}" }
            Settings.selectedDemo.set(newDemo.id)

            // release old demo
            currentDemo?.second?.let { demo ->
                demo.scenes.forEach {
                    ctx.scenes -= it
                    it.release()
                }
                demo.menuUi?.let {
                    menu.ui -= it
                    it.release()
                }
                demo.onRelease(ctx)
            }
            ctx.scenes.stageAdd(loadingScreen, 0)

            // set new demo
            currentDemo = newDemo.id to newDemo.newInstance(ctx).also {
                it.demoEntry = newDemo
                it.demoLoader = this
                it.loadingScreen = loadingScreen
            }
        }
    }

    private fun applySettings(ctx: KoolContext) {
        if (Settings.isFullscreen.value != ctx.isFullscreen) {
            ctx.isFullscreen = Settings.isFullscreen.value
        }
        dbgOverlay.ui.isVisible = Settings.showDebugOverlay.value
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

        private inline fun <reified T> getProperty(key: String, default: T): T {
            return demoProps[key] as? T ?: default
        }
    }
}
