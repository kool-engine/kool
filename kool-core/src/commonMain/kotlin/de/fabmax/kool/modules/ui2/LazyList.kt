package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round
import kotlin.math.roundToInt

class LazyListState : ScrollState() {
    var numTotalItems = 0
    var numVisibleItems = 0
    val itemsFrom = mutableStateOf(0)
    val itemsTo: Int get() = itemsFrom.value + numVisibleItems - 1

    val scrollToItem = mutableStateOf(-1)

    var avgItemSizeDp = 0f
    var prevRemainingSpace = 0f
}

interface LazyListScope : UiScope {
    override val modifier: LazyListModifier

    val isHorizontal: Boolean get() = modifier.orientation == ListOrientation.Horizontal
    val isVertical: Boolean get() = modifier.orientation == ListOrientation.Vertical

    fun <T> items(items: List<T>, block: UiScope.(T) -> Unit) {
        (items as? MutableStateList)?.use()
        indices(items.size) { block(items[it]) }
    }

    fun <T> itemsIndexed(items: List<T>, block: UiScope.(Int, T) -> Unit) {
        (items as? MutableStateList)?.use()
        indices(items.size) { block(it, items[it]) }
    }

    fun indices(numItems: Int, block: UiScope.(Int) -> Unit)
}

open class LazyListModifier(surface: UiSurface) : UiModifier(surface) {
    var orientation: ListOrientation by property(ListOrientation.Vertical)
    var isAutoScrollToEnd: Boolean by property(false)
}

fun <T: LazyListModifier> T.isAutoScrollToEnd(flag: Boolean): T { isAutoScrollToEnd = flag; return this }

fun <T: LazyListModifier> T.orientation(orientation: ListOrientation): T {
    this.orientation = orientation
    return this
}

enum class ListOrientation {
    Horizontal,
    Vertical
}

