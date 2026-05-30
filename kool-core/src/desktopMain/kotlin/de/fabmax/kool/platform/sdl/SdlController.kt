package de.fabmax.kool.platform.sdl

import de.fabmax.kool.input.Controller
import de.fabmax.kool.input.ControllerAxis
import de.fabmax.kool.input.ControllerButton
import de.fabmax.kool.input.ControllerInput
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import org.lwjgl.sdl.SDLGamepad.*

internal object SdlControllers {
    private val gamepads = mutableMapOf<Int, SdlController>()

    fun gamepadEvent(gamepad: SdlEvent.Gamepad) {
        when (gamepad.type) {
            SdlEventType.GAMEPAD_ADDED -> gamepadAdded(gamepad)
            SdlEventType.GAMEPAD_REMOVED -> gamepadRemoved(gamepad)
            SdlEventType.GAMEPAD_UPDATE_COMPLETE -> {}
            else -> logW { "Unhandled gamepad event type: ${gamepad.type}" }
        }
    }

    fun gamepadButtonEvent(button: SdlEvent.GamepadButton) {
        gamepads[button.which]?.buttonEvent(button)
    }

    fun gamepadAxisEvent(axis: SdlEvent.GamepadAxis) {
        gamepads[axis.which]?.axisEvent(axis)
    }

    private fun gamepadAdded(gamepad: SdlEvent.Gamepad) {
        val handle = SDL_OpenGamepad(gamepad.which)
        val controller = SdlController(handle, gamepad.which)
        logI { "Gamepad added: ID=${gamepad.which}, name=${controller.name}" }
        gamepads[gamepad.which] = controller
        ControllerInput.addController(controller)
    }

    private fun gamepadRemoved(gamepad: SdlEvent.Gamepad) {
        logI { "Gamepad removed: ID=${gamepad.which}" }
        val controller = gamepads.remove(gamepad.which) ?: return
        SDL_CloseGamepad(controller.handle)
        ControllerInput.removeController(controller.id)
    }
}

class SdlController(internal val handle: Long, id: Int) : Controller(id) {
    override val name: String = SDL_GetGamepadName(handle) ?: "Unknown Gamepad"
    override val isGamepad: Boolean = true
    override var isConnected: Boolean = true; private set
    override val buttonStates: BooleanArray = BooleanArray(ControllerButton.entries.size)
    override val axisStates: FloatArray = FloatArray(ControllerAxis.entries.size)

    init {
        ControllerButton.entries.forEach {
            when (it) {
                ControllerButton.A -> setButtonMapping(it, 0)
                ControllerButton.B -> setButtonMapping(it, 1)
                ControllerButton.X -> setButtonMapping(it, 2)
                ControllerButton.Y -> setButtonMapping(it, 3)
                ControllerButton.DPAD_UP -> setButtonMapping(it, 4)
                ControllerButton.DPAD_DOWN -> setButtonMapping(it, 5)
                ControllerButton.DPAD_LEFT -> setButtonMapping(it, 6)
                ControllerButton.DPAD_RIGHT -> setButtonMapping(it, 7)
                ControllerButton.SHOULDER_LEFT -> setButtonMapping(it, 8)
                ControllerButton.SHOULDER_RIGHT -> setButtonMapping(it, 9)
                ControllerButton.THUMB_LEFT -> setButtonMapping(it, 10)
                ControllerButton.THUMB_RIGHT -> setButtonMapping(it, 11)
                ControllerButton.START -> setButtonMapping(it, 12)
                ControllerButton.BACK -> setButtonMapping(it, 13)
                ControllerButton.GUIDE -> setButtonMapping(it, 14)
            }
        }
        ControllerAxis.entries.forEach {
            when (it) {
                ControllerAxis.LEFT_X -> setAxisMapping(it, 0)
                ControllerAxis.LEFT_Y -> setAxisMapping(it, 1)
                ControllerAxis.RIGHT_X -> setAxisMapping(it, 2)
                ControllerAxis.RIGHT_Y -> setAxisMapping(it, 3)
                ControllerAxis.TRIGGER_LEFT -> setAxisMapping(it, 4)
                ControllerAxis.TRIGGER_RIGHT -> setAxisMapping(it, 5)
            }
        }
    }

