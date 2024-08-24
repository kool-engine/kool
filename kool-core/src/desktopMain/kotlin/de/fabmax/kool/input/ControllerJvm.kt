package de.fabmax.kool.input

import de.fabmax.kool.util.logI
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGamepadState
import kotlin.math.min

class ControllerJvm(id: Int) : Controller(id) {
    override var isConnected: Boolean = true
    override val isGamepad = glfwJoystickIsGamepad(id)
    override val name = if (isGamepad) {
        glfwGetGamepadName(id) ?: "unknown-gamepad"
    } else {
        glfwGetJoystickName(id) ?: "unknown"
    }

    override val buttonStates: BooleanArray
    override val axisStates: FloatArray

    private val gamepadState = GLFWGamepadState.calloc()

    init {
        if (isGamepad) {
            buttonStates = BooleanArray(15)
            axisStates = FloatArray(6)
        } else {
            buttonStates = BooleanArray(glfwGetJoystickButtons(id)?.capacity() ?: 0)
            axisStates = FloatArray(glfwGetJoystickAxes(id)?.capacity() ?: 0)
        }
        logI { "Controller connected: $name (${axisStates.size} axes, ${buttonStates.size} buttons, isGamepad: $isGamepad)" }
    }

    override fun updateState() {
        if (isGamepad && glfwGetGamepadState(id, gamepadState)) {
            buttonStates[ControllerButton.A.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.B.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.X.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.Y.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y) == GLFW_PRESS.toByte()

            buttonStates[ControllerButton.DPAD_LEFT.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.DPAD_RIGHT.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.DPAD_UP.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.DPAD_DOWN.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW_PRESS.toByte()

            buttonStates[ControllerButton.SHOULDER_LEFT.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.SHOULDER_RIGHT.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.THUMB_LEFT.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.THUMB_RIGHT.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB) == GLFW_PRESS.toByte()

            buttonStates[ControllerButton.START.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_START) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.BACK.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK) == GLFW_PRESS.toByte()
            buttonStates[ControllerButton.GUIDE.defaultIndex] = gamepadState.buttons(GLFW_GAMEPAD_BUTTON_GUIDE) == GLFW_PRESS.toByte()

            axisStates[ControllerAxis.LEFT_X.defaultIndex] = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X)
            axisStates[ControllerAxis.LEFT_Y.defaultIndex] = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y)
            axisStates[ControllerAxis.RIGHT_X.defaultIndex] = gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_X)
            axisStates[ControllerAxis.RIGHT_X.defaultIndex] = gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_X)
            axisStates[ControllerAxis.LEFT_TRIGGER.defaultIndex] = gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER)
            axisStates[ControllerAxis.RIGHT_TRIGGER.defaultIndex] = gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)

        } else {
            glfwGetJoystickButtons(id)?.let { buttons ->
                for (i in 0 until min(buttonStates.size, buttons.capacity())) {
                    buttonStates[i] = buttons[i] == GLFW_PRESS.toByte()
                }
            }
            glfwGetJoystickAxes(id)?.let { axes ->
                for (i in 0 until min(axisStates.size, axes.capacity())) {
                    axisStates[i] = axes[i]
                }
            }
        }
    }

    override fun onDisconnect() {
        isConnected = false
        super.onDisconnect()
    }
}