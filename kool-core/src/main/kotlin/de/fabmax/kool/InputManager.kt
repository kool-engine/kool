package de.fabmax.kool

import de.fabmax.kool.util.TouchGestureEvaluator
import de.fabmax.kool.util.logW

/**
 * @author fabmax
 */

class InputManager internal constructor() {

    interface DragHandler {
        companion object {
            const val HANDLED = 1
            const val REMOVE_HANDLER = 2
        }

        fun handleDrag(dragPtrs: List<Pointer>, ctx: KoolContext): Int
    }

    private val compatGestureEvaluator = TouchGestureEvaluator()
    var isEvaluatingCompatGestures = true

    private val queuedKeyEvents: MutableList<KeyEvent> = mutableListOf()
    val keyEvents: MutableList<KeyEvent> = mutableListOf()

    private var lastPtrInput = 0.0
    private val inputPointers = Array(MAX_POINTERS) { BufferedPointerInput() }
    val pointers = Array(MAX_POINTERS) { Pointer() }

    /**
     * The primary pointer. For mouse-input that's the mouse cursor, for touch-input it's the first finger
     * that touched the screen. Keep in mind that the returned [Pointer] might be invalid (i.e. [Pointer.isValid] is
     * false) if the cursor exited the GL surface or if no finger touches the screen.
     */
    val primaryPointer = pointers[0]

    fun getActivePointers(result: MutableList<Pointer>) {
        result.clear()
        // nicer but produces heap garbage: pointers.filter { it.isValid }.forEach { result.add(it) }
        for (i in pointers.indices) {
            if (pointers[i].isValid) {
                result.add(pointers[i])
            }
        }
    }

    private fun getFreeInputPointer(): BufferedPointerInput? {
        // nicer but produces heap garbage: return inputPointers.firstOrNull { !it.isValid }
        for (i in inputPointers.indices) {
            if (!inputPointers[i].isValid) {
                return inputPointers[i]
            }
        }
        return null
    }

    private fun findInputPointer(pointerId: Int): BufferedPointerInput? {
        // nicer but produces heap garbage: return inputPointers.firstOrNull { it.isValid && it.id == pointerId }
        for (i in inputPointers.indices) {
            if (inputPointers[i].isValid && inputPointers[i].id == pointerId) {
                return inputPointers[i]
            }
        }
        return null
    }

    internal fun onNewFrame(ctx: KoolContext) {
        synchronized(inputPointers) {
            for (i in pointers.indices) {
                inputPointers[i].update(pointers[i], lastPtrInput)
            }
        }

        if (isEvaluatingCompatGestures) {
            compatGestureEvaluator.evaluate(ctx)
            when (compatGestureEvaluator.currentGesture.type) {
                TouchGestureEvaluator.PINCH -> {
                    // set primary pointer deltaScroll for compatibility with mouse input
                    primaryPointer.deltaScroll = compatGestureEvaluator.currentGesture.dPinchAmount / 20
                    primaryPointer.x = compatGestureEvaluator.currentGesture.centerCurrent.x
                    primaryPointer.y = compatGestureEvaluator.currentGesture.centerCurrent.y
                    primaryPointer.deltaX = compatGestureEvaluator.currentGesture.dCenter.x
                    primaryPointer.deltaY = compatGestureEvaluator.currentGesture.dCenter.y
                }
                TouchGestureEvaluator.TWO_FINGER_DRAG -> {
                    // set primary pointer right button down for compatibility with mouse input
                    primaryPointer.x = compatGestureEvaluator.currentGesture.centerCurrent.x
                    primaryPointer.y = compatGestureEvaluator.currentGesture.centerCurrent.y
                    primaryPointer.deltaX = compatGestureEvaluator.currentGesture.dCenter.x
                    primaryPointer.deltaY = compatGestureEvaluator.currentGesture.dCenter.y
                    if (primaryPointer.buttonMask == LEFT_BUTTON_MASK) {
                        primaryPointer.buttonMask = RIGHT_BUTTON_MASK
                        if (compatGestureEvaluator.currentGesture.numUpdates > 1){
                            primaryPointer.buttonEventMask = 0
                        }
                    }
                }
            }
        }

        synchronized(queuedKeyEvents) {
            keyEvents.clear()
            keyEvents.addAll(queuedKeyEvents)
            queuedKeyEvents.clear()
        }
    }

