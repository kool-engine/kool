package de.fabmax.kool.modules.ui2

fun Panel(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium(),
    name: String = "Panel",
    content: UiScope.() -> Any
): UiSurface {
    val surface = UiSurface(colors, sizes, name)
    surface.content = {
        Box {
            modifier
                .backgroundColor(colors.background)
                .layout(ColumnLayout)
            content()
        }
    }
    return surface
}