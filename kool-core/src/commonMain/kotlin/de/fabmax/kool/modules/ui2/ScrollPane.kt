package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Time
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

open class ScrollState {
    val xScrollDp = mutableStateOf(0f)
    val yScrollDp = mutableStateOf(0f)
    val xScrollDpDesired = mutableStateOf(0f)
    val yScrollDpDesired = mutableStateOf(0f)

    val contentWidthDp = mutableStateOf(0f)
    val contentHeightDp = mutableStateOf(0f)
    val viewWidthDp = mutableStateOf(0f)
    val viewHeightDp = mutableStateOf(0f)

    val relativeBarLenX: Float get() = (round(viewWidthDp.value) / round(contentWidthDp.value)).clamp()
    val relativeBarLenY: Float get() = (round(viewHeightDp.value) / round(contentHeightDp.value)).clamp()

    val relativeBarPosX: Float get() {
        val div = xScrollDp.value + contentWidthDp.value - (xScrollDp.value + viewWidthDp.value)
        return if (div == 0f) 1f else (xScrollDp.value / div)
    }

    val relativeBarPosY: Float get() {
        val div = yScrollDp.value + contentHeightDp.value - (yScrollDp.value + viewHeightDp.value)
        return if (div == 0f) 0f else (yScrollDp.value / (div))
    }

    val remainingSpaceTop: Float
        get() = yScrollDp.value
    val remainingSpaceBottom: Float
        get() = contentHeightDp.value - (yScrollDp.value + viewHeightDp.value)
    val remainingSpaceStart: Float
        get() = xScrollDp.value
    val remainingSpaceEnd: Float
        get() = contentWidthDp.value - (xScrollDp.value + viewWidthDp.value)

    fun scrollDpX(amount: Float, smooth: Boolean = true) {
        if (smooth) {
            val x = min(max(0f, xScrollDpDesired.value + amount), contentWidthDp.value - viewWidthDp.value)
            xScrollDpDesired.set(x)
        } else {
            val x = min(max(0f, xScrollDp.value + amount), contentWidthDp.value - viewWidthDp.value)
            xScrollDp.set(x)
            xScrollDpDesired.set(x)
        }
    }

    fun scrollDpY(amount: Float, smooth: Boolean = true) {
        if (smooth) {
            val y = min(max(0f, yScrollDpDesired.value + amount), contentHeightDp.value - viewHeightDp.value)
            yScrollDpDesired.set(y)
        } else {
            val y = min(max(0f, yScrollDp.value + amount), contentHeightDp.value - viewHeightDp.value)
            yScrollDp.set(y)
            yScrollDpDesired.set(y)
        }
    }

    fun scrollRelativeX(relativeX: Float, smooth: Boolean = true) {
        val width = max(contentWidthDp.value, viewWidthDp.value)
        xScrollDpDesired.set((width - viewWidthDp.value) * relativeX)
        if (!smooth) {
            xScrollDp.set(xScrollDpDesired.value)
        }
    }

    fun scrollRelativeY(relativeY: Float, smooth: Boolean = true) {
        val height = max(contentHeightDp.value, viewHeightDp.value)
        yScrollDpDesired.set((height - viewHeightDp.value) * relativeY)
        if (!smooth) {
            yScrollDp.set(yScrollDpDesired.value)
        }
    }

    fun computeSmoothScrollPosDpX(): Float {
        return xScrollDp.value + computeSmoothScrollAmountDpX()
    }

    fun computeSmoothScrollPosDpY(): Float {
        return yScrollDp.value + computeSmoothScrollAmountDpY()
    }

    fun computeSmoothScrollAmountDpX(): Float {
        val error = xScrollDpDesired.value - xScrollDp.value
        return if (abs(error) < 1f) {
            error
        } else {
            var step = error * SMOOTHING_FAC * Time.deltaT
            if (abs(step) > abs(error)) {
                step = error
            }
            step
        }
    }

