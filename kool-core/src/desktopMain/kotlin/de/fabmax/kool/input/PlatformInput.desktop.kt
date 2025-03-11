package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.isMacOs
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW.*

internal actual fun PlatformInput(): PlatformInput = PlatformInputJvm

object PlatformInputJvm : PlatformInput {

    var isMouseOverWindow = false
        private set

    private val localCharKeyCodes = mutableMapOf<Int, Int>()
    private val cursorShapes = mutableMapOf<CursorShape, Long>()
    private var currentCursorShape = CursorShape.DEFAULT

    private val isMacOs: Boolean = KoolSystem.isMacOs
    private val ctx: KoolContext by lazy { KoolSystem.requireContext() }

    override fun setCursorMode(cursorMode: CursorMode) {
        val ctx = KoolSystem.getContextOrNull() as Lwjgl3Context? ?: return
        val window = ctx.backend.glfwWindow
        val windowHandle = window.windowPtr

        if (cursorMode == CursorMode.NORMAL || ctx.isWindowFocused) {
            val x = doubleArrayOf(0.0)
            val y = doubleArrayOf(0.0)
            glfwGetCursorPos(windowHandle, x, y)
            glfwSetInputMode(windowHandle, GLFW_CURSOR, cursorMode.glfwMode)
            if (cursorMode == CursorMode.NORMAL) {
                val setX = ((x[0] % window.framebufferWidth) + window.framebufferWidth) % window.framebufferWidth
                val setY = ((y[0] % window.framebufferHeight) + window.framebufferHeight) % window.framebufferHeight
                glfwSetCursorPos(windowHandle, setX, setY)
            }
        }
    }

    override fun applyCursorShape(cursorShape: CursorShape) {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context? ?: return
        val windowHandle = ctx.backend.glfwWindow.windowPtr

        if (cursorShape != currentCursorShape) {
            glfwSetCursor(windowHandle, cursorShapes[cursorShape] ?: 0L)
            currentCursorShape = cursorShape
        }
    }

    internal fun onContextCreated(ctx: Lwjgl3Context) {
        deriveLocalKeyCodes()
        createStandardCursors()

        val windowHandle = ctx.backend.glfwWindow.windowPtr
        installInputHandlers(windowHandle)

        ctx.onWindowFocusChanged += {
            if (PointerInput.cursorMode == CursorMode.LOCKED) {
                if (!it.isWindowFocused) {
                    logD { "Switching to normal cursor mode because of focus loss" }
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
                } else {
                    logD { "Re-engaging cursor-lock because of focus gain" }
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
                }
            }
        }
    }

    private fun deriveLocalKeyCodes() {
        val printableKeys = mutableListOf<Int>()
        for (c in GLFW_KEY_0..GLFW_KEY_9) { printableKeys += c }
        for (c in GLFW_KEY_A..GLFW_KEY_Z) { printableKeys += c }
        for (c in GLFW_KEY_KP_0..GLFW_KEY_KP_9) { printableKeys += c }
        printableKeys += GLFW_KEY_APOSTROPHE
        printableKeys += GLFW_KEY_COMMA
        printableKeys += GLFW_KEY_MINUS
        printableKeys += GLFW_KEY_PERIOD
        printableKeys += GLFW_KEY_SLASH
        printableKeys += GLFW_KEY_SEMICOLON
        printableKeys += GLFW_KEY_EQUAL
        printableKeys += GLFW_KEY_LEFT_BRACKET
        printableKeys += GLFW_KEY_RIGHT_BRACKET
        printableKeys += GLFW_KEY_BACKSLASH
        printableKeys += GLFW_KEY_KP_DECIMAL
        printableKeys += GLFW_KEY_KP_DIVIDE
        printableKeys += GLFW_KEY_KP_MULTIPLY
        printableKeys += GLFW_KEY_KP_SUBTRACT
        printableKeys += GLFW_KEY_KP_ADD
        printableKeys += GLFW_KEY_KP_EQUAL

        printableKeys.forEach { c ->
            val localName = glfwGetKeyName(c, 0) ?: ""
            if (localName.isNotBlank()) {
                val localChar = localName[0].uppercaseChar()
                localCharKeyCodes[c] = localChar.code
            }
        }
    }

