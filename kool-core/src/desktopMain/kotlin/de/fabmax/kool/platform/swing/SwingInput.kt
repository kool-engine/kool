package de.fabmax.kool.platform.swing

import de.fabmax.kool.input.*
import java.awt.event.KeyAdapter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

private typealias KoolKeyEvent = KeyEvent
private typealias AwtKeyEvent = java.awt.event.KeyEvent

internal class SwingInput(private val canvasWrapper: CanvasWrapper) : PlatformInput {
    private val scale: Float get() = canvasWrapper.parentScreenScale

    override fun setCursorMode(cursorMode: CursorMode) { }

    override fun applyCursorShape(cursorShape: CursorShape) { }

    init {
        canvasWrapper.canvas.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                PointerInput.handleMouseButtonEvent(e.koolButton, true)
            }

            override fun mouseReleased(e: MouseEvent) {
                PointerInput.handleMouseButtonEvent(e.koolButton, false)
            }

            override fun mouseEntered(e: MouseEvent) {
                canvasWrapper.isMouseOverWindow = true
            }

            override fun mouseExited(e: MouseEvent) {
                canvasWrapper.isMouseOverWindow = false
                PointerInput.handleMouseExit()
            }
        })

        canvasWrapper.canvas.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                PointerInput.handleMouseMove(e.x.toFloat() * scale, e.y.toFloat() * scale)
            }

            override fun mouseMoved(e: MouseEvent) {
                PointerInput.handleMouseMove(e.x.toFloat() * scale, e.y.toFloat() * scale)
            }
        })
        canvasWrapper.canvas.addMouseWheelListener { e ->
            PointerInput.handleMouseScroll(
                0f,
                -e.preciseWheelRotation.toFloat()
            )
        }

        canvasWrapper.canvas.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: AwtKeyEvent) {
                KeyboardInput.handleKeyEvent(e.toKoolKeyEvent(KeyboardInput.KEY_EV_DOWN))
            }

            override fun keyReleased(e: AwtKeyEvent) {
                KeyboardInput.handleKeyEvent(e.toKoolKeyEvent(KeyboardInput.KEY_EV_UP))
            }

            override fun keyTyped(e: AwtKeyEvent) {
                if (e.keyChar.code > 31) {
                    KeyboardInput.handleCharTyped(e.keyChar)
                }
            }
        })
    }

    private fun AwtKeyEvent.toKoolKeyEvent(event: Int): KoolKeyEvent {
        val koolKeyCode = when(keyCode) {
            AwtKeyEvent.VK_CONTROL if keyLocation == AwtKeyEvent.KEY_LOCATION_LEFT -> KeyboardInput.KEY_CTRL_LEFT
            AwtKeyEvent.VK_CONTROL if keyLocation == AwtKeyEvent.KEY_LOCATION_RIGHT -> KeyboardInput.KEY_CTRL_RIGHT
            AwtKeyEvent.VK_ALT if keyLocation == AwtKeyEvent.KEY_LOCATION_LEFT -> KeyboardInput.KEY_ALT_LEFT
            AwtKeyEvent.VK_ALT if keyLocation == AwtKeyEvent.KEY_LOCATION_RIGHT -> KeyboardInput.KEY_ALT_RIGHT
            AwtKeyEvent.VK_SHIFT if keyLocation == AwtKeyEvent.KEY_LOCATION_LEFT -> KeyboardInput.KEY_SHIFT_LEFT
            AwtKeyEvent.VK_SHIFT if keyLocation == AwtKeyEvent.KEY_LOCATION_RIGHT -> KeyboardInput.KEY_SHIFT_RIGHT
            AwtKeyEvent.VK_META if keyLocation == AwtKeyEvent.KEY_LOCATION_LEFT -> KeyboardInput.KEY_SUPER_LEFT
            AwtKeyEvent.VK_META if keyLocation == AwtKeyEvent.KEY_LOCATION_RIGHT -> KeyboardInput.KEY_SUPER_RIGHT
            AwtKeyEvent.VK_ENTER if keyLocation == AwtKeyEvent.KEY_LOCATION_NUMPAD -> KeyboardInput.KEY_NP_ENTER
            AwtKeyEvent.VK_DIVIDE if keyLocation == AwtKeyEvent.KEY_LOCATION_NUMPAD -> KeyboardInput.KEY_NP_DIV
            AwtKeyEvent.VK_MULTIPLY if keyLocation == AwtKeyEvent.KEY_LOCATION_NUMPAD -> KeyboardInput.KEY_NP_MUL
            AwtKeyEvent.VK_ADD if keyLocation == AwtKeyEvent.KEY_LOCATION_NUMPAD -> KeyboardInput.KEY_NP_PLUS
            AwtKeyEvent.VK_SUBTRACT if keyLocation == AwtKeyEvent.KEY_LOCATION_NUMPAD -> KeyboardInput.KEY_NP_MINUS
            AwtKeyEvent.VK_DECIMAL if keyLocation == AwtKeyEvent.KEY_LOCATION_NUMPAD -> KeyboardInput.KEY_NP_DECIMAL
            else -> KEY_CODE_MAP[keyCode] ?: UniversalKeyCode(keyCode)
        }
        val localKeyCode = LocalKeyCode(koolKeyCode.code)
        return KoolKeyEvent(
            keyCode = koolKeyCode,
            localKeyCode = localKeyCode,
            event = event,
            modifiers = keyModifiers,
            typedChar = if (event == KeyboardInput.KEY_EV_CHAR_TYPED) keyCode.toChar() else 0.toChar()
        )
    }

    private val MouseEvent.koolButton: Int get() {
        return when (button) {
            MouseEvent.BUTTON1 -> 0 // left button
            MouseEvent.BUTTON2 -> 2 // middle button
            MouseEvent.BUTTON3 -> 1 // right button
            else -> 0
        }
    }

    private val AwtKeyEvent.keyModifiers: Int get() {
        var mods = 0
        if (isControlDown) mods = mods or KeyboardInput.KEY_MOD_CTRL
        if (isAltDown) mods = mods or KeyboardInput.KEY_MOD_ALT
        if (isAltGraphDown) mods = mods or KeyboardInput.KEY_MOD_ALT
        if (isShiftDown) mods = mods or KeyboardInput.KEY_MOD_SHIFT
        if (isMetaDown) mods = mods or KeyboardInput.KEY_MOD_SUPER
        return mods
    }

    companion object {
        private val KEY_CODE_MAP: Map<Int, KeyCode> = mutableMapOf(
            AwtKeyEvent.VK_CONTROL to KeyboardInput.KEY_CTRL_LEFT,
            AwtKeyEvent.VK_SHIFT to KeyboardInput.KEY_SHIFT_LEFT,
            AwtKeyEvent.VK_ALT to KeyboardInput.KEY_ALT_LEFT,
            AwtKeyEvent.VK_ALT_GRAPH to KeyboardInput.KEY_ALT_RIGHT,
            AwtKeyEvent.VK_META to KeyboardInput.KEY_SUPER_LEFT,
            AwtKeyEvent.VK_ESCAPE to KeyboardInput.KEY_ESC,
            AwtKeyEvent.VK_CONTEXT_MENU to KeyboardInput.KEY_MENU,
            AwtKeyEvent.VK_ENTER to KeyboardInput.KEY_ENTER,
            AwtKeyEvent.VK_DIVIDE to KeyboardInput.KEY_NP_DIV,
            AwtKeyEvent.VK_MULTIPLY to KeyboardInput.KEY_NP_MUL,
            AwtKeyEvent.VK_ADD to KeyboardInput.KEY_NP_PLUS,
            AwtKeyEvent.VK_SUBTRACT to KeyboardInput.KEY_NP_MINUS,
            AwtKeyEvent.VK_DECIMAL to KeyboardInput.KEY_NP_DECIMAL,
            AwtKeyEvent.VK_BACK_SPACE to KeyboardInput.KEY_BACKSPACE,
            AwtKeyEvent.VK_TAB to KeyboardInput.KEY_TAB,
            AwtKeyEvent.VK_DELETE to KeyboardInput.KEY_DEL,
            AwtKeyEvent.VK_INSERT to KeyboardInput.KEY_INSERT,
            AwtKeyEvent.VK_HOME to KeyboardInput.KEY_HOME,
            AwtKeyEvent.VK_END to KeyboardInput.KEY_END,
            AwtKeyEvent.VK_PAGE_UP to KeyboardInput.KEY_PAGE_UP,
            AwtKeyEvent.VK_PAGE_DOWN to KeyboardInput.KEY_PAGE_DOWN,
            AwtKeyEvent.VK_LEFT to KeyboardInput.KEY_CURSOR_LEFT,
            AwtKeyEvent.VK_RIGHT to KeyboardInput.KEY_CURSOR_RIGHT,
            AwtKeyEvent.VK_UP to KeyboardInput.KEY_CURSOR_UP,
            AwtKeyEvent.VK_DOWN to KeyboardInput.KEY_CURSOR_DOWN,
            AwtKeyEvent.VK_F1 to KeyboardInput.KEY_F1,
            AwtKeyEvent.VK_F2 to KeyboardInput.KEY_F2,
            AwtKeyEvent.VK_F3 to KeyboardInput.KEY_F3,
            AwtKeyEvent.VK_F4 to KeyboardInput.KEY_F4,
            AwtKeyEvent.VK_F5 to KeyboardInput.KEY_F5,
            AwtKeyEvent.VK_F6 to KeyboardInput.KEY_F6,
            AwtKeyEvent.VK_F7 to KeyboardInput.KEY_F7,
            AwtKeyEvent.VK_F8 to KeyboardInput.KEY_F8,
            AwtKeyEvent.VK_F9 to KeyboardInput.KEY_F9,
            AwtKeyEvent.VK_F10 to KeyboardInput.KEY_F10,
            AwtKeyEvent.VK_F11 to KeyboardInput.KEY_F11,
            AwtKeyEvent.VK_F12 to KeyboardInput.KEY_F12
        )
    }
}