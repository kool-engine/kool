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
    withVerticalScrollBar: Boolean = true,
    withHorizontalScrollBar: Boolean = true,
    backgroundColor: Color? = null,
    scrollBarColor: Color? = null,
    cellModifier: ((UiModifier) -> Unit)? = null,
    vScrollBarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollBarModifier: ((ScrollbarModifier) -> Unit)? = null,
    block: ScrollPaneScope.() -> Unit
) {
    Cell {
        modifier
            .width(width)
            .height(height)
            .background(backgroundColor)
            .onWheelX { state.scrollPosX.value -= it.pointer.deltaScrollX.toFloat() * 10f }
            .onWheelY { state.scrollPosY.value -= it.pointer.deltaScrollY.toFloat() * 10f }
        cellModifier?.invoke(modifier)

        ScrollPane {
            modifier
                .scrollPos(state.scrollPosX.use().dp, state.scrollPosY.use().dp)
                .onScrollPosChanged { x, y ->
                    state.scrollPosX.set(x)
                    state.scrollPosY.set(y)
                }
            block()
        }
        if (withVerticalScrollBar) {
            VerticalScrollbar {
                scrollBarColor?.let { modifier.barColor(it) }
                vScrollBarModifier?.invoke(modifier)
            }
        }
        if (withHorizontalScrollBar) {
            HorizontalScrollbar {
                scrollBarColor?.let { modifier.barColor(it) }
                hScrollBarModifier?.invoke(modifier)
            }
        }
    }
}
