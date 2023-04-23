package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Viewport

open class Pointer {
    var id = 0
        internal set

    var x = 0.0
        internal set
    var y = 0.0
        internal set

    var deltaX = 0.0
        internal set
    var deltaY = 0.0
        internal set
    var dragDeltaX = 0.0
        internal set
    var dragDeltaY = 0.0
        internal set

    var deltaScrollY = 0.0
        internal set
    var deltaScrollX = 0.0
        internal set

    val deltaScroll: Double
        get() = deltaScrollY

    var buttonMask = 0
        internal set(value) {
            buttonEventMask = buttonEventMask or (field xor value)
            field = value
            if (buttonEventMask and value != 0) {
                updateButtonDownTimes()
            }
        }
    var buttonEventMask = 0
        internal set
    var isValid = false
        internal set

    protected val buttonClickTimes = DoubleArray(5)
    protected val buttonClickFrames = IntArray(5)
    protected val buttonDownTimes = DoubleArray(5)
    protected val buttonDownFrames = IntArray(5)

    internal var consumptionMask = 0
    internal var dragMovement = 0.0

    val isAnyButtonDown: Boolean get() = buttonMask != 0
    val isLeftButtonDown: Boolean get() = (buttonMask and PointerInput.LEFT_BUTTON_MASK) != 0
    val isRightButtonDown: Boolean get() = (buttonMask and PointerInput.RIGHT_BUTTON_MASK) != 0
    val isMiddleButtonDown: Boolean get() = (buttonMask and PointerInput.MIDDLE_BUTTON_MASK) != 0
    val isBackButtonDown: Boolean get() = (buttonMask and PointerInput.BACK_BUTTON_MASK) != 0
    val isForwardButtonDown: Boolean get() = (buttonMask and PointerInput.FORWARD_BUTTON_MASK) != 0

    val isAnyButtonEvent: Boolean get() = buttonEventMask != 0
    val isLeftButtonEvent: Boolean get() = (buttonEventMask and PointerInput.LEFT_BUTTON_MASK) != 0
    val isRightButtonEvent: Boolean get() = (buttonEventMask and PointerInput.RIGHT_BUTTON_MASK) != 0
    val isMiddleButtonEvent: Boolean get() = (buttonEventMask and PointerInput.MIDDLE_BUTTON_MASK) != 0
    val isBackButtonEvent: Boolean get() = (buttonEventMask and PointerInput.BACK_BUTTON_MASK) != 0
    val isForwardButtonEvent: Boolean get() = (buttonEventMask and PointerInput.FORWARD_BUTTON_MASK) != 0

    val isLeftButtonPressed: Boolean get() = isLeftButtonEvent && isLeftButtonDown
    val isRightButtonPressed: Boolean get() = isRightButtonEvent && isRightButtonDown
    val isMiddleButtonPressed: Boolean get() = isMiddleButtonEvent && isMiddleButtonDown
    val isBackButtonPressed: Boolean get() = isBackButtonEvent && isBackButtonDown
    val isForwardButtonPressed: Boolean get() = isForwardButtonEvent && isForwardButtonDown

    val isLeftButtonReleased: Boolean get() = isLeftButtonEvent && !isLeftButtonDown
    val isRightButtonReleased: Boolean get() = isRightButtonEvent && !isRightButtonDown
    val isMiddleButtonReleased: Boolean get() = isMiddleButtonEvent && !isMiddleButtonDown
    val isBackButtonReleased: Boolean get() = isBackButtonEvent && !isBackButtonDown
    val isForwardButtonReleased: Boolean get() = isForwardButtonEvent && !isForwardButtonDown

    var isLeftButtonClicked = false
        internal set
    var isRightButtonClicked = false
        internal set
    var isMiddleButtonClicked = false
        internal set
    var isBackButtonClicked = false
        internal set
    var isForwardButtonClicked = false
        internal set
    val isAnyButtonClicked: Boolean
        get() = isLeftButtonClicked
                || isRightButtonClicked
                || isMiddleButtonClicked
                || isBackButtonClicked
                || isForwardButtonClicked

    var leftButtonRepeatedClickCount = 0
        internal set
    var rightButtonRepeatedClickCount = 0
        internal set
    var middleButtonRepeatedClickCount = 0
        internal set
    var backButtonRepeatedClickCount = 0
        internal set
    var forwardButtonRepeatedClickCount = 0
        internal set

    val isDrag: Boolean get() = isAnyButtonDown && (dragDeltaX != 0.0 || dragDeltaY != 0.0)

    fun consume(mask: Int = PointerInput.CONSUMED_ALL) {
        consumptionMask = consumptionMask or mask
    }

    fun isConsumed(mask: Int = PointerInput.CONSUMED_ALL) = (consumptionMask and mask) != 0

    /**
     * Usually, if a pointer is outside the viewport, it is not valid. However, they can be
     * outside a viewport and valid, if there is more than one viewport (e.g. split viewport
     * demo).
     */
    fun isInViewport(viewport: Viewport, ctx: KoolContext): Boolean {
        // y-axis of viewport is inverted to window coordinates
        val ptrY = ctx.windowHeight - y
        return (isValid) && viewport.isInViewport(x.toFloat(), ptrY.toFloat())
    }

    private fun updateButtonDownTimes() {
        val downEvents = buttonEventMask and buttonMask
        for (i in buttonDownTimes.indices) {
            if (downEvents and (1 shl i) != 0) {
                buttonDownTimes[i] = Time.precisionTime
                buttonDownFrames[i] = Time.frameCount
            }
        }
    }
}