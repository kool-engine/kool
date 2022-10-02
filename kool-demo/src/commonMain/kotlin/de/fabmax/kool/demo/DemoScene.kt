package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.demo.menu.TitleBgRenderer
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

abstract class DemoScene(val name: String) {
    var demoEntry: Demos.Entry? = null
    var demoState = State.NEW

    val mainScene = Scene(name)
    var menuUi: UiSurface? = null
    val scenes = mutableListOf(mainScene)

    var loadingScreen: LoadingScreen? = null
        set(value) {
            field = value
            value?.loadingText1?.set("Loading $name")
            value?.loadingText2?.set("")
        }

    suspend fun showLoadText(text: String, delayFrames: Int = 1) {
        loadingScreen?.let { ls ->
            ls.loadingText2.set(text)
            ls.ctx.delayFrames(delayFrames)
        }
    }

    fun checkDemoState(loader: DemoLoader, ctx: KoolContext) {
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
            setupScenes(loader.menu, ctx)
            demoState = State.RUNNING
        }
    }

    private fun setupScenes(menu: DemoMenu, ctx: KoolContext) {
        mainScene.setupMainScene(ctx)
        menuUi = createMenu(menu, ctx)
        menuUi?.let { menu.ui.addNode(it, 0) }
        lateInit(ctx)
    }

    open suspend fun AssetManager.loadResources(ctx: KoolContext) { }

    abstract fun Scene.setupMainScene(ctx: KoolContext)

    open fun createMenu(menu: DemoMenu, ctx: KoolContext): UiSurface? {
        return null
    }

    open fun lateInit(ctx: KoolContext) { }

    open fun dispose(ctx: KoolContext) { }

    protected fun menuSurface(title: String? = null, block: UiScope.() -> Unit): UiSurface {
        val accent = demoEntry?.color ?: MdColor.PINK
        val accentVariant = accent.mix(Color.BLACK, 0.3f)
        val titleTxt = title ?: demoEntry?.title ?: "Demo"
        val titleFrom = demoEntry?.category?.fromColor ?: 0f
        val titleTo = demoEntry?.category?.toColor ?: 0.2f

        val scrollState = ScrollState()

        return UiSurface(
            colors = Colors.darkColors(accent, accentVariant, onAccent = Color.WHITE)
        ) {
            surface.sizes = Settings.uiSize.use().sizes
            val cornerRadius = sizes.gap

            modifier
                .width(UiSizes.menuWidth)
                .height(Grow(1f, max = WrapContent))
                .align(AlignmentX.End, AlignmentY.Top)
                .margin(UiSizes.baseSize * 2f)
                .layout(ColumnLayout)
                .background(RoundRectBackground(colors.background, cornerRadius))

            Box {
                modifier
                    .width(Grow.Std)
                    .height(UiSizes.baseSize)
                    .background(RoundRectBackground(colors.accent, cornerRadius))

                Text(titleTxt) {
                    modifier
                        .width(Grow.Std)
                        .height(UiSizes.baseSize)
                        .background(TitleBgRenderer(titleBgMesh, titleFrom, titleTo, (cornerRadius + 1.dp).px))
                        .textColor(colors.onAccent)
                        .font(sizes.largeText)
                        .textAlign(AlignmentX.Center, AlignmentY.Center)
                }
            }

            ScrollArea(
                state = scrollState,
                withHorizontalScrollbar = false,
                containerModifier = { it.background(null) }
            ) {
                modifier.width(Grow.Std).margin(top = sizes.smallGap, bottom = sizes.smallGap * 0.5f)
                Column(width = Grow.Std, block = block)
            }
        }
    }

    enum class State {
        NEW,
        LOADING,
        SETUP,
        RUNNING
    }

    companion object {
        private val titleBgMesh = TitleBgRenderer.BgMesh()
    }
}