package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun UiScope.Panel(
    layout: Layout = ColumnLayout,
    scopeName: String? = null,
    block: UiScope.() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    Box(scopeName = scopeName) {
        modifier
            .layout(layout)
            .backgroundColor(colors.background)
        block()
    }
}

fun PanelSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    layout: Layout = ColumnLayout,
    block: UiScope.() -> Unit
): UiSurface {
    val panelSurface = UiSurface(colors, sizes, name)
    panelSurface.content = {
        Panel(layout, name, block)
    }
    return panelSurface
}

fun Node.addPanelSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    layout: Layout = ColumnLayout,
    block: UiScope.() -> Unit
): UiSurface {
    val panelSurface = PanelSurface(colors, sizes, name, layout, block)
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