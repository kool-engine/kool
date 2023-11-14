package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable

abstract class DemoWindow(name: String, val uiDemo: UiDemo, isClosable: Boolean = true) {
    val windowDockable = UiDockable(name, uiDemo.dock)

    val windowSurface = WindowSurface(windowDockable) {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        modifyWindow()

        var isMinimizedToTitle by remember(false)
        val isDocked = windowDockable.isDocked.use()

        Column(Grow.Std, Grow.Std) {
            TitleBar(
                windowDockable,
                isMinimizedToTitle = isMinimizedToTitle,
                onMinimizeAction = if (!isDocked && !isMinimizedToTitle) {
                    {
                        isMinimizedToTitle = true
                        windowDockable.setFloatingBounds(height = FitContent)
                    }
                } else null,
                onMaximizeAction = if (!isDocked && isMinimizedToTitle) {
                    { isMinimizedToTitle = false }
                } else null,
                onCloseAction = if (isClosable) {
                    {
                        uiDemo.closeWindow(this@DemoWindow)
                    }
                } else null
            )
            if (!isMinimizedToTitle) {
                windowContent()
            }
        }
    }

    protected open fun UiScope.modifyWindow() { }

    protected abstract fun UiScope.windowContent(): Any

    open fun onClose() { }

    protected fun UiScope.applyThemeBackgroundColor() {
        val borderColor = colors.secondaryVariantAlpha(0.3f)
        if (windowDockable.isDocked.use()) {
            modifier
                .background(RectBackground(colors.background))
                .border(RectBorder(borderColor, sizes.borderWidth))
        } else {
            modifier
                .background(RoundRectBackground(colors.background, sizes.gap))
                .border(RoundRectBorder(borderColor, sizes.gap, sizes.borderWidth))
        }
    }
}