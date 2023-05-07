package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.DockableBounds

abstract class DemoWindow(name: String, val uiDemo: UiDemo, isClosable: Boolean = true) {
    val windowBounds = DockableBounds(name, uiDemo.dock)

    val windowSurface = UiSurface(name = name) {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        Window(windowBounds) {
            modifyWindow()

            var isMinimizedToTitle by remember(false)
            val isDocked = windowBounds.isDocked.use()

            Column(Grow.Std, Grow.Std) {
                TitleBar(
                    windowBounds,
                    isMinimizedToTitle = isMinimizedToTitle,
                    onMinimizeAction = if (!isDocked && !isMinimizedToTitle) {
                        {
                            isMinimizedToTitle = true
                            windowBounds.setFloatingBounds(height = FitContent)
                        }
                    } else null,
                    onMaximizeAction = if (!isDocked && isMinimizedToTitle) {
                        { isMinimizedToTitle = false }
                    } else null,
                    onCloseAction = if (isClosable) {
                        {
                            uiDemo.closeWindow(this@DemoWindow, it.ctx)
                        }
                    } else null
                )
                if (!isMinimizedToTitle) {
                    windowContent()
                }
            }
        }
    }

    init {
        windowSurface.onUpdate {
            windowBounds.dockedTo.value?.let { dockNode ->
                if (windowSurface.isVisible && !dockNode.isOnTop(windowBounds)) {
                    windowSurface.isVisible = false
                } else if (!windowSurface.isVisible && dockNode.isOnTop(windowBounds)) {
                    windowSurface.isVisible = true
                }
            }
        }
    }

    protected open fun UiScope.modifyWindow() { }

    protected abstract fun UiScope.windowContent(): Any

    open fun onClose() { }

    protected fun UiScope.applyThemeBackgroundColor() {
        val borderColor = colors.secondaryVariantAlpha(0.3f)
        if (windowBounds.isDocked.use()) {
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