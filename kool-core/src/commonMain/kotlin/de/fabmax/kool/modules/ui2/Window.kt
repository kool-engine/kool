package de.fabmax.kool.modules.ui2

import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color

fun UiScope.Window(
    windowDockable: UiDockable,
    backgroundColor: Color? = colors.background,
    borderColor: Color? = colors.secondaryVariantAlpha(0.3f),
    isResizable: Boolean = true,
    block: UiScope.() -> Unit
) {
    //contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    Box(scopeName = windowDockable.name) {
        backgroundColor?.let { color ->
            if (windowDockable.isDocked.use()) {
                modifier.background(RectBackground(color))
            } else {
                modifier.background(RoundRectBackground(color, sizes.gap))
            }
        }
        borderColor?.let { color ->
            if (windowDockable.isDocked.use()) {
                modifier.border(RectBorder(color, sizes.borderWidth))
            } else {
                modifier.border(RoundRectBorder(color, sizes.gap, sizes.borderWidth))
            }
        }

        block()

        with (windowDockable) {
            applySizeAndPosition()
            if (isResizable) {
                registerResizeCallbacks()
            }
        }
    }
}

fun WindowSurface(
    windowDockable: UiDockable,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    backgroundColor: (UiScope.() -> Color?) = { surface.colors.background },
    borderColor: (UiScope.() -> Color?) = { surface.colors.secondaryVariantAlpha(0.3f) },
    isResizable: Boolean = true,
    hideIfDockedInBackground: Boolean = true,
    block: UiScope.() -> Unit
): UiSurface {
    val windowSurface = UiSurface(colors, sizes, windowDockable.name)
    windowSurface.content = {
        Window(windowDockable, backgroundColor(), borderColor(), isResizable, block)
    }
    if (hideIfDockedInBackground) {
        windowSurface.onUpdate {
            windowDockable.dockedTo.value?.let { dockNode ->
                if (!dockNode.dock.isVisible) {
                    windowSurface.isVisible = false
                } else if (windowSurface.isVisible && !dockNode.isOnTop(windowDockable)) {
                    windowSurface.isVisible = false
                } else if (!windowSurface.isVisible && dockNode.isOnTop(windowDockable)) {
                    windowSurface.isVisible = true
                }
            }
        }
    }
    return windowSurface
}

fun Node.addWindowSurface(
    windowDockable: UiDockable,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    backgroundColor: (UiScope.() -> Color?) = { colors.background },
    borderColor: (UiScope.() -> Color?) = { colors.secondaryVariantAlpha(0.3f) },
    isResizable: Boolean = true,
    hideIfDockedInBackground: Boolean = true,
    block: UiScope.() -> Unit
): UiSurface {
    val windowSurface = WindowSurface(windowDockable, colors, sizes, backgroundColor, borderColor, isResizable, hideIfDockedInBackground, block)
    addNode(windowSurface)
    return windowSurface
}
