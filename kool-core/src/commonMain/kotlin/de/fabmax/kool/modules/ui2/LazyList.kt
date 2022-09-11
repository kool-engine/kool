package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

class LazyListState : ScrollState() {

}

interface LazyListScope : UiScope {
    fun <T> items(items: Collection<T>, block: LazyListScope.(T) -> Unit) {
        if (items is MutableListState) items.use()
        // todo: find a clever way to call this only for visible items instead of all
        items.forEach { item ->
            block(item)
        }
    }

    fun <T> itemsIndexed(items: Collection<T>, block: LazyListScope.(Int, T) -> Unit) {
        if (items is MutableListState) items.use()
        // todo: find a clever way to call this only for visible items instead of all
        items.forEachIndexed { i, item ->
            block(i, item)
        }
    }
}

fun UiScope.LazyList(
    state: LazyListState,
    layout: Layout = ColumnLayout,
    width: Dimension = Grow(),
    height: Dimension = Grow(),
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = false,
    backgroundColor: Color? = null,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    scrollPaneModifier: ((ScrollPaneModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    block: LazyListScope.() -> Unit
) {
    ScrollArea(
        state,
        width, height,
        withVerticalScrollbar, withHorizontalScrollbar,
        backgroundColor, scrollbarColor,
        containerModifier, vScrollbarModifier, hScrollbarModifier
    ) {
        scrollPaneModifier?.let { it(modifier) }
        val lazyList = uiNode.createChild(LazyListNode::class, LazyListNode.factory)
        lazyList.state = state
        lazyList.modifier
            .layout(layout)
            .margin(0.dp)
        lazyList.block()
    }
}

class LazyListNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), LazyListScope {
    override val modifier = UiModifier()

    lateinit var state: LazyListState

    companion object {
        val factory: (UiNode, UiSurface) -> LazyListNode = { parent, surface -> LazyListNode(parent, surface) }
    }
}
