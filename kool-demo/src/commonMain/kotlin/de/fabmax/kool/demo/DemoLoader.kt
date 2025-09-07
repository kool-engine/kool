package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.util.DebugOverlay
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logI

fun demo(startScene: String? = null, ctx: KoolContext) {
    // launch demo
    DemoLoader(ctx, startScene?.lowercase()?.removeSuffix("demo"))
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
        ctx.window.renderResolutionFactor = Settings.renderScale.value / 100f

        ctx.scenes += loadingScreen
        ctx.scenes += dbgOverlay.ui
        ctx.scenes += menu.ui
        ctx.onRender += this::onRender
        ctx.onShutdown += {
            if (!loadingScreen.isReleased) {
                loadingScreen.release()
            }
        }

        val loadScene = startScene ?: Settings.selectedDemo.value
        val loadDemo = Demos.demos[loadScene] ?: Demos.demos[Demos.defaultDemo]!!
        switchDemo = loadDemo
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
                        if (shouldAutoHideMenu <= 0f && (!ptr.isValid || ptr.pos.x > UiSizes.menuWidth.px)) {
                            menu.isExpanded = false
                        }
                    }
                }
            }
        }
    }

    private fun switchContentScene(newDemo: Demos.Entry, ctx: KoolContext) {
        logI { "Loading demo ${newDemo.title}..." }
        Settings.selectedDemo.set(newDemo.id)

        currentDemo?.second?.let { demo ->
            demo.scenes.forEach { ctx.scenes -= it }
            demo.menuUi?.let { menu.ui -= it }
            demo.scenes.forEach { it.release() }
            demo.menuUi?.release()
        }
        ctx.scenes -= loadingScreen
        ctx.scenes.stageAdd(loadingScreen, 0)

        // set new demo
        currentDemo = newDemo.id to newDemo.newInstance(ctx).also {
            it.demoEntry = newDemo
            it.demoLoader = this@DemoLoader
            it.loadingScreen = loadingScreen
        }
    }

    private fun applySettings(ctx: KoolContext) {
        if (ctx.window.capabilities.canSetFullscreen && Settings.isFullscreen.value != ctx.window.flags.isFullscreen) {
            ctx.window.setFullscreen(Settings.isFullscreen.value)
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
