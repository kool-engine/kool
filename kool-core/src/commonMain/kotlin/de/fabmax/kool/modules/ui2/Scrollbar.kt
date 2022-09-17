package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import kotlin.math.max

interface ScrollbarScope : UiScope {
    override val modifier: ScrollbarModifier

    val isHorizontal: Boolean get() = modifier.orientation == ScrollbarOrientation.Horizontal
    val isVertical: Boolean get() = modifier.orientation == ScrollbarOrientation.Vertical
}

open class ScrollbarModifier : UiModifier() {
    var orientation: ScrollbarOrientation by property(ScrollbarOrientation.Vertical)
    var minBarLength: Dp by property(Dp(24f))
    var hideIfFullyExtended: Boolean by property(true)

    // default color value are overridden by theme colors
    var barColor: Color by property(Color.GRAY)
    var hoverColor: Color? by property(null)
    var trackColor: Color? by property(null)
    var trackHoverColor: Color? by property(null)
}

fun <T: ScrollbarModifier> T.orientation(orientation: ScrollbarOrientation): T {
    this.orientation = orientation
    return this
}

fun <T: ScrollbarModifier> T.minBarLength(length: Dp): T { minBarLength = length; return this }
fun <T: ScrollbarModifier> T.hideIfFullyExtended(flag: Boolean): T { hideIfFullyExtended = flag; return this }

fun <T: ScrollbarModifier> T.colors(
    color: Color = barColor,
    hoverColor: Color? = this.hoverColor,
    trackColor: Color? = this.trackColor,
    trackHoverColor: Color? = this.trackHoverColor
): T {
    barColor = color
    this.hoverColor = hoverColor
    this.trackColor = trackColor
    this.trackHoverColor = trackHoverColor
    return this
}

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
    scrollBar.modifier.onClick(scrollBar)
    scrollBar.modifier.hoverListener(scrollBar)
    scrollBar.modifier.dragListener(scrollBar)
    scrollBar.block()
}