    private fun createStandardCursors() {
        cursorShapes[CursorShape.DEFAULT] = 0
        cursorShapes[CursorShape.TEXT] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR)
        cursorShapes[CursorShape.CROSSHAIR] = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR)
        cursorShapes[CursorShape.HAND] = glfwCreateStandardCursor(GLFW_HAND_CURSOR)
        cursorShapes[CursorShape.NOT_ALLOWED] = glfwCreateStandardCursor(GLFW_NOT_ALLOWED_CURSOR)
        cursorShapes[CursorShape.RESIZE_EW] = glfwCreateStandardCursor(GLFW_RESIZE_EW_CURSOR)
        cursorShapes[CursorShape.RESIZE_NS] = glfwCreateStandardCursor(GLFW_RESIZE_NS_CURSOR)
        cursorShapes[CursorShape.RESIZE_NESW] = glfwCreateStandardCursor(GLFW_RESIZE_NESW_CURSOR)
        cursorShapes[CursorShape.RESIZE_NWSE] = glfwCreateStandardCursor(GLFW_RESIZE_NWSE_CURSOR)
        cursorShapes[CursorShape.RESIZE_ALL] = glfwCreateStandardCursor(GLFW_RESIZE_ALL_CURSOR)
    }

    private fun installInputHandlers(windowHandle: Long) {
        // install mouse callbacks
        glfwSetMouseButtonCallback(windowHandle) { _, btn, act, _ ->
            PointerInput.handleMouseButtonEvent(btn, act == GLFW_PRESS)
        }
        glfwSetCursorPosCallback(windowHandle) { _, x, y ->
            val scale = if (isMacOs) ctx.windowScale else ctx.renderScale
            PointerInput.handleMouseMove(x.toFloat() * scale, y.toFloat() * scale)
        }
        glfwSetCursorEnterCallback(windowHandle) { _, entered ->
            if (!entered) {
                isMouseOverWindow = false
                PointerInput.handleMouseExit()
            } else {
                isMouseOverWindow = true
            }
        }
        glfwSetScrollCallback(windowHandle) { _, xOff, yOff ->
            PointerInput.handleMouseScroll(xOff.toFloat(), yOff.toFloat())
        }

        // install keyboard callbacks
        glfwSetKeyCallback(windowHandle) { _, key, _, action, mods ->
            val event = when (action) {
                GLFW_PRESS -> KeyboardInput.KEY_EV_DOWN
                GLFW_REPEAT -> KeyboardInput.KEY_EV_DOWN or KeyboardInput.KEY_EV_REPEATED
                GLFW_RELEASE -> KeyboardInput.KEY_EV_UP
                else -> -1
            }
            if (event != -1) {
                val keyCode = KEY_CODE_MAP[key] ?: UniversalKeyCode(key)
                val localKeyCode = LocalKeyCode(localCharKeyCodes[keyCode.code] ?: keyCode.code)
                var keyMod = 0
                if (mods and GLFW_MOD_ALT != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_ALT
                }
                if (mods and GLFW_MOD_CONTROL != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_CTRL
                }
                if (mods and GLFW_MOD_SHIFT != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_SHIFT
                }
                if (mods and GLFW_MOD_SUPER != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_SUPER
                }

                // set keyMod explicitly if a mod key is pressed / released (apparently mods passed by GLFW lag behind)
                when (key) {
                    GLFW_KEY_LEFT_SHIFT -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_SHIFT, event)
                    GLFW_KEY_RIGHT_SHIFT -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_SHIFT, event)
                    GLFW_KEY_LEFT_CONTROL -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_CTRL, event)
                    GLFW_KEY_RIGHT_CONTROL -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_CTRL, event)
                    GLFW_KEY_LEFT_ALT -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_ALT, event)
                    GLFW_KEY_RIGHT_ALT -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_ALT, event)
                    GLFW_KEY_LEFT_SUPER -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_SUPER, event)
                    GLFW_KEY_RIGHT_SUPER -> keyMod = updateDownMask(keyMod, KeyboardInput.KEY_MOD_SUPER, event)
                }

                KeyboardInput.handleKeyEvent(KeyEvent(keyCode, localKeyCode, event, keyMod))
            }
        }
        glfwSetCharCallback(windowHandle) { _, codepoint ->
            KeyboardInput.handleCharTyped(codepoint.toChar())
        }

        for (i in GLFW_JOYSTICK_1 until GLFW_JOYSTICK_LAST) {
            if (glfwJoystickPresent(i)) {
                ControllerInput.addController(ControllerJvm(i))
            }
        }

        glfwSetJoystickCallback { joystickId, event ->
            when (event) {
                GLFW_CONNECTED -> ControllerInput.addController(ControllerJvm(joystickId))
                GLFW_DISCONNECTED -> ControllerInput.removeController(joystickId)
            }
        }
    }

    private fun updateDownMask(mask: Int, bit: Int, event: Int): Int {
        return if (event and KeyboardInput.KEY_EV_DOWN != 0) {
            mask or bit
        } else {
            mask and bit.inv()
        }
    }

    private val CursorMode.glfwMode: Int
        get() = when (this) {
            CursorMode.NORMAL -> GLFW_CURSOR_NORMAL
            CursorMode.LOCKED -> GLFW_CURSOR_DISABLED
        }


    private val KEY_CODE_MAP: Map<Int, KeyCode> = mutableMapOf(
        GLFW_KEY_LEFT_CONTROL to KeyboardInput.KEY_CTRL_LEFT,
        GLFW_KEY_RIGHT_CONTROL to KeyboardInput.KEY_CTRL_RIGHT,
        GLFW_KEY_LEFT_SHIFT to KeyboardInput.KEY_SHIFT_LEFT,
        GLFW_KEY_RIGHT_SHIFT to KeyboardInput.KEY_SHIFT_RIGHT,
        GLFW_KEY_LEFT_ALT to KeyboardInput.KEY_ALT_LEFT,
        GLFW_KEY_RIGHT_ALT to KeyboardInput.KEY_ALT_RIGHT,
        GLFW_KEY_LEFT_SUPER to KeyboardInput.KEY_SUPER_LEFT,
        GLFW_KEY_RIGHT_SUPER to KeyboardInput.KEY_SUPER_RIGHT,
        GLFW_KEY_ESCAPE to KeyboardInput.KEY_ESC,
        GLFW_KEY_MENU to KeyboardInput.KEY_MENU,
        GLFW_KEY_ENTER to KeyboardInput.KEY_ENTER,
        GLFW_KEY_KP_ENTER to KeyboardInput.KEY_NP_ENTER,
        GLFW_KEY_KP_DIVIDE to KeyboardInput.KEY_NP_DIV,
        GLFW_KEY_KP_MULTIPLY to KeyboardInput.KEY_NP_MUL,
        GLFW_KEY_KP_ADD to KeyboardInput.KEY_NP_PLUS,
        GLFW_KEY_KP_SUBTRACT to KeyboardInput.KEY_NP_MINUS,
        GLFW_KEY_KP_DECIMAL to KeyboardInput.KEY_NP_DECIMAL,
        GLFW_KEY_BACKSPACE to KeyboardInput.KEY_BACKSPACE,
        GLFW_KEY_TAB to KeyboardInput.KEY_TAB,
        GLFW_KEY_DELETE to KeyboardInput.KEY_DEL,
        GLFW_KEY_INSERT to KeyboardInput.KEY_INSERT,
        GLFW_KEY_HOME to KeyboardInput.KEY_HOME,
        GLFW_KEY_END to KeyboardInput.KEY_END,
        GLFW_KEY_PAGE_UP to KeyboardInput.KEY_PAGE_UP,
        GLFW_KEY_PAGE_DOWN to KeyboardInput.KEY_PAGE_DOWN,
        GLFW_KEY_LEFT to KeyboardInput.KEY_CURSOR_LEFT,
        GLFW_KEY_RIGHT to KeyboardInput.KEY_CURSOR_RIGHT,
        GLFW_KEY_UP to KeyboardInput.KEY_CURSOR_UP,
        GLFW_KEY_DOWN to KeyboardInput.KEY_CURSOR_DOWN,
        GLFW_KEY_F1 to KeyboardInput.KEY_F1,
        GLFW_KEY_F2 to KeyboardInput.KEY_F2,
        GLFW_KEY_F3 to KeyboardInput.KEY_F3,
        GLFW_KEY_F4 to KeyboardInput.KEY_F4,
        GLFW_KEY_F5 to KeyboardInput.KEY_F5,
        GLFW_KEY_F6 to KeyboardInput.KEY_F6,
        GLFW_KEY_F7 to KeyboardInput.KEY_F7,
        GLFW_KEY_F8 to KeyboardInput.KEY_F8,
        GLFW_KEY_F9 to KeyboardInput.KEY_F9,
        GLFW_KEY_F10 to KeyboardInput.KEY_F10,
        GLFW_KEY_F11 to KeyboardInput.KEY_F11,
        GLFW_KEY_F12 to KeyboardInput.KEY_F12
    )
}