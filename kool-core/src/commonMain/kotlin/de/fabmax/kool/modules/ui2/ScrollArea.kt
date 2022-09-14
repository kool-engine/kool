package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

fun UiScope.ScrollArea(
    state: ScrollState,
    width: Dimension = Grow(),
    height: Dimension = Grow(),
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = true,
    background: UiRenderer<UiNode>? = null,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    block: ScrollPaneScope.() -> Unit
) {
    Cell {
        modifier
            .width(width)
            .height(height)
            .background(background)
            .onWheelX { state.xScrollClamped(it.pointer.deltaScrollX.toFloat() * -10f) }
            .onWheelY { state.yScrollClamped(it.pointer.deltaScrollY.toFloat() * -20f) }
        containerModifier?.invoke(modifier)

        ScrollPane(state) {
            block()
        }
        if (withVerticalScrollbar) {
            VerticalScrollbar(state) {
                scrollbarColor?.let { modifier.barColor(it) }
                vScrollbarModifier?.invoke(modifier)
            }
        }
        if (withHorizontalScrollbar) {
            HorizontalScrollbar(state) {
                scrollbarColor?.let { modifier.barColor(it) }
                hScrollbarModifier?.invoke(modifier)
            }
        }
    }
}
