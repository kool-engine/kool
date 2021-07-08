package de.fabmax.kool.platform

import de.fabmax.kool.InputManager
import de.fabmax.kool.KeyCode
import de.fabmax.kool.LocalKeyCode
import de.fabmax.kool.UniversalKeyCode
import de.fabmax.kool.math.MutableVec2d
import de.fabmax.kool.util.logI
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

class JsInputManager(private val canvas: HTMLCanvasElement, private val props: JsContext.InitProps) : InputManager() {

    private val pointerLockState = PointerLockState(canvas)
    private val virtualPointerPos = MutableVec2d()

    override var cursorMode: CursorMode
        get() = pointerLockState.cursorMode
        set(value) { pointerLockState.cursorMode = value }

    init {
        installInputHandlers()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun pointerMovementX(ev: MouseEvent) = js("ev.movementX") as Double

    @Suppress("UNUSED_PARAMETER")
    private fun pointerMovementY(ev: MouseEvent) = js("ev.movementY") as Double

    private fun installInputHandlers() {
        // install mouse handlers
        canvas.onmousemove = { ev ->
            val bounds = canvas.getBoundingClientRect()
            if (pointerLockState.hasPointerLock) {
                // on active pointer lock, mouse event position is constant and only deltas are reported
                //  -> use deltas to compute a virtual unbounded pointer position
                virtualPointerPos.x += pointerMovementX(ev)
                virtualPointerPos.y += pointerMovementY(ev)
            } else {
                virtualPointerPos.x = (ev.clientX - bounds.left)
                virtualPointerPos.y = (ev.clientY - bounds.top)
            }
            handleMouseMove(virtualPointerPos.x, virtualPointerPos.y)
        }
        canvas.onmousedown = { ev ->
            pointerLockState.checkLockState()
            handleMouseButtonStates(ev.buttons.toInt())
        }
        canvas.onmouseup = { ev ->
            handleMouseButtonStates(ev.buttons.toInt())
        }
        canvas.onmouseleave = { handleMouseExit() }
        canvas.onwheel = { ev ->
            // scroll amount is browser dependent, try to norm it to roughly 1.0 ticks per mouse
            // scroll wheel tick
            var ticks = -ev.deltaY.toFloat() / 3.0
            if (ev.deltaMode == 0) {
                // scroll delta is specified in pixels...
                ticks /= 30.0
            }
            handleMouseScroll(ticks)
            ev.preventDefault()
        }

        document.addEventListener("pointerlockchange", { pointerLockState.onPointerLockChange(canvas) }, false)

        // install touch handlers
        canvas.addEventListener("touchstart", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                handleTouchStart(touch.identifier, touch.elementX, touch.elementY)
            }
        }, false)
        canvas.addEventListener("touchend", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                handleTouchEnd(touch.identifier)
            }
        }, false)
        canvas.addEventListener("touchcancel", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                handleTouchCancel(touch.identifier)
            }
        }, false)
        canvas.addEventListener("touchmove", { ev ->
            ev.preventDefault()
            val changedTouches = (ev as TouchEvent).changedTouches
            for (i in 0 until changedTouches.length) {
                val touch = changedTouches.item(i)
                handleTouchMove(touch.identifier, touch.elementX, touch.elementY)
            }
        }, false)

        document.onkeydown = { ev -> handleKeyDown(ev) }
        document.onkeyup = { ev -> handleKeyUp(ev) }
    }

    private fun handleKeyDown(ev: KeyboardEvent) {
        val keyCode = ev.toKeyCode()
        val localKeyCode = ev.toLocalKeyCode()
        if (keyCode.code != 0 || localKeyCode.code != 0) {
            var mods = 0
            if (ev.altKey) { mods = mods or KEY_MOD_ALT }
            if (ev.ctrlKey) { mods = mods or KEY_MOD_CTRL }
            if (ev.shiftKey) { mods = mods or KEY_MOD_SHIFT }
            if (ev.metaKey) { mods = mods or KEY_MOD_SUPER }

            var event = KEY_EV_DOWN
            if (ev.repeat) {
                event = event or KEY_EV_REPEATED
            }
            keyEvent(KeyEvent(keyCode, localKeyCode, event, mods))
        }
        if (ev.key.length == 1) {
            charTyped(ev.key[0])
        }

        if (!props.excludedKeyCodes.contains(ev.code)) {
            ev.preventDefault()
        }
    }

    private fun handleKeyUp(ev: KeyboardEvent) {
        val keyCode = ev.toKeyCode()
        val localKeyCode = ev.toLocalKeyCode()
        if (keyCode.code != 0 || localKeyCode.code != 0) {
            var mods = 0
            if (ev.altKey) { mods = mods or KEY_MOD_ALT }
            if (ev.ctrlKey) { mods = mods or KEY_MOD_CTRL }
            if (ev.shiftKey) { mods = mods or KEY_MOD_SHIFT }
            if (ev.metaKey) { mods = mods or KEY_MOD_SUPER }
            keyEvent(KeyEvent(keyCode, localKeyCode, KEY_EV_UP, mods))
        }

        if (!props.excludedKeyCodes.contains(ev.code)) {
            ev.preventDefault()
        }
    }

    private fun KeyboardEvent.toLocalKeyCode(): KeyCode {
        return KEY_CODE_MAP[code] ?: when (key.length) {
            1 -> LocalKeyCode(key[0].uppercaseChar().code)
            else -> LocalKeyCode(0)
        }
    }

    private fun KeyboardEvent.toKeyCode(): KeyCode {
        return KEY_CODE_MAP[code] ?: when (key.length) {
            1 -> UniversalKeyCode(key[0].uppercaseChar().code)
            else -> UniversalKeyCode(0)
        }
    }

    companion object {
        val KEY_CODE_MAP: Map<String, KeyCode> = mutableMapOf(
            "ControlLeft" to KEY_CTRL_LEFT,
            "ControlRight" to KEY_CTRL_RIGHT,
            "ShiftLeft" to KEY_SHIFT_LEFT,
            "ShiftRight" to KEY_SHIFT_RIGHT,
            "AltLeft" to KEY_ALT_LEFT,
            "AltRight" to KEY_ALT_RIGHT,
            "MetaLeft" to KEY_SUPER_LEFT,
            "MetaRight" to KEY_SUPER_RIGHT,
            "Escape" to KEY_ESC,
            "ContextMenu" to KEY_MENU,
            "Enter" to KEY_ENTER,
            "NumpadEnter" to KEY_NP_ENTER,
            "NumpadDivide" to KEY_NP_DIV,
            "NumpadMultiply" to KEY_NP_MUL,
            "NumpadAdd" to KEY_NP_PLUS,
            "NumpadSubtract" to KEY_NP_MINUS,
            "Backspace" to KEY_BACKSPACE,
            "Tab" to KEY_TAB,
            "Delete" to KEY_DEL,
            "Insert" to KEY_INSERT,
            "Home" to KEY_HOME,
            "End" to KEY_END,
            "PageUp" to KEY_PAGE_UP,
            "PageDown" to KEY_PAGE_DOWN,
            "ArrowLeft" to KEY_CURSOR_LEFT,
            "ArrowRight" to KEY_CURSOR_RIGHT,
            "ArrowUp" to KEY_CURSOR_UP,
            "ArrowDown" to KEY_CURSOR_DOWN,
            "F1" to KEY_F1,
            "F2" to KEY_F2,
            "F3" to KEY_F3,
            "F4" to KEY_F4,
            "F5" to KEY_F5,
            "F6" to KEY_F6,
            "F7" to KEY_F7,
            "F8" to KEY_F8,
            "F9" to KEY_F9,
            "F10" to KEY_F10,
            "F11" to KEY_F11,
            "F12" to KEY_F12,
            "Space" to UniversalKeyCode(' ')
        )
    }

    private inner class PointerLockState(val canvas: HTMLCanvasElement) {
        var hasPointerLock = false
        var isApiExitRequest = false

        var cursorMode = CursorMode.NORMAL
            set(value) {
                field = value
                when (value) {
                    CursorMode.NORMAL -> exitPointerLock()
                    CursorMode.LOCKED -> requestPointerLock(canvas)
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
                keyEvent(KeyEvent(KEY_ESC, KEY_EV_DOWN, 0))
                keyEvent(KeyEvent(KEY_ESC, KEY_EV_UP, 0))
            }
            isApiExitRequest = false
        }

        fun checkLockState() {
            if (cursorMode == CursorMode.LOCKED && !hasPointerLock) {
                // previous attempt to requestPointerLock() has failed, re-request it on user interaction
                requestPointerLock(canvas)
            }
        }
    }
}