package de.fabmax.kool

import de.fabmax.kool.util.TouchGestureEvaluator
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlin.math.abs

abstract class InputManager internal constructor() {

    private val queuedKeyEvents: MutableList<KeyEvent> = mutableListOf()
    val keyEvents: MutableList<KeyEvent> = mutableListOf()
    private var currentKeyMods = 0
    private var currentKeyRepeated = 0

    private val keyHandlers = mutableMapOf<KeyCode, MutableList<KeyEventListener>>()

    abstract var cursorMode: CursorMode

    val pointerState = PointerState()

    val isShiftDown: Boolean get() = (currentKeyMods and KEY_MOD_SHIFT) != 0
    val isCtrlDown: Boolean get() = (currentKeyMods and KEY_MOD_CTRL) != 0
    val isAltDown: Boolean get() = (currentKeyMods and KEY_MOD_ALT) != 0
    val isSuperDown: Boolean get() = (currentKeyMods and KEY_MOD_SUPER) != 0

    fun registerKeyListener(keyCode: KeyCode, name: String, filter: (KeyEvent) -> Boolean = { true }, callback: (KeyEvent) -> Unit): KeyEventListener {
        val keyStr = keyCode.toString()

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

    fun getKeyCodeForChar(char: Char) = char.uppercaseChar().code

    internal fun onNewFrame(ctx: KoolContext) {
        pointerState.onNewFrame(ctx)

        lock(queuedKeyEvents) {
            keyEvents.clear()
            keyEvents.addAll(queuedKeyEvents)
            queuedKeyEvents.clear()
        }

        for (i in keyEvents.indices) {
            val evt = keyEvents[i]
            if (evt.keyCode.code != 0) {
                keyHandlers[evt.keyCode]?.let { listeners ->
                    for (j in listeners.indices) {
                        if (listeners[j].filter(evt)) {
                            listeners[j](evt)
                        }
                    }
                }
            }
            if (evt.localKeyCode.code != 0) {
                keyHandlers[evt.localKeyCode]?.let { listeners ->
                    for (j in listeners.indices) {
                        if (listeners[j].filter(evt)) {
                            listeners[j](evt)
                        }
                    }
                }
            }
        }
    }

    fun keyEvent(ev: KeyEvent) {
        currentKeyMods = ev.modifiers
        currentKeyRepeated = ev.event and KEY_EV_REPEATED

        lock(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    fun charTyped(typedChar: Char) {
        val ev = KeyEvent(LocalKeyCode(typedChar.code), KEY_EV_CHAR_TYPED or currentKeyRepeated, currentKeyMods)
        ev.typedChar = typedChar
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

        private val buttonDownTimes = DoubleArray(5)

        internal var consumptionMask = 0
        internal var dragMovement = 0.0

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

        val isLeftButtonClicked: Boolean get() = isLeftButtonReleased
                && now() - buttonDownTimes[0] < MAX_CLICK_TIME_MS && dragMovement < MAX_CLICK_MOVE_PX
        val isRightButtonClicked: Boolean get() = isRightButtonReleased
                && now() - buttonDownTimes[1] < MAX_CLICK_TIME_MS && dragMovement < MAX_CLICK_MOVE_PX
        val isMiddleButtonClicked: Boolean get() = isMiddleButtonReleased
                && now() - buttonDownTimes[2] < MAX_CLICK_TIME_MS && dragMovement < MAX_CLICK_MOVE_PX
        val isBackButtonClicked: Boolean get() = isBackButtonReleased
                && now() - buttonDownTimes[3] < MAX_CLICK_TIME_MS && dragMovement < MAX_CLICK_MOVE_PX
        val isForwardButtonClicked: Boolean get() = isForwardButtonReleased
                && now() - buttonDownTimes[4] < MAX_CLICK_TIME_MS && dragMovement < MAX_CLICK_MOVE_PX

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
                dragMovement = 0.0
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
            deltaScroll = 0.0
            updateState = UpdateState.STARTED
            isValid = true
        }

        fun movePointer(x: Double, y: Double) {
            if (buttonMask != 0) {
                dragDeltaX += x - this.x
                dragDeltaY += y - this.y
                dragMovement += abs(x - this.x) + abs(y - this.y)
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
            dragMovement = 0.0
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
            target.dragMovement = dragMovement
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

    class KeyEventListener(val keyCode: KeyCode, val name: String, val filter: (KeyEvent) -> Boolean = { true }, val callback: (KeyEvent) -> Unit) {
        operator fun invoke(evt: KeyEvent) = callback.invoke(evt)
    }

    class KeyEvent(keyCode: KeyCode, localKeyCode: KeyCode, event: Int, modifiers: Int) {
        /**
         * Key code for US keyboard layout
         */
        var keyCode = keyCode
            internal set

        /**
         * Key code for local keyboard layout
         */
        var localKeyCode = localKeyCode
            internal set

        var modifiers = modifiers
            internal set
        var event = event
            internal set
        var typedChar: Char = 0.toChar()
            internal set

        constructor(keyCode: KeyCode, event: Int, modifiers: Int) : this(keyCode, keyCode, event, modifiers)

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

        private fun getFreeInputPointer(): BufferedPointerInput? {
            for (i in inputPointers.indices) {
                if (!inputPointers[i].isValid) {
                    return inputPointers[i]
                }
            }
            return null
        }

        private fun findInputPointer(pointerId: Int): BufferedPointerInput? {
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
        const val MAX_CLICK_MOVE_PX = 20.0
        const val MAX_CLICK_TIME_MS = 250.0
        const val DOUBLE_CLICK_INTERVAL_SECS = 0.3

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

        val KEY_CTRL_LEFT = UniversalKeyCode(-1, "CTRL_LEFT")
        val KEY_CTRL_RIGHT = UniversalKeyCode(-2, "CTRL_RIGHT")
        val KEY_SHIFT_LEFT = UniversalKeyCode(-3, "SHIFT_LEFT")
        val KEY_SHIFT_RIGHT = UniversalKeyCode(-4, "SHIFT_RIGHT")
        val KEY_ALT_LEFT = UniversalKeyCode(-5, "ALT_LEFT")
        val KEY_ALT_RIGHT = UniversalKeyCode(-6, "ALT_RIGHT")
        val KEY_SUPER_LEFT = UniversalKeyCode(-7, "SUPER_LEFT")
        val KEY_SUPER_RIGHT = UniversalKeyCode(-8, "SUPER_RIGHT")
        val KEY_ESC = UniversalKeyCode(-9, "ESC")
        val KEY_MENU = UniversalKeyCode(-10, "MENU")
        val KEY_ENTER = UniversalKeyCode(-11, "ENTER")
        val KEY_NP_ENTER = UniversalKeyCode(-12, "NP_ENTER")
        val KEY_NP_DIV = UniversalKeyCode(-13, "NP_DIV")
        val KEY_NP_MUL = UniversalKeyCode(-14, "NP_MUL")
        val KEY_NP_PLUS = UniversalKeyCode(-15, "NP_PLUS")
        val KEY_NP_MINUS = UniversalKeyCode(-16, "NP_MINUS")
        val KEY_BACKSPACE = UniversalKeyCode(-17, "BACKSPACE")
        val KEY_TAB = UniversalKeyCode(-18, "TAB")
        val KEY_DEL = UniversalKeyCode(-19, "DEL")
        val KEY_INSERT = UniversalKeyCode(-20, "INSERT")
        val KEY_HOME = UniversalKeyCode(-21, "HOME")
        val KEY_END = UniversalKeyCode(-22, "END")
        val KEY_PAGE_UP = UniversalKeyCode(-23, "PAGE_UP")
        val KEY_PAGE_DOWN = UniversalKeyCode(-24, "PAGE_DOWN")
        val KEY_CURSOR_LEFT = UniversalKeyCode(-25, "CURSOR_LEFT")
        val KEY_CURSOR_RIGHT = UniversalKeyCode(-26, "CURSOR_RIGHT")
        val KEY_CURSOR_UP = UniversalKeyCode(-27, "CURSOR_UP")
        val KEY_CURSOR_DOWN = UniversalKeyCode(-28, "CURSOR_DOWN")
        val KEY_F1 = UniversalKeyCode(-29, "F1")
        val KEY_F2 = UniversalKeyCode(-30, "F2")
        val KEY_F3 = UniversalKeyCode(-31, "F3")
        val KEY_F4 = UniversalKeyCode(-32, "F4")
        val KEY_F5 = UniversalKeyCode(-33, "F5")
        val KEY_F6 = UniversalKeyCode(-34, "F6")
        val KEY_F7 = UniversalKeyCode(-35, "F7")
        val KEY_F8 = UniversalKeyCode(-36, "F8")
        val KEY_F9 = UniversalKeyCode(-37, "F9")
        val KEY_F10 = UniversalKeyCode(-38, "F10")
        val KEY_F11 = UniversalKeyCode(-39, "F11")
        val KEY_F12 = UniversalKeyCode(-40, "F12")
    }
}

sealed class KeyCode(val code: Int, val isLocal: Boolean, name: String?) {
    val name = name ?: if (code in 32..126) "${code.toChar()}" else "$code"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KeyCode) return false
        return code == other.code && isLocal == other.isLocal
    }

    override fun hashCode(): Int = code * if (isLocal) -1 else 1
}

class UniversalKeyCode(code: Int, name: String? = null) : KeyCode(code, false, name) {
    constructor(codeChar: Char) : this(codeChar.uppercaseChar().code)
    override fun toString() = "{universal:$name}"
}

class LocalKeyCode(code: Int, name: String? = null) : KeyCode(code, true, name) {
    constructor(codeChar: Char) : this(codeChar.uppercaseChar().code)
    override fun toString() = "{local:$name}"
}
