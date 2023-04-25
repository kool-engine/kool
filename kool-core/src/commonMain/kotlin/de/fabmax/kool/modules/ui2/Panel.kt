package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.Scene

fun Scene.Panel(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Panel",
    block: UiScope.() -> Any
): UiSurface {
    val surface = UiSurface(colors, sizes, name)
    surface.content = {
        Box {
            modifier
                .backgroundColor(colors.background)
                .layout(ColumnLayout)
            block()
        }
    }
    addNode(surface)
    return surface
}