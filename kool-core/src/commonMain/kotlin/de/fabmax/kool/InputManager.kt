package de.fabmax.kool

import de.fabmax.kool.util.TouchGestureEvaluator
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW

abstract class InputManager internal constructor() {

    private val queuedKeyEvents: MutableList<KeyEvent> = mutableListOf()
    val keyEvents: MutableList<KeyEvent> = mutableListOf()
    private var currentKeyMods = 0
    private var currentKeyRepeated = 0

    private val keyHandlers = mutableMapOf<Int, MutableList<KeyEventListener>>()

    abstract var cursorMode: CursorMode

    val pointerState = PointerState()

    val isShiftDown: Boolean get() = (currentKeyMods and KEY_MOD_SHIFT) != 0
    val isCtrlDown: Boolean get() = (currentKeyMods and KEY_MOD_CTRL) != 0
    val isAltDown: Boolean get() = (currentKeyMods and KEY_MOD_ALT) != 0
    val isSuperDown: Boolean get() = (currentKeyMods and KEY_MOD_SUPER) != 0

    fun registerKeyListener(keyCode: Int, name: String, filter: (KeyEvent) -> Boolean = { true }, callback: (KeyEvent) -> Unit): KeyEventListener {
        val keyStr = if (keyCode in 32..126) "'${keyCode.toChar()}'" else "$keyCode"

        val listeners = keyHandlers.getOrPut(keyCode) { mutableListOf() }
        if (listeners.isNotEmpty()) {
            logW { "Multiple bindings for key $keyStr: ${listeners.map { it.name }}" }
        }

        val handler = KeyEventListener(keyCode, name, filter, callback)
        listeners += handler
        logD { "Registered key handler: \"$name\" [keyCode=$keyStr]" }
        return handler
    }

    fun removeKeyListener(listener: KeyEventListener) {
        val listeners = keyHandlers[listener.keyCode] ?: return
        listeners -= listener
    }

    open fun getKeyCodeForChar(char: Char, useLocalKeyboardLayout: Boolean = false) = char.uppercaseChar().code

    internal fun onNewFrame(ctx: KoolContext) {
        pointerState.onNewFrame(ctx)

        lock(queuedKeyEvents) {
            keyEvents.clear()
            keyEvents.addAll(queuedKeyEvents)
            queuedKeyEvents.clear()
        }

        for (i in keyEvents.indices) {
            val evt = keyEvents[i]
            val listeners = keyHandlers[evt.keyCode]
            if (listeners != null) {
                for (j in listeners.indices) {
                    if (listeners[j].filter(evt)) {
                        listeners[j](evt)
                    }
                }
            }
        }
    }

    fun keyEvent(keyCode: Int, modifiers: Int, event: Int) {
        val ev = KeyEvent()
        ev.keyCode = keyCode
        ev.event = event
        ev.modifiers = modifiers

        currentKeyMods = modifiers
        currentKeyRepeated = event and KEY_EV_REPEATED

        lock(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    fun charTyped(typedChar: Char) {
        val ev = KeyEvent()
        ev.event = KEY_EV_CHAR_TYPED or currentKeyRepeated
        ev.typedChar = typedChar
        ev.keyCode = typedChar.code
        ev.modifiers = currentKeyMods

        lock(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    //
    // mouse and touch handler functions to be called by platform code
    //

    fun handleTouchStart(pointerId: Int, x: Double, y: Double) = pointerState.handleTouchStart(pointerId, x, y)

    fun handleTouchEnd(pointerId: Int) = pointerState.handleTouchEnd(pointerId)

    fun handleTouchCancel(pointerId: Int) = pointerState.handleTouchCancel(pointerId)

    fun handleTouchMove(pointerId: Int, x: Double, y: Double) = pointerState.handleTouchMove(pointerId, x, y)

    fun handleMouseMove(x: Double, y: Double) = pointerState.handleMouseMove(x, y)

    fun handleMouseButtonState(button: Int, down: Boolean) = pointerState.handleMouseButtonState(button, down)

    fun handleMouseButtonStates(mask: Int) = pointerState.handleMouseButtonStates(mask)

    fun handleMouseScroll(ticks: Double) = pointerState.handleMouseScroll(ticks)

    fun handleMouseExit() = pointerState.handleMouseExit()

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
        var deltaScroll = 0.0
            internal set

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

        protected val buttonDownTimes = DoubleArray(5)

        internal var consumptionMask = 0

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

        val isLeftButtonClicked: Boolean get() = isLeftButtonReleased && now() - buttonDownTimes[0] < 300.0
        val isRightButtonClicked: Boolean get() = isRightButtonReleased && now() - buttonDownTimes[1] < 300.0
        val isMiddleButtonClicked: Boolean get() = isMiddleButtonReleased && now() - buttonDownTimes[2] < 300.0
        val isBackButtonClicked: Boolean get() = isBackButtonReleased && now() - buttonDownTimes[3] < 300.0
        val isForwardButtonClicked: Boolean get() = isForwardButtonReleased && now() - buttonDownTimes[4] < 300.0

        fun consume(mask: Int = CONSUMED_ALL) {
            consumptionMask = consumptionMask or mask
        }

        fun isConsumed(mask: Int = CONSUMED_ALL) = (consumptionMask and mask) != 0

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
                    buttonDownTimes[i] = now()
                }
            }
        }
    }

