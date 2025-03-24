package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*

class UiDockable(
    override val name: String,
    val dock: Dock? = null,
    val undockSizeBehavior: UndockSizeBehavior = UndockSizeBehavior.UsePreviousUndockedSize,
    floatingX: Dp = Dp.ZERO,
    floatingY: Dp = Dp.ZERO,
    floatingWidth: Dimension = FitContent,
    floatingHeight: Dimension = FitContent,
    floatingAlignmentX: AlignmentX = AlignmentX.Start,
    floatingAlignmentY: AlignmentY = AlignmentY.Top,
    override val isHidden: Boolean = false
): Dockable {

    override val dockedTo = mutableStateOf<DockNodeLeaf?>(null)
    override val isDocked: MutableStateValue<Boolean> = transformedStateOf(dockedTo) { it != null }

    override val preferredWidth: Dp?
        get() = floatingWidth.value as? Dp
    override val preferredHeight: Dp?
        get() = floatingHeight.value as? Dp

    override val floatingX = mutableStateOf(floatingX)
    override val floatingY = mutableStateOf(floatingY)
    override val floatingWidth = mutableStateOf(floatingWidth)
    override val floatingHeight = mutableStateOf(floatingHeight)
    val floatingAlignmentX = mutableStateOf(floatingAlignmentX)
    val floatingAlignmentY = mutableStateOf(floatingAlignmentY)

    var minWidthFloating = Dp(50f)
    var minHeightFloating = Dp(50f)

    private var surface: UiSurface? = null
    private val dragStartItemBounds = MutableVec4f()
    private val currentItemBounds = MutableVec4f()
    private val currentWidthPx: Float get() = currentItemBounds.z - currentItemBounds.x
    private val currentHeightPx: Float get() = currentItemBounds.w - currentItemBounds.y

    private var floatingWidthPx = 0f
    private var floatingHeightPx = 0f
    private var resizeDragEdgeMask = 0

    fun setFloatingBounds(
        x: Dp = floatingX.value,
        y: Dp = floatingY.value,
        width: Dimension = floatingWidth.value,
        height: Dimension = floatingHeight.value,
        alignmentX: AlignmentX = floatingAlignmentX.value,
        alignmentY: AlignmentY = floatingAlignmentY.value
    ) {
        floatingX.set(x)
        floatingY.set(y)
        floatingWidth.set(width)
        floatingHeight.set(height)
        floatingAlignmentX.set(alignmentX)
        floatingAlignmentY.set(alignmentY)
    }

    override fun isInBounds(screenPosPx: Vec2f): Boolean {
        return screenPosPx.x in currentItemBounds.x .. currentItemBounds.z
                && screenPosPx.y in currentItemBounds.y .. currentItemBounds.w
    }

    fun UiScope.registerDragCallbacks(resizeEdgeAware: Boolean = true) {
        modifier
            .onClick { it.isConsumed = true }
            .onDragStart {
                if (resizeEdgeAware && getResizeEdgeMask(it) != 0) {
                    // do not initiate move drag when pointer is on an edge, instead the drag will resize the dockItem
                    it.isConsumed = false
                } else {
                    dockedTo.value?.undock(this@UiDockable)
                    val itemBounds = uiNode.undockedBounds4f
                    moveUndockBoundsUnderPointer(itemBounds, it)
                    dragStartItemBounds.set(itemBounds)
                    floatingX.set(Dp.fromPx(dragStartItemBounds.x))
                    floatingY.set(Dp.fromPx(dragStartItemBounds.y))
                    dock?.dndContext?.startDrag(this@UiDockable, it, null)
                }
            }
            .onDrag {
                floatingX.set(floatingX.value + Dp.fromPx(it.pointer.delta.x))
                floatingY.set(floatingY.value + Dp.fromPx(it.pointer.delta.y))
                floatingAlignmentX.set(AlignmentX.Start)
                floatingAlignmentY.set(AlignmentY.Top)
                dock?.dndContext?.drag(it)
            }
            .onDragEnd {
                dock?.dndContext?.endDrag(it)
            }
    }

    private fun moveUndockBoundsUnderPointer(itemBounds: MutableVec4f, ptrEv: PointerEvent) {
        if (ptrEv.clampedPos.x < itemBounds.x) {
            val dx = ptrEv.clampedPos.x - itemBounds.x
            itemBounds.x += dx
            itemBounds.z += dx
        } else if (ptrEv.clampedPos.x > itemBounds.z) {
            val dx = ptrEv.clampedPos.x - itemBounds.z
            itemBounds.x += dx
            itemBounds.z += dx
        }
        if (ptrEv.clampedPos.y < itemBounds.y) {
            val dy = ptrEv.clampedPos.y - itemBounds.y
            itemBounds.y += dy
            itemBounds.w += dy
        } else if (ptrEv.clampedPos.y > itemBounds.w) {
            val dy = ptrEv.clampedPos.y - itemBounds.w
            itemBounds.y += dy
            itemBounds.w += dy
        }
    }

    fun UiScope.registerResizeCallbacks() {
        // add an empty click listener - updates the UiSurface lastInputTime to move the window on top
        modifier.onClick { it.isConsumed = true }

        if (modifier.layout == CellLayout) {
            registerBoxResizeCallbacks(uiNode)
        } else {
            registerNonBoxResizeCallbacks()
        }
    }

    private fun UiScope.registerBoxResizeCallbacks(resizeNode: UiNode) {
        val resizeMargin = dock?.resizeMargin?.use() ?: Dp(4f)
        ResizeBox(resizeNode, RESIZE_EDGE_TOP, Grow.Std, resizeMargin, alignY = AlignmentY.Top)
        ResizeBox(resizeNode, RESIZE_EDGE_BOTTOM, Grow.Std, resizeMargin, alignY = AlignmentY.Bottom)
        ResizeBox(resizeNode, RESIZE_EDGE_RIGHT, resizeMargin, Grow.Std, alignX = AlignmentX.End)
        ResizeBox(resizeNode, RESIZE_EDGE_LEFT, resizeMargin, Grow.Std, alignX = AlignmentX.Start)
    }

    private fun UiScope.ResizeBox(
        resizeNode: UiNode,
        edgeMask: Int,
        width: Dimension,
        height: Dimension,
        alignX: AlignmentX = AlignmentX.Center,
        alignY: AlignmentY = AlignmentY.Center
    ) = Box {
        modifier
            .align(alignX, alignY)
            .size(width, height)
            .zLayer(UiSurface.LAYER_FLOATING)
            .onEnter { setResizeCursorShape(getBoxResizeEdgeMask(it, edgeMask)) }
            .onHover { setResizeCursorShape(getBoxResizeEdgeMask(it, edgeMask)) }
            .onDragStart { resizeDragStart(resizeNode, getBoxResizeEdgeMask(it, edgeMask), it) }
            .onDrag { resizeDrag(it) }
    }

    private fun UiScope.registerNonBoxResizeCallbacks() {
        modifier
            .onEnter { setResizeCursorShape(getResizeEdgeMask(it)) }
            .onHover { setResizeCursorShape(getResizeEdgeMask(it)) }
            .onDragStart { resizeDragStart(uiNode, getResizeEdgeMask(it), it) }
            .onDrag { resizeDrag(it) }
    }

    private fun resizeDragStart(resizeNode: UiNode, edgeMask: Int, ptrEv: PointerEvent) {
        setResizeCursorShape(edgeMask)
        if (edgeMask == 0) {
            // don't proceed with drag if the cursor is not over an edge
            ptrEv.isConsumed = false
        } else {
            resizeDragEdgeMask = edgeMask
            dragStartItemBounds.set(resizeNode.bounds4f)
        }
    }

    private fun resizeDrag(ptrEv: PointerEvent) {
        setResizeCursorShape(resizeDragEdgeMask)
        if (isDocked.value) {
            if (resizeDragEdgeMask and RESIZE_EDGE_LEFT != 0) {
                dockedTo.value?.moveLeftEdgeTo(ptrEv.screenPosition.x)
            }
            if (resizeDragEdgeMask and RESIZE_EDGE_RIGHT != 0) {
                dockedTo.value?.moveRightEdgeTo(ptrEv.screenPosition.x)
            }
            if (resizeDragEdgeMask and RESIZE_EDGE_TOP != 0) {
                dockedTo.value?.moveTopEdgeTo(ptrEv.screenPosition.y)
            }
            if (resizeDragEdgeMask and RESIZE_EDGE_BOTTOM != 0) {
                dockedTo.value?.moveBottomEdgeTo(ptrEv.screenPosition.y)
            }

        } else {
            if (resizeDragEdgeMask and RESIZE_EDGE_LEFT != 0) {
                val w = maxOf(minWidthFloating, Dp.fromPx(dragStartItemBounds.z - ptrEv.clampedPos.x))
                floatingWidth.set(w)
                floatingX.set(Dp.fromPx(dragStartItemBounds.z) - w)
                floatingAlignmentX.set(AlignmentX.Start)
            }
            if (resizeDragEdgeMask and RESIZE_EDGE_RIGHT != 0) {
                val w = maxOf(minWidthFloating, Dp.fromPx(ptrEv.clampedPos.x - dragStartItemBounds.x))
                floatingWidth.set(w)
                floatingAlignmentX.set(AlignmentX.Start)
            }
            if (resizeDragEdgeMask and RESIZE_EDGE_TOP != 0) {
                val h = maxOf(minHeightFloating, Dp.fromPx(dragStartItemBounds.w - ptrEv.clampedPos.y))
                floatingHeight.set(h)
                floatingY.set(Dp.fromPx(dragStartItemBounds.w) - h)
                floatingAlignmentY.set(AlignmentY.Top)
            }
            if (resizeDragEdgeMask and RESIZE_EDGE_BOTTOM != 0) {
                val h = maxOf(minHeightFloating, Dp.fromPx(ptrEv.clampedPos.y - dragStartItemBounds.y))
                floatingHeight.set(h)
                floatingAlignmentY.set(AlignmentY.Top)
            }
        }
    }

    private val PointerEvent.clampedPos: Vec2f
        get() {
        val pos = MutableVec2f()
        val dock = dock
        if (dock != null) {
            dock.dockingSurface.viewport.toLocal(screenPosition, pos)
            pos.x = pos.x.clamp(0f, dock.dockingSurface.viewport.widthPx)
            pos.y = pos.y.clamp(0f, dock.dockingSurface.viewport.heightPx)
        } else {
            pos.set(screenPosition)
            pos.x = pos.x.clamp(0f, ctx.windowWidth.toFloat())
            pos.y = pos.y.clamp(0f, ctx.windowHeight.toFloat())
        }
        return pos
    }

    fun getResizeEdgeMask(ptrEv: PointerEvent): Int {
        var mask = 0
        val resizeMargin = dock?.resizeMargin?.value ?: Dp(4f)
        if (ptrEv.position.x < resizeMargin.px) mask = mask or RESIZE_EDGE_LEFT
        if (ptrEv.position.x > currentWidthPx - resizeMargin.px) mask = mask or RESIZE_EDGE_RIGHT
        if (ptrEv.position.y < resizeMargin.px) mask = mask or RESIZE_EDGE_TOP
        if (ptrEv.position.y > currentHeightPx - resizeMargin.px) mask = mask or RESIZE_EDGE_BOTTOM
        return filterResizeEdgeMaskByDockNode(mask)
    }

    fun getBoxResizeEdgeMask(ptrEv: PointerEvent, boxEdge: Int): Int {
        val resizeMargin = dock?.resizeMargin?.value ?: Dp(4f)
        val ptrLeft = ptrEv.position.x < resizeMargin.px
        val ptrRight = ptrEv.position.x > currentWidthPx - resizeMargin.px
        val ptrTop = ptrEv.position.y < resizeMargin.px
        val ptrBottom = ptrEv.position.y > currentHeightPx - resizeMargin.px

        val maskLeftRight = if (ptrLeft) RESIZE_EDGE_LEFT else if (ptrRight) RESIZE_EDGE_RIGHT else 0
        val maskTopBot = if (ptrTop) RESIZE_EDGE_TOP else if (ptrBottom) RESIZE_EDGE_BOTTOM else 0

        val mask = when (boxEdge) {
            RESIZE_EDGE_LEFT -> RESIZE_EDGE_LEFT or maskTopBot
            RESIZE_EDGE_RIGHT -> RESIZE_EDGE_RIGHT or maskTopBot
            RESIZE_EDGE_TOP -> RESIZE_EDGE_TOP or maskLeftRight
            RESIZE_EDGE_BOTTOM -> RESIZE_EDGE_BOTTOM or maskLeftRight
            else -> 0
        }
        return filterResizeEdgeMaskByDockNode(mask)
    }

    private fun filterResizeEdgeMaskByDockNode(inputEdgeMask: Int): Int {
        val dockNode = dockedTo.value ?: return inputEdgeMask
        var nodeEdgeMask = 0
        if (dockNode.isLeftEdgeMovable()) nodeEdgeMask = nodeEdgeMask or RESIZE_EDGE_LEFT
        if (dockNode.isRightEdgeMovable()) nodeEdgeMask = nodeEdgeMask or RESIZE_EDGE_RIGHT
        if (dockNode.isTopEdgeMovable()) nodeEdgeMask = nodeEdgeMask or RESIZE_EDGE_TOP
        if (dockNode.isBottomEdgeMovable()) nodeEdgeMask = nodeEdgeMask or RESIZE_EDGE_BOTTOM
        return inputEdgeMask and nodeEdgeMask
    }

    private val UiNode.bounds4f: MutableVec4f get() = MutableVec4f(leftPx, topPx, rightPx, bottomPx)
    private val UiNode.undockedBounds4f: MutableVec4f
        get() = MutableVec4f(leftPx, topPx, leftPx + floatingWidthPx, topPx + floatingHeightPx)

    private fun setResizeCursorShape(edgeMask: Int) {
        when (edgeMask) {
            RESIZE_EDGE_LEFT -> PointerInput.cursorShape = CursorShape.RESIZE_EW
            RESIZE_EDGE_RIGHT -> PointerInput.cursorShape = CursorShape.RESIZE_EW
            RESIZE_EDGE_TOP -> PointerInput.cursorShape = CursorShape.RESIZE_NS
            RESIZE_EDGE_BOTTOM -> PointerInput.cursorShape = CursorShape.RESIZE_NS

            RESIZE_EDGE_LEFT or RESIZE_EDGE_TOP -> PointerInput.cursorShape = CursorShape.RESIZE_NWSE
            RESIZE_EDGE_RIGHT or RESIZE_EDGE_BOTTOM -> PointerInput.cursorShape = CursorShape.RESIZE_NWSE
            RESIZE_EDGE_RIGHT or RESIZE_EDGE_TOP -> PointerInput.cursorShape = CursorShape.RESIZE_NESW
            RESIZE_EDGE_LEFT or RESIZE_EDGE_BOTTOM -> PointerInput.cursorShape = CursorShape.RESIZE_NESW
        }
    }

    fun UiScope.applySizeAndPosition() {
        val w: Dimension
        val h: Dimension

        val dockNode = dockedTo.use()
        if (dockNode == null) {
            val x = floatingX.use()
            val y = floatingY.use()
            w = floatingWidth.use()
            h = floatingHeight.use()

            modifier.align(floatingAlignmentX.use(), floatingAlignmentY.use())
            if (modifier.alignX == AlignmentX.End) {
                modifier.margin(end = x)
            } else {
                modifier.margin(start = x)
            }
            if (modifier.alignY == AlignmentY.Top) {
                modifier.margin(top = y)
            } else {
                modifier.margin(bottom = y)
            }

        } else {
            val x = dockNode.boundsLeftDp.use()
            val y = dockNode.boundsTopDp.use()
            w = dockNode.boundsRightDp.use() - x
            h = dockNode.boundsBottomDp.use() - y

            modifier.margin(start = x, top = y)
            if (undockSizeBehavior == UndockSizeBehavior.KeepSize) {
                floatingX.set(x)
                floatingY.set(y)
                floatingWidth.set(w)
                floatingHeight.set(h)
            }
        }

        modifier
            .size(w, h)
            .onPositioned {
                currentItemBounds.set(it.bounds4f)
                if (!isDocked.value) {
                    floatingWidthPx = currentWidthPx
                    floatingHeightPx = currentHeightPx
                }
            }
        this@UiDockable.surface = surface
    }

    companion object {
        const val RESIZE_EDGE_NONE = 0
        const val RESIZE_EDGE_LEFT = 1
        const val RESIZE_EDGE_RIGHT = 2
        const val RESIZE_EDGE_TOP = 4
        const val RESIZE_EDGE_BOTTOM = 8
    }

    enum class UndockSizeBehavior {
        KeepSize,
        UsePreviousUndockedSize
    }
}