package de.fabmax.kool.input

import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logW
import kotlin.math.abs

internal class BufferedPointerInput : Pointer() {
    private var updateState = UpdateState.INVALID
    private var processedState = UpdateState.INVALID

    private val buttonEventQueue = List<MutableList<Boolean>>(8) { mutableListOf() }
    private var gotPointerEvents = false

    var lastUpdate = 0.0

    fun enqueueButtonEvent(button: Int, down: Boolean) {
        if (button !in buttonEventQueue.indices) {
            logW { "Discarding pointer button event for out of bounds button: $button" }
            return
        }
        buttonEventQueue[button] += down
        gotPointerEvents = true
    }

    fun processPointerEvents() {
        clearClickStates()

        while (gotPointerEvents) {
            gotPointerEvents = false

            // clear click states before every button event iteration so that we can count repeated click events
            clearClickStates()

            // process enqueued button events, update button mask after each iteration, so that we get click events
            // even if a button was pressed and released again within a single frame
            var updateMask = buttonMask
            buttonEventQueue.forEachIndexed { button, events ->
                if (events.isNotEmpty()) {
                    val event = events.removeAt(0)
                    updateMask = if (event) {
                        updateMask or (1 shl button)
                    } else {
                        updateMask and (1 shl button).inv()
                    }
                    if (events.isNotEmpty()) {
                        // there are button events left in the queue, continue processing them after all buttons
                        // are handled for this iteration
                        gotPointerEvents = true
                    }
                }
            }
            buttonMask = updateMask

            // reset drag tracker if left / mid / right button has been pressed
            if (isLeftButtonPressed || isRightButtonPressed || isMiddleButtonPressed) {
                dragDeltaX = 0.0
                dragDeltaY = 0.0
                dragMovement = 0.0
            }
            updateClickStates()
        }
    }

    fun startPointer(pointerId: Int, x: Double, y: Double) {
        movePointer(x, y)
        id = pointerId
        deltaX = 0.0
        deltaY = 0.0
        dragDeltaX = 0.0
        dragDeltaY = 0.0
        dragMovement = 0.0
        deltaScrollX = 0.0
        deltaScrollY = 0.0
        updateState = UpdateState.STARTED
        isValid = true
    }

    fun movePointer(x: Double, y: Double, accumulateDeltas: Boolean = true) {
        val wasDrag = dragMovement != 0.0

        if (accumulateDeltas) {
            if (isAnyButtonDown) {
                dragDeltaX += x - this.x
                dragDeltaY += y - this.y
                dragMovement += abs(x - this.x) + abs(y - this.y)
            }
            deltaX += x - this.x
            deltaY += y - this.y
        }

        // Do not update the position if a drag has just started - this way drag start events are guaranteed
        // to have the same position as the previous hover event. Otherwise, the drag event might not be received
        // by the correct receiver if the first movement is too large and / or the hover / drag target is very
        // small (e.g. resizing of a window border)
        if (!isDrag || isDrag == wasDrag) {
            this.x = x
            this.y = y
        }
    }

    fun endPointer() {
        updateState = when (processedState) {
            UpdateState.ACTIVE -> UpdateState.ENDED
            UpdateState.STARTED -> UpdateState.ENDED_BEFORE_ACTIVE
            else -> UpdateState.ENDED_BEFORE_STARTED
        }
    }

    fun cancelPointer() {
        buttonMask = 0
        buttonEventMask = 0
        deltaX = 0.0
        deltaY = 0.0
        deltaScrollX = 0.0
        deltaScrollY = 0.0
        dragDeltaX = 0.0
        dragDeltaY = 0.0
        dragMovement = 0.0
        updateState = UpdateState.INVALID
        isValid = false
    }

    fun update(target: Pointer) {
        processPointerEvents()

        target.id = id
        target.deltaX = deltaX
        target.deltaY = deltaY
        target.dragDeltaX = dragDeltaX
        target.dragDeltaY = dragDeltaY
        target.dragMovement = dragMovement
        target.deltaScrollX = deltaScrollX
        target.deltaScrollY = deltaScrollY
        target.x = x
        target.y = y
        target.isValid = true
        target.consumptionMask = 0
        target.buttonEventMask = 0

        target.isLeftButtonClicked = isLeftButtonClicked
        target.isRightButtonClicked = isRightButtonClicked
        target.isMiddleButtonClicked = isMiddleButtonClicked
        target.isBackButtonClicked = isBackButtonClicked
        target.isForwardButtonClicked = isForwardButtonClicked
        target.leftButtonRepeatedClickCount = leftButtonRepeatedClickCount
        target.rightButtonRepeatedClickCount = rightButtonRepeatedClickCount
        target.middleButtonRepeatedClickCount = middleButtonRepeatedClickCount
        target.backButtonRepeatedClickCount = backButtonRepeatedClickCount
        target.forwardButtonRepeatedClickCount = forwardButtonRepeatedClickCount

        when (updateState) {
            UpdateState.STARTED -> target.buttonMask = 0
            UpdateState.ENDED_BEFORE_STARTED -> target.buttonMask = 0
            UpdateState.ACTIVE -> target.buttonMask = buttonMask
            UpdateState.ENDED_BEFORE_ACTIVE -> target.buttonMask = buttonMask
            UpdateState.ENDED -> target.buttonMask = 0
            UpdateState.INVALID -> {
                isValid = false
                target.isValid = false
            }
        }

        deltaX = 0.0
        deltaY = 0.0
        deltaScrollX = 0.0
        deltaScrollY = 0.0
        buttonEventMask = 0

        processedState = updateState
        updateState = updateState.next()
    }

