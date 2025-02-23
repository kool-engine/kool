package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logE

object PointerInput {

    const val MAX_POINTERS = 10

    private val inputPointers = Array(MAX_POINTERS) { BufferedPointerInput() }

    val pointerState = PointerState()
    val primaryPointer: Pointer get() = pointerState.primaryPointer

    var isEvaluatingCompatGestures = true

    private var cursorLockTime: UInt = 0u
    var cursorMode: CursorMode = CursorMode.NORMAL
        set(value) {
            if (value != field) {
                cursorLockTime = if (value == CursorMode.LOCKED) Time.frameCount.toUInt() else 0u
                field = value
                Input.platformInput.setCursorMode(value)
            }
        }
    var cursorShape: CursorShape = CursorShape.DEFAULT

    internal fun poll(ctx: KoolContext) {
        Input.platformInput.applyCursorShape(cursorShape)
        cursorShape = CursorShape.DEFAULT
        pointerState.onNewFrame(inputPointers, ctx)
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

    internal fun handleTouchStart(pointerId: Int, x: Float, y: Float) {
        val inPtr = getFreeInputPointer() ?: return
        inPtr.startPointer(pointerId, x, y)

        if (pointerId == 0) {
            inPtr.enqueueButtonEvent(0, true)
        }
    }

    internal fun handleTouchEnd(pointerId: Int) {
        val inPtr = findInputPointer(pointerId)
        if (inPtr == null) {
            logE { "Pointer not found: $pointerId" }
            inputPointers.forEach { it.cancelPointer() }
            return
        }

        inPtr.endPointer()
        if (pointerId == 0) {
            inPtr.enqueueButtonEvent(0, false)
        }
    }

    internal fun handleTouchCancel(pointerId: Int) {
        findInputPointer(pointerId)?.cancelPointer()
    }

    internal fun handleTouchMove(pointerId: Int, x: Float, y: Float) {
        findInputPointer(pointerId)?.movePointer(x, y)
    }

    //
    // mouse handler functions to be called by platform code
    //

    internal fun handleMouseMove(x: Float, y: Float) {
        val mousePtr = findInputPointer(MOUSE_POINTER_ID)
        if (mousePtr == null) {
            val startPtr = getFreeInputPointer() ?: return
            startPtr.startPointer(MOUSE_POINTER_ID, x, y)
        } else {
            // *sometimes* locking / hiding the cursor messes up the cursor position
            // if this happens during drag this can be bad -> suppress any drag movement
            // within 3 frames after cursor lock was engaged
            val cursorLockDelay = Time.frameCount.toUInt() - cursorLockTime
            val accuDeltas = cursorLockDelay > 2u
            mousePtr.movePointer(x, y, accuDeltas)
        }
    }

    internal fun handleMouseButtonEvent(button: Int, down: Boolean) {
        val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
        ptr.enqueueButtonEvent(button, down)
    }

    internal fun handleMouseScroll(xTicks: Float, yTicks: Float) {
        val ptr = findInputPointer(MOUSE_POINTER_ID) ?: return
        ptr._scroll.x += xTicks
        ptr._scroll.y += yTicks
    }

    internal fun handleMouseExit() {
        findInputPointer(MOUSE_POINTER_ID)?.cancelPointer()
    }

    const val MAX_CLICK_MOVE_PX = 15.0
    const val MAX_CLICK_TIME_SECS = 0.25
    const val DOUBLE_CLICK_INTERVAL_SECS = 0.35

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

    const val MOUSE_POINTER_ID = -1000000

    const val CONSUMED_ALL = -1     // 0xffffffff
    const val CONSUMED_LEFT_BUTTON = LEFT_BUTTON_MASK
    const val CONSUMED_RIGHT_BUTTON = RIGHT_BUTTON_MASK
    const val CONSUMED_MIDDLE_BUTTON = MIDDLE_BUTTON_MASK
    const val CONSUMED_BACK_BUTTON = BACK_BUTTON_MASK
    const val CONSUMED_FORWARD_BUTTON = FORWARD_BUTTON_MASK
    const val CONSUMED_SCROLL_X = 32
    const val CONSUMED_SCROLL_Y = 64
    const val CONSUMED_X = 128
    const val CONSUMED_Y = 256
}

enum class CursorMode {
    NORMAL,
    LOCKED
}

enum class CursorShape {
    DEFAULT,
    TEXT,
    CROSSHAIR,
    HAND,
    NOT_ALLOWED,
    RESIZE_EW,
    RESIZE_NS,
    RESIZE_NWSE,
    RESIZE_NESW,
    RESIZE_ALL
}
