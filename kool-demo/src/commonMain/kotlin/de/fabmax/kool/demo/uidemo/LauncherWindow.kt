package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class LauncherWindow(val uiDemo: UiDemo) : UiDemo.DemoWindow {

    private val windowState = WindowState().apply { setWindowSize(Dp(250f), FitContent) }

    override val windowSurface = Window(windowState, name = "Window Launcher") {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        val isMinimizedToTitle = weakRememberState(false)
        modifier
            .isMinimizedToTitle(isMinimizedToTitle.use())
            .isResizable(false, false)

        TitleBar(
            onMinimizeAction = if (!isDocked && !isMinimizedToTitle.use()) { { isMinimizedToTitle.set(true) } } else null,
            onMaximizeAction = if (!isDocked && isMinimizedToTitle.use()) { { isMinimizedToTitle.set(false) } } else null
        )
        if (!isMinimizedToTitle.value) {
            WindowContent()
        }
    }

    override val windowScope = windowSurface.windowScope!!

    private fun UiScope.WindowContent() = Column(Grow.Std) {
        val allowMultiInstances = weakRememberState(false)

        Button("UI Basics") {
            launcherButtonStyle("Example window with a few basic UI components")
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances.use(), BasicUiWindow::class) { BasicUiWindow(uiDemo) }
            }
        }
        Button("Text Style") {
            launcherButtonStyle("Signed-distance-field font rendering showcase")
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances.use(), TextStyleWindow::class) { TextStyleWindow(uiDemo) }
            }
        }
        Button("Attributed Text") {
            launcherButtonStyle("Colorful text area")
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances.use(), TextAreaWindow::class) { TextAreaWindow(uiDemo) }
            }
        }
        Button("Conway's Game of Life") {
            launcherButtonStyle("Game of Life simulation / toggle-button benchmark")
            modifier.onClick {
                launchOrBringToTop(allowMultiInstances.use(), GameOfLifeWindow::class) { GameOfLifeWindow(uiDemo) }
            }
        }
        Button("Theme Editor") {
            launcherButtonStyle("UI color theme editor")
            modifier.onClick {
                launchOrBringToTop(false, ThemeEditorWindow::class) { ThemeEditorWindow(uiDemo) }
            }
        }
        Row(Grow.Std) {
            modifier
                .margin(sizes.largeGap)
            Text("Multiple instances") {
                modifier
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .onClick { allowMultiInstances.toggle() }
            }
            Switch(allowMultiInstances.use()) {
                modifier.onToggle { allowMultiInstances.set(it) }
            }
        }
    }

    private fun <T: UiDemo.DemoWindow> launchOrBringToTop(multiAllowed: Boolean, windowClass: KClass<T>, factory: () -> T) {
        if (!multiAllowed) {
            val existing = uiDemo.demoWindows.find { it::class == windowClass }
            if (existing != null) {
                existing.windowSurface.bringToTop()
                return
            }
        }
        uiDemo.spawnWindow(factory())
    }

    private fun ButtonScope.launcherButtonStyle(tooltip: String) {
        modifier
            .width(Grow.Std)
            .margin(sizes.largeGap)
            .padding(vertical = sizes.gap)

        Tooltip(tooltip)
    }
}