    internal class BufferedPointerInput : Pointer() {
        private var updateState = UpdateState.INVALID
        private var processedState = UpdateState.INVALID

        var lastUpdate = 0.0

        fun setButtonMask(mask: Int) {
            buttonMask = mask
            if (isLeftButtonPressed || isRightButtonPressed || isMiddleButtonPressed) {
                dragDeltaX = 0.0
                dragDeltaY = 0.0
            }
        }

        fun startPointer(pointerId: Int, x: Double, y: Double) {
            movePointer(x, y)
            id = pointerId
            deltaX = 0.0
            deltaY = 0.0
            dragDeltaX = 0.0
            dragDeltaY = 0.0
            deltaScroll = 0.0
            updateState = UpdateState.STARTED
            isValid = true
        }

        fun movePointer(x: Double, y: Double) {
            if (buttonMask != 0) {
                dragDeltaX += x - this.x
                dragDeltaY += y - this.y
            }

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
            buttonMask = 0
            buttonEventMask = 0
            deltaX = 0.0
            deltaY = 0.0
            deltaScroll = 0.0
            dragDeltaX = 0.0
            dragDeltaY = 0.0
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
            target.dragDeltaX = dragDeltaX
            target.dragDeltaY = dragDeltaY
            target.deltaScroll = deltaScroll
            target.x = x
            target.y = y
            target.isValid = true
            target.consumptionMask = 0
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

            deltaX = 0.0
            deltaY = 0.0
            deltaScroll = 0.0
            buttonEventMask = 0

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

    class KeyEventListener(val keyCode: Int, val name: String, val filter: (KeyEvent) -> Boolean = { true }, val callback: (KeyEvent) -> Unit) {
        operator fun invoke(evt: KeyEvent) = callback.invoke(evt)
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

    class PointerState {
        val pointers = Array(MAX_POINTERS) { Pointer() }

        private var lastPtrInput = 0.0
        private val inputPointers = Array(MAX_POINTERS) { BufferedPointerInput() }

        private val compatGestureEvaluator = TouchGestureEvaluator()
        var isEvaluatingCompatGestures = true

        /**
         * The primary pointer. For mouse-input that's the mouse cursor, for touch-input it's the first finger
         * that touched the screen. Keep in mind that the returned [Pointer] might be invalid (i.e. [Pointer.isValid] is
         * false) if the cursor exited the GL surface or if no finger touches the screen.
         */
        val primaryPointer = pointers[0]

        fun getActivePointers(result: MutableList<Pointer>, consumedMask: Int = CONSUMED_ALL) {
            result.clear()
            // pointers.filter { it.isValid }.forEach { result.add(it) }
            for (i in pointers.indices) {
                if (pointers[i].isValid && !pointers[i].isConsumed(consumedMask)) {
                    result.add(pointers[i])
                }
            }
        }

        internal fun onNewFrame(ctx: KoolContext) {
            lock(inputPointers) {
                for (i in pointers.indices) {
                    inputPointers[i].update(pointers[i], lastPtrInput)
                }
            }

            if (isEvaluatingCompatGestures) {
                compatGestureEvaluator.evaluate(this, ctx)
                when (compatGestureEvaluator.currentGesture.type) {
                    TouchGestureEvaluator.PINCH -> {
                        // set primary pointer deltaScroll for compatibility with mouse input
                        primaryPointer.consumptionMask = 0
                        primaryPointer.deltaScroll = compatGestureEvaluator.currentGesture.dPinchAmount / 20.0
                        primaryPointer.x = compatGestureEvaluator.currentGesture.centerCurrent.x
                        primaryPointer.y = compatGestureEvaluator.currentGesture.centerCurrent.y
                        primaryPointer.deltaX = compatGestureEvaluator.currentGesture.dCenter.x
                        primaryPointer.deltaY = compatGestureEvaluator.currentGesture.dCenter.y
                    }
                    TouchGestureEvaluator.TWO_FINGER_DRAG -> {
                        // set primary pointer right button down for compatibility with mouse input
                        primaryPointer.consumptionMask = 0
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
        }

        internal fun getFreeInputPointer(): BufferedPointerInput? {
            // return inputPointers.firstOrNull { !it.isValid }
            for (i in inputPointers.indices) {
                if (!inputPointers[i].isValid) {
                    return inputPointers[i]
                }
            }
            return null
        }

        internal fun findInputPointer(pointerId: Int): BufferedPointerInput? {
            // return inputPointers.firstOrNull { it.isValid && it.id == pointerId }
            for (i in inputPointers.indices) {
                if (inputPointers[i].isValid && inputPointers[i].id == pointerId) {
                    return inputPointers[i]
                }
            }
            return null
        }

        internal fun handleTouchStart(pointerId: Int, x: Double, y: Double) {
            lock(inputPointers) {
                lastPtrInput = now()
                val inPtr = getFreeInputPointer() ?: return
                inPtr.startPointer(pointerId, x, y)
                inPtr.buttonMask = 1
            }
        }

        internal fun handleTouchEnd(pointerId: Int) {
            lock(inputPointers) {
                findInputPointer(pointerId)?.endPointer()
            }
        }

        internal fun handleTouchCancel(pointerId: Int) {
            lock(inputPointers) {
                findInputPointer(pointerId)?.cancelPointer()
            }
        }

        internal fun handleTouchMove(pointerId: Int, x: Double, y: Double) {
            lock(inputPointers) {
                lastPtrInput = now()
                findInputPointer(pointerId)?.movePointer(x, y)
            }
        }

        //
        // mouse handler functions to be called by platform code
        //

        internal fun handleMouseMove(x: Double, y: Double) {
            lock(inputPointers) {
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

        internal fun handleMouseButtonState(button: Int, down: Boolean) {
            lock(inputPointers) {
                val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
                if (down) {
                    ptr.setButtonMask(ptr.buttonMask or (1 shl button))
                } else {
                    ptr.setButtonMask(ptr.buttonMask and (1 shl button).inv())
                }
                // todo: on low frame rates, mouse button events can get lost if button is pressed
                // and released again before a new frame was rendered
            }
        }

        internal fun handleMouseButtonStates(mask: Int) {
            lock(inputPointers) {
                val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
                ptr.setButtonMask(mask)
                // todo: on low frame rates, mouse button events can get lost if button is pressed
                // and released again before a new frame was rendered
            }
        }

        internal fun handleMouseScroll(ticks: Double) {
            lock(inputPointers) {
                val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
                ptr.deltaScroll += ticks
            }
        }

        internal fun handleMouseExit() {
            lock(inputPointers) {
                findInputPointer(MOUSE_POINTER_ID)?.cancelPointer()
            }
        }
    }

    enum class CursorMode {
        NORMAL,
        LOCKED
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

        const val CONSUMED_ALL = -1     // 0xffffffff
        const val CONSUMED_LEFT_BUTTON = LEFT_BUTTON_MASK
        const val CONSUMED_RIGHT_BUTTON = RIGHT_BUTTON_MASK
        const val CONSUMED_MIDDLE_BUTTON = MIDDLE_BUTTON_MASK
        const val CONSUMED_BACK_BUTTON = BACK_BUTTON_MASK
        const val CONSUMED_FORWARD_BUTTON = FORWARD_BUTTON_MASK
        const val CONSUMED_SCROLL = 32
        const val CONSUMED_X = 64
        const val CONSUMED_Y = 128

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
