package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class LazyListState : ScrollState() {
    var firstElementSizeDp = 0f
    var averageElementSizeDp = 0f
    var spaceBeforeVisibleItems = 0f
    var spaceAfterVisibleItems = 0f

    var numTotalItems = 0
    var itemsFrom = 0f
    var itemsTo = 0
}

interface LazyListScope : UiScope {
    override val modifier: LazyListModifier

    val isHorizontal: Boolean get() = modifier.orientation == ListOrientation.Horizontal
    val isVertical: Boolean get() = modifier.orientation == ListOrientation.Vertical

    fun <T> items(items: List<T>, block: LazyListScope.(T) -> Unit)
    fun <T> itemsIndexed(items: List<T>, block: LazyListScope.(Int, T) -> Unit)
}

open class LazyListModifier(surface: UiSurface) : UiModifier(surface) {
    var orientation: ListOrientation by property(ListOrientation.Vertical)
    var extraItemsAfter: Int by property(3)
}

fun <T: LazyListModifier> T.orientation(orientation: ListOrientation): T {
    this.orientation = orientation
    return this
}

enum class ListOrientation {
    Horizontal,
    Vertical
}

fun UiScope.LazyList(
    state: LazyListState,
    layout: Layout = ColumnLayout,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = false,
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
        withVerticalScrollbar,
        withHorizontalScrollbar,
        scrollbarColor,
        containerModifier,
        vScrollbarModifier,
        hScrollbarModifier
    ) {
        modifier.width(Grow.Std)
        scrollPaneModifier?.let { it(modifier) }

        val lazyList = uiNode.createChild(LazyListNode::class, LazyListNode.factory)
        lazyList.state = state
        lazyList.modifier
            .layout(layout)
            .width(Grow.Std)
        lazyList.block()
    }
}

class LazyListNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), LazyListScope {
    override val modifier = LazyListModifier(surface)

    lateinit var state: LazyListState

    override fun <T> items(items: List<T>, block: LazyListScope.(T) -> Unit) =
        iterateItems(items, block, null)

    override fun <T> itemsIndexed(items: List<T>, block: LazyListScope.(Int, T) -> Unit) =
        iterateItems(items, null, block)

    private fun <T> iterateItems(
        items: List<T>,
        block: (LazyListScope.(T) -> Unit)?,
        indexedBlock: (LazyListScope.(Int, T) -> Unit)?
    ) {
        // auto-depend on list state in case it is a MutableListState
        (items as? MutableStateList)?.use()
        state.numTotalItems = items.size

        val elemSize = state.firstElementSizeDp
        if (elemSize == 0f) {
            // this apparently is the first layout run, we have absolutely no knowledge about the future content
            // start by adding up to 100 items and hope for the best
            state.itemsFrom = 0f
            state.itemsTo = min(items.lastIndex, 100)
            state.spaceBeforeVisibleItems = 0f
            state.spaceAfterVisibleItems = 0f

        } else {
            // use the element size seen in previous layout runs to estimate the total list dimensions
            val viewSize = if (isVertical) state.viewSizeDp.y else state.viewSizeDp.x
            val numViewItems = (viewSize / state.averageElementSizeDp).toInt() + 1

            // compute the scroll change amount
            val oldListPos = if (isVertical) state.yScrollDp.value else state.xScrollDp.value
            val listPos = if (isVertical) {
                state.computeSmoothScrollPosDpY(Time.deltaT)
            } else {
                state.computeSmoothScrollPosDpX(Time.deltaT)
            }
            val deltaScroll = listPos - oldListPos

            // update scroll position to correspond to visible item range based on current element size
            val updatePos = state.itemsFrom * elemSize
            if (isVertical) {
                if (abs(updatePos - state.yScrollDp.value) > 1f) {
                    val oldError = state.yScrollDpDesired.value - state.yScrollDp.value
                    state.yScrollDp.set(updatePos)
                    state.yScrollDpDesired.set(state.yScrollDp.value + oldError)
                }
            } else {
                if (abs(updatePos - state.xScrollDp.value) > 1f) {
                    val oldError = state.xScrollDpDesired.value - state.xScrollDp.value
                    state.xScrollDp.set(updatePos)
                    state.xScrollDpDesired.set(state.xScrollDp.value + oldError)
                }
            }

            // compute new first visible item based on previous value and scroll delta
            // make sure visible item range stays inside list bounds
            state.itemsFrom = min(
                max(0f, items.size - numViewItems.toFloat() + 0.9999f),
                max(0f, state.itemsFrom + deltaScroll / elemSize)
            )
            state.itemsTo = min(items.lastIndex, (state.itemsFrom).roundToInt() + numViewItems)

            state.spaceBeforeVisibleItems = (state.itemsFrom).toInt() * elemSize
            state.spaceAfterVisibleItems = (items.lastIndex - state.itemsTo) * elemSize

            state.itemsTo = min(items.lastIndex, state.itemsTo + modifier.extraItemsAfter)
        }

        // add a placeholder in front of visible items to achieve correct scroll pane dimensions
        if (state.spaceBeforeVisibleItems > 0f) {
            if (isVertical) {
                Box(1.dp, state.spaceBeforeVisibleItems.dp) { }
            } else {
                Box(state.spaceBeforeVisibleItems.dp, 1.dp) { }
            }
        }

        for (i in state.itemsFrom.toInt()..state.itemsTo) {
            val item = items[i]
            indexedBlock?.invoke(this, i, item)
            block?.invoke(this, item)
        }

        // add a placeholder behind of visible items to achieve correct scroll pane dimensions
        if (state.spaceAfterVisibleItems > 0f) {
            if (isVertical) {
                Box(1.dp, state.spaceAfterVisibleItems.dp) { }
            } else {
                Box(state.spaceAfterVisibleItems.dp, 1.dp) { }
            }
        }

    }

    override fun measureContentSize(ctx: KoolContext) {
        super.measureContentSize(ctx)

        // compute average child size, exclude spacer boxes before and after visible list elements
        val from = if (state.spaceBeforeVisibleItems == 0f) 0 else 1
        val to = if (state.spaceAfterVisibleItems == 0f) children.lastIndex else children.lastIndex - 1

        var size = 0f
        var count = 0
        var prevMargin = 0f
        for (i in from..to) {
            val child = children[i]
            if (i == from) {
                state.firstElementSizeDp = Dp.fromPx(if (isVertical) child.contentHeightPx else child.contentWidthPx).value
            }
            if (isVertical) {
                size += child.contentHeightPx + max(prevMargin, child.marginTopPx)
                prevMargin = child.marginBottomPx
            } else {
                size += child.contentWidthPx + max(prevMargin, child.marginStartPx)
                prevMargin = child.marginEndPx
            }
            count++
        }
        size = Dp.fromPx(size).value
        state.averageElementSizeDp = size / count

        val viewSize = if (isVertical) state.viewSizeDp.y else state.viewSizeDp.x
        if (size < viewSize && state.itemsTo < state.numTotalItems - 1) {
            // we selected too few elements re-run layout with updated average element size
            surface.triggerUpdate()
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> LazyListNode = { parent, surface -> LazyListNode(parent, surface) }
    }
}