    fun computeSmoothScrollAmountDpY(): Float {
        val error = yScrollDpDesired.value - yScrollDp.value
        return if (abs(error) < 1f) {
            error
        } else {
            var step = error * SMOOTHING_FAC * Time.deltaT
            if (abs(step) > abs(error)) {
                step = error
            }
            step
        }
    }

    fun setSmoothScrollAmountDpX(step: Float) {
        val error = step / (SMOOTHING_FAC * Time.deltaT)
        xScrollDpDesired.set(xScrollDp.value + error)
    }

    fun setSmoothScrollAmountDpY(step: Float) {
        val error = step / (SMOOTHING_FAC * Time.deltaT)
        yScrollDpDesired.set(yScrollDp.value + error)
    }

    companion object {
        private const val SMOOTHING_FAC = 15f
    }
}

interface ScrollPaneScope : UiScope {
    override val modifier: ScrollPaneModifier
}

open class ScrollPaneModifier(surface: UiSurface) : UiModifier(surface) {
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

inline fun UiScope.ScrollPane(
    state: ScrollState,
    scopeName: String? = null,
    block: ScrollPaneScope.() -> Unit
): ScrollPaneScope {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val scrollPane = uiNode.createChild(scopeName, ScrollPaneNode::class, ScrollPaneNode.factory)
    scrollPane.state = state
    scrollPane.block()
    return scrollPane
}

open class ScrollPaneNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ScrollPaneScope {
    override val modifier = ScrollPaneModifier(surface)

    lateinit var state: ScrollState

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        updateScrollPos()
        val scrollX = round(state.xScrollDp.use() * UiScale.measuredScale)
        val scrollY = round(state.yScrollDp.use() * UiScale.measuredScale)
        super.setBounds(minX - scrollX, minY - scrollY, maxX - scrollX, maxY - scrollY)
    }

    protected open fun updateScrollPos() {
        var currentScrollX = state.xScrollDp.use()
        var currentScrollY = state.yScrollDp.use()
        var desiredScrollX = state.xScrollDpDesired.use()
        var desiredScrollY = state.yScrollDpDesired.use()

        if (parent != null) {
            state.viewWidthDp.set(parent.widthPx / UiScale.measuredScale)
            state.viewHeightDp.set(parent.heightPx / UiScale.measuredScale)
            state.contentWidthDp.set(contentWidthPx / UiScale.measuredScale)
            state.contentHeightDp.set(contentHeightPx / UiScale.measuredScale)

            // clamp scroll positions to  content size
            if (!modifier.allowOverscrollX) {
                if (currentScrollX + state.viewWidthDp.value > state.contentWidthDp.value) {
                    currentScrollX = state.contentWidthDp.value - state.viewWidthDp.value
                }
                if (desiredScrollX + state.viewWidthDp.value > state.contentWidthDp.value) {
                    desiredScrollX = state.contentWidthDp.value - state.viewWidthDp.value
                }
                state.xScrollDp.set(max(0f, currentScrollX))
                state.xScrollDpDesired.set(max(0f, desiredScrollX))
            }
            if (!modifier.allowOverscrollY) {
                if (currentScrollY + state.viewHeightDp.value > state.contentHeightDp.value) {
                    currentScrollY = state.contentHeightDp.value - state.viewHeightDp.value
                }
                if (desiredScrollY + state.viewHeightDp.value > state.contentHeightDp.value) {
                    desiredScrollY = state.contentHeightDp.value - state.viewHeightDp.value
                }
                state.yScrollDp.set(max(0f, currentScrollY))
                state.yScrollDpDesired.set(max(0f, desiredScrollY))
            }
        }

        state.xScrollDp.set(state.computeSmoothScrollPosDpX())
        state.yScrollDp.set(state.computeSmoothScrollPosDpY())
        if (state.xScrollDp.isStateChanged || state.yScrollDp.isStateChanged) {
            modifier.onScrollPosChanged?.invoke(state.xScrollDp.value, state.yScrollDp.value)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollPaneNode = { parent, surface -> ScrollPaneNode(parent, surface) }
    }
}