inline fun UiScope.VerticalScrollbar(
    state: ScrollState,
    block: ScrollbarScope.() -> Unit
) = Scrollbar(state) {
    modifier
        .orientation(ScrollbarOrientation.Vertical)
        .width(8.dp)
        .height(Grow())
        .alignX(AlignmentX.End)

    // try to be smart: add some margin if parent scope (which hopefully is a cell) already contains a horizontal scrollbar
    val horizontalBar = uiNode.parent?.children?.find { it is ScrollbarScope && it.isHorizontal }
    if (horizontalBar != null) {
        val horizontalBarHeight = (horizontalBar.modifier.height as? Dp) ?: 8.dp
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
        .height(8.dp)
        .width(Grow())
        .alignY(AlignmentY.Bottom)

    // try to be smart: add some margin if parent scope (which hopefully is a cell) already contains a vertical scrollbar
    val verticalBar = uiNode.parent?.children?.find { it is ScrollbarScope && it.isVertical }
    if (verticalBar != null) {
        val verticalBarWidth = (verticalBar.modifier.width as? Dp) ?: 8.dp
        if (verticalBar.modifier.alignX == AlignmentX.End) {
            modifier.margin(end = verticalBarWidth)
        } else if (verticalBar.modifier.alignX == AlignmentX.Start) {
            modifier.margin(start = verticalBarWidth)
        }
    }

    block()
}

open class ScrollbarNode(parent: UiNode?, surface: UiSurface)
    : UiNode(parent, surface), ScrollbarScope, Clickable, Draggable, Hoverable {

    override val modifier = ScrollbarModifier()
    lateinit var state: ScrollState

    private val isTrackVisible: Boolean get() = modifier.trackColor != null
    private var isHovered = mutableStateOf(false)

    private val dragHelper = DragHelper()
    private var barMinX = 0f
    private var barMaxX = 0f
    private var barMinY = 0f
    private var barMaxY = 0f

    override fun resetDefaults() {
        super.resetDefaults()
        modifier.colors(
            color = colors.secondary.withAlpha(0.5f),
            hoverColor = colors.secondary
        )
    }

    override fun render(ctx: KoolContext) {
        // draw background
        super.render(ctx)

        val len = if (isVertical) state.relativeBarLenY else state.relativeBarLenX
        val pos = if (isVertical) state.relativeBarPosY else state.relativeBarPosX
        if (modifier.hideIfFullyExtended && len > 0.999f) {
            return
        }

        // compute scrollbar dimensions
        val refHeight = uiNode.heightPx - paddingTopPx - paddingBottomPx
        val refWidth = uiNode.widthPx - paddingStartPx - paddingEndPx
        val clampLen = max(len, modifier.minBarLength.px / if (isVertical) refHeight else refWidth)

        val radius: Float
        val origin = MutableVec2f()
        val size = MutableVec2f()
        if (isVertical) {
            radius = refWidth * 0.5f
            origin.set(paddingStartPx, pos * (1f - clampLen) * refHeight + paddingTopPx)
            size.set(refWidth, clampLen * refHeight)
        } else {
            radius = refHeight * 0.5f
            origin.set(pos * (1f - clampLen) * refWidth + paddingStartPx, paddingTopPx)
            size.set(clampLen * refWidth, refHeight)
        }

        barMinX = origin.x
        barMinY = origin.y
        barMaxX = origin.x + size.x
        barMaxY = origin.y + size.y

        // draw scrollbar
        var barColor = modifier.barColor
        var trackColor = modifier.trackColor
        if (isHovered.use()) {
            modifier.hoverColor?.let { barColor = it }
            modifier.trackHoverColor?.let { trackColor = it }
        }

        val uiPrimitives = surface.getUiPrimitives(UiSurface.LAYER_FLOATING)
        if (trackColor != null) {
            uiPrimitives.localRoundRect(0f, 0f, widthPx, heightPx, radius, trackColor!!)
        }
        uiPrimitives.localRoundRect(origin.x, origin.y, size.x, size.y, radius, barColor)
    }

    private fun isOnBar(ev: PointerEvent): Boolean {
        return ev.position.x in barMinX..barMaxX && ev.position.y in barMinY..barMaxY
    }

    private fun isPointerHovering(ev: PointerEvent): Boolean {
        return (isTrackVisible && isInBoundsLocal(ev.position)) || isOnBar(ev)
    }

    override fun onClick(ev: PointerEvent) {
        if (isTrackVisible && !isOnBar(ev)) {
            when {
                isVertical && ev.position.y < barMinY -> { state.scrollDpY(-state.viewSizeDp.y) }
                isVertical && ev.position.y > barMaxY -> { state.scrollDpY(state.viewSizeDp.y) }
                isHorizontal && ev.position.x < barMinX -> { state.scrollDpX(-state.viewSizeDp.x) }
                isHorizontal && ev.position.x > barMaxX -> { state.scrollDpX(state.viewSizeDp.x) }
            }
        } else {
            ev.reject()
        }
    }

    override fun onEnter(ev: PointerEvent) {
        if (isPointerHovering(ev)) {
            isHovered.set(true)
        } else {
            ev.reject()
        }
    }

    override fun onHover(ev: PointerEvent) {
        if (!isPointerHovering(ev)) {
            isHovered.set(false)
            ev.reject()
        }
    }

    override fun onExit(ev: PointerEvent) {
        isHovered.set(false)
    }

    override fun onDragStart(ev: PointerEvent) {
        if (isOnBar(ev)) {
            dragHelper.captureDragStart()
        } else {
            ev.reject()
        }
    }

    override fun onDrag(ev: PointerEvent) {
        dragHelper.updateScrollPos(ev.pointer)
        isHovered.set(true)
    }

    override fun onDragEnd(ev: PointerEvent) {
        if (!isPointerHovering(ev)) {
            isHovered.set(false)
        }
    }

    private inner class DragHelper {
        var trackLenPx = 0f
        var barLenPx = 0f
        var barStartPx = 0f

        fun captureDragStart() {
            if (isVertical) {
                trackLenPx = uiNode.heightPx - paddingTopPx - paddingBottomPx
                barLenPx = barMaxY - barMinY
                barStartPx = barMinY
            } else {
                trackLenPx = uiNode.widthPx - paddingStartPx - paddingEndPx
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
                state.scrollRelativeY(relativeScroll)
            } else {
                state.scrollRelativeX(relativeScroll)
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollbarNode = { parent, surface -> ScrollbarNode(parent, surface) }
    }
}