    fun keyEvent(keyCode: Int, modifiers: Int, event: Int) {
        val ev = KeyEvent()
        ev.keyCode = keyCode
        ev.event = event
        ev.modifiers = modifiers

        synchronized(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    fun charTyped(typedChar: Char) {
        val ev = KeyEvent()
        ev.event = KEY_EV_CHAR_TYPED
        ev.typedChar = typedChar

        synchronized(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    //
    // touch handler functions to be called by platform code
    //

    fun handleTouchStart(pointerId: Int, x: Float, y: Float) {
        synchronized(inputPointers) {
            lastPtrInput = now()
            val inPtr = getFreeInputPointer() ?: return
            inPtr.startPointer(pointerId, x, y)
            inPtr.buttonMask = 1
        }
    }

    fun handleTouchEnd(pointerId: Int) {
        synchronized(inputPointers) {
            findInputPointer(pointerId)?.endPointer()
        }
    }

    fun handleTouchCancel(pointerId: Int) {
        synchronized(inputPointers) {
            findInputPointer(pointerId)?.cancelPointer()
        }
    }

    fun handleTouchMove(pointerId: Int, x: Float, y: Float) {
        synchronized(inputPointers) {
            lastPtrInput = now()
            findInputPointer(pointerId)?.movePointer(x, y)
        }
    }

    //
    // mouse handler functions to be called by platform code
    //

    fun handleMouseMove(x: Float, y: Float) {
        synchronized(inputPointers) {
            lastPtrInput = now()
            val mousePtr = findInputPointer(MOUSE_POINTER_ID)
            if (mousePtr == null) {
                val startPtr = getFreeInputPointer() ?: return
                startPtr.startPointer(MOUSE_POINTER_ID, x, y)
            } else {
                mousePtr.movePointer(x, y)
            }
        }
    }

    fun handleMouseButtonState(button: Int, down: Boolean) {
        synchronized(inputPointers) {
            val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
            if (down) {
                ptr.buttonMask = ptr.buttonMask or (1 shl button)
            } else {
                ptr.buttonMask = ptr.buttonMask and (1 shl button).inv()
            }
            // todo: on low frame rates, mouse button events can get lost if button is pressed
            // and released again before a new frame was rendered
        }
    }

    fun handleMouseButtonStates(mask: Int) {
        synchronized(inputPointers) {
            val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
            ptr.buttonMask = mask
            // todo: on low frame rates, mouse button events can get lost if button is pressed
            // and released again before a new frame was rendered
        }
    }

    fun handleMouseScroll(ticks: Float) {
        synchronized(inputPointers) {
            val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
            ptr.deltaScroll += ticks
        }
    }

    fun handleMouseExit() {
        synchronized(inputPointers) {
            findInputPointer(MOUSE_POINTER_ID)?.cancelPointer()
        }
    }

    open class Pointer {
        var id = 0
            internal set

        var x = 0f
            internal set
        var y = 0f
            internal set

        var deltaX = 0f
            internal set
        var deltaY = 0f
            internal set
        var deltaScroll = 0f
            internal set

        var buttonMask = 0
            internal set(value) {
                buttonEventMask = buttonEventMask or (field xor value)
                field = value
            }
        var buttonEventMask = 0
            internal set
        var isValid = false
            internal set

        val isLeftButtonDown: Boolean get() = (buttonMask and LEFT_BUTTON_MASK) != 0
        val isRightButtonDown: Boolean get() = (buttonMask and RIGHT_BUTTON_MASK) != 0
        val isMiddleButtonDown: Boolean get() = (buttonMask and MIDDLE_BUTTON_MASK) != 0
        val isBackButtonDown: Boolean get() = (buttonMask and BACK_BUTTON_MASK) != 0
        val isForwardButtonDown: Boolean get() = (buttonMask and FORWARD_BUTTON_MASK) != 0

        val isLeftButtonEvent: Boolean get() = (buttonEventMask and LEFT_BUTTON_MASK) != 0
        val isRightButtonEvent: Boolean get() = (buttonEventMask and RIGHT_BUTTON_MASK) != 0
        val isMiddleButtonEvent: Boolean get() = (buttonEventMask and MIDDLE_BUTTON_MASK) != 0
        val isBackButtonEvent: Boolean get() = (buttonEventMask and BACK_BUTTON_MASK) != 0
        val isForwardButtonEvent: Boolean get() = (buttonEventMask and FORWARD_BUTTON_MASK) != 0

        /**
         * Usually, if a pointer is outside the viewport, it is not valid. However, they can be
         * outside a viewport and valid, if there is more than one viewport (e.g. split viewport
         * demo).
         */
        fun isInViewport(ctx: KoolContext): Boolean {
            // y-axis of viewport is inverted to window coordinates
            val ptrY = ctx.windowHeight - y
            //return (isValid || wasValid) && ctx.viewport.isInViewport(x, ptrY)
            return (isValid) && ctx.viewport.isInViewport(x, ptrY)
        }
    }

    internal class BufferedPointerInput : Pointer() {
        private var updateState = UpdateState.INVALID
        private var processedState = UpdateState.INVALID

        var lastUpdate = 0.0

        fun startPointer(pointerId: Int, x: Float, y: Float) {
            movePointer(x, y)
            id = pointerId
            deltaX = 0f
            deltaY = 0f
            deltaScroll = 0f
            updateState = UpdateState.STARTED
            isValid = true
        }

        fun movePointer(x: Float, y: Float) {
            deltaX += x - this.x
            deltaY += y - this.y
            this.x = x
            this.y = y
            lastUpdate = now()
        }

        fun endPointer() {
            updateState = when (processedState) {
                UpdateState.ACTIVE -> UpdateState.ENDED
                UpdateState.STARTED -> UpdateState.ENDED_BEFORE_ACTIVE
                else -> UpdateState.ENDED_BEFORE_STARTED
            }
        }

        fun cancelPointer() {
            updateState = UpdateState.INVALID
            isValid = false
        }

        fun update(target: Pointer, t: Double) {
            if (updateState != UpdateState.INVALID && t - lastUpdate > 200) {
                logW { "Pointer $id timed out!" }
                cancelPointer()
            }

            target.id = id
            target.deltaX = deltaX
            target.deltaY = deltaY
            target.deltaScroll = deltaScroll
            target.x = x
            target.y = y
            target.isValid = true
            target.buttonEventMask = 0

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

            deltaX = 0f
            deltaY = 0f
            deltaScroll = 0f

            processedState = updateState
            updateState = updateState.next()
        }

        /**
         * State machine for handling pointer state, needed for correct mouse button emulation for
         * touche events.
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

    class KeyEvent {
        var keyCode = 0
            internal set
        var modifiers = 0
            internal set
        var event = 0
            internal set
        var typedChar: Char = 0.toChar()
            internal set

        val isPressed: Boolean get() = (event and KEY_EV_DOWN) != 0
        val isRepeated: Boolean get() = (event and KEY_EV_REPEATED) != 0
        val isReleased: Boolean get() = (event and KEY_EV_UP) != 0
        val isCharTyped: Boolean get() = (event and KEY_EV_CHAR_TYPED) != 0

        val isShiftDown: Boolean get() = (modifiers and KEY_MOD_SHIFT) != 0
        val isCtrlDown: Boolean get() = (modifiers and KEY_MOD_CTRL) != 0
        val isAltDown: Boolean get() = (modifiers and KEY_MOD_ALT) != 0
        val isSuperDown: Boolean get() = (modifiers and KEY_MOD_SUPER) != 0
    }

    companion object {
        const val LEFT_BUTTON = 0
        const val LEFT_BUTTON_MASK = 1
        const val RIGHT_BUTTON = 1
        const val RIGHT_BUTTON_MASK = 2
        const val MIDDLE_BUTTON = 2
        const val MIDDLE_BUTTON_MASK = 4
        const val BACK_BUTTON = 3
        const val BACK_BUTTON_MASK = 8
        const val FORWARD_BUTTON = 4
        const val FORWARD_BUTTON_MASK = 16

        const val MAX_POINTERS = 10
        const val MOUSE_POINTER_ID = -1000000

        const val KEY_EV_UP = 1
        const val KEY_EV_DOWN = 2
        const val KEY_EV_REPEATED = 4
        const val KEY_EV_CHAR_TYPED = 8

        const val KEY_MOD_SHIFT = 1
        const val KEY_MOD_CTRL = 2
        const val KEY_MOD_ALT = 4
        const val KEY_MOD_SUPER = 8

        const val KEY_CTRL_LEFT = -1
        const val KEY_CTRL_RIGHT = -2
        const val KEY_SHIFT_LEFT = -3
        const val KEY_SHIFT_RIGHT = -4
        const val KEY_ALT_LEFT = -5
        const val KEY_ALT_RIGHT = -6
        const val KEY_SUPER_LEFT = -7
        const val KEY_SUPER_RIGHT = -8
        const val KEY_ESC = -9
        const val KEY_MENU = -10
        const val KEY_ENTER = -11
        const val KEY_NP_ENTER = -12
        const val KEY_NP_DIV = -13
        const val KEY_NP_MUL = -14
        const val KEY_NP_PLUS = -15
        const val KEY_NP_MINUS = -16
        const val KEY_BACKSPACE = -17
        const val KEY_TAB = -18
        const val KEY_DEL = -19
        const val KEY_INSERT = -20
        const val KEY_HOME = -21
        const val KEY_END = -22
        const val KEY_PAGE_UP = -23
        const val KEY_PAGE_DOWN = -24
        const val KEY_CURSOR_LEFT = -25
        const val KEY_CURSOR_RIGHT = -26
        const val KEY_CURSOR_UP = -27
        const val KEY_CURSOR_DOWN = -28
        const val KEY_F1 = -29
        const val KEY_F2 = -30
        const val KEY_F3 = -31
        const val KEY_F4 = -32
        const val KEY_F5 = -33
        const val KEY_F6 = -34
        const val KEY_F7 = -35
        const val KEY_F8 = -36
        const val KEY_F9 = -37
        const val KEY_F10 = -38
        const val KEY_F11 = -39
        const val KEY_F12 = -40
    }

}