    private fun clearClickStates() {
        isLeftButtonClicked = false
        isRightButtonClicked = false
        isMiddleButtonClicked = false
        isBackButtonClicked = false
        isForwardButtonClicked = false
    }

    private fun updateClickStates() {
        isLeftButtonClicked = isClick(isLeftButtonReleased, 0, dragMovement)
        isRightButtonClicked = isClick(isRightButtonReleased, 1, dragMovement)
        isMiddleButtonClicked = isClick(isMiddleButtonReleased, 2, dragMovement)
        isBackButtonClicked = isClick(isBackButtonReleased, 3, dragMovement)
        isForwardButtonClicked = isClick(isForwardButtonReleased, 4, dragMovement)

        leftButtonRepeatedClickCount = updateRepeatedClickCount(isLeftButtonClicked, 0, leftButtonRepeatedClickCount)
        rightButtonRepeatedClickCount = updateRepeatedClickCount(isRightButtonClicked, 0, rightButtonRepeatedClickCount)
        middleButtonRepeatedClickCount = updateRepeatedClickCount(isMiddleButtonClicked, 0, middleButtonRepeatedClickCount)
        backButtonRepeatedClickCount = updateRepeatedClickCount(isBackButtonClicked, 0, backButtonRepeatedClickCount)
        forwardButtonRepeatedClickCount = updateRepeatedClickCount(isForwardButtonClicked, 0, forwardButtonRepeatedClickCount)

        if (isLeftButtonClicked) {
            buttonClickTimes[0] = Time.precisionTime
            buttonClickFrames[0] = Time.frameCount
        }
        if (isRightButtonClicked) {
            buttonClickTimes[1] = Time.precisionTime
            buttonClickFrames[1] = Time.frameCount
        }
        if (isMiddleButtonClicked) {
            buttonClickTimes[2] = Time.precisionTime
            buttonClickFrames[2] = Time.frameCount
        }
        if (isBackButtonClicked) {
            buttonClickTimes[3] = Time.precisionTime
            buttonClickFrames[3] = Time.frameCount
        }
        if (isForwardButtonClicked) {
            buttonClickTimes[4] = Time.precisionTime
            buttonClickFrames[4] = Time.frameCount
        }
    }

    private fun isClick(isReleased: Boolean, buttonI: Int, dragMovement: Double): Boolean {
        val pressedTime = buttonDownTimes[buttonI]
        val pressedFrame = buttonDownFrames[buttonI]
        return isReleased && dragMovement < PointerInput.MAX_CLICK_MOVE_PX
                && (Time.precisionTime - pressedTime < PointerInput.MAX_CLICK_TIME_SECS || Time.frameCount - pressedFrame == 1)
    }

    private fun updateRepeatedClickCount(isClick: Boolean, buttonI: Int, currentClickCount: Int): Int {
        val dt = Time.precisionTime - buttonClickTimes[buttonI]
        val dFrm = Time.frameCount - buttonClickFrames[buttonI]
        return if (!isClick && dt > PointerInput.DOUBLE_CLICK_INTERVAL_SECS && dFrm > 2) {
            0
        } else if (isClick) {
            currentClickCount + 1
        } else {
            currentClickCount
        }
    }

    /**
     * State machine for handling pointer state, needed for correct mouse button emulation for touch events.
     */
    enum class UpdateState {
        STARTED {
            override fun next(): UpdateState = ACTIVE
        },
        ACTIVE {
            override fun next(): UpdateState = ACTIVE
        },
        ENDED_BEFORE_STARTED {
            override fun next(): UpdateState = ENDED_BEFORE_ACTIVE
        },
        ENDED_BEFORE_ACTIVE {
            override fun next(): UpdateState = ENDED
        },
        ENDED {
            override fun next(): UpdateState = INVALID
        },
        INVALID {
            override fun next(): UpdateState = INVALID
        };

        abstract fun next(): UpdateState
    }
}