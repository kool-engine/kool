package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.Viewport

open class Pointer {
    var id = 0
        internal set

    internal val _pos = MutableVec2f()
    internal val _delta = MutableVec2f()
    internal val _dragMovement = MutableVec2f()
    internal val _scroll = MutableVec2f()

    /**
     * Pointer position in screen coordinates (origin: top-left).
     */
    val pos: Vec2f get() = _pos

    /**
     * Movement / change of pointer position since last frame.
     */
    val delta: Vec2f get() = _delta

    /**
     * Total movement of pointer since a drag operation started. Only valid if [isDrag] is true.
     */
    val dragMovement: Vec2f get() = _dragMovement

    /**
     * Scroll amount in x and y direction since last frame.
     */
    val scroll: Vec2f get() = _scroll

    /**
     * Window-scale of the parent window.
     */
    val windowScale: Float get() = KoolSystem.requireContext().windowScale

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

    val isDrag: Boolean get() = isAnyButtonDown && _dragMovement != Vec2f.ZERO

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
    val isAnyButtonPressed: Boolean
        get() = isLeftButtonPressed
                || isRightButtonPressed
                || isMiddleButtonPressed
                || isBackButtonPressed
                || isForwardButtonPressed

    val isLeftButtonReleased: Boolean get() = isLeftButtonEvent && !isLeftButtonDown
    val isRightButtonReleased: Boolean get() = isRightButtonEvent && !isRightButtonDown
    val isMiddleButtonReleased: Boolean get() = isMiddleButtonEvent && !isMiddleButtonDown
    val isBackButtonReleased: Boolean get() = isBackButtonEvent && !isBackButtonDown
    val isForwardButtonReleased: Boolean get() = isForwardButtonEvent && !isForwardButtonDown
    val isAnyButtonReleased: Boolean
        get() = isLeftButtonReleased
                || isRightButtonReleased
                || isMiddleButtonReleased
                || isBackButtonReleased
                || isForwardButtonReleased

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
        val ptrY = ctx.windowHeight - pos.y
        return (isValid) && viewport.isInViewport(pos.x, ptrY)
    }

    override fun toString(): String {
        return "{ id: $id, x: ${pos.x}, y: ${pos.y} }"
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