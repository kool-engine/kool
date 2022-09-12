package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.logW
import kotlin.math.max

open class ScrollState {
    val xScrollDp = mutableStateOf(0f)
    val yScrollDp = mutableStateOf(0f)

    val contentSizeDp = MutableVec2f()
    val viewSizeDp = MutableVec2f()

    val relativeBarLenX: Float get() = (viewSizeDp.x / contentSizeDp.x).clamp()
    val relativeBarLenY: Float get() = (viewSizeDp.y / contentSizeDp.y).clamp()

    val relativeBarPosX: Float get() {
        val div = xScrollDp.value + contentSizeDp.x - (xScrollDp.value + viewSizeDp.x)
        return if (div == 0f) 1f else (xScrollDp.value / div)
    }

    val relativeBarPosY: Float get() {
        val div = yScrollDp.value + contentSizeDp.y - (yScrollDp.value + viewSizeDp.y)
        return if (div == 0f) 0f else (yScrollDp.value / (div))
    }

    fun setXScrollRelative(relativeX: Float) {
        val width = max(contentSizeDp.x, viewSizeDp.x)
        xScrollDp.set((width - viewSizeDp.x) * relativeX)
    }

    fun setYScrollRelative(relativeY: Float) {
        val height = max(contentSizeDp.y, viewSizeDp.y)
        yScrollDp.set((height - viewSizeDp.y) * relativeY)
    }
}

interface ScrollPaneScope : UiScope {
    override val modifier: ScrollPaneModifier
    override val uiNode: ScrollPaneNode
}

open class ScrollPaneModifier : UiModifier() {
    var onScrollPosChanged: ((Float, Float) -> Unit)? by property(null)

    var allowOverscrollX by property(false)
    var allowOverscrollY by property(false)
}

fun <T: ScrollPaneModifier> T.allowOverScroll(x: Boolean, y: Boolean): T {
    allowOverscrollX = x
    allowOverscrollY = y
    return this
}

fun <T: ScrollPaneModifier> T.onScrollPosChanged(block: (Float, Float) -> Unit): T {
    onScrollPosChanged = block
    return this
}

inline fun UiScope.ScrollPane(state: ScrollState, block: ScrollPaneScope.() -> Unit): ScrollPaneScope {
    val scrollPane = uiNode.createChild(ScrollPaneNode::class, ScrollPaneNode.factory)
    scrollPane.state = state
    scrollPane.block()
    return scrollPane
}

open class ScrollPaneNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ScrollPaneScope {
    override val modifier = ScrollPaneModifier()
    override val uiNode: ScrollPaneNode get() = this

    lateinit var state: ScrollState

    private var complainedAboutSize = false

    override fun measureContentSize(ctx: KoolContext) {
        if ((modifier.width !== WrapContent || modifier.height !== WrapContent) && !complainedAboutSize) {
            complainedAboutSize = true
            logW { "ScrollPane width / height should be set to WrapContent for scrolling to work as expected" }
        }
        super.measureContentSize(ctx)
    }

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        updateScrollPos()
        val scrollX = state.xScrollDp.use() * surface.measuredScale
        val scrollY = state.yScrollDp.use() * surface.measuredScale
        super.setBounds(minX - scrollX, minY - scrollY, maxX - scrollX, maxY - scrollY)
    }

    protected open fun updateScrollPos() {
        var desiredScrollX = state.xScrollDp.use()
        var desiredScrollY = state.yScrollDp.use()

        if (parent != null) {
            state.contentSizeDp.set(contentWidth, contentHeight).scale(1f / surface.measuredScale)
            state.viewSizeDp.set(parent.clippedMaxX - parent.clippedMinX, parent.clippedMaxY - parent.clippedMinY)
                .scale(1f / surface.measuredScale)

            if (!modifier.allowOverscrollX) {
                if (desiredScrollX + state.viewSizeDp.x > state.contentSizeDp.x) {
                    desiredScrollX = state.contentSizeDp.x - state.viewSizeDp.x
                }
                state.xScrollDp.set(max(0f, desiredScrollX))
            }
            if (!modifier.allowOverscrollY) {
                if (desiredScrollY + state.viewSizeDp.y > state.contentSizeDp.y) {
                    desiredScrollY = state.contentSizeDp.y - state.viewSizeDp.y
                }
                state.yScrollDp.set(max(0f, desiredScrollY))
            }
        }

        if (state.xScrollDp.isStateChanged || state.yScrollDp.isStateChanged) {
            modifier.onScrollPosChanged?.invoke(state.xScrollDp.value, state.yScrollDp.value)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollPaneNode = { parent, surface -> ScrollPaneNode(parent, surface) }
    }
}