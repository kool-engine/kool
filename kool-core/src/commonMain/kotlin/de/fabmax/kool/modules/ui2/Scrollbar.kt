package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ScrollbarScope : UiScope {
    override val modifier: ScrollbarModifier

    val isHorizontal: Boolean get() = modifier.orientation == ScrollbarOrientation.Horizontal
    val isVertical: Boolean get() = modifier.orientation == ScrollbarOrientation.Vertical
}

open class ScrollbarModifier(surface: UiSurface) : UiModifier(surface) {
    var orientation: ScrollbarOrientation by property(ScrollbarOrientation.Vertical)
    var minBarLength: Dp by property(Dp(24f))
    var hideIfFullyExtended: Boolean by property(true)

    var relativeBarPos: Float by property(0f)
    var relativeBarLen: Float by property(0.5f)
    var onChange: ((Float) -> Unit)? by property(null)

    var barColor: Color by property { it.colors.secondaryVariant }
    var hoverColor: Color? by property { it.colors.secondary }
    var trackColor: Color? by property(null)
    var trackHoverColor: Color? by property(null)
}

fun <T: ScrollbarModifier> T.orientation(orientation: ScrollbarOrientation): T {
    this.orientation = orientation
    return this
}

fun <T: ScrollbarModifier> T.minBarLength(length: Dp): T { minBarLength = length; return this }
fun <T: ScrollbarModifier> T.hideIfFullyExtended(flag: Boolean): T { hideIfFullyExtended = flag; return this }
fun <T: ScrollbarModifier> T.relativeBarPos(pos: Float): T { relativeBarPos = pos; return this }
fun <T: ScrollbarModifier> T.relativeBarLen(len: Float): T { relativeBarLen = len; return this }
fun <T: ScrollbarModifier> T.onChange(block: ((Float) -> Unit)?): T { onChange = block; return this }

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
    scopeName: String? = null,
    block: ScrollbarScope.() -> Unit
): ScrollbarScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val scrollBar = uiNode.createChild(scopeName, ScrollbarNode::class, ScrollbarNode.factory)
    scrollBar.modifier.onClick(scrollBar)
        .zLayer(UiSurface.LAYER_FLOATING)
        .hoverListener(scrollBar)
        .dragListener(scrollBar)
    scrollBar.block()
    return scrollBar
}

inline fun UiScope.VerticalScrollbar(
    scopeName: String? = null,
    block: ScrollbarScope.() -> Unit
): ScrollbarScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return Scrollbar(scopeName = scopeName) {
        modifier
            .orientation(ScrollbarOrientation.Vertical)
            .width(8.dp)
            .height(Grow.Std)
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
}

inline fun UiScope.HorizontalScrollbar(
    scopeName: String? = null,
    block: ScrollbarScope.() -> Unit
): ScrollbarScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return Scrollbar(scopeName = scopeName) {
        modifier
            .orientation(ScrollbarOrientation.Horizontal)
            .height(8.dp)
            .width(Grow.Std)
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
}

open class ScrollbarNode(parent: UiNode?, surface: UiSurface)
    : UiNode(parent, surface), ScrollbarScope, Clickable, Draggable, Hoverable {

    override val modifier = ScrollbarModifier(surface)

    private val isTrackVisible: Boolean get() = modifier.trackColor != null
    private var isHovered = mutableStateOf(false)

    private val dragHelper = DragHelper()
    private var barMinX = 0f
    private var barMaxX = 0f
    private var barMinY = 0f
    private var barMaxY = 0f

    override fun render(ctx: KoolContext) {
        // draw background
        super.render(ctx)

        if (uiNode.innerWidthPx <= 0f || uiNode.innerHeightPx <= 0f) {
            return
        }

        val minRelLen = if (isVertical) {
            (modifier.minBarLength.px / innerHeightPx).clamp(0f, 1f)
        } else if (isHorizontal) {
            (modifier.minBarLength.px / innerWidthPx).clamp(0f, 1f)
        } else {
            0f
        }
        val len = modifier.relativeBarLen.clamp(minRelLen, 1f)
        val pos = modifier.relativeBarPos.clamp(0f, 1f)
        if (modifier.hideIfFullyExtended && len > 0.999f) {
            return
        }

        val radius: Float
        val origin = MutableVec2f()
        val size = MutableVec2f()
        if (isVertical) {
            radius = innerWidthPx * 0.5f
            origin.set(paddingStartPx, pos * (1f - len) * innerHeightPx + paddingTopPx)
            size.set(innerWidthPx, len * innerHeightPx)
        } else {
            radius = innerHeightPx * 0.5f
            origin.set(pos * (1f - len) * innerWidthPx + paddingStartPx, paddingTopPx)
            size.set(len * innerWidthPx, innerHeightPx)
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

        val uiPrimitives = getUiPrimitives()
        if (trackColor != null) {
            uiPrimitives.localRoundRect(0f, 0f, widthPx, heightPx, radius, trackColor)
        }
        uiPrimitives.localRoundRect(origin.x, origin.y, size.x, size.y, radius, barColor)
    }

    private fun isOnBar(ev: PointerEvent): Boolean {
        return ev.position.x in barMinX..barMaxX && ev.position.y in barMinY..barMaxY
    }

    private fun isPointerHovering(ev: PointerEvent): Boolean {
        return (isTrackVisible && isInBoundsLocal(ev.position)) || isOnBar(ev)
    }

    private fun onChange(newPos: Float) {
        modifier.onChange?.invoke(newPos.clamp(0f, 1f))
    }

    override fun onClick(ev: PointerEvent) {
        if (isTrackVisible && !isOnBar(ev)) {
            val div = 1f - modifier.relativeBarLen
            val step = if (div > 0f) (modifier.relativeBarLen / div) * 0.95f else 1f
            when {
                isVertical && ev.position.y < barMinY -> { onChange(modifier.relativeBarPos - step) }
                isVertical && ev.position.y > barMaxY -> { onChange(modifier.relativeBarPos + step) }
                isHorizontal && ev.position.x < barMinX -> { onChange(modifier.relativeBarPos - step) }
                isHorizontal && ev.position.x > barMaxX -> { onChange(modifier.relativeBarPos + step) }
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
                trackLenPx = uiNode.innerHeightPx
                barLenPx = barMaxY - barMinY
                barStartPx = barMinY
            } else {
                trackLenPx = uiNode.innerWidthPx
                barLenPx = barMaxX - barMinX
                barStartPx = barMinX
            }
        }

        fun updateScrollPos(dragPointer: Pointer) {
            val dragPos = if (isVertical) {
                dragPointer.dragMovement.y
            } else {
                dragPointer.dragMovement.x
            }

            val barPos = (barStartPx + dragPos).clamp(0f, trackLenPx - barLenPx)
            val spaceAfter = trackLenPx - (barPos + barLenPx)
            val relativeScroll = if (barPos + spaceAfter > 0f) barPos / (barPos + spaceAfter) else 0f
            if (isVertical) {
                onChange(relativeScroll)
            } else {
                onChange(relativeScroll)
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollbarNode = { parent, surface -> ScrollbarNode(parent, surface) }
    }
}