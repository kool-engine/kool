package de.fabmax.kool.platform.sdl

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.*
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.util.BackendScope
import de.fabmax.kool.util.logW
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.launch
import org.lwjgl.sdl.SDLKeyboard.SDL_StartTextInput
import org.lwjgl.sdl.SDLKeyboard.SDL_StopTextInput
import org.lwjgl.sdl.SDLKeycode.*
import org.lwjgl.sdl.SDLMouse.*
import org.lwjgl.sdl.SDLScancode.*

class SdlInput(val window: SdlWindow) : PlatformInput {
    private val isTextInput = atomic(false)

    private val defaultCursor = SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_DEFAULT)
    private val cursorShapes = mutableMapOf(
        CursorShape.DEFAULT to defaultCursor,
        CursorShape.TEXT to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_TEXT),
        CursorShape.HAND to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_POINTER),
        CursorShape.CROSSHAIR to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_CROSSHAIR),
        CursorShape.NOT_ALLOWED to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_NOT_ALLOWED),
        CursorShape.MOVE to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_MOVE),
        CursorShape.RESIZE_E to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_E_RESIZE),
        CursorShape.RESIZE_W to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_W_RESIZE),
        CursorShape.RESIZE_N to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_N_RESIZE),
        CursorShape.RESIZE_S to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_S_RESIZE),
        CursorShape.RESIZE_NW to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_NW_RESIZE),
        CursorShape.RESIZE_SE to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_SE_RESIZE),
        CursorShape.RESIZE_NE to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_NE_RESIZE),
        CursorShape.RESIZE_SW to SDL_CreateSystemCursor(SDL_SYSTEM_CURSOR_SW_RESIZE),
    )

    private var cursorMode = CursorMode.NORMAL
    private val cursorPos = MutableVec2f()

    override fun setCursorMode(cursorMode: CursorMode) {
        this.cursorMode = cursorMode
        BackendScope.launch {
            SDL_SetWindowRelativeMouseMode(window.handle, cursorMode == CursorMode.LOCKED)
        }
    }

    override fun applyCursorShape(cursorShape: CursorShape) {
        BackendScope.launch {
            val shape = cursorShapes[cursorShape] ?: defaultCursor
            if (!SDL_SetCursor(shape)) {
                logW { "Failed to set cursor shape with SDL: $cursorShape" }
            }
        }
    }

    override fun requestKeyboard() {
        if (isTextInput.compareAndSet(expect = false, update = true)) {
            BackendScope.launch { SDL_StartTextInput(window.handle) }
        }
    }

    override fun hideKeyboard() {
        if (isTextInput.compareAndSet(expect = true, update = false)) {
            BackendScope.launch { SDL_StopTextInput(window.handle) }
        }
    }

    internal fun handleMouseMotion(event: SdlEvent.Motion) {
        val baseScale = if (KoolSystem.platform.isMacOs || KoolSystem.platform.isWayland) window.parentScreenScale else 1f
        val scale = baseScale * window.renderResolutionFactor

        if (cursorMode == CursorMode.NORMAL) {
            cursorPos.set(event.x * scale, event.y * scale)
        } else {
            cursorPos.x += event.xRel * scale
            cursorPos.y += event.yRel * scale
        }
        PointerInput.handleMouseMove(cursorPos.x, cursorPos.y)
    }

    internal fun handleMouseWheel(event: SdlEvent.Wheel) {
        PointerInput.handleMouseScroll(event.x, event.y)
    }

    internal fun handleMouseButton(event: SdlEvent.Button) {
        val button = when (val btn = event.button) {
            1 -> PointerInput.LEFT_BUTTON
            2 -> PointerInput.MIDDLE_BUTTON
            3 -> PointerInput.RIGHT_BUTTON
            4 -> PointerInput.BACK_BUTTON
            5 -> PointerInput.FORWARD_BUTTON
            else -> btn - 1
        }
        PointerInput.handleMouseButtonEvent(button, event.down)
    }

    internal fun handleKey(event: SdlEvent.Key) {
        val type = when {
            event.repeat -> KeyboardInput.KEY_EV_REPEATED
            event.down -> KeyboardInput.KEY_EV_DOWN
            else -> KeyboardInput.KEY_EV_UP
        }
        val keyCode = sdlScancodesToKeys[event.scancode] ?: UniversalKeyCode(event.key)

        val sdlMod = event.mod
        var modifiers = 0
        if (sdlMod and SDL_KMOD_SHIFT != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_SHIFT
        if (sdlMod and SDL_KMOD_CTRL != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_CTRL
        if (sdlMod and SDL_KMOD_ALT != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_ALT
        if (sdlMod and SDL_KMOD_GUI != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_SUPER

        val ev = KeyEvent(
            keyCode = keyCode,
            localKeyCode = LocalKeyCode(event.key),
            event = type,
            modifiers = modifiers
        )
        KeyboardInput.handleKeyEvent(ev)
    }

    internal fun handleText(event: SdlEvent.Text) {
        event.text.forEach { char ->
            KeyboardInput.handleCharTyped(char)
        }
    }

    companion object {
        private val sdlScancodesToKeys = mapOf(
            SDL_SCANCODE_A to UniversalKeyCode('a'),
            SDL_SCANCODE_B to UniversalKeyCode('b'),
            SDL_SCANCODE_C to UniversalKeyCode('c'),
            SDL_SCANCODE_D to UniversalKeyCode('d'),
            SDL_SCANCODE_E to UniversalKeyCode('e'),
            SDL_SCANCODE_F to UniversalKeyCode('f'),
            SDL_SCANCODE_G to UniversalKeyCode('g'),
            SDL_SCANCODE_H to UniversalKeyCode('h'),
            SDL_SCANCODE_I to UniversalKeyCode('i'),
            SDL_SCANCODE_J to UniversalKeyCode('j'),
            SDL_SCANCODE_K to UniversalKeyCode('k'),
            SDL_SCANCODE_L to UniversalKeyCode('l'),
            SDL_SCANCODE_M to UniversalKeyCode('m'),
            SDL_SCANCODE_N to UniversalKeyCode('n'),
            SDL_SCANCODE_O to UniversalKeyCode('o'),
            SDL_SCANCODE_P to UniversalKeyCode('p'),
            SDL_SCANCODE_Q to UniversalKeyCode('q'),
            SDL_SCANCODE_R to UniversalKeyCode('r'),
            SDL_SCANCODE_S to UniversalKeyCode('s'),
            SDL_SCANCODE_T to UniversalKeyCode('t'),
            SDL_SCANCODE_U to UniversalKeyCode('u'),
            SDL_SCANCODE_V to UniversalKeyCode('v'),
            SDL_SCANCODE_W to UniversalKeyCode('w'),
            SDL_SCANCODE_X to UniversalKeyCode('x'),
            SDL_SCANCODE_Y to UniversalKeyCode('y'),
            SDL_SCANCODE_Z to UniversalKeyCode('z'),

            SDL_SCANCODE_1 to UniversalKeyCode('1'),
            SDL_SCANCODE_2 to UniversalKeyCode('2'),
            SDL_SCANCODE_3 to UniversalKeyCode('3'),
            SDL_SCANCODE_4 to UniversalKeyCode('4'),
            SDL_SCANCODE_5 to UniversalKeyCode('5'),
            SDL_SCANCODE_6 to UniversalKeyCode('6'),
            SDL_SCANCODE_7 to UniversalKeyCode('7'),
            SDL_SCANCODE_8 to UniversalKeyCode('8'),
            SDL_SCANCODE_9 to UniversalKeyCode('9'),
            SDL_SCANCODE_0 to UniversalKeyCode('0'),

            SDL_SCANCODE_SPACE to UniversalKeyCode(' '),
            SDL_SCANCODE_MINUS to UniversalKeyCode('-'),
            SDL_SCANCODE_EQUALS to UniversalKeyCode('='),
            SDL_SCANCODE_LEFTBRACKET to UniversalKeyCode('['),
            SDL_SCANCODE_RIGHTBRACKET to UniversalKeyCode(']'),
            SDL_SCANCODE_SEMICOLON to UniversalKeyCode(';'),
            SDL_SCANCODE_APOSTROPHE to UniversalKeyCode('\''),
            SDL_SCANCODE_GRAVE to UniversalKeyCode('`'),
            SDL_SCANCODE_COMMA to UniversalKeyCode(','),
            SDL_SCANCODE_PERIOD to UniversalKeyCode('.'),
            SDL_SCANCODE_SLASH to UniversalKeyCode('/'),
            SDL_SCANCODE_BACKSLASH to UniversalKeyCode('\\'),
            SDL_SCANCODE_NONUSBACKSLASH to UniversalKeyCode('\\'),

            SDL_SCANCODE_LCTRL to KeyboardInput.KEY_CTRL_LEFT,
            SDL_SCANCODE_RCTRL to KeyboardInput.KEY_CTRL_RIGHT,
            SDL_SCANCODE_LSHIFT to KeyboardInput.KEY_SHIFT_LEFT,
            SDL_SCANCODE_RSHIFT to KeyboardInput.KEY_SHIFT_RIGHT,
            SDL_SCANCODE_LALT to KeyboardInput.KEY_ALT_LEFT,
            SDL_SCANCODE_RALT to KeyboardInput.KEY_ALT_RIGHT,
            SDL_SCANCODE_LGUI to KeyboardInput.KEY_SUPER_LEFT,
            SDL_SCANCODE_RGUI to KeyboardInput.KEY_SUPER_RIGHT,
            SDL_SCANCODE_ESCAPE to KeyboardInput.KEY_ESC,
            SDL_SCANCODE_MENU to KeyboardInput.KEY_MENU,
            SDL_SCANCODE_RETURN to KeyboardInput.KEY_ENTER,
            SDL_SCANCODE_KP_ENTER to KeyboardInput.KEY_NP_ENTER,
            SDL_SCANCODE_KP_DIVIDE to KeyboardInput.KEY_NP_DIV,
            SDL_SCANCODE_KP_MULTIPLY to KeyboardInput.KEY_NP_MUL,
            SDL_SCANCODE_KP_PLUS to KeyboardInput.KEY_NP_PLUS,
            SDL_SCANCODE_KP_MINUS to KeyboardInput.KEY_NP_MINUS,
            SDL_SCANCODE_KP_PERIOD to KeyboardInput.KEY_NP_DECIMAL,
            SDL_SCANCODE_BACKSPACE to KeyboardInput.KEY_BACKSPACE,
            SDL_SCANCODE_TAB to KeyboardInput.KEY_TAB,
            SDL_SCANCODE_DELETE to KeyboardInput.KEY_DEL,
            SDL_SCANCODE_INSERT to KeyboardInput.KEY_INSERT,
            SDL_SCANCODE_HOME to KeyboardInput.KEY_HOME,
            SDL_SCANCODE_END to KeyboardInput.KEY_END,
            SDL_SCANCODE_PAGEUP to KeyboardInput.KEY_PAGE_UP,
            SDL_SCANCODE_PAGEDOWN to KeyboardInput.KEY_PAGE_DOWN,
            SDL_SCANCODE_LEFT to KeyboardInput.KEY_CURSOR_LEFT,
            SDL_SCANCODE_RIGHT to KeyboardInput.KEY_CURSOR_RIGHT,
            SDL_SCANCODE_UP to KeyboardInput.KEY_CURSOR_UP,
            SDL_SCANCODE_DOWN to KeyboardInput.KEY_CURSOR_DOWN,
            SDL_SCANCODE_F1 to KeyboardInput.KEY_F1,
            SDL_SCANCODE_F2 to KeyboardInput.KEY_F2,
            SDL_SCANCODE_F3 to KeyboardInput.KEY_F3,
            SDL_SCANCODE_F4 to KeyboardInput.KEY_F4,
            SDL_SCANCODE_F5 to KeyboardInput.KEY_F5,
            SDL_SCANCODE_F6 to KeyboardInput.KEY_F6,
            SDL_SCANCODE_F7 to KeyboardInput.KEY_F7,
            SDL_SCANCODE_F8 to KeyboardInput.KEY_F8,
            SDL_SCANCODE_F9 to KeyboardInput.KEY_F9,
            SDL_SCANCODE_F10 to KeyboardInput.KEY_F10,
            SDL_SCANCODE_F11 to KeyboardInput.KEY_F11,
            SDL_SCANCODE_F12 to KeyboardInput.KEY_F12,
        )
    }
}