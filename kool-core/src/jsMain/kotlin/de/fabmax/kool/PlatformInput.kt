package de.fabmax.kool

import de.fabmax.kool.math.MutableVec2d
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.TouchEvent
import de.fabmax.kool.platform.elementX
import de.fabmax.kool.platform.elementY
import de.fabmax.kool.util.logI
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

internal actual object PlatformInput {

    val excludedKeyCodes = mutableSetOf("F5", "F11")

    private val virtualPointerPos = MutableVec2d()
    private var currentCursorShape = CursorShape.DEFAULT
    private var mouseButtonState = 0

    actual fun setCursorMode(cursorMode: CursorMode) {
        PointerLockState.cursorMode = cursorMode
    }

    actual fun applyCursorShape(cursorShape: CursorShape) {
        if (cursorShape != currentCursorShape) {
            JsImpl.canvas.style.cursor = when (cursorShape) {
                CursorShape.DEFAULT -> "default"
                CursorShape.TEXT -> "text"
                CursorShape.CROSSHAIR -> "crosshair"
                CursorShape.HAND -> "pointer"
                CursorShape.H_RESIZE -> "e-resize"
                CursorShape.V_RESIZE -> "n-resize"
            }
            currentCursorShape = cursorShape
        }
    }

    fun onContextCreated(ctx: JsContext) {
        installInputHandlers(ctx.canvas)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun pointerMovementX(ev: MouseEvent) = js("ev.movementX") as Double * window.devicePixelRatio

    @Suppress("UNUSED_PARAMETER")
    private fun pointerMovementY(ev: MouseEvent) = js("ev.movementY") as Double * window.devicePixelRatio

    private fun installInputHandlers(canvas: HTMLCanvasElement) {
        // install mouse handlers
        canvas.onmousemove = { ev ->
            val bounds = canvas.getBoundingClientRect()
            if (PointerLockState.hasPointerLock) {
                // on active pointer lock, mouse event position is constant and only deltas are reported
                //  -> use deltas to compute a virtual unbounded pointer position
                virtualPointerPos.x += pointerMovementX(ev)
                virtualPointerPos.y += pointerMovementY(ev)
            } else {
                virtualPointerPos.x = (ev.clientX * window.devicePixelRatio - bounds.left)
                virtualPointerPos.y = (ev.clientY * window.devicePixelRatio - bounds.top)
            }
            Input.handleMouseMove(virtualPointerPos.x, virtualPointerPos.y)
        }
        canvas.onmousedown = { ev ->
            PointerLockState.checkLockState()
            val changeMask = ev.buttons.toInt() and mouseButtonState.inv()
            mouseButtonState = ev.buttons.toInt()
            for (btn in 0..7) {
                if (changeMask and (1 shl btn) != 0) {
                    Input.handleMouseButtonState(btn, true)
                }
            }
        }
        canvas.onmouseup = { ev ->
            val changeMask = ev.buttons.toInt().inv() and mouseButtonState
            mouseButtonState = ev.buttons.toInt()
            for (btn in 0..7) {
                if (changeMask and (1 shl btn) != 0) {
                    Input.handleMouseButtonState(btn, false)
                }
            }
        }
        canvas.onmouseleave = { Input.handleMouseExit() }
        canvas.onwheel = { ev ->
            // scroll amount is browser dependent, try to norm it to roughly 1.0 ticks per mouse scroll wheel tick
            var yTicks = -ev.deltaY.toFloat() / 3.0
            var xTicks = -ev.deltaX.toFloat() / 3.0
            if (ev.deltaMode == 0) {
                // scroll delta is specified in pixels...
                yTicks /= 30.0
                xTicks /= 30.0
            }
            Input.handleMouseScroll(xTicks, yTicks)
            ev.preventDefault()
        }

        document.addEventListener("pointerlockchange", { PointerLockState.onPointerLockChange(canvas) }, false)

        // install touch handlers
        canvas.addEventListener("touchstart", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                Input.handleTouchStart(touch.identifier, touch.elementX, touch.elementY)
            }
        }, false)
        canvas.addEventListener("touchend", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                Input.handleTouchEnd(touch.identifier)
            }
        }, false)
        canvas.addEventListener("touchcancel", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                Input.handleTouchCancel(touch.identifier)
            }
        }, false)
        canvas.addEventListener("touchmove", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                Input.handleTouchMove(touch.identifier, touch.elementX, touch.elementY)
            }
        }, false)

        document.onkeydown = { ev -> handleKeyDown(ev) }
        document.onkeyup = { ev -> handleKeyUp(ev) }
    }

    private fun handleKeyDown(ev: KeyboardEvent) {
        val keyCode = ev.toKeyCode()
        val localKeyCode = ev.toLocalKeyCode()
        var mods = 0
        if (keyCode.code != 0 || localKeyCode.code != 0) {
            if (ev.altKey) { mods = mods or Input.KEY_MOD_ALT }
            if (ev.ctrlKey) { mods = mods or Input.KEY_MOD_CTRL }
            if (ev.shiftKey) { mods = mods or Input.KEY_MOD_SHIFT }
            if (ev.metaKey) { mods = mods or Input.KEY_MOD_SUPER }

            var event = Input.KEY_EV_DOWN
            if (ev.repeat) {
                event = event or Input.KEY_EV_REPEATED
            }
            Input.keyEvent(Input.KeyEvent(keyCode, localKeyCode, event, mods))
        }
        // do not issue an charType() if a modifier key is down (e.g. Ctrl+C), Shift is fine however (it's just
        // a capital letter then...)
        if (ev.key.length == 1 && (mods and Input.KEY_MOD_SHIFT.inv()) == 0) {
            Input.charTyped(ev.key[0])
        }

        if (!excludedKeyCodes.contains(ev.code)) {
            ev.preventDefault()
        }
    }

    private fun handleKeyUp(ev: KeyboardEvent) {
        val keyCode = ev.toKeyCode()
        val localKeyCode = ev.toLocalKeyCode()
        if (keyCode.code != 0 || localKeyCode.code != 0) {
            var mods = 0
            if (ev.altKey) { mods = mods or Input.KEY_MOD_ALT }
            if (ev.ctrlKey) { mods = mods or Input.KEY_MOD_CTRL }
            if (ev.shiftKey) { mods = mods or Input.KEY_MOD_SHIFT }
            if (ev.metaKey) { mods = mods or Input.KEY_MOD_SUPER }
            Input.keyEvent(Input.KeyEvent(keyCode, localKeyCode, Input.KEY_EV_UP, mods))
        }

        if (!excludedKeyCodes.contains(ev.code)) {
            ev.preventDefault()
        }
    }

    private fun KeyboardEvent.toLocalKeyCode(): LocalKeyCode {
        val specialKey = KEY_CODE_MAP[code]
        return if (specialKey != null) {
            LocalKeyCode(specialKey.code, specialKey.name)
        } else {
            when (key.length) {
                1 -> LocalKeyCode(key[0].uppercaseChar().code)
                else -> LocalKeyCode(0)
            }
        }
    }

    private fun KeyboardEvent.toKeyCode(): UniversalKeyCode {
        return KEY_CODE_MAP[code] ?: when (key.length) {
            1 -> UniversalKeyCode(key[0].uppercaseChar().code)
            else -> UniversalKeyCode(0)
        }
    }

    val KEY_CODE_MAP: Map<String, UniversalKeyCode> = mutableMapOf(
        "ControlLeft" to Input.KEY_CTRL_LEFT,
        "ControlRight" to Input.KEY_CTRL_RIGHT,
        "ShiftLeft" to Input.KEY_SHIFT_LEFT,
        "ShiftRight" to Input.KEY_SHIFT_RIGHT,
        "AltLeft" to Input.KEY_ALT_LEFT,
        "AltRight" to Input.KEY_ALT_RIGHT,
        "MetaLeft" to Input.KEY_SUPER_LEFT,
        "MetaRight" to Input.KEY_SUPER_RIGHT,
        "Escape" to Input.KEY_ESC,
        "ContextMenu" to Input.KEY_MENU,
        "Enter" to Input.KEY_ENTER,
        "NumpadEnter" to Input.KEY_NP_ENTER,
        "NumpadDivide" to Input.KEY_NP_DIV,
        "NumpadMultiply" to Input.KEY_NP_MUL,
        "NumpadAdd" to Input.KEY_NP_PLUS,
        "NumpadSubtract" to Input.KEY_NP_MINUS,
        "Backspace" to Input.KEY_BACKSPACE,
        "Tab" to Input.KEY_TAB,
        "Delete" to Input.KEY_DEL,
        "Insert" to Input.KEY_INSERT,
        "Home" to Input.KEY_HOME,
        "End" to Input.KEY_END,
        "PageUp" to Input.KEY_PAGE_UP,
        "PageDown" to Input.KEY_PAGE_DOWN,
        "ArrowLeft" to Input.KEY_CURSOR_LEFT,
        "ArrowRight" to Input.KEY_CURSOR_RIGHT,
        "ArrowUp" to Input.KEY_CURSOR_UP,
        "ArrowDown" to Input.KEY_CURSOR_DOWN,
        "F1" to Input.KEY_F1,
        "F2" to Input.KEY_F2,
        "F3" to Input.KEY_F3,
        "F4" to Input.KEY_F4,
        "F5" to Input.KEY_F5,
        "F6" to Input.KEY_F6,
        "F7" to Input.KEY_F7,
        "F8" to Input.KEY_F8,
        "F9" to Input.KEY_F9,
        "F10" to Input.KEY_F10,
        "F11" to Input.KEY_F11,
        "F12" to Input.KEY_F12,
        "Space" to UniversalKeyCode(' ')
    )

    private object PointerLockState {
        var hasPointerLock = false
        var isApiExitRequest = false

        var cursorMode = CursorMode.NORMAL
            set(value) {
                field = value
                when (value) {
                    CursorMode.NORMAL -> exitPointerLock()
                    CursorMode.LOCKED -> requestPointerLock(JsImpl.canvas)
                }
            }

        @Suppress("UNUSED_PARAMETER")
        fun requestPointerLock(canvas: HTMLCanvasElement) {
            if (!hasPointerLock) {
                js("canvas.requestPointerLock()")
            }
        }

        fun exitPointerLock() {
            if (hasPointerLock) {
                isApiExitRequest = true
                js("document.exitPointerLock()")
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun onPointerLockChange(canvas: HTMLCanvasElement) {
            hasPointerLock = js("document.pointerLockElement === canvas") as Boolean

            if (!hasPointerLock && !isApiExitRequest) {
                // we lost pointer lock without requesting it via api -> user requested it by hitting the esc key
                // report an esc key-event, so the application can react on it
                logI { "pointer lock exited by user" }
                Input.keyEvent(Input.KeyEvent(Input.KEY_ESC, Input.KEY_EV_DOWN, 0))
                Input.keyEvent(Input.KeyEvent(Input.KEY_ESC, Input.KEY_EV_UP, 0))
            }
            isApiExitRequest = false
        }

        fun checkLockState() {
            if (cursorMode == CursorMode.LOCKED && !hasPointerLock) {
                // previous attempt to requestPointerLock() has failed, re-request it on user interaction
                requestPointerLock(JsImpl.canvas)
            }
        }
    }
}