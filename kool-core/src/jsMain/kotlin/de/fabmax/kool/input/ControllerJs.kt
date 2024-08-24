package de.fabmax.kool.input

import de.fabmax.kool.platform.navigator
import de.fabmax.kool.util.logI
import kotlin.math.min

class ControllerJs(private var gamepad: Gamepad) : Controller(gamepad.index) {
    override val name: String = gamepad.id

    override val isConnected: Boolean get() = gamepad.connected
    override val isGamepad: Boolean = gamepad.mapping == "standard"
    override val buttonStates: BooleanArray
    override val axisStates: FloatArray

    init {
        if (isGamepad) {
            buttonStates = BooleanArray(STANDARD_LAYOUT_NUM_BUTTONS)
            axisStates = FloatArray(STANDARD_LAYOUT_NUM_AXES)
        } else {
            buttonStates = BooleanArray(gamepad.buttons.size)
            axisStates = FloatArray(gamepad.axes.size)
        }
        logI { "Controller connected: $name (${gamepad.axes.size} axes, ${gamepad.buttons.size} buttons, isGamepad: $isGamepad)" }
    }

    override fun updateState() {
        gamepad = navigator.getGamepads()?.get(gamepad.index) ?: return
        if (!isConnected) {
            return
        }

        if (isGamepad) {
            // use standardized mapping
            setButtonState(ControllerButton.A, gamepad.getButtonState(STD_BTN_A))
            setButtonState(ControllerButton.B, gamepad.getButtonState(STD_BTN_B))
            setButtonState(ControllerButton.X, gamepad.getButtonState(STD_BTN_X))
            setButtonState(ControllerButton.Y, gamepad.getButtonState(STD_BTN_Y))

            setButtonState(ControllerButton.DPAD_LEFT, gamepad.getButtonState(STD_BTN_DPAD_LEFT))
            setButtonState(ControllerButton.DPAD_RIGHT, gamepad.getButtonState(STD_BTN_DPAD_RIGHT))
            setButtonState(ControllerButton.DPAD_UP, gamepad.getButtonState(STD_BTN_DPAD_UP))
            setButtonState(ControllerButton.DPAD_DOWN, gamepad.getButtonState(STD_BTN_DPAD_DOWN))

            setButtonState(ControllerButton.SHOULDER_LEFT, gamepad.getButtonState(STD_BTN_SHOULDER_LEFT))
            setButtonState(ControllerButton.SHOULDER_RIGHT, gamepad.getButtonState(STD_BTN_SHOULDER_RIGHT))
            setButtonState(ControllerButton.THUMB_LEFT, gamepad.getButtonState(STD_BTN_THUMB_LEFT))
            setButtonState(ControllerButton.THUMB_RIGHT, gamepad.getButtonState(STD_BTN_THUMB_RIGHT))

            setButtonState(ControllerButton.START, gamepad.getButtonState(STD_BTN_START))
            setButtonState(ControllerButton.BACK, gamepad.getButtonState(STD_BTN_BACK))
            setButtonState(ControllerButton.GUIDE, gamepad.getButtonState(STD_BTN_GUIDE))

            setAxisState(ControllerAxis.LEFT_X, gamepad.axes[STD_AX_LEFT_X].toFloat())
            setAxisState(ControllerAxis.LEFT_Y, gamepad.axes[STD_AX_LEFT_Y].toFloat())
            setAxisState(ControllerAxis.RIGHT_X, gamepad.axes[STD_AX_RIGHT_X].toFloat())
            setAxisState(ControllerAxis.RIGHT_Y, gamepad.axes[STD_AX_RIGHT_Y].toFloat())
            setAxisState(ControllerAxis.TRIGGER_LEFT, gamepad.buttons[STD_BTN_TRIGGER_LEFT].value.toFloat())
            setAxisState(ControllerAxis.TRIGGER_RIGHT, gamepad.buttons[STD_BTN_TRIGGER_RIGHT].value.toFloat())

        } else {
            for (i in 0 until min(buttonStates.size, gamepad.buttons.size)) {
                buttonStates[i] = gamepad.buttons[i].pressed
            }
            for (i in 0 until min(axisStates.size, gamepad.axes.size)) {
                axisStates[i] = gamepad.axes[i].toFloat()
            }
        }
    }

    private fun Gamepad.getButtonState(index: Int): Boolean {
        return buttons.getOrNull(index)?.pressed == true
    }

    companion object {
        // standard input mapping
        //  https://w3c.github.io/gamepad/#dfn-standard-gamepad

        private const val STD_BTN_A = 0
        private const val STD_BTN_B = 1
        private const val STD_BTN_X = 2
        private const val STD_BTN_Y = 3

        private const val STD_BTN_SHOULDER_LEFT = 4
        private const val STD_BTN_SHOULDER_RIGHT = 5
        private const val STD_BTN_TRIGGER_LEFT = 6
        private const val STD_BTN_TRIGGER_RIGHT = 7

        private const val STD_BTN_BACK = 8
        private const val STD_BTN_START = 9
        private const val STD_BTN_GUIDE = 16

        private const val STD_BTN_THUMB_LEFT = 10
        private const val STD_BTN_THUMB_RIGHT = 11

        private const val STD_BTN_DPAD_UP = 12
        private const val STD_BTN_DPAD_DOWN = 13
        private const val STD_BTN_DPAD_LEFT = 14
        private const val STD_BTN_DPAD_RIGHT = 15

        private const val STD_AX_LEFT_X = 0
        private const val STD_AX_LEFT_Y = 1
        private const val STD_AX_RIGHT_X = 2
        private const val STD_AX_RIGHT_Y = 3
    }
}