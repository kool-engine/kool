package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.Node

fun Panel(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    block: UiScope.() -> Any
): UiSurface {
    val panelSurface = UiSurface(colors, sizes, name)
    panelSurface.content = {
        Box {
            modifier
                .backgroundColor(colors.background)
                .layout(ColumnLayout)
            block()
        }
    }
    return panelSurface
}

fun Node.addPanel(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    block: UiScope.() -> Any
): UiSurface {
    val panelSurface = de.fabmax.kool.modules.ui2.Panel(colors, sizes, name, block)
    addNode(panelSurface)
    return panelSurface
}

@Deprecated("Use addPanel() instead to avoid confusing it with non-adding Panel()", replaceWith = ReplaceWith("addPanel(colors, sizes, name) { block() }"))
fun Node.Panel(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    block: UiScope.() -> Any
): UiSurface = addPanel(colors, sizes, name, block)