package de.fabmax.kool.platform.headless

import de.fabmax.kool.input.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Input of a headless window. There are no OS-level input devices in headless mode, instead input events can be
 * forwarded by the host application (e.g. from the UI framework kool is embedded in) or be injected by tests.
 *
 * All functions are thread-safe. Events are queued and applied at the beginning of the next frame.
 * Pointer coordinates are given in framebuffer pixels (i.e. the same coordinate space as
 * [HeadlessWindow.framebufferSize]) with the origin in the upper left corner.
 */
class HeadlessInput internal constructor(private val subsystem: HeadlessWindowSubsystem) : PlatformInput {
    private val queuedEvents = ConcurrentLinkedQueue<() -> Unit>()

    /**
     * Called (on the kool render thread) whenever the cursor mode requested by kool changes. Hosts can use this to
     * hide / lock the cursor.
     */
    var onCursorModeChanged: ((CursorMode) -> Unit)? = null

    /**
     * Called (on the kool render thread) whenever the cursor shape requested by kool changes. Hosts can use this to
     * change the cursor icon.
     */
    var onCursorShapeChanged: ((CursorShape) -> Unit)? = null

    private var currentCursorShape = CursorShape.DEFAULT

    private val inputScale: Float get() = subsystem.window.renderResolutionFactor

    fun mouseMove(x: Float, y: Float) = enqueue {
        subsystem.window.isMouseOverWindow = true
        PointerInput.handleMouseMove(x * inputScale, y * inputScale)
    }

    /**
     * Mouse button event, [button] is one of [PointerInput.LEFT_BUTTON], [PointerInput.RIGHT_BUTTON], etc.
     */
    fun mouseButton(button: Int, isDown: Boolean) = enqueue {
        PointerInput.handleMouseButtonEvent(button, isDown)
    }

    fun mouseScroll(xTicks: Float, yTicks: Float) = enqueue {
        PointerInput.handleMouseScroll(xTicks, yTicks)
    }

    fun mouseExit() = enqueue {
        subsystem.window.isMouseOverWindow = false
        PointerInput.handleMouseExit()
    }

    fun touchStart(pointerId: Int, x: Float, y: Float) = enqueue {
        PointerInput.handleTouchStart(pointerId, x * inputScale, y * inputScale)
    }

    fun touchMove(pointerId: Int, x: Float, y: Float) = enqueue {
        PointerInput.handleTouchMove(pointerId, x * inputScale, y * inputScale)
    }

    fun touchEnd(pointerId: Int) = enqueue {
        PointerInput.handleTouchEnd(pointerId)
    }

    fun touchCancel(pointerId: Int) = enqueue {
        PointerInput.handleTouchCancel(pointerId)
    }

    fun keyEvent(event: KeyEvent) = enqueue {
        KeyboardInput.handleKeyEvent(event)
    }

    fun charTyped(char: Char) = enqueue {
        KeyboardInput.handleCharTyped(char)
    }

    private fun enqueue(event: () -> Unit) {
        queuedEvents += event
    }

    internal fun applyQueuedEvents() {
        while (true) {
            val event = queuedEvents.poll() ?: break
            event()
        }
    }

    override fun setCursorMode(cursorMode: CursorMode) {
        onCursorModeChanged?.invoke(cursorMode)
    }

    override fun applyCursorShape(cursorShape: CursorShape) {
        if (cursorShape != currentCursorShape) {
            currentCursorShape = cursorShape
            onCursorShapeChanged?.invoke(cursorShape)
        }
    }
}
