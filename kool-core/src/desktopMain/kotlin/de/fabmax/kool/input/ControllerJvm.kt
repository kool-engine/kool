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
            buttonStates = BooleanArray(STANDARD_LAYOUT_NUM_BUTTONS)
            axisStates = FloatArray(STANDARD_LAYOUT_NUM_AXES)
        } else {
            buttonStates = BooleanArray(glfwGetJoystickButtons(id)?.capacity() ?: 0)
            axisStates = FloatArray(glfwGetJoystickAxes(id)?.capacity() ?: 0)
        }
        logI { "Controller connected: $name (${axisStates.size} axes, ${buttonStates.size} buttons, isGamepad: $isGamepad)" }
    }

    private fun setButtonState(button: ControllerButton, state: Byte) =
        setButtonState(button, state == GLFW_PRESS.toByte())

    override fun updateState() {
        if (isGamepad && glfwGetGamepadState(id, gamepadState)) {
            setButtonState(ControllerButton.A, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A))
            setButtonState(ControllerButton.B, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B))
            setButtonState(ControllerButton.X, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X))
            setButtonState(ControllerButton.Y, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y))

            setButtonState(ControllerButton.DPAD_LEFT, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT))
            setButtonState(ControllerButton.DPAD_RIGHT, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT))
            setButtonState(ControllerButton.DPAD_UP, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP))
            setButtonState(ControllerButton.DPAD_DOWN, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN))

            setButtonState(ControllerButton.SHOULDER_LEFT, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER))
            setButtonState(ControllerButton.SHOULDER_RIGHT, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER))
            setButtonState(ControllerButton.THUMB_LEFT, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB))
            setButtonState(ControllerButton.THUMB_RIGHT, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB))

            setButtonState(ControllerButton.START, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_START))
            setButtonState(ControllerButton.BACK, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK))
            setButtonState(ControllerButton.GUIDE, gamepadState.buttons(GLFW_GAMEPAD_BUTTON_GUIDE))

            setAxisState(ControllerAxis.LEFT_X, gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X))
            setAxisState(ControllerAxis.LEFT_Y, gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y))
            setAxisState(ControllerAxis.RIGHT_X, gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_X))
            setAxisState(ControllerAxis.RIGHT_Y, gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_Y))
            setAxisState(ControllerAxis.TRIGGER_LEFT, gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) * 0.5f + 0.5f)
            setAxisState(ControllerAxis.TRIGGER_RIGHT, gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) * 0.5f + 0.5f)

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