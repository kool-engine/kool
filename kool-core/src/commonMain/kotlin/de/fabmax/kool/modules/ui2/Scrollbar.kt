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
    var minBarSize: Dp by property(Dp(24f))
    var hideIfFullyExtended: Boolean by property(true)

    // default color value are overridden by theme colors
    var barColor: Color by property(Color.GRAY)
    var barColorHovered: Color? by property(null)
    var trackColor: Color? by property(null)
    var trackColorHovered: Color? by property(null)
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
    scrollBar.modifier.onClick = scrollBar::onClick
    scrollBar.modifier.onEnter = scrollBar::onEnter
    scrollBar.modifier.onHover = scrollBar::onHover
    scrollBar.modifier.onExit = scrollBar::onExit
    scrollBar.modifier.onDragStart = scrollBar::onDragStart
    scrollBar.modifier.onDrag = scrollBar::onDrag
    scrollBar.modifier.onDragEnd = scrollBar::onDragEnd
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

open class ScrollbarNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ScrollbarScope {
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
        modifier.barColor = colors.secondary.withAlpha(0.5f)
        modifier.barColorHovered = colors.secondary
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
        val clampLen = max(len, modifier.minBarSize.px / if (isVertical) refHeight else refWidth)

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
            modifier.barColorHovered?.let { barColor = it }
            modifier.trackColorHovered?.let { trackColor = it }
        }

        val uiPrimitives = surface.getUiPrimitives(UiSurface.LAYER_FLOATING)
        if (trackColor != null) {
            uiPrimitives.localRoundRect(0f, 0f, widthPx, heightPx, radius, trackColor!!)
        }
        uiPrimitives.localRoundRect(origin.x, origin.y, size.x, size.y, radius, barColor)
    }

    private fun isPointerHovering(ev: PointerEvent): Boolean {
        return (isTrackVisible && isInBounds(ev.position)) ||
                ev.position.x in barMinX..barMaxX && ev.position.y in barMinY..barMaxY
    }

    fun onClick(ev: PointerEvent) {
        if (isTrackVisible) {
            // todo: move scrollbar towards clicked pos
        } else {
            ev.reject()
        }
    }

    fun onEnter(ev: PointerEvent) {
        if (isPointerHovering(ev)) {
            isHovered.set(true)
        } else {
            ev.reject()
        }
    }

    fun onHover(ev: PointerEvent) {
        if (!isPointerHovering(ev)) {
            isHovered.set(false)
            ev.reject()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onExit(ev: PointerEvent) {
        isHovered.set(false)
    }

    fun onDragStart(ev: PointerEvent) {
        if (ev.position.x in barMinX..barMaxX && ev.position.y in barMinY..barMaxY) {
            dragHelper.captureDragStart()
        } else {
            ev.reject()
        }
    }

    fun onDrag(ev: PointerEvent) {
        dragHelper.updateScrollPos(ev.pointer)
        isHovered.set(true)
    }

    fun onDragEnd(ev: PointerEvent) {
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