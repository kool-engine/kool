package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

fun UiScope.ScrollArea(
    state: ScrollState,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = true,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    block: ScrollPaneScope.() -> Unit
) {
    Box {
        modifier
            .width(width)
            .height(height)
            .backgroundColor(colors.backgroundVariant)
            .onWheelX { state.scrollDpX(it.pointer.deltaScrollX.toFloat() * -20f) }
            .onWheelY { state.scrollDpY(it.pointer.deltaScrollY.toFloat() * -50f) }

        containerModifier?.invoke(modifier)

        ScrollPane(state) {
            block()
        }
        if (withVerticalScrollbar) {
            VerticalScrollbar(state) {
                scrollbarColor?.let { modifier.colors(it) }
                vScrollbarModifier?.invoke(modifier)
            }
        }
        if (withHorizontalScrollbar) {
            HorizontalScrollbar(state) {
                scrollbarColor?.let { modifier.colors(it) }
                hScrollbarModifier?.invoke(modifier)
            }
        }
    }
}
