package de.fabmax.kool

import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */

class InputManager internal constructor() {

    interface DragHandler {
        companion object {
            const val HANDLED = 1
            const val REMOVE_HANDLER = 2
        }

        fun handleDrag(dragPtrs: List<Pointer>, ctx: RenderContext): Int
    }

    private val queuedKeyEvents: MutableList<KeyEvent> = mutableListOf()
    val keyEvents: MutableList<KeyEvent> = mutableListOf()

    private val tmpPointers = Array(MAX_POINTERS, ::Pointer)
    val pointers = Array(MAX_POINTERS, ::Pointer)

    /**
     * The primary pointer. For mouse-input that's the mouse cursor, for touch-input it's the first finger
     * that touched the screen. Keep in mind that the returned [Pointer] might be invalid (i.e. [Pointer.isValid] is
     * false) if the cursor exited the GL surface or if no finger touches the screen. Invalid pointers will keep
     * the last state that was set.
     */
    val primaryPointer = pointers[PRIMARY_POINTER]

    internal fun onNewFrame() {
        synchronized(tmpPointers) {
            for (i in pointers.indices) {
                pointers[i].updateFrom(tmpPointers[i])
                tmpPointers[i].buttonEventMask = 0
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

        //println("key event: $keyCode ev=$event mods=$modifiers")

        synchronized(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    fun charTyped(typedChar: Char) {
        val ev = KeyEvent()
        ev.event = KEY_EV_CHAR_TYPED
        ev.typedChar = typedChar

        //println("char type: $typedChar")

        synchronized(queuedKeyEvents) {
            queuedKeyEvents.add(ev)
        }
    }

    /**
     * Updates the position of the specified pointer (mouse or finger on touch devices). Position is given
     * in screen coordinates.
     */
    fun updatePointerPos(pointer: Int, x: Float, y: Float) {
        if (pointer in 0..(MAX_POINTERS - 1)) {
            synchronized(tmpPointers) {
                val ptr = tmpPointers[pointer]
                ptr.isValid = true
                ptr.x = x
                ptr.y = y
            }
        }
    }

    /**
     * Updates the button state of a single button of the specified pointer.
     */
    fun updatePointerButtonState(pointer: Int, button: Int, down: Boolean) {
        if (pointer in 0..(MAX_POINTERS - 1)) {
            synchronized(tmpPointers) {
                val ptr = tmpPointers[pointer]
                ptr.isValid = true
                if (down) {
                    ptr.buttonMask = ptr.buttonMask or (1 shl button)
                } else {
                    ptr.buttonMask = ptr.buttonMask and (1 shl button).inv()
                }
            }
        }
    }

    /**
     * Updates the button state of all buttons of the specified pointer.
     */
    fun updatePointerButtonStates(pointer: Int, mask: Int) {
        if (pointer in 0..(MAX_POINTERS - 1)) {
            synchronized(tmpPointers) {
                val ptr = tmpPointers[pointer]
                ptr.isValid = true
                ptr.buttonMask = mask
            }
        }
    }

    /**
     * Updates the scroll position of the specified pointer.
     */
    fun updatePointerScrollPos(pointer: Int, ticks: Float) {
        if (pointer in 0..(MAX_POINTERS - 1)) {
            synchronized(tmpPointers) {
                val ptr = tmpPointers[pointer]
                ptr.isValid = true
                ptr.scrollPos += ticks
            }
        }
    }

    /**
     * Updates the isValid state of the specified pointer. A pointer gets invalid if the cursor leaves the GL surface.
     */
    fun updatePointerValid(pointer: Int, valid: Boolean) {
        if (pointer in 0..(MAX_POINTERS - 1)) {
            synchronized(tmpPointers) {
                tmpPointers[pointer].isValid = valid
            }
        }
    }

    class Pointer(val id: Int) {
        var x = 0f
            internal set
        var y = 0f
            internal set
        var scrollPos = 0f
            internal set

        var deltaX = 0f
        var deltaY = 0f
        var deltaScroll = 0f

        var buttonMask = 0
            internal set(value) {
                buttonEventMask = buttonEventMask or (field xor value)
                field = value
            }
        var buttonEventMask = 0
            internal set
        var wasValid = false
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

        internal fun updateFrom(ptr: Pointer) {
            deltaX = ptr.x - x
            deltaY = ptr.y - y
            deltaScroll = ptr.scrollPos - scrollPos
            x = ptr.x
            y = ptr.y
            scrollPos = ptr.scrollPos
            buttonMask = ptr.buttonMask
            buttonEventMask = ptr.buttonEventMask
            wasValid = isValid
            isValid = ptr.isValid
        }

        fun isInViewport(ctx: RenderContext): Boolean {
            // y-axis of viewport is inverted to window coordinates
            val ptrY = ctx.windowHeight - y
            return (isValid || wasValid) && x >= ctx.viewportX && ptrY >= ctx.viewportY &&
                    x < ctx.viewportX + ctx.viewportWidth && ptrY < ctx.viewportY + ctx.viewportHeight
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
        const val PRIMARY_POINTER = 0

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
