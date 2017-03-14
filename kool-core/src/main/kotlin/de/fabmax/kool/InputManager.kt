package de.fabmax.kool

/**
 * @author fabmax
 */

class InputManager internal constructor() {

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
    }

    interface DragHandler {
        companion object {
            const val HANDLED = 1
            const val REMOVE_HANDLER = 2
        }

        fun handleDrag(dragPtrs: List<Pointer>): Int
    }

    private val tmpPointers = Array(MAX_POINTERS, ::Pointer)
    private val pointers = Array(MAX_POINTERS, ::Pointer)

    private val dragPtrs: MutableList<Pointer> = mutableListOf()
    private val dragHandlers: MutableList<DragHandler> = mutableListOf()

    /**
     * The primary pointer. For mouse-input that's the mouse cursor, for touch-input it's the first finger
     * that touched the screen. Keep in mind that the returned [Pointer] might be invalid (i.e. [Pointer.isValid] is
     * false) if the cursor exited the GL surface or if no finger touches the screen. Invalid pointers will keep
     * the last state that was set.
     */
    val primaryPointer = pointers[PRIMARY_POINTER]

    fun getPointer(idx: Int): Pointer {
        return pointers[idx]
    }

    fun registerDragHandler(handler: DragHandler) {
        if (handler !in dragHandlers) {
            dragHandlers += handler
        }
    }

    fun removeDraghandler(handler: DragHandler) {
        dragHandlers -= handler
    }

    fun handleDrag() {
        dragPtrs.clear()
        for (i in pointers.indices) {
            val ptr = pointers[i]
            if ((ptr.isValid || ptr.wasValid) &&
                    (ptr.buttonMask != 0 || ptr.buttonEventMask != 0 || ptr.deltaScroll != 0f)) {
                dragPtrs.add(pointers[i])
            }
        }
        var handlerIdx = dragHandlers.lastIndex
        while (handlerIdx >= 0) {
            val result = dragHandlers[handlerIdx].handleDrag(dragPtrs)
            if (result and DragHandler.REMOVE_HANDLER != 0) {
                dragHandlers.removeAt(handlerIdx)
            }
            if (result and DragHandler.HANDLED != 0) {
                break
            }
            handlerIdx--
        }
    }

    internal fun onNewFrame() {
        synchronized(tmpPointers) {
            for (i in pointers.indices) {
                pointers[i].updateFrom(tmpPointers[i])
                tmpPointers[i].buttonEventMask = 0
            }
        }
    }

    /**
     * Updates the position of the specified pointer (mouse or finger on touch devices). Position is given
     * in screen coordinates.
     */
    fun updatePointerPos(pointer: Int, x: Float, y: Float) {
        if (pointer >= 0 && pointer < MAX_POINTERS) {
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
        if (pointer >= 0 && pointer < MAX_POINTERS) {
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
        if (pointer >= 0 && pointer < MAX_POINTERS) {
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
        if (pointer >= 0 && pointer < MAX_POINTERS) {
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
        if (pointer >= 0 && pointer < MAX_POINTERS) {
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

        val isLeftButtonEvent: Boolean
            get() = (buttonEventMask and LEFT_BUTTON_MASK) != 0
        val isRightButtonEvent: Boolean
            get() = (buttonEventMask and RIGHT_BUTTON_MASK) != 0
        val isMiddleButtonEvent: Boolean
            get() = (buttonEventMask and MIDDLE_BUTTON_MASK) != 0
        val isBackButtonEvent: Boolean
            get() = (buttonEventMask and BACK_BUTTON_MASK) != 0
        val isForwardButtonEvent: Boolean
            get() = (buttonEventMask and FORWARD_BUTTON_MASK) != 0

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
    }
}
