package de.fabmax.kool.input

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW

internal actual object PlatformInput {

    var isMouseOverWindow = false
        private set

    private val localCharKeyCodes = mutableMapOf<Int, Int>()
    private val cursorShapes = mutableMapOf<CursorShape, Long>()
    private var currentCursorShape = CursorShape.DEFAULT

    actual fun setCursorMode(cursorMode: CursorMode) {
        val ctx = KoolSystem.getContextOrNull() as Lwjgl3Context? ?: return
        val windowHandle = ctx.renderBackend.glfwWindow.windowPtr

        if (cursorMode == CursorMode.NORMAL || ctx.isWindowFocused) {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, cursorMode.glfwMode)
        }
    }

    actual fun applyCursorShape(cursorShape: CursorShape) {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context? ?: return
        val windowHandle = ctx.renderBackend.glfwWindow.windowPtr

        if (cursorShape != currentCursorShape) {
            GLFW.glfwSetCursor(windowHandle, cursorShapes[cursorShape] ?: 0L)
            currentCursorShape = cursorShape
        }
    }

    fun onContextCreated(ctx: Lwjgl3Context) {
        deriveLocalKeyCodes()
        createStandardCursors()

        val windowHandle = ctx.renderBackend.glfwWindow.windowPtr
        installInputHandlers(windowHandle)

        ctx.onWindowFocusChanged += {
            if (PointerInput.cursorMode == CursorMode.LOCKED) {
                if (!it.isWindowFocused) {
                    logD { "Switching to normal cursor mode because of focus loss" }
                    GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
                } else {
                    logD { "Re-engaging cursor-lock because of focus gain" }
                    GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
                }
            }
        }
    }

    private fun deriveLocalKeyCodes() {
        val printableKeys = mutableListOf<Int>()
        for (c in GLFW.GLFW_KEY_0..GLFW.GLFW_KEY_9) { printableKeys += c }
        for (c in GLFW.GLFW_KEY_A..GLFW.GLFW_KEY_Z) { printableKeys += c }
        for (c in GLFW.GLFW_KEY_KP_0..GLFW.GLFW_KEY_KP_9) { printableKeys += c }
        printableKeys += GLFW.GLFW_KEY_APOSTROPHE
        printableKeys += GLFW.GLFW_KEY_COMMA
        printableKeys += GLFW.GLFW_KEY_MINUS
        printableKeys += GLFW.GLFW_KEY_PERIOD
        printableKeys += GLFW.GLFW_KEY_SLASH
        printableKeys += GLFW.GLFW_KEY_SEMICOLON
        printableKeys += GLFW.GLFW_KEY_EQUAL
        printableKeys += GLFW.GLFW_KEY_LEFT_BRACKET
        printableKeys += GLFW.GLFW_KEY_RIGHT_BRACKET
        printableKeys += GLFW.GLFW_KEY_BACKSLASH
        //printableKeys += GLFW.GLFW_KEY_WORLD_1
        printableKeys += GLFW.GLFW_KEY_WORLD_2
        printableKeys += GLFW.GLFW_KEY_KP_DECIMAL
        printableKeys += GLFW.GLFW_KEY_KP_DIVIDE
        printableKeys += GLFW.GLFW_KEY_KP_MULTIPLY
        printableKeys += GLFW.GLFW_KEY_KP_SUBTRACT
        printableKeys += GLFW.GLFW_KEY_KP_ADD
        printableKeys += GLFW.GLFW_KEY_KP_EQUAL

        printableKeys.forEach { c ->
            val localName = GLFW.glfwGetKeyName(c, 0) ?: ""
            if (localName.isNotBlank()) {
                val localChar = localName[0].uppercaseChar()
                localCharKeyCodes[c] = localChar.code
            }
        }
    }

    private fun createStandardCursors() {
        cursorShapes[CursorShape.DEFAULT] = 0
        cursorShapes[CursorShape.TEXT] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR)
        cursorShapes[CursorShape.CROSSHAIR] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR)
        cursorShapes[CursorShape.HAND] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR)
        cursorShapes[CursorShape.H_RESIZE] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR)
        cursorShapes[CursorShape.V_RESIZE] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR)
    }

    private fun installInputHandlers(windowHandle: Long) {
        // install mouse callbacks
        GLFW.glfwSetMouseButtonCallback(windowHandle) { _, btn, act, _ ->
            PointerInput.handleMouseButtonEvent(btn, act == GLFW.GLFW_PRESS)
        }
        GLFW.glfwSetCursorPosCallback(windowHandle) { _, x, y ->
            PointerInput.handleMouseMove(x, y)
        }
        GLFW.glfwSetCursorEnterCallback(windowHandle) { _, entered ->
            if (!entered) {
                isMouseOverWindow = false
                PointerInput.handleMouseExit()
            } else {
                isMouseOverWindow = true
            }
        }
        GLFW.glfwSetScrollCallback(windowHandle) { _, xOff, yOff ->
            PointerInput.handleMouseScroll(xOff, yOff)
        }

        // install keyboard callbacks
        GLFW.glfwSetKeyCallback(windowHandle) { _, key, _, action, mods ->
            val event = when (action) {
                GLFW.GLFW_PRESS -> KeyboardInput.KEY_EV_DOWN
                GLFW.GLFW_REPEAT -> KeyboardInput.KEY_EV_DOWN or KeyboardInput.KEY_EV_REPEATED
                GLFW.GLFW_RELEASE -> KeyboardInput.KEY_EV_UP
                else -> -1
            }
            if (event != -1) {
                val keyCode = Lwjgl3Context.KEY_CODE_MAP[key] ?: UniversalKeyCode(key)
                val localKeyCode = LocalKeyCode(localCharKeyCodes[keyCode.code] ?: keyCode.code)
                var keyMod = 0
                if (mods and GLFW.GLFW_MOD_ALT != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_ALT
                }
                if (mods and GLFW.GLFW_MOD_CONTROL != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_CTRL
                }
                if (mods and GLFW.GLFW_MOD_SHIFT != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_SHIFT
                }
                if (mods and GLFW.GLFW_MOD_SUPER != 0) {
                    keyMod = keyMod or KeyboardInput.KEY_MOD_SUPER
                }
                KeyboardInput.handleKeyEvent(KeyEvent(keyCode, localKeyCode, event, keyMod))
            }
        }
        GLFW.glfwSetCharCallback(windowHandle) { _, codepoint ->
            KeyboardInput.handleCharTyped(codepoint.toChar())
        }
    }

    private val CursorMode.glfwMode: Int
        get() = when (this) {
            CursorMode.NORMAL -> GLFW.GLFW_CURSOR_NORMAL
            CursorMode.LOCKED -> GLFW.GLFW_CURSOR_DISABLED
        }
}