package de.fabmax.kool.platform.sdl

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.*
import de.fabmax.kool.util.logE
import org.lwjgl.sdl.SDLKeycode.*
import org.lwjgl.sdl.SDLScancode.*
import org.lwjgl.sdl.SDL_KeyboardEvent
import org.lwjgl.sdl.SDL_MouseButtonEvent
import org.lwjgl.sdl.SDL_MouseMotionEvent
import org.lwjgl.sdl.SDL_MouseWheelEvent

class SdlInput(val window: SdlWindow) : PlatformInput {

    override fun setCursorMode(cursorMode: CursorMode) {
        logE { "setCursorMode not yet implemented" }
    }

    override fun applyCursorShape(cursorShape: CursorShape) {
        logE { "applyCursorShape not yet implemented" }
    }

    internal fun handleMouseMotion(event: SDL_MouseMotionEvent) {
        val baseScale = if (KoolSystem.platform.isMacOs /*|| isWayland*/) window.parentScreenScale else 1f
        val scale = baseScale * window.renderResolutionFactor
        PointerInput.handleMouseMove(event.x() * scale, event.y() * scale)
    }

    internal fun handleMouseWheel(event: SDL_MouseWheelEvent) {
        PointerInput.handleMouseScroll(event.x(), event.y())
    }

    internal fun handleMouseButton(event: SDL_MouseButtonEvent) {
        val button = event.button().toInt()
        val down = event.down()
        PointerInput.handleMouseButtonEvent(button, down)
    }

    internal fun handleKey(event: SDL_KeyboardEvent) {
        val type = when {
            event.repeat() -> KeyboardInput.KEY_EV_REPEATED
            event.down() -> KeyboardInput.KEY_EV_DOWN
            else -> KeyboardInput.KEY_EV_UP
        }
        val keyCode = sdlScancodesToKeys[event.scancode()] ?: UniversalKeyCode(event.key()).also { println("unmapped: ${event.scancode()}") }

        val sdlMod = event.mod().toInt()
        var modifiers = 0
        if (sdlMod and SDL_KMOD_SHIFT != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_SHIFT
        if (sdlMod and SDL_KMOD_CTRL != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_CTRL
        if (sdlMod and SDL_KMOD_ALT != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_ALT
        if (sdlMod and SDL_KMOD_GUI != 0) modifiers = modifiers or KeyboardInput.KEY_MOD_SUPER

        val ev = KeyEvent(
            keyCode = keyCode,
            localKeyCode = LocalKeyCode(event.key()),
            event = type,
            modifiers = modifiers
        )
        KeyboardInput.handleKeyEvent(ev)
    }

    companion object {
        private val sdlScancodesToKeys = mapOf(
            SDL_SCANCODE_A to UniversalKeyCode('A'),
            SDL_SCANCODE_B to UniversalKeyCode('B'),
            SDL_SCANCODE_C to UniversalKeyCode('C'),
            SDL_SCANCODE_D to UniversalKeyCode('D'),
            SDL_SCANCODE_E to UniversalKeyCode('E'),
            SDL_SCANCODE_F to UniversalKeyCode('F'),
            SDL_SCANCODE_G to UniversalKeyCode('G'),
            SDL_SCANCODE_H to UniversalKeyCode('H'),
            SDL_SCANCODE_I to UniversalKeyCode('I'),
            SDL_SCANCODE_J to UniversalKeyCode('J'),
            SDL_SCANCODE_K to UniversalKeyCode('K'),
            SDL_SCANCODE_L to UniversalKeyCode('L'),
            SDL_SCANCODE_M to UniversalKeyCode('M'),
            SDL_SCANCODE_N to UniversalKeyCode('N'),
            SDL_SCANCODE_O to UniversalKeyCode('O'),
            SDL_SCANCODE_P to UniversalKeyCode('P'),
            SDL_SCANCODE_Q to UniversalKeyCode('Q'),
            SDL_SCANCODE_R to UniversalKeyCode('R'),
            SDL_SCANCODE_S to UniversalKeyCode('S'),
            SDL_SCANCODE_T to UniversalKeyCode('T'),
            SDL_SCANCODE_U to UniversalKeyCode('U'),
            SDL_SCANCODE_V to UniversalKeyCode('V'),
            SDL_SCANCODE_W to UniversalKeyCode('W'),
            SDL_SCANCODE_X to UniversalKeyCode('X'),
            SDL_SCANCODE_Y to UniversalKeyCode('Y'),
            SDL_SCANCODE_Z to UniversalKeyCode('Z'),

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