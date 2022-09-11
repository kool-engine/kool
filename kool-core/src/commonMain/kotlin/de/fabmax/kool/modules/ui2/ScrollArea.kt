package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

open class ScrollState {
    val scrollPosX = mutableStateOf(0f)
    val scrollPosY = mutableStateOf(0f)
}

fun UiScope.ScrollArea(
    state: ScrollState,
    width: Dimension = Grow(),
    height: Dimension = Grow(),
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = true,
    backgroundColor: Color? = null,
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
            .background(backgroundColor)
            .onWheelX { state.scrollPosX.value -= it.pointer.deltaScrollX.toFloat() * 10f }
            .onWheelY { state.scrollPosY.value -= it.pointer.deltaScrollY.toFloat() * 20f }
        containerModifier?.invoke(modifier)

        ScrollPane {
            modifier
                .margin(0.dp)
                .scrollPos(state.scrollPosX.use().dp, state.scrollPosY.use().dp)
                .onScrollPosChanged { x, y ->
                    state.scrollPosX.set(x)
                    state.scrollPosY.set(y)
                }
            block()
        }
        if (withVerticalScrollbar) {
            VerticalScrollbar {
                scrollbarColor?.let { modifier.barColor(it) }
                vScrollbarModifier?.invoke(modifier)
            }
        }
        if (withHorizontalScrollbar) {
            HorizontalScrollbar {
                scrollbarColor?.let { modifier.barColor(it) }
                hScrollbarModifier?.invoke(modifier)
            }
        }
    }
}
