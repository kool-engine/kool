package de.fabmax.kool

/**
 * @author fabmax
 */

class InputHandler internal constructor() {

    companion object {
        val LEFT_BUTTON = 0
        val LEFT_BUTTON_MASK = 1
        val RIGHT_BUTTON = 1
        val RIGHT_BUTTON_MASK = 2
        val MIDDLE_BUTTON = 2
        val MIDDLE_BUTTON_MASK = 4
        val BACK_BUTTON = 3
        val BACK_BUTTON_MASK = 8
        val FORWARD_BUTTON = 4
        val FORWARD_BUTTON_MASK = 16

        val MAX_POINTERS = 10
        val PRIMARY_POINTER = 0
    }

    private val pointers = Array(MAX_POINTERS, ::Pointer)

    /**
     * Returns the primary pointer. For mouse-input that's the mouse cursor, for touch-input it's the first finger
     * that touched the screen. Keep in mind that the returned [Pointer] might be invalid (i.e. [Pointer.valid] is
     * false) if the cursor exited the GL surface or if no finger touches the screen. Invalid pointers will keep
     * the last state that was set.
     */
    fun getPrimaryPointer(): Pointer {
        return pointers[PRIMARY_POINTER]
    }

    /**
     * Updates the position of the specified pointer (mouse or finger on touch devices). Position is given
     * in screen coordinates.
     */
    fun updatePointerPos(pointer: Int, x: Double, y: Double) {
        if (pointer >= 0 && pointer < MAX_POINTERS) {
            val ptr = pointers[pointer]
            ptr.valid = true
            ptr.x = x
            ptr.y = y
        }
    }

    /**
     * Updates the button state of a single button of the specified pointer.
     */
    fun updatePointerButtonState(pointer: Int, button: Int, down: Boolean) {
        if (pointer >= 0 && pointer < MAX_POINTERS) {
            val ptr = pointers[pointer]
            ptr.valid = true
            if (down) {
                ptr.buttonMask = ptr.buttonMask or (1 shl button)
                //println("pressed: $button, mask = ${ptr.buttonMask}")
            } else {
                ptr.buttonMask = ptr.buttonMask and (1 shl button).inv()
                //println("released: $button, mask = ${ptr.buttonMask}")
            }
        }
    }

    /**
     * Updates the scroll position of the specified pointer.
     */
    fun updatePointerScrollPos(pointer: Int, ticks: Double) {
        if (pointer >= 0 && pointer < MAX_POINTERS) {
            pointers[pointer].scrollPos += ticks
            println("scrollPos: ${pointers[pointer].scrollPos}")
        }
    }

    /**
     * Updates the valid state of the specified pointer. A pointer gets invalid if the cursor leaves the GL surface.
     */
    fun updatePointerValid(pointer: Int, valid: Boolean) {
        if (pointer >= 0 && pointer < MAX_POINTERS) {
            pointers[pointer].valid = valid
            //println("valid: $valid")
        }
    }

    /**
     * Updates the button state of all buttons of the specified pointer.
     */
    fun updatePointerButtonStates(pointer: Int, mask: Int) {
        if (pointer >= 0 && pointer < MAX_POINTERS) {
            val ptr = pointers[pointer]
            ptr.valid = true
            //if (mask != ptr.buttonMask) {
            //    println("button state changed: ${ptr.buttonMask} -> $mask")
            //}
            ptr.buttonMask = mask
        }
    }

    class Pointer(val id: Int) {
        var x = 0.0
            internal set
        var y = 0.0
            internal set
        var scrollPos = 0.0
            internal set
        var buttonMask = 0
            internal set
        var valid = false
            internal set

        val isLeftButtonDown: Boolean
            get() = (buttonMask and LEFT_BUTTON_MASK) != 0
        val isRightButtonDown: Boolean
            get() = (buttonMask and RIGHT_BUTTON_MASK) != 0
        val isMiddleButtonDown: Boolean
            get() = (buttonMask and MIDDLE_BUTTON_MASK) != 0
        val isBackButtonDown: Boolean
            get() = (buttonMask and BACK_BUTTON_MASK) != 0
        val isForwardButtonDown: Boolean
            get() = (buttonMask and FORWARD_BUTTON_MASK) != 0
    }
}
