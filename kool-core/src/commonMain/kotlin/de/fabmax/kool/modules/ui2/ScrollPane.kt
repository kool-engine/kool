package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import kotlin.math.abs
import kotlin.math.max

open class ScrollState {
    val xScrollDp = mutableStateOf(0f)
    val yScrollDp = mutableStateOf(0f)
    val xScrollDpDesired = mutableStateOf(0f)
    val yScrollDpDesired = mutableStateOf(0f)

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

    fun scrollDpX(amount: Float, smooth: Boolean = true) {
        if (smooth) {
            xScrollDpDesired.set((xScrollDpDesired.value + amount))
        } else {
            xScrollDp.set(xScrollDp.value + amount)
            xScrollDpDesired.set(xScrollDp.value)
        }
    }

    fun scrollDpY(amount: Float, smooth: Boolean = true) {
        if (smooth) {
            yScrollDpDesired.set((yScrollDpDesired.value + amount))
        } else {
            yScrollDp.set(yScrollDp.value + amount)
            yScrollDpDesired.set(yScrollDp.value)
        }
    }

    fun scrollRelativeX(relativeX: Float, smooth: Boolean = true) {
        val width = max(contentSizeDp.x, viewSizeDp.x)
        xScrollDpDesired.set((width - viewSizeDp.x) * relativeX)
        if (!smooth) {
            xScrollDp.set(xScrollDpDesired.value)
        }
    }

    fun scrollRelativeY(relativeY: Float, smooth: Boolean = true) {
        val height = max(contentSizeDp.y, viewSizeDp.y)
        yScrollDpDesired.set((height - viewSizeDp.y) * relativeY)
        if (!smooth) {
            yScrollDp.set(yScrollDpDesired.value)
        }
    }

    fun computeSmoothScrollPosDpX(deltaT: Float): Float {
        val error = xScrollDpDesired.value - xScrollDp.value
        return if (abs(error) < 1f) {
            xScrollDpDesired.value
        } else {
            var step = error * 15f * deltaT
            if (abs(step) > abs(error)) {
                step = error
            }
            xScrollDp.value + step
        }
    }

    fun computeSmoothScrollPosDpY(deltaT: Float): Float {
        val error = yScrollDpDesired.value - yScrollDp.value
        return if (abs(error) < 1f) {
            yScrollDpDesired.value
        } else {
            var step = error * 15f * deltaT
            if (abs(step) > abs(error)) {
                step = error
            }
            yScrollDp.value + step
        }
    }
}

interface ScrollPaneScope : UiScope {
    override val modifier: ScrollPaneModifier
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

    lateinit var state: ScrollState

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        updateScrollPos()
        val scrollX = state.xScrollDp.use() * surface.measuredScale
        val scrollY = state.yScrollDp.use() * surface.measuredScale
        super.setBounds(minX - scrollX, minY - scrollY, maxX - scrollX, maxY - scrollY)
    }

    protected open fun updateScrollPos() {
        var currentScrollX = state.xScrollDp.use()
        var currentScrollY = state.yScrollDp.use()
        var desiredScrollX = state.xScrollDpDesired.use()
        var desiredScrollY = state.yScrollDpDesired.use()

        if (parent != null) {
            state.viewSizeDp.set(parent.widthPx, parent.heightPx).scale(1f / surface.measuredScale)
            state.contentSizeDp.set(contentWidthPx, contentHeightPx).scale(1f / surface.measuredScale)

            // clamp scroll positions to  content size
            if (!modifier.allowOverscrollX) {
                if (currentScrollX + state.viewSizeDp.x > state.contentSizeDp.x) {
                    currentScrollX = state.contentSizeDp.x - state.viewSizeDp.x
                }
                if (desiredScrollX + state.viewSizeDp.x > state.contentSizeDp.x) {
                    desiredScrollX = state.contentSizeDp.x - state.viewSizeDp.x
                }
                state.xScrollDp.set(max(0f, currentScrollX))
                state.xScrollDpDesired.set(max(0f, desiredScrollX))
            }
            if (!modifier.allowOverscrollY) {
                if (currentScrollY + state.viewSizeDp.y > state.contentSizeDp.y) {
                    currentScrollY = state.contentSizeDp.y - state.viewSizeDp.y
                }
                if (desiredScrollY + state.viewSizeDp.y > state.contentSizeDp.y) {
                    desiredScrollY = state.contentSizeDp.y - state.viewSizeDp.y
                }
                state.yScrollDp.set(max(0f, currentScrollY))
                state.yScrollDpDesired.set(max(0f, desiredScrollY))
            }
        }

        state.xScrollDp.set(state.computeSmoothScrollPosDpX(surface.deltaT))
        state.yScrollDp.set(state.computeSmoothScrollPosDpY(surface.deltaT))
        if (state.xScrollDp.isStateChanged || state.yScrollDp.isStateChanged) {
            modifier.onScrollPosChanged?.invoke(state.xScrollDp.value, state.yScrollDp.value)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollPaneNode = { parent, surface -> ScrollPaneNode(parent, surface) }
    }
}