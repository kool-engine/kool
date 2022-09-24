package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.modules.ui2.*
import kotlin.math.sqrt

class DemoMenu(val demoLoader: DemoLoader) {

    private val isExpandedState = mutableStateOf(false)
    private val menuPositionAnimator = AnimationState(0.1f)

    private val drawerButton = DrawerButton(this)
    private val navDemoButton = NavDemoButton(this)
    private val navSettingsButton = NavSettingsButton(this)

    private val demoList = DemoListContent(this)
    private val settings = SettingsContent(this)

    val showDebugOverlay = mutableStateOf(true)
    val showHiddenDemos = mutableStateOf(false)
    val isFullscreen = mutableStateOf(false)

    val content = mutableStateOf(MenuContent.Demos)

    var isExpanded: Boolean
        get() = isExpandedState.value
        set(value) {
            if (value != isExpandedState.value) {
                isExpandedState.set(value)
                menuPositionAnimator.start()
            }
        }

    val ui = Ui2Scene {
        onUpdate += {
            demoLoader.dbgOverlay.ui.isVisible = showDebugOverlay.value
            if (it.ctx.isFullscreen != isFullscreen.value) {
                it.ctx.isFullscreen = isFullscreen.value
            }
        }

        +UiSurface(sizes = Sizes.large()) {
            modifier
                .width(WrapContent)
                .padding(start = (-menuWidth).dp)
                .height(Grow.Std)
                .background(background = null)

            if (isExpandedState.use() || menuPositionAnimator.isActive) {
                MenuContent()
            }

            drawerButton()
        }.apply { printTiming = true }
    }

    private fun UiScope.MenuContent() = Row {
        val p = menuPositionAnimator.progressAndUse()
        val position = if (isExpandedState.use()) menuWidth * (sqrt(p) - 1f) else -menuWidth * p * p
        modifier
            .margin(start = position.dp)
            .width(menuWidth.dp)
            .height(Grow.Std)
            .backgroundColor(colors.background)

        NavigationBar()
        when (content.use()) {
            MenuContent.Demos -> demoList()
            MenuContent.Settings -> settings()
        }
    }

    private fun UiScope.NavigationBar() = Column {
        modifier
            .padding(top = itemSize.dp)
            .width(itemSize.dp)
            .height(Grow.Std)
            .backgroundColor(colors.backgroundVariant)

        navDemoButton()
        navSettingsButton()
    }

    enum class MenuContent {
        Demos,
        Settings
    }

    companion object {
        const val menuWidth = 300
        const val itemSize = 48

        const val navBarButtonSelectedAlpha = 0.20f
        const val navBarButtonHoveredAlpha = 0.35f
    }
}