package de.fabmax.kool.modules.ui2

import de.fabmax.kool.modules.ui2.docking.DockableBounds
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun UiScope.Window(
    windowBounds: DockableBounds,
    backgroundColor: Color? = colors.background,
    borderColor: Color? = colors.secondaryVariantAlpha(0.3f),
    block: UiScope.() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    Box(scopeName = windowBounds.name) {
        backgroundColor?.let { color ->
            if (windowBounds.isDocked.use()) {
                modifier.background(RectBackground(color))
            } else {
                modifier.background(RoundRectBackground(color, sizes.gap))
            }
        }
        borderColor?.let { color ->
            if (windowBounds.isDocked.use()) {
                modifier.border(RectBorder(color, sizes.borderWidth))
            } else {
                modifier.border(RoundRectBorder(color, sizes.gap, sizes.borderWidth))
            }
        }

        block()

        with (windowBounds) {
            applySizeAndPosition()
            registerResizeCallbacks()
        }
    }
}

fun WindowSurface(
    windowBounds: DockableBounds,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    backgroundColor: Color? = colors.background,
    borderColor: Color? = colors.secondaryVariantAlpha(0.3f),
    hideIfDockedInBackground: Boolean = true,
    block: UiScope.() -> Unit
): UiSurface {
    val windowSurface = UiSurface(colors, sizes, windowBounds.name)
    windowSurface.content = {
        Window(windowBounds, backgroundColor, borderColor, block)
    }
    if (hideIfDockedInBackground) {
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
    return windowSurface
}

fun Node.addWindowSurface(
    windowBounds: DockableBounds,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    backgroundColor: Color? = colors.background,
    borderColor: Color? = colors.secondaryVariantAlpha(0.3f),
    hideIfDockedInBackground: Boolean = true,
    block: UiScope.() -> Unit
): UiSurface {
    val windowSurface = WindowSurface(windowBounds, colors, sizes, backgroundColor, borderColor, hideIfDockedInBackground, block)
    addNode(windowSurface)
    return windowSurface
}
