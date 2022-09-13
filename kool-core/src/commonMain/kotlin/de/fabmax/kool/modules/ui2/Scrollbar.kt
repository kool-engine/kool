package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.max

interface ScrollbarScope : UiScope {
    override val modifier: ScrollbarModifier

    val isHorizontal: Boolean get() = modifier.orientation == ScrollbarOrientation.Horizontal
    val isVertical: Boolean get() = modifier.orientation == ScrollbarOrientation.Vertical
}

open class ScrollbarModifier : UiModifier() {
    var orientation: ScrollbarOrientation by property(ScrollbarOrientation.Vertical)
    var barColor: Color by property((MdColor.GREY tone 400).withAlpha(0.5f))
    var minBarSize: Dp by property(Dp(24f))
    var hideIfFullyExtended: Boolean by property(true)
}

fun <T: ScrollbarModifier> T.orientation(orientation: ScrollbarOrientation): T {
    this.orientation = orientation
    return this
}

fun <T: ScrollbarModifier> T.barColor(color: Color): T { barColor = color; return this }
fun <T: ScrollbarModifier> T.hideIfFullyExtended(flag: Boolean): T { hideIfFullyExtended = flag; return this }

enum class ScrollbarOrientation {
    Horizontal,
    Vertical
}

inline fun UiScope.Scrollbar(
    state: ScrollState,
    block: ScrollbarScope.() -> Unit
) {
    val scrollBar = uiNode.createChild(ScrollbarNode::class, ScrollbarNode.factory)
    scrollBar.state = state
    scrollBar.modifier.onDragStart = scrollBar::onDragStart
    scrollBar.modifier.onDrag = scrollBar::onDrag
    scrollBar.block()
}

inline fun UiScope.VerticalScrollbar(
    state: ScrollState,
    block: ScrollbarScope.() -> Unit
) = Scrollbar(state) {
    modifier
        .orientation(ScrollbarOrientation.Vertical)
        .width(10.dp)
        .height(Grow())
        .margin(2.dp)
        .alignX(AlignmentX.End)

    // try to be smart: add some margin if parent scope (which hopefully is a cell) already contains a horizontal scrollbar
    val horizontalBar = uiNode.parent?.children?.find { it is ScrollbarScope && it.isHorizontal }
    if (horizontalBar != null) {
        val horizontalBarHeight = (horizontalBar.modifier.height as? Dp) ?: 12.dp
        if (horizontalBar.modifier.alignY == AlignmentY.Bottom) {
            modifier.margin(bottom = horizontalBarHeight)
        } else if (horizontalBar.modifier.alignY == AlignmentY.Top) {
            modifier.margin(top = horizontalBarHeight)
        }
    }

    block()
}

inline fun UiScope.HorizontalScrollbar(
    state: ScrollState,
    block: ScrollbarScope.() -> Unit
) = Scrollbar(state) {
    modifier
        .orientation(ScrollbarOrientation.Horizontal)
        .height(10.dp)
        .width(Grow())
        .margin(2.dp)
        .alignY(AlignmentY.Bottom)

    // try to be smart: add some margin if parent scope (which hopefully is a cell) already contains a vertical scrollbar
    val verticalBar = uiNode.parent?.children?.find { it is ScrollbarScope && it.isVertical }
    if (verticalBar != null) {
        val verticalBarWidth = (verticalBar.modifier.width as? Dp) ?: 12.dp
        if (verticalBar.modifier.alignX == AlignmentX.End) {
            modifier.margin(end = verticalBarWidth)
        } else if (verticalBar.modifier.alignX == AlignmentX.Start) {
            modifier.margin(start = verticalBarWidth)
        }
    }

    block()
}

open class ScrollbarNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ScrollbarScope {
    override val modifier = ScrollbarModifier()
    lateinit var state: ScrollState

    private val dragHelper = DragHelper()
    private var barMinX = 0f
    private var barMaxX = 0f
    private var barMinY = 0f
    private var barMaxY = 0f

    override fun render(ctx: KoolContext) {
        // draw background
        super.render(ctx)

        val len = if (isVertical) state.relativeBarLenY else state.relativeBarLenX
        val pos = if (isVertical) state.relativeBarPosY else state.relativeBarPosX
        if (modifier.hideIfFullyExtended && len > 0.999f) {
            return
        }

        // compute scrollbar dimensions
        val refHeight = uiNode.height - paddingTop - paddingBottom
        val refWidth = uiNode.width - paddingStart - paddingEnd
        val clampLen = max(len, modifier.minBarSize.px / if (isVertical) refHeight else refWidth)

        val radius: Float
        val origin = MutableVec2f()
        val size = MutableVec2f()
        if (isVertical) {
            radius = refWidth * 0.5f
            origin.set(paddingStart, pos * (1f - clampLen) * refHeight + paddingTop)
            size.set(refWidth, clampLen * refHeight)
        } else {
            radius = refHeight * 0.5f
            origin.set(pos * (1f - clampLen) * refWidth + paddingStart, paddingTop)
            size.set(clampLen * refWidth, refHeight)
        }

        barMinX = origin.x
        barMinY = origin.y
        barMaxX = origin.x + size.x
        barMaxY = origin.y + size.y

        // draw scrollbar
        surface.defaultPrimitives.addRoundRect(minX + origin.x, minY + origin.y, size.x, size.y, radius, modifier.barColor, clipBounds)
    }

    fun onDragStart(ev: PointerEvent) {
        val localPtrPos = toLocal(ev.pointer.x, ev.pointer.y)
        if (localPtrPos.x in barMinX..barMaxX && localPtrPos.y in barMinY..barMaxY) {
            dragHelper.captureDragStart()
        } else {
            ev.reject()
        }
    }

    fun onDrag(ev: PointerEvent) {
        dragHelper.updateScrollPos(ev.pointer)
    }

    private inner class DragHelper {
        var trackLenPx = 0f
        var barLenPx = 0f
        var barStartPx = 0f

        fun captureDragStart() {
            if (isVertical) {
                trackLenPx = uiNode.height - paddingTop - paddingBottom
                barLenPx = barMaxY - barMinY
                barStartPx = barMinY
            } else {
                trackLenPx = uiNode.width - paddingStart - paddingEnd
                barLenPx = barMaxX - barMinX
                barStartPx = barMinX
            }
        }

        fun updateScrollPos(dragPointer: InputManager.Pointer) {
            val dragPos = if (isVertical) {
                dragPointer.dragDeltaY.toFloat()
            } else {
                dragPointer.dragDeltaX.toFloat()
            }

            val barPos = (barStartPx + dragPos).clamp(0f, trackLenPx - barLenPx)
            val spaceAfter = trackLenPx - (barPos + barLenPx)
            val relativeScroll = if (barPos + spaceAfter > 0f) barPos / (barPos + spaceAfter) else 0f
            if (isVertical) {
                state.setYScrollRelative(relativeScroll)
            } else {
                state.setXScrollRelative(relativeScroll)
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollbarNode = { parent, surface -> ScrollbarNode(parent, surface) }
    }
}