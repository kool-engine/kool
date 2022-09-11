package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

interface ScrollBarScope : UiScope {
    override val modifier: ScrollBarModifier
}

open class ScrollBarModifier : UiModifier() {
    var scrollPane: ScrollPaneScope? = null
    var orientation = ScrollBarOrientation.Vertical
    var barColor: Color = (MdColor.GREY tone 400).withAlpha(0.5f)
    var hideIfFull = true

    override fun resetDefaults() {
        super.resetDefaults()
        scrollPane = null
        orientation = ScrollBarOrientation.Vertical
        barColor = (MdColor.GREY tone 400).withAlpha(0.5f)
        hideIfFull = true
    }
}


fun <T: ScrollBarModifier> T.scrollPane(scrollPane: ScrollPaneScope): T {
    this.scrollPane = scrollPane
    return this
}

fun <T: ScrollBarModifier> T.orientation(orientation: ScrollBarOrientation): T {
    this.orientation = orientation
    return this
}

fun <T: ScrollBarModifier> T.barColor(color: Color): T { barColor = color; return this }
fun <T: ScrollBarModifier> T.hideIfFull(flag: Boolean): T { hideIfFull = flag; return this }

enum class ScrollBarOrientation {
    Horizontal,
    Vertical
}

inline fun UiScope.ScrollBar(scrollPane: ScrollPaneScope? = null, block: ScrollBarScope.() -> Unit) {
    val scrollBar = uiNode.createChild(ScrollBarNode::class, ScrollBarNode.factory)
    scrollBar.modifier.pointerCallbacks.onDragStart = scrollBar::onDragStart
    scrollBar.modifier.pointerCallbacks.onDrag = scrollBar::onDrag

    if (scrollPane != null) {
        scrollBar.modifier.scrollPane(scrollPane)
    } else {
        uiNode.children.find { it is ScrollPaneScope }?.let {
            scrollBar.modifier.scrollPane(it as ScrollPaneScope)
        }
    }
    scrollBar.block()
}

inline fun UiScope.ScrollBarV(scrollPane: ScrollPaneScope? = null, block: ScrollBarScope.() -> Unit) = ScrollBar(scrollPane) {
    modifier
        .orientation(ScrollBarOrientation.Vertical)
        .width(12.dp)
        .height(Grow())
        .padding(2.dp)
        .alignX(AlignmentX.End)

    // try to be smart: add some margin if parent scope (which hopefully is a cell) already contains a horizontal scrollbar
    val horizontalBar = uiNode.parent?.children?.find { it is ScrollBarScope && it.modifier.orientation == ScrollBarOrientation.Horizontal }
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

inline fun UiScope.ScrollBarH(scrollPane: ScrollPaneScope? = null, block: ScrollBarScope.() -> Unit) = ScrollBar(scrollPane) {
    modifier
        .orientation(ScrollBarOrientation.Horizontal)
        .height(12.dp)
        .width(Grow())
        .padding(2.dp)
        .alignY(AlignmentY.Bottom)

    // try to be smart: add some margin if parent scope (which hopefully is a cell) already contains a vertical scrollbar
    val verticalBar = uiNode.parent?.children?.find { it is ScrollBarScope && it.modifier.orientation == ScrollBarOrientation.Vertical }
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

open class ScrollBarNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ScrollBarScope {
    override val modifier = ScrollBarModifier()

    private val dragHelper = DragHelper()
    private var barMinX = 0f
    private var barMaxX = 0f
    private var barMinY = 0f
    private var barMaxY = 0f

    open fun computeRelativeBarLen(): Float {
        val spNode = modifier.scrollPane?.uiNode ?: return 1f
        val size: Float
        val view: Float
        if (modifier.orientation == ScrollBarOrientation.Vertical) {
            size = spNode.height
            view = spNode.clippedMaxY - spNode.clippedMinY
        } else {
            size = spNode.width
            view = spNode.clippedMaxX - spNode.clippedMinX
        }
        return (view / size).clamp()
    }

    open fun computeRelativeBarPos(): Float  {
        val spNode = modifier.scrollPane?.uiNode ?: return 0f
        val min: Float
        val max: Float
        val clipMin: Float
        val clipMax: Float
        if (modifier.orientation == ScrollBarOrientation.Vertical) {
            min = spNode.minY
            max = spNode.maxY
            clipMin = spNode.clippedMinY
            clipMax = spNode.clippedMaxY
        } else {
            min = spNode.minX
            max = spNode.maxX
            clipMin = spNode.clippedMinX
            clipMax = spNode.clippedMaxX
        }
        val div = clipMin - min + max - clipMax
        return if (div == 0f) 0f else ((clipMin - min) / div).clamp()
    }

    override fun render(ctx: KoolContext) {
        val len = computeRelativeBarLen()
        if (modifier.hideIfFull && len == 1f) {
            return
        }

        super.render(ctx)
        surface.defaultBuilder.configured(modifier.barColor) {
            rect {
                val pos = computeRelativeBarPos()
                val refHeight = uiNode.height - paddingTop - paddingBottom
                val refWidth = uiNode.width - paddingStart - paddingEnd

                cornerSteps = 4
                if (modifier.orientation == ScrollBarOrientation.Vertical) {
                    cornerRadius = refWidth * 0.5f
                    origin.set(paddingStart, pos * (1f - len) * refHeight + paddingTop, 0f)
                    size.set(refWidth, len * refHeight)
                } else {
                    cornerRadius = refHeight * 0.5f
                    origin.set(pos * (1f - len) * refWidth + paddingStart, paddingTop, 0f)
                    size.set(len * refWidth, refHeight)
                }

                barMinX = origin.x
                barMinY = origin.y
                barMaxX = origin.x + size.x
                barMaxY = origin.y + size.y
            }
        }
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
        modifier.scrollPane?.uiNode?.let {
            dragHelper.computeScrollPos(ev.pointer, it)
        }
    }

    private inner class DragHelper {
        var trackLenPx = 0f
        var barLenPx = 0f
        var barStartPx = 0f

        fun captureDragStart() {
            if (modifier.orientation == ScrollBarOrientation.Vertical) {
                trackLenPx = uiNode.height - paddingTop - paddingBottom
                barLenPx = barMaxY - barMinY
                barStartPx = barMinY
            } else {
                trackLenPx = uiNode.width - paddingStart - paddingEnd
                barLenPx = barMaxX - barMinX
                barStartPx = barMinX
            }
        }

        fun computeScrollPos(dragPointer: InputManager.Pointer, scrollPane: ScrollPaneNode) {
            val dragPos = if (modifier.orientation == ScrollBarOrientation.Vertical) {
                dragPointer.dragDeltaY.toFloat()
            } else {
                dragPointer.dragDeltaX.toFloat()
            }

            val barPos = (barStartPx + dragPos).clamp(0f, trackLenPx - barLenPx)
            val spaceAfter = trackLenPx - (barPos + barLenPx)
            val relativeScroll = if (barPos + spaceAfter > 0f) barPos / (barPos + spaceAfter) else 0f

            if (modifier.orientation == ScrollBarOrientation.Vertical) {
                val absoluteScroll = scrollPane.computeScrollPosY(relativeScroll)
                scrollPane.modifier.onScrollPosChanged?.invoke(scrollPane.modifier.scrollPosX.value, absoluteScroll)
            } else {
                val absoluteScroll = scrollPane.computeScrollPosX(relativeScroll)
                scrollPane.modifier.onScrollPosChanged?.invoke(absoluteScroll, scrollPane.modifier.scrollPosY.value)
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollBarNode = { parent, surface -> ScrollBarNode(parent, surface) }
    }
}