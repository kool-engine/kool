package de.fabmax.kool.input

import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.logI

object ControllerInput {

    internal val _controllers = mutableMapOf<Int, Controller>()
    val controllers: Map<Int, Controller> get() = _controllers

    var defaultController: Controller? = null

    fun poll() {
        if (controllers.size == 1) {
            defaultController?.poll()
        } else if (controllers.size > 1) {
            for (ctrl in controllers.values) {
                ctrl.poll()
            }
        }
    }

    internal fun addController(controller: Controller) {
        _controllers[controller.id] = controller
        if (defaultController == null) {
            defaultController = controller
        }
    }

    internal fun removeController(controllerId: Int) {
        _controllers[controllerId]?.onDisconnect()
        _controllers -= controllerId
        if (defaultController?.id == controllerId) {
            defaultController = _controllers.keys.minOrNull()?.let { _controllers[it] }
        }
    }
}

abstract class Controller(val id: Int) {
    abstract val name: String
    abstract val isGamepad: Boolean

    abstract val buttonStates: BooleanArray
    abstract val axisStates: FloatArray

    val axisMap = mutableMapOf<ControllerAxis, Int>()
    val buttonMap = mutableMapOf<ControllerButton, Int>()
    private val buttonMonitors = mutableListOf<ButtonMonitor>()

    val buttonA: Boolean get() = getButtonState(ControllerButton.A)
    val buttonB: Boolean get() = getButtonState(ControllerButton.B)
    val buttonX: Boolean get() = getButtonState(ControllerButton.X)
    val buttonY: Boolean get() = getButtonState(ControllerButton.Y)

    val buttonDpadUp: Boolean get() = getButtonState(ControllerButton.DPAD_UP)
    val buttonDpadDown: Boolean get() = getButtonState(ControllerButton.DPAD_DOWN)
    val buttonDpadLeft: Boolean get() = getButtonState(ControllerButton.DPAD_LEFT)
    val buttonDpadRight: Boolean get() = getButtonState(ControllerButton.DPAD_RIGHT)

    val buttonShoulderLeft: Boolean get() = getButtonState(ControllerButton.SHOULDER_LEFT)
    val buttonShoulderRight: Boolean get() = getButtonState(ControllerButton.SHOULDER_RIGHT)
    val buttonThumbLeft: Boolean get() = getButtonState(ControllerButton.THUMB_LEFT)
    val buttonThumbRight: Boolean get() = getButtonState(ControllerButton.THUMB_RIGHT)

    val buttonStart: Boolean get() = getButtonState(ControllerButton.START)
    val buttonBack: Boolean get() = getButtonState(ControllerButton.BACK)
    val buttonGuide: Boolean get() = getButtonState(ControllerButton.GUIDE)

    val axisLeftX: Float get() = getAxisState(ControllerAxis.LEFT_X)
    val axisLeftY: Float get() = getAxisState(ControllerAxis.LEFT_Y)
    val axisRightX: Float get() = getAxisState(ControllerAxis.RIGHT_X)
    val axisRightY: Float get() = getAxisState(ControllerAxis.RIGHT_Y)
    val axisLeftTrigger: Float get() = getAxisState(ControllerAxis.LEFT_TRIGGER)
    val axisRightTrigger: Float get() = getAxisState(ControllerAxis.RIGHT_TRIGGER)

    protected abstract fun updateState()

    internal fun poll() {
        updateState()
        for (i in buttonMonitors.indices) {
            buttonMonitors[i].update()
        }
    }

    fun setButtonMapping(button: ControllerButton, index: Int) {
        buttonMap[button] = index
    }

    fun setAxisMapping(axis: ControllerAxis, index: Int) {
        axisMap[axis] = index
    }

    fun getButtonState(button: ControllerButton): Boolean {
        val idx = buttonMap[button] ?: button.defaultIndex
        return if (idx in buttonStates.indices) buttonStates[idx] else false
    }

    fun getAxisState(axis: ControllerAxis): Float {
        val idx = axisMap[axis] ?: axis.defaultIndex
        return if (idx in axisStates.indices) axisStates[idx] else 0f
    }

    fun addButtonListener(button: ControllerButton, listener: ButtonListener) {
        val monitor = buttonMonitors.find { it.button == button }
            ?: ButtonMonitor(button).also { buttonMonitors.add(it) }
        monitor.listeners += listener
    }

    fun removeButtonListener(listener: ButtonListener) {
        buttonMonitors.forEach { it.listeners -= listener }
        buttonMonitors.removeAll { it.listeners.isEmpty() }
    }

    open fun onDisconnect() {
        logI { "Controller disconnected: $name" }
    }

    private inner class ButtonMonitor(val button: ControllerButton) {
        val listeners = BufferedList<ButtonListener>()
        var oldState: Boolean = false

        fun update() {
            val newState = getButtonState(button)
            if (newState != oldState) {
                listeners.update()
                for (i in listeners.indices) {
                    listeners[i].buttonChanged(button, newState)
                }
                oldState = newState
            }
        }
    }

    fun interface ButtonListener {
        fun buttonChanged(button: ControllerButton, newState: Boolean)
    }
}

enum class ControllerButton(val defaultIndex: Int) {
    A(0),
    B(1),
    X(2),
    Y(3),

    DPAD_UP(4),
    DPAD_DOWN(5),
    DPAD_LEFT(6),
    DPAD_RIGHT(7),

    SHOULDER_LEFT(8),
    SHOULDER_RIGHT(9),
    THUMB_LEFT(10),
    THUMB_RIGHT(11),

    START(12),
    BACK(13),
    GUIDE(14),
}

enum class ControllerAxis(val defaultIndex: Int) {
    LEFT_X(0),
    LEFT_Y(1),
    RIGHT_X(2),
    RIGHT_Y(3),
    LEFT_TRIGGER(4),
    RIGHT_TRIGGER(5),
}
