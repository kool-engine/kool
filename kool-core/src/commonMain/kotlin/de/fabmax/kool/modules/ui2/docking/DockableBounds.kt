package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*


class DockableBounds(
    override val name: String,
    val dock: Dock?,
    val undockSizeBehavior: UndockSizeBehavior = UndockSizeBehavior.UsePreviousUndockedSize,
    initialX: Dp = Dp.ZERO,
    initialY: Dp = Dp.ZERO,
    initialWidth: Dimension = FitContent,
    initialHeight: Dimension = FitContent
): Dockable {

    val dockedTo: DockNode? get() = dockedToState.value
    override val isDocked: Boolean get() = dockedToState.value != null

    override var dockOrderIndex = 0

    val floatingX = mutableStateOf(initialX)
    val floatingY = mutableStateOf(initialY)
    val floatingWidth = mutableStateOf(initialWidth)
    val floatingHeight = mutableStateOf(initialHeight)

    var minWidthFloating = Dp(50f)
    var minHeightFloating = Dp(50f)

    private val dragStartItemBounds = mutableStateOf(Vec4f.ZERO)
    private val dockedToState = mutableStateOf<DockNode?>(null)

    private var currentWidthPx = 0f
    private var currentHeightPx = 0f
    private var floatingWidthPx = 0f
    private var floatingHeightPx = 0f
    private var resizeDragEdgeMask = 0

    fun setFloatingBounds(x: Dp, y: Dp, width: Dimension, height: Dimension) {
        floatingX.set(x)
        floatingY.set(y)
        floatingWidth.set(width)
        floatingHeight.set(height)
    }

    fun UiScope.registerDragCallbacks() {
        modifier
            .onDragStart {
                if (it.getResizeEdgeMask() != 0) {
                    // do not initiate move drag when pointer is on an edge, instead the drag will resize the dockItem
                    it.isConsumed = false
                } else {
                    dockedTo?.undock(this@DockableBounds)
                    val itemBounds = uiNode.undockedBounds4f
                    moveUndockBoundsUnderPointer(itemBounds, it)
                    dragStartItemBounds.set(itemBounds)
                    dock?.dndContext?.startDrag(this@DockableBounds, it, null)
                }
            }
            .onDrag {
                floatingX.set(Dp.fromPx(dragStartItemBounds.value.x + it.pointer.dragDeltaX.toFloat()))
                floatingY.set(Dp.fromPx(dragStartItemBounds.value.y + it.pointer.dragDeltaY.toFloat()))
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
        modifier
            .onHover {
                setResizeCursorShape(it.getResizeEdgeMask())
            }
            .onDragStart {
                val edgeMask = it.getResizeEdgeMask()
                setResizeCursorShape(edgeMask)
                if (edgeMask == 0) {
                    // don't proceed with drag if the cursor is not over an edge
                    it.isConsumed = false
                } else {
                    resizeDragEdgeMask = edgeMask
                    dragStartItemBounds.set(uiNode.bounds4f)
                }
            }
            .onDrag {
                setResizeCursorShape(resizeDragEdgeMask)
                if (isDocked) {
                    if (resizeDragEdgeMask and CURSOR_LEFT_EDGE != 0) {
                        dockedTo?.moveLeftEdgeTo(it.screenPosition.x)
                    }
                    if (resizeDragEdgeMask and CURSOR_RIGHT_EDGE != 0) {
                        dockedTo?.moveRightEdgeTo(it.screenPosition.x)
                    }
                    if (resizeDragEdgeMask and CURSOR_TOP_EDGE != 0) {
                        dockedTo?.moveTopEdgeTo(it.screenPosition.y)
                    }
                    if (resizeDragEdgeMask and CURSOR_BOTTOM_EDGE != 0) {
                        dockedTo?.moveBottomEdgeTo(it.screenPosition.y)
                    }

                } else {
                    if (resizeDragEdgeMask and CURSOR_LEFT_EDGE != 0) {
                        val w = maxOf(minWidthFloating, Dp.fromPx(dragStartItemBounds.value.z - it.clampedPos.x))
                        floatingWidth.set(w)
                        floatingX.set(Dp.fromPx(dragStartItemBounds.value.z) - w)
                    }
                    if (resizeDragEdgeMask and CURSOR_RIGHT_EDGE != 0) {
                        val w = maxOf(minWidthFloating, Dp.fromPx(it.clampedPos.x - dragStartItemBounds.value.x))
                        floatingWidth.set(w)
                    }
                    if (resizeDragEdgeMask and CURSOR_TOP_EDGE != 0) {
                        val h = maxOf(minHeightFloating, Dp.fromPx(dragStartItemBounds.value.w - it.clampedPos.y))
                        floatingHeight.set(h)
                        floatingY.set(Dp.fromPx(dragStartItemBounds.value.w) - h)
                    }
                    if (resizeDragEdgeMask and CURSOR_BOTTOM_EDGE != 0) {
                        val h = maxOf(minHeightFloating, Dp.fromPx(it.clampedPos.y - dragStartItemBounds.value.y))
                        floatingHeight.set(h)
                    }
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

    private fun PointerEvent.getResizeEdgeMask(): Int {
        var mask = 0
        if (position.x < RESIZE_MARGIN.px) mask = mask or CURSOR_LEFT_EDGE
        if (position.x > currentWidthPx - RESIZE_MARGIN.px) mask = mask or CURSOR_RIGHT_EDGE
        if (position.y < RESIZE_MARGIN.px) mask = mask or CURSOR_TOP_EDGE
        if (position.y > currentHeightPx - RESIZE_MARGIN.px) mask = mask or CURSOR_BOTTOM_EDGE

        val dockNode = dockedTo
        if (dockNode != null) {
            var resizableEdgeMask = 0
            if (dockNode.isLeftEdgeMovable()) resizableEdgeMask = resizableEdgeMask or CURSOR_LEFT_EDGE
            if (dockNode.isRightEdgeMovable()) resizableEdgeMask = resizableEdgeMask or CURSOR_RIGHT_EDGE
            if (dockNode.isTopEdgeMovable()) resizableEdgeMask = resizableEdgeMask or CURSOR_TOP_EDGE
            if (dockNode.isBottomEdgeMovable()) resizableEdgeMask = resizableEdgeMask or CURSOR_BOTTOM_EDGE
            mask = mask and resizableEdgeMask
        }

        return mask
    }

    private val UiNode.bounds4f: MutableVec4f get() = MutableVec4f(leftPx, topPx, rightPx, bottomPx)
    private val UiNode.undockedBounds4f: MutableVec4f
        get() = MutableVec4f(leftPx, topPx, leftPx + floatingWidthPx, topPx + floatingHeightPx)

    private fun setResizeCursorShape(edgeMask: Int) {
        if ((edgeMask and 3) != 0) {
            PointerInput.cursorShape = CursorShape.H_RESIZE
        } else if ((edgeMask and 12) != 0) {
            PointerInput.cursorShape = CursorShape.V_RESIZE
        }
    }

    fun UiScope.applySizeAndPosition() {
        val x: Dp
        val y: Dp
        val w: Dimension
        val h: Dimension

        val dockNode = dockedToState.use()
        if (dockNode == null) {
            x = floatingX.use()
            y = floatingY.use()
            w = floatingWidth.use()
            h = floatingHeight.use()
        } else {
            x = dockNode.boundsLeftDp.use()
            y = dockNode.boundsTopDp.use()
            w = dockNode.boundsRightDp.use() - x
            h = dockNode.boundsBottomDp.use() - y

            if (undockSizeBehavior == UndockSizeBehavior.KeepSize) {
                floatingX.set(x)
                floatingY.set(y)
                floatingWidth.set(w)
                floatingHeight.set(h)
            }
        }

        modifier
            .margin(start = x, top = y)
            .size(w, h)
            .onPositioned {
                currentWidthPx = it.widthPx
                currentHeightPx = it.heightPx
                if (!isDocked) {
                    floatingWidthPx = currentWidthPx
                    floatingHeightPx = currentHeightPx
                }
            }
    }

    override fun onDocked(dockNode: DockNodeLeaf) {
        dockedToState.set(dockNode)
    }

    override fun onUndocked(dockNode: DockNodeLeaf) {
        dockedToState.set(null)
    }

    companion object {
        private val RESIZE_MARGIN = Dp(6f)

        private const val CURSOR_NO_EDGE = 0
        private const val CURSOR_LEFT_EDGE = 1
        private const val CURSOR_RIGHT_EDGE = 2
        private const val CURSOR_TOP_EDGE = 4
        private const val CURSOR_BOTTOM_EDGE = 8
    }

    enum class UndockSizeBehavior {
        KeepSize,
        UsePreviousUndockedSize
    }
}