@Suppress("DEPRECATION")
fun UiScope.LazyColumn(
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = false,
    isScrollableVertical: Boolean = true,
    isScrollableHorizontal: Boolean = true,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    scrollPaneModifier: ((ScrollPaneModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    isScrollByDrag: Boolean = false,
    state: LazyListState = rememberListState(),
    scopeName: String? = null,
    block: LazyListScope.() -> Unit
) = LazyList(
    width = width,
    height = height,
    listOrientation = ListOrientation.Vertical,
    withVerticalScrollbar = withVerticalScrollbar,
    withHorizontalScrollbar = withHorizontalScrollbar,
    isScrollableVertical = isScrollableVertical,
    isScrollableHorizontal = isScrollableHorizontal,
    scrollbarColor = scrollbarColor,
    containerModifier = containerModifier,
    scrollPaneModifier = scrollPaneModifier,
    vScrollbarModifier = vScrollbarModifier,
    hScrollbarModifier = hScrollbarModifier,
    isScrollByDrag = isScrollByDrag,
    state = state,
    scopeName = scopeName,
    block = block,
)

@Suppress("DEPRECATION")
fun UiScope.LazyRow(
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    withVerticalScrollbar: Boolean = false,
    withHorizontalScrollbar: Boolean = true,
    isScrollableVertical: Boolean = true,
    isScrollableHorizontal: Boolean = true,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    scrollPaneModifier: ((ScrollPaneModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    isScrollByDrag: Boolean = false,
    state: LazyListState = rememberListState(),
    scopeName: String? = null,
    block: LazyListScope.() -> Unit
) = LazyList(
    width = width,
    height = height,
    listOrientation = ListOrientation.Horizontal,
    withVerticalScrollbar = withVerticalScrollbar,
    withHorizontalScrollbar = withHorizontalScrollbar,
    isScrollableVertical = isScrollableVertical,
    isScrollableHorizontal = isScrollableHorizontal,
    scrollbarColor = scrollbarColor,
    containerModifier = containerModifier,
    scrollPaneModifier = scrollPaneModifier,
    vScrollbarModifier = vScrollbarModifier,
    hScrollbarModifier = hScrollbarModifier,
    isScrollByDrag = isScrollByDrag,
    state = state,
    scopeName = scopeName,
    block = block,
)

@Deprecated("use LazyColumn / LazyRow instead")
fun UiScope.LazyList(
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    listOrientation: ListOrientation = ListOrientation.Vertical,
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = false,
    isScrollableVertical: Boolean = true,
    isScrollableHorizontal: Boolean = true,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    scrollPaneModifier: ((ScrollPaneModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    isScrollByDrag: Boolean = true,
    state: LazyListState = rememberListState(),
    scopeName: String? = null,
    block: LazyListScope.() -> Unit
) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    Box {
        modifier
            .width(width)
            .height(height)
            .backgroundColor(colors.backgroundVariant)
            .onWheelX {
                if (isScrollableHorizontal) {
                    state.scrollDpX(it.pointer.scroll.x * -20f)
                }
            }
            .onWheelY {
                if (isScrollableVertical) {
                    state.scrollDpY(it.pointer.scroll.y * -50f)
                }
            }

        if (isScrollByDrag) {
            modifier.onDrag {
                val delta = it.pointer.delta
                if (isScrollableHorizontal && delta.x != 0f) {
                    state.scrollDpX(Dp.fromPx(-delta.x).value)
                }
                if (isScrollableVertical && delta.y != 0f) {
                    state.scrollDpY(Dp.fromPx(-delta.y).value)
                }
            }
        }

        containerModifier?.invoke(modifier)

        ScrollPane(state) {
            // expand / grow list in cross axis direction
            val isGrowWidth = listOrientation == ListOrientation.Vertical
            val isGrowHeight = listOrientation == ListOrientation.Horizontal

            if (isGrowWidth) modifier.width(Grow.Std)
            if (isGrowHeight) modifier.height(Grow.Std)
            scrollPaneModifier?.let { it(modifier) }

            val lazyList = uiNode.createChild(scopeName, LazyListNode::class, LazyListNode.factory)
            lazyList.state = state
            lazyList.modifier
                .orientation(listOrientation)
                .layout(if (listOrientation == ListOrientation.Vertical) ColumnLayout else RowLayout)
            if (isGrowWidth) lazyList.modifier.width(Grow.Std)
            if (isGrowHeight) lazyList.modifier.height(Grow.Std)
            lazyList.block()
        }

        if (withVerticalScrollbar) {
            VerticalScrollbar {
                lazyListAware(state, ScrollbarOrientation.Vertical, listOrientation, scrollbarColor, vScrollbarModifier)
            }
        }
        if (withHorizontalScrollbar) {
            HorizontalScrollbar {
                lazyListAware(state, ScrollbarOrientation.Horizontal, listOrientation, scrollbarColor, hScrollbarModifier)
            }
        }
    }
}

fun ScrollbarScope.lazyListAware(
    state: LazyListState,
    scrollbarOrientation: ScrollbarOrientation,
    listOrientation: ListOrientation,
    scrollbarColor: Color?,
    scrollbarModifier: ((ScrollbarModifier) -> Unit)?
) {
    if ((listOrientation == ListOrientation.Horizontal) == (scrollbarOrientation == ScrollbarOrientation.Horizontal)) {
        modifier
            .relativeBarPos(state.itemsFrom.value.toFloat() / (state.numTotalItems - state.numVisibleItems))
            .relativeBarLen(state.numVisibleItems.toFloat() / state.numTotalItems)
            .onChange { state.itemsFrom.set(((state.numTotalItems - state.numVisibleItems) * it).toInt()) }
    } else {
        val isV = scrollbarOrientation == ScrollbarOrientation.Vertical
        modifier
            .relativeBarPos(if (isV) state.relativeBarPosY else state.relativeBarPosX)
            .relativeBarLen(if (isV) state.relativeBarLenY else state.relativeBarLenX)
            .onChange { if (isV) state.scrollRelativeY(it) else state.scrollRelativeX(it) }
    }
    scrollbarColor?.let { modifier.colors(it) }
    scrollbarModifier?.invoke(modifier)
}

open class LazyListNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), LazyListScope {
    override val modifier = LazyListModifier(surface)

    lateinit var state: LazyListState
    private val prevItems = mutableListOf<ListItemBox>()
    private val padPrevItems = mutableListOf<ListItemBox>()

    private var itemBlock: (UiScope.(Int) -> Unit)? = null

    private var scrollAmountDp = 0f

    override fun indices(numItems: Int, block: UiScope.(Int) -> Unit)  {
        itemBlock = block
        state.numTotalItems = numItems
        if (modifier.isAutoScrollToEnd && state.itemsTo > 0 && state.itemsTo < state.numTotalItems - 1) {
            // auto scroll to last list item if modifier flag is set and list was layouted before
            state.scrollToItem.set(state.numTotalItems - 1)
        }
    }

    override fun measureContentSize(ctx: KoolContext) {
        computeScrollAmount()

        val insertBeforeItems = mutableListOf<ListItemBox>()
        var availableSpace = if (isVertical) {
            updateFromAndAvailableSpaceVertical(insertBeforeItems, ctx)
        } else {
            updateFromAndAvailableSpaceHorizontal(insertBeforeItems, ctx)
        }

        // add some space after list content to allow scrolling
        scrollSpacerBox()
        stopOverscrollStart()

        padPrevItems.clear()
        padPrevItems += prevItems
        prevItems.clear()
        var itemI = max(0, insertNewItemsBefore(insertBeforeItems))

        // pad oldChildren
        oldChildren.clear()
        for (i in padPrevItems.lastIndex downTo 0) {
            val prev = padPrevItems[i]
            if (prev.itemIndex >= itemI) {
                oldChildren += prev
            } else {
                break
            }
        }

        while (itemI < state.numTotalItems && availableSpace > 0f) {
            val itemBox = createChild(null, ListItemBox::class, ListItemBox.factory)
            itemBox.makeItem(itemI, ctx)
            prevItems += itemBox

            val itemSize = if (isVertical) round(itemBox.contentHeightPx) else round(itemBox.contentWidthPx)
            availableSpace -= itemSize
            itemI++
        }

        state.numVisibleItems = itemI - state.itemsFrom.use()
        state.prevRemainingSpace = availableSpace
        state.avgItemSizeDp = Dp.fromPx(
            prevItems.sumOf {
                if (isVertical) round(it.contentHeightPx.toDouble()) else round(it.contentWidthPx.toDouble())
            }.toFloat()
        ).value
        if (prevItems.isNotEmpty()) {
            state.avgItemSizeDp /= prevItems.size
        }

        // add some space after list content to allow scrolling
        scrollSpacerBox()
        stopOverscrollEnd(itemI, availableSpace)
        super.measureContentSize(ctx)
    }

    private fun computeScrollAmount() {
        val scrollAmount = if (isVertical) state.computeSmoothScrollAmountDpY() else state.computeSmoothScrollAmountDpX()

        val minScroll = -(state.itemsFrom.value + 0.5f) * state.avgItemSizeDp * 0.75f
        val maxScroll = max(minScroll, (state.numTotalItems - (state.itemsTo) - 0.5f) * state.avgItemSizeDp * 0.75f)
        val clamped = scrollAmount.clamp(minScroll, maxScroll)
        if (clamped != scrollAmount) {
            if (isVertical) {
                state.setSmoothScrollAmountDpY(clamped)
            } else {
                state.setSmoothScrollAmountDpX(clamped)
            }
        }
        scrollAmountDp = clamped

        // scroll to requested item if set
        scrollToItem()

        if (scrollAmountDp > 0f && state.prevRemainingSpace > 0f) {
            // don't allow scrolling, when we don't have enough items to fill up the entire space
            if (isVertical) {
                state.yScrollDpDesired.set(state.yScrollDp.value)
            } else {
                state.xScrollDpDesired.set(state.xScrollDp.value)
            }
            scrollAmountDp = 0f
        }
    }

    private fun scrollToItem() {
        val scrollItem = state.scrollToItem.use()
        if (scrollItem < 0) {
            return
        }

        if (scrollItem in state.itemsFrom.value .. state.itemsTo) {
            // scroll item already is in visible range, we should be able to find it in prevItems
            prevItems.find { it.itemIndex == scrollItem }?.let { item ->
                // check if item is entirely visible and set scroll accordingly if not
                val itemStart = toContainer(item.leftPx, item.topPx, MutableVec2f())
                val itemEnd = toContainer(item.rightPx, item.bottomPx, MutableVec2f())
                val viewEnd = if (isVertical) state.viewHeightDp.value.dp.px else state.viewWidthDp.value.dp.px
                if (isVertical) {
                    if (itemStart.y < 0f) {
                        state.yScrollDpDesired.set(state.yScrollDp.value + Dp.fromPx(itemStart.y).value)
                    } else if (itemEnd.y > viewEnd) {
                        state.yScrollDpDesired.set(state.yScrollDp.value + Dp.fromPx(itemEnd.y - viewEnd).value)
                    }
                } else {
                    if (itemStart.x < 0f) {
                        state.xScrollDpDesired.set(state.xScrollDp.value + Dp.fromPx(itemStart.x).value)
                    } else if (itemEnd.x > viewEnd) {
                        state.xScrollDpDesired.set(state.xScrollDp.value + Dp.fromPx(itemEnd.x - viewEnd).value)
                    }
                }

                // clear scroll to item value
                state.scrollToItem.set(-1)
            }
        } else {
            // item is not in visible range
            val delta = if (scrollItem < state.itemsFrom.value) scrollItem - state.itemsFrom.value else scrollItem - state.itemsTo
            if (abs(delta) < state.numVisibleItems && state.avgItemSizeDp > 0f) {
                // item is close to visible range, smooth scroll to it
                state.yScrollDpDesired.set(state.yScrollDp.value + delta * state.avgItemSizeDp * 0.9f)
            } else {
                // item is far from visible range, hard-set itemsFrom index
                state.itemsFrom.set((scrollItem - state.numVisibleItems).coerceAtMost(state.numTotalItems - state.numVisibleItems).coerceAtLeast(0))
            }
        }
    }

    private fun insertNewItemsBefore(newItems: List<ListItemBox>): Int {
        newItems.forEach {
            mutChildren += it
            prevItems += it
        }
        return state.itemsFrom.value + newItems.size
    }

    private fun stopOverscrollStart() {
        if (state.itemsFrom.value == 0) {
            if (isVertical) {
                if (state.yScrollDp.value < overscrollSpaceDp) state.yScrollDp.set(overscrollSpaceDp)
                if (state.yScrollDpDesired.value < overscrollSpaceDp) state.yScrollDpDesired.set(overscrollSpaceDp)
            } else {
                if (state.xScrollDp.value < overscrollSpaceDp) state.xScrollDp.set(overscrollSpaceDp)
                if (state.xScrollDpDesired.value < overscrollSpaceDp) state.xScrollDpDesired.set(overscrollSpaceDp)
            }
        }
    }

    private fun stopOverscrollEnd(lastItemI: Int, availableSpace: Float) {
        if (state.itemsFrom.value > 0 && lastItemI == state.numTotalItems && availableSpace > 0f && scrollAmountDp > 0f) {
            moveScrollViewportDp(Dp.fromPx(-availableSpace).value)
        }
    }

    private fun moveScrollViewportDp(deltaDp: Float) {
        if (isVertical) {
            state.yScrollDp.set(state.yScrollDp.value + deltaDp)
            state.yScrollDpDesired.set(state.yScrollDpDesired.value + deltaDp)
        } else {
            state.xScrollDp.set(state.xScrollDp.value + deltaDp)
            state.xScrollDpDesired.set(state.xScrollDpDesired.value + deltaDp)
        }
    }

    private fun scrollSpacerBox() {
        Box {
            if (isVertical) {
                uiNode.setContentSize(1.dp.px, overscrollSpaceDp.dp.px)
            } else {
                uiNode.setContentSize(overscrollSpaceDp.dp.px, 1.dp.px)
            }
        }
    }

    private fun ListItemBox.makeItem(itemI: Int, ctx: KoolContext) {
        var safeItemI = itemI
        if (itemI !in 0 until state.numTotalItems) {
            logE { "Invalid lazy list item index: $itemI" }
            safeItemI = itemI.coerceAtMost(state.numTotalItems - 1).coerceAtLeast(0)
        }

        itemIndex = safeItemI
        modifier
            .width(if (isVertical) Grow.MinFit else FitContent)
            .height(if (isVertical) FitContent else Grow.MinFit)
        itemBlock?.invoke(this, safeItemI)

        earlyMeasureContentSize(ctx)
    }

    private fun toContainer(x: Float, y: Float, result: MutableVec2f): MutableVec2f {
        // translate position to container frame (lazy-list -> scroll-pane -> container)
        var p = parent
        while (p != null && p !is ScrollPaneNode) {
            p = p.parent
        }
        p?.parent?.toLocal(x, y, result)
        return result
    }

    private fun computeFirstVisible(): Int {
        val local = MutableVec2f(1f, 1f)
        val scrollPx = Dp(scrollAmountDp).px
        for (i in prevItems.indices) {
            val box = prevItems[i]
            // item index check is required to avoid out of bounds item index in case items were removed
            if (box.itemIndex in 0 until state.numTotalItems) {
                toContainer(box.rightPx - scrollPx, box.bottomPx - scrollPx, local)
                if ((isVertical && local.y > 0f) || (isHorizontal && local.x > 0f)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun updateFromAndAvailableSpaceVertical(insertBeforeItems: MutableList<ListItemBox>, ctx: KoolContext): Float {
        var availableSpace = state.viewHeightDp.use().dp.px

        val firstVisible = computeFirstVisible()
        if (firstVisible < 0) {
            // no previous item still visible
            if (state.avgItemSizeDp > 0f) {
                state.itemsFrom.value = (state.itemsFrom.value + (scrollAmountDp / state.avgItemSizeDp).roundToInt())
                    .coerceAtMost(state.numTotalItems - state.numVisibleItems).coerceAtLeast(0)
            }
            val removeScrollPosDp = Dp.fromPx(scrollAmountDp).value + state.yScrollDp.value - overscrollSpaceDp
            moveScrollViewportDp(-removeScrollPosDp)

        } else {
            // first visible item is present in previous items
            var removeScrollPos = 0f
            for (i in 0 until firstVisible) {
                removeScrollPos += prevItems[i].contentHeightPx
                state.itemsFrom.value++
            }
            val box = prevItems[firstVisible]
            val scrollPx = Dp(scrollAmountDp).px
            val start = toContainer(box.leftPx - scrollPx, box.topPx - scrollPx, MutableVec2f())

            if (start.y > 0.5f) {
                collectItemsBefore(box.itemIndex, start.y, ctx, insertBeforeItems)
            }

            if (removeScrollPos > 0f) {
                val removeScrollPosDp = Dp.fromPx(removeScrollPos).value
                moveScrollViewportDp(-removeScrollPosDp)
            }
            availableSpace -= start.y
        }
        return availableSpace
    }

    private fun updateFromAndAvailableSpaceHorizontal(insertBeforeItems: MutableList<ListItemBox>, ctx: KoolContext): Float {
        var availableSpace = state.viewWidthDp.use().dp.px

        val firstVisible = computeFirstVisible()
        if (firstVisible < 0) {
            // no previous item still visible
            if (state.avgItemSizeDp > 0f) {
                state.itemsFrom.value = (state.itemsFrom.value + (scrollAmountDp / state.avgItemSizeDp).roundToInt())
                    .coerceAtMost(state.numTotalItems - state.numVisibleItems).coerceAtLeast(0)
            }
            val removeScrollPosDp = Dp.fromPx(scrollAmountDp).value + state.xScrollDp.value - overscrollSpaceDp
            moveScrollViewportDp(-removeScrollPosDp)

        } else {
            // first visible item is present in previous items
            var removeScrollPos = 0f
            for (i in 0 until firstVisible) {
                removeScrollPos += prevItems[i].contentWidthPx
                state.itemsFrom.value++
            }
            val box = prevItems[firstVisible]
            val scrollPx = Dp(scrollAmountDp).px
            val start = toContainer(box.leftPx - scrollPx, box.topPx - scrollPx, MutableVec2f())

            if (start.x > 0.5f) {
                collectItemsBefore(box.itemIndex, start.x, ctx, insertBeforeItems)
            }

            if (removeScrollPos > 0f) {
                val removeScrollPosDp = Dp.fromPx(removeScrollPos).value
                moveScrollViewportDp(-removeScrollPosDp)
            }
            availableSpace -= start.x
        }
        return availableSpace
    }

    private fun collectItemsBefore(beforeI: Int, availableSpace: Float, ctx: KoolContext, result: MutableList<ListItemBox>) {
        var avSpace = availableSpace
        var itemI = beforeI - 1
        var insertedSpace = 0f
        while (itemI >= 0 && avSpace > 0) {
            val insertItem = ListItemBox(this, surface)
            insertItem.makeItem(itemI, ctx)

            val insertSpace = if (isVertical) insertItem.contentHeightPx else insertItem.contentWidthPx
            avSpace -= insertSpace
            insertedSpace += Dp.fromPx(insertSpace).value
            state.itemsFrom.value--
            result += insertItem

            itemI--
        }
        result.reverse()

        moveScrollViewportDp(insertedSpace)
    }

    private class ListItemBox(parent: UiNode?, surface: UiSurface) : BoxNode(parent, surface) {
        var itemIndex = 0

        private fun measureUiNodeContent(node: UiNode, ctx: KoolContext) {
            for (i in node.children.indices) {
                measureUiNodeContent(node.children[i], ctx)
            }
            node.measureContentSize(ctx)
        }

        fun earlyMeasureContentSize(ctx: KoolContext) {
            measureUiNodeContent(this, ctx)
            super.measureContentSize(ctx)
        }

        override fun measureContentSize(ctx: KoolContext) {
            // do nothing here, content size was already measured during earlyMeasureContentSize()
        }

        companion object {
            val factory: (UiNode, UiSurface) -> ListItemBox = { parent, surface -> ListItemBox(parent, surface) }
        }
    }

    companion object {
        private const val overscrollSpaceDp = 1000f
        val factory: (UiNode, UiSurface) -> LazyListNode = { parent, surface -> LazyListNode(parent, surface) }
    }
}
