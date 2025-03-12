package de.fabmax.kool.input

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logW

internal class BufferedPointerInput : Pointer() {
    private var updateState = UpdateState.INVALID
    private var processedState = UpdateState.INVALID

    private val buttonEventQueue = List<MutableList<Boolean>>(8) { mutableListOf() }
    private var gotPointerEvents = false

    private val dragStartPos = MutableVec2f()

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
            updateClickStates()
        }
    }

    fun startPointer(pointerId: Int, x: Float, y: Float) {
        movePointer(x, y)
        id = pointerId
        _delta.set(0f, 0f)
        _scroll.set(0f, 0f)
        _dragMovement.set(0f, 0f)
        updateState = UpdateState.STARTED
        isValid = true
    }

    fun movePointer(x: Float, y: Float, accumulateDeltas: Boolean = true) {
        val wasDrag = isDrag

        if (accumulateDeltas) {
            _delta.x += x - pos.x
            _delta.y += y - pos.y

            if (isAnyButtonDown) {
                _dragMovement.x = x - dragStartPos.x
                _dragMovement.y = y - dragStartPos.y
            } else {
                dragStartPos.set(x, y)
            }
        }

        // Do not update the position if a drag has just started - this way drag start events are guaranteed
        // to have the same position as the previous hover event. Otherwise, the drag event might not be received
        // by the correct receiver if the first movement is too large and / or the hover / drag target is very
        // small (e.g. resizing of a window border)
        if (!isDrag || isDrag == wasDrag) {
            _pos.set(x, y)
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
        _delta.set(0f, 0f)
        _scroll.set(0f, 0f)
        _dragMovement.set(0f, 0f)
        updateState = UpdateState.INVALID
        isValid = false
    }

    fun update(target: Pointer) {
        processPointerEvents()

        target.id = id
        target._pos.set(_pos)
        target._delta.set(_delta)
        target._dragMovement.set(_dragMovement)
        target._scroll.set(_scroll)
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

        _delta.set(0f, 0f)
        _scroll.set(0f, 0f)
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
        isLeftButtonClicked = isClick(isLeftButtonReleased, 0)
        isRightButtonClicked = isClick(isRightButtonReleased, 1)
        isMiddleButtonClicked = isClick(isMiddleButtonReleased, 2)
        isBackButtonClicked = isClick(isBackButtonReleased, 3)
        isForwardButtonClicked = isClick(isForwardButtonReleased, 4)

        leftButtonRepeatedClickCount = updateRepeatedClickCount(isLeftButtonClicked, 0, leftButtonRepeatedClickCount)
        rightButtonRepeatedClickCount = updateRepeatedClickCount(isRightButtonClicked, 1, rightButtonRepeatedClickCount)
        middleButtonRepeatedClickCount = updateRepeatedClickCount(isMiddleButtonClicked, 2, middleButtonRepeatedClickCount)
        backButtonRepeatedClickCount = updateRepeatedClickCount(isBackButtonClicked, 3, backButtonRepeatedClickCount)
        forwardButtonRepeatedClickCount = updateRepeatedClickCount(isForwardButtonClicked, 4, forwardButtonRepeatedClickCount)

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

        if (isLeftButtonPressed || isRightButtonPressed || isMiddleButtonPressed) {
            // reset drag tracker if left / mid / right button has been pressed
            _dragMovement.set(0f, 0f)
        } else if (!isAnyButtonDown) {
            _dragMovement.set(0f, 0f)
        }
    }

    private fun isClick(isReleased: Boolean, buttonI: Int): Boolean {
        val pressedTime = buttonDownTimes[buttonI]
        val pressedFrame = buttonDownFrames[buttonI]
        return isReleased && dragMovement.length() < PointerInput.MAX_CLICK_MOVE_PX
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