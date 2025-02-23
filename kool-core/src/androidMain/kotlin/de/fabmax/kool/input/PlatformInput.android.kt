package de.fabmax.kool.input

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configAndroid
import de.fabmax.kool.platform.KoolContextAndroid
import de.fabmax.kool.util.logD

internal actual fun PlatformInput(): PlatformInput = PlatformInputAndroid

private typealias KoolKeyEvent = de.fabmax.kool.input.KeyEvent

object PlatformInputAndroid : PlatformInput, View.OnTouchListener, View.OnKeyListener {

    private val tmpCoords = MotionEvent.PointerCoords()

    override fun setCursorMode(cursorMode: CursorMode) { }

    override fun applyCursorShape(cursorShape: CursorShape) { }

    override fun requestKeyboard() {
        logD { "show keyboard" }
        val ctx = (KoolSystem.requireContext() as KoolContextAndroid)
        ctx.surfaceView.post {
            val imm = KoolSystem.configAndroid.appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ctx.surfaceView.requestFocus()
            imm.showSoftInput(ctx.surfaceView, 0)
        }
    }

    override fun hideKeyboard() {
        logD { "hide keyboard" }
        val ctx = (KoolSystem.requireContext() as KoolContextAndroid)
        ctx.surfaceView.post {
            val imm = KoolSystem.configAndroid.appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ctx.surfaceView.requestFocus()
            @Suppress("DEPRECATION")
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val pointerId = event.getPointerId(event.actionIndex)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                event.getPointerCoords(0, tmpCoords)
                PointerInput.handleTouchStart(pointerId, tmpCoords.x, tmpCoords.y)
            }
            MotionEvent.ACTION_UP -> {
                event.getPointerCoords(0, tmpCoords)
                PointerInput.handleTouchEnd(pointerId)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                event.getPointerCoords(event.actionIndex, tmpCoords)
                PointerInput.handleTouchStart(pointerId, tmpCoords.x, tmpCoords.y)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                PointerInput.handleTouchEnd(pointerId)
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    event.getPointerCoords(i, tmpCoords)
                    val ptrId = event.getPointerId(i)
                    PointerInput.handleTouchMove(ptrId, tmpCoords.x, tmpCoords.y)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                PointerInput.handleTouchCancel(pointerId)
            }
        }
        return true
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        logD { "code: $keyCode (${keyCode.toChar()}), event: $event" }
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }

        val action = when (event.action) {
            KeyEvent.ACTION_DOWN -> KeyboardInput.KEY_EV_DOWN
            KeyEvent.ACTION_UP -> KeyboardInput.KEY_EV_UP
            else -> -1
        }
        if (action != -1) {
            val univKeyCode = KEY_CODE_MAP[event.keyCode] ?: UniversalKeyCode(event.keyCode)
            val localKeyCode = LocalKeyCode(univKeyCode.code)
            var keyMod = 0

            if (event.metaState and KeyEvent.META_ALT_ON != 0) {
                keyMod = keyMod or KeyboardInput.KEY_MOD_ALT
            }
            if (event.metaState and KeyEvent.META_CTRL_ON != 0) {
                keyMod = keyMod or KeyboardInput.KEY_MOD_CTRL
            }
            if (event.metaState and KeyEvent.META_SHIFT_ON != 0) {
                keyMod = keyMod or KeyboardInput.KEY_MOD_SHIFT
            }
            if (event.metaState and KeyEvent.META_META_ON != 0) {
                keyMod = keyMod or KeyboardInput.KEY_MOD_SUPER
            }
            KeyboardInput.handleKeyEvent(KoolKeyEvent(univKeyCode, localKeyCode, action, keyMod))

            if (action == KeyboardInput.KEY_EV_DOWN && event.unicodeChar != 0) {
                KeyboardInput.handleCharTyped(event.unicodeChar.toChar())
            }
        }
        return true
    }

    private val KEY_CODE_MAP: Map<Int, KeyCode> = mutableMapOf(
        KeyEvent.KEYCODE_CTRL_LEFT to KeyboardInput.KEY_CTRL_LEFT,
        KeyEvent.KEYCODE_CTRL_RIGHT to KeyboardInput.KEY_CTRL_RIGHT,
        KeyEvent.KEYCODE_SHIFT_LEFT to KeyboardInput.KEY_SHIFT_LEFT,
        KeyEvent.KEYCODE_SHIFT_RIGHT to KeyboardInput.KEY_SHIFT_RIGHT,
        KeyEvent.KEYCODE_ALT_LEFT to KeyboardInput.KEY_ALT_LEFT,
        KeyEvent.KEYCODE_ALT_RIGHT to KeyboardInput.KEY_ALT_RIGHT,
        KeyEvent.KEYCODE_META_LEFT to KeyboardInput.KEY_SUPER_LEFT,
        KeyEvent.KEYCODE_META_RIGHT to KeyboardInput.KEY_SUPER_RIGHT,
        KeyEvent.KEYCODE_ESCAPE to KeyboardInput.KEY_ESC,
        KeyEvent.KEYCODE_ENTER to KeyboardInput.KEY_ENTER,
        KeyEvent.KEYCODE_NUMPAD_ENTER to KeyboardInput.KEY_NP_ENTER,
        KeyEvent.KEYCODE_NUMPAD_DIVIDE to KeyboardInput.KEY_NP_DIV,
        KeyEvent.KEYCODE_NUMPAD_MULTIPLY to KeyboardInput.KEY_NP_MUL,
        KeyEvent.KEYCODE_NUMPAD_ADD to KeyboardInput.KEY_NP_PLUS,
        KeyEvent.KEYCODE_NUMPAD_SUBTRACT to KeyboardInput.KEY_NP_MINUS,
        KeyEvent.KEYCODE_NUMPAD_DOT to KeyboardInput.KEY_NP_DECIMAL,
        KeyEvent.KEYCODE_DEL to KeyboardInput.KEY_BACKSPACE,
        KeyEvent.KEYCODE_TAB to KeyboardInput.KEY_TAB,
        KeyEvent.KEYCODE_FORWARD_DEL to KeyboardInput.KEY_DEL,
        KeyEvent.KEYCODE_INSERT to KeyboardInput.KEY_INSERT,
        KeyEvent.KEYCODE_MOVE_HOME to KeyboardInput.KEY_HOME,
        KeyEvent.KEYCODE_MOVE_END to KeyboardInput.KEY_END,
        KeyEvent.KEYCODE_PAGE_UP to KeyboardInput.KEY_PAGE_UP,
        KeyEvent.KEYCODE_PAGE_DOWN to KeyboardInput.KEY_PAGE_DOWN,
        KeyEvent.KEYCODE_DPAD_LEFT to KeyboardInput.KEY_CURSOR_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT to KeyboardInput.KEY_CURSOR_RIGHT,
        KeyEvent.KEYCODE_DPAD_UP to KeyboardInput.KEY_CURSOR_UP,
        KeyEvent.KEYCODE_DPAD_DOWN to KeyboardInput.KEY_CURSOR_DOWN,
        KeyEvent.KEYCODE_F1 to KeyboardInput.KEY_F1,
        KeyEvent.KEYCODE_F2 to KeyboardInput.KEY_F2,
        KeyEvent.KEYCODE_F3 to KeyboardInput.KEY_F3,
        KeyEvent.KEYCODE_F4 to KeyboardInput.KEY_F4,
        KeyEvent.KEYCODE_F5 to KeyboardInput.KEY_F5,
        KeyEvent.KEYCODE_F6 to KeyboardInput.KEY_F6,
        KeyEvent.KEYCODE_F7 to KeyboardInput.KEY_F7,
        KeyEvent.KEYCODE_F8 to KeyboardInput.KEY_F8,
        KeyEvent.KEYCODE_F9 to KeyboardInput.KEY_F9,
        KeyEvent.KEYCODE_F10 to KeyboardInput.KEY_F10,
        KeyEvent.KEYCODE_F11 to KeyboardInput.KEY_F11,
        KeyEvent.KEYCODE_F12 to KeyboardInput.KEY_F12,

        KeyEvent.KEYCODE_A to UniversalKeyCode('A'),
        KeyEvent.KEYCODE_B to UniversalKeyCode('B'),
        KeyEvent.KEYCODE_C to UniversalKeyCode('C'),
        KeyEvent.KEYCODE_D to UniversalKeyCode('D'),
        KeyEvent.KEYCODE_E to UniversalKeyCode('E'),
        KeyEvent.KEYCODE_F to UniversalKeyCode('F'),
        KeyEvent.KEYCODE_G to UniversalKeyCode('G'),
        KeyEvent.KEYCODE_H to UniversalKeyCode('H'),
        KeyEvent.KEYCODE_I to UniversalKeyCode('I'),
        KeyEvent.KEYCODE_J to UniversalKeyCode('J'),
        KeyEvent.KEYCODE_K to UniversalKeyCode('K'),
        KeyEvent.KEYCODE_L to UniversalKeyCode('L'),
        KeyEvent.KEYCODE_M to UniversalKeyCode('M'),
        KeyEvent.KEYCODE_N to UniversalKeyCode('N'),
        KeyEvent.KEYCODE_O to UniversalKeyCode('O'),
        KeyEvent.KEYCODE_P to UniversalKeyCode('P'),
        KeyEvent.KEYCODE_Q to UniversalKeyCode('Q'),
        KeyEvent.KEYCODE_R to UniversalKeyCode('R'),
        KeyEvent.KEYCODE_S to UniversalKeyCode('S'),
        KeyEvent.KEYCODE_T to UniversalKeyCode('T'),
        KeyEvent.KEYCODE_U to UniversalKeyCode('U'),
        KeyEvent.KEYCODE_V to UniversalKeyCode('V'),
        KeyEvent.KEYCODE_W to UniversalKeyCode('W'),
        KeyEvent.KEYCODE_X to UniversalKeyCode('X'),
        KeyEvent.KEYCODE_Y to UniversalKeyCode('Y'),
        KeyEvent.KEYCODE_Z to UniversalKeyCode('Z'),
    )
}