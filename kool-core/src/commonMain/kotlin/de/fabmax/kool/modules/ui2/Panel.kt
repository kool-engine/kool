package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun UiScope.Panel(
    backgroundColor: Color? = colors.background,
    layout: Layout = ColumnLayout,
    scopeName: String? = null,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    block: UiScope.() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    Box(scopeName = scopeName) {
        modifier
            .size(width, height)
            .layout(layout)
            .backgroundColor(backgroundColor)
        block()
    }
}

fun PanelSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    backgroundColor: (UiScope.() -> Color?) = { surface.colors.background },
    layout: Layout = ColumnLayout,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    block: UiScope.() -> Unit
): UiSurface {
    val panelSurface = UiSurface(colors, sizes, name)
    panelSurface.content = {
        Panel(backgroundColor(), layout, name, width, height, block)
    }
    return panelSurface
}

fun Node.addPanelSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    backgroundColor: (UiScope.() -> Color?) = { colors.background },
    layout: Layout = ColumnLayout,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    block: UiScope.() -> Unit
): UiSurface {
    val panelSurface = PanelSurface(colors, sizes, name, backgroundColor, layout, width, height, block)
    addNode(panelSurface)
    return panelSurface
}

@Deprecated("Use addPanelSurface() instead", replaceWith = ReplaceWith("addPanelSurface(colors, sizes, name) { block() }"))
fun Node.Panel(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    block: UiScope.() -> Unit
): UiSurface = addPanelSurface(colors, sizes, name, block = block)