    override fun updateState() {}

    internal fun buttonEvent(ev: SdlEvent.GamepadButton) {
        when (ev.button) {
            SDL_GAMEPAD_BUTTON_SOUTH -> setButtonState(ControllerButton.A, ev.down)
            SDL_GAMEPAD_BUTTON_EAST -> setButtonState(ControllerButton.B, ev.down)
            SDL_GAMEPAD_BUTTON_WEST -> setButtonState(ControllerButton.X, ev.down)
            SDL_GAMEPAD_BUTTON_NORTH -> setButtonState(ControllerButton.Y, ev.down)
            SDL_GAMEPAD_BUTTON_DPAD_UP -> setButtonState(ControllerButton.DPAD_UP, ev.down)
            SDL_GAMEPAD_BUTTON_DPAD_DOWN -> setButtonState(ControllerButton.DPAD_DOWN, ev.down)
            SDL_GAMEPAD_BUTTON_DPAD_LEFT -> setButtonState(ControllerButton.DPAD_LEFT, ev.down)
            SDL_GAMEPAD_BUTTON_DPAD_RIGHT -> setButtonState(ControllerButton.DPAD_RIGHT, ev.down)
            SDL_GAMEPAD_BUTTON_LEFT_SHOULDER -> setButtonState(ControllerButton.SHOULDER_LEFT, ev.down)
            SDL_GAMEPAD_BUTTON_RIGHT_SHOULDER -> setButtonState(ControllerButton.SHOULDER_RIGHT, ev.down)
            SDL_GAMEPAD_BUTTON_LEFT_STICK -> setButtonState(ControllerButton.THUMB_LEFT, ev.down)
            SDL_GAMEPAD_BUTTON_RIGHT_STICK -> setButtonState(ControllerButton.THUMB_RIGHT, ev.down)
            SDL_GAMEPAD_BUTTON_START -> setButtonState(ControllerButton.START, ev.down)
            SDL_GAMEPAD_BUTTON_BACK -> setButtonState(ControllerButton.BACK, ev.down)
            SDL_GAMEPAD_BUTTON_GUIDE -> setButtonState(ControllerButton.GUIDE, ev.down)
        }

        if (ev.button == SDL_GAMEPAD_BUTTON_DPAD_LEFT && ev.down) {
            rumble(1f, 0f, 500)
        }
        if (ev.button == SDL_GAMEPAD_BUTTON_DPAD_RIGHT && ev.down) {
            rumble(0f, 1f, 500)
        }
    }

    internal fun axisEvent(ev: SdlEvent.GamepadAxis) {
        when (ev.axis) {
            SDL_GAMEPAD_AXIS_LEFTX -> setAxisState(ControllerAxis.LEFT_X, (ev.value / 32767f).clamp(-1f, 1f))
            SDL_GAMEPAD_AXIS_LEFTY -> setAxisState(ControllerAxis.LEFT_Y, (ev.value / 32767f).clamp(-1f, 1f))
            SDL_GAMEPAD_AXIS_RIGHTX -> setAxisState(ControllerAxis.RIGHT_X, (ev.value / 32767f).clamp(-1f, 1f))
            SDL_GAMEPAD_AXIS_RIGHTY -> setAxisState(ControllerAxis.RIGHT_Y, (ev.value / 32767f).clamp(-1f, 1f))
            SDL_GAMEPAD_AXIS_LEFT_TRIGGER -> setAxisState(ControllerAxis.TRIGGER_LEFT, (ev.value / 32767f).clamp(-1f, 1f))
            SDL_GAMEPAD_AXIS_RIGHT_TRIGGER -> setAxisState(ControllerAxis.TRIGGER_RIGHT, (ev.value / 32767f).clamp(-1f, 1f))
        }
    }

    override fun rumble(intensityLow: Float, intensityHigh: Float, durationMillis: Int) {
        val intLow = (intensityLow.clamp() * 65535).toInt().toShort()
        val intHigh = (intensityHigh.clamp() * 65535).toInt().toShort()
        SDL_RumbleGamepad(handle, intLow, intHigh, durationMillis)
    }

    override fun onDisconnect() {
        isConnected = false
        super.onDisconnect()
    }
}
