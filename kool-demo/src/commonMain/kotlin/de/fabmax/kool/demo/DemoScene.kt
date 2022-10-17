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

    val isMenu = mutableStateOf(true)
    val isMenuMinimized = mutableStateOf(false)

    private val windowState = WindowState().apply {
        setWindowLocation(Dp(100f), Dp(100f), AlignmentX.End, AlignmentY.Top)
        setWindowSize(UiSizes.menuWidth, FitContent)
    }

    var demoLoader: DemoLoader? = null
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
        val titleTxt = title ?: demoEntry?.title ?: "Demo"

        return Window(
            windowState,
            colors = Colors.singleColorDark(accent, Color("101010d0"))
        ) {
            if (!isMenu.use()) {
                // reset window location, so that it will appear at default location when it is shown again
                windowState.setWindowLocation(Dp(100f), Dp(100f), AlignmentX.End, AlignmentY.Top)
                isMenuMinimized.set(false)
                // hide window
                surface.isVisible = false
                return@Window
            }
            surface.isVisible = true
            windowState.setWindowSize(UiSizes.menuWidth, FitContent)

            surface.sizes = Settings.uiSize.use().sizes
            val cornerRadius = sizes.gap
            modifier
                .align(AlignmentX.End, AlignmentY.Top)
                .margin(UiSizes.baseSize * 2f)
                .background(RoundRectBackground(colors.background, cornerRadius))
                .isResizable(false, true)
                .isMinimizedToTitle(isMenuMinimized.use())

            TitleBar(titleTxt, cornerRadius, isMenuMinimized.value)

            if (!isMenuMinimized.value) {
                ScrollArea(
                    withHorizontalScrollbar = false,
                    containerModifier = { it.background(null) }
                ) {
                    modifier.width(Grow.Std).margin(top = sizes.smallGap, bottom = sizes.smallGap * 0.5f)
                    Column(width = Grow.Std, block = block)
                }
            }
        }
    }

    private fun WindowScope.TitleBar(titleTxt: String, cornerRadius: Dp, bottomRounded: Boolean) {
        val titleFrom = demoEntry?.category?.fromColor ?: 0f
        val titleTo = demoEntry?.category?.toColor ?: 0.2f

        val isMinimized = modifier.isMinimizedToTitle
        val isMinimizeHovered = weakRememberState(false)

        Box {
            modifier
                .width(Grow.Std)
                .height(UiSizes.baseSize)
                .background(RoundRectBackground(colors.primary, cornerRadius))
                .dragListener(WindowMoveDragHandler(this@TitleBar))

            Text(titleTxt) {
                val bgRadius = cornerRadius.px + 1f
                val bottomRadius = if (bottomRounded) bgRadius else 0f
                modifier
                    .width(Grow.Std)
                    .height(UiSizes.baseSize)
                    .background(TitleBgRenderer(titleBgMesh, titleFrom, titleTo, bgRadius, bottomRadius))
                    .textColor(colors.onPrimary)
                    .font(sizes.largeText)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
            }

            val minButtonBgColor = if (isMinimizeHovered.use()) MdColor.RED tone 600 else Color.WHITE.withAlpha(0.8f)
            Box {
                modifier
                    .size(sizes.gap * 1.75f, sizes.gap * 1.75f)
                    .align(AlignmentX.End, AlignmentY.Center)
                    .margin(end = sizes.gap * 1.2f)
                    .background(CircularBackground(minButtonBgColor))
                    .zLayer(UiSurface.LAYER_FLOATING)

                Arrow(if (isMinimized) -90f else 90f) {
                    modifier
                        .size(Grow.Std, Grow.Std)
                        .colors(colors.primaryVariant, Color.WHITE)
                        .onClick { isMenuMinimized.toggle() }
                        .onEnter { isMinimizeHovered.set(true) }
                        .onExit { isMinimizeHovered.set(false) }
                        .margin(sizes.smallGap * 0.7f)
                }
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