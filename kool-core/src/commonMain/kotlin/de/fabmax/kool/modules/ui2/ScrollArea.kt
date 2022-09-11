package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

class ScrollState {
    val scrollPosX = mutableStateOf(0f)
    val scrollPosY = mutableStateOf(0f)
}

fun UiScope.ScrollArea(
    scrollState: ScrollState,
    width: Dimension = Grow(),
    height: Dimension = Grow(),
    withVerticalScrollBar: Boolean = true,
    withHorizontalScrollBar: Boolean = true,
    backgroundColor: Color? = null,
    scrollBarColor: Color? = null,
    cellModifier: ((UiModifier) -> Unit)? = null,
    vScrollBarModifier: ((ScrollBarModifier) -> Unit)? = null,
    hScrollBarModifier: ((ScrollBarModifier) -> Unit)? = null,
    block: ScrollPaneScope.() -> Unit
) {
    Cell {
        modifier
            .width(width)
            .height(height)
            .background(backgroundColor)
            .onWheelX { scrollState.scrollPosX.value -= it.pointer.deltaScrollX.toFloat() * 10f }
            .onWheelY { scrollState.scrollPosY.value -= it.pointer.deltaScrollY.toFloat() * 10f }
        cellModifier?.invoke(modifier)

        ScrollPane {
            modifier
                .scrollPos(scrollState.scrollPosX.use().dp, scrollState.scrollPosY.use().dp)
                .onScrollPosChanged { x, y ->
                    scrollState.scrollPosX.set(x)
                    scrollState.scrollPosY.set(y)
                }
            block()
        }
        if (withVerticalScrollBar) {
            ScrollBarV {
                scrollBarColor?.let { modifier.barColor(it) }
                vScrollBarModifier?.invoke(modifier)
            }
        }
        if (withHorizontalScrollBar) {
            ScrollBarH {
                scrollBarColor?.let { modifier.barColor(it) }
                hScrollBarModifier?.invoke(modifier)
            }
        }
    }
}
