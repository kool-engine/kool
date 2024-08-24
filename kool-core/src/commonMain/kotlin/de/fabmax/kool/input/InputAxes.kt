package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.checkIsNotReleased
import kotlin.math.abs
import kotlin.math.max

open class InputAxes(
    ctx: KoolContext,
    val inputHandler: InputStack.InputHandler = InputStack.defaultInputHandler,
    fixedController: Controller? = null
) : BaseReleasable(), ControllerInput.ConnectionListener {
    private val axesList = mutableListOf<Axis>()
    private val axes = mutableMapOf<String, Axis>()

    private var controller: Controller? = fixedController

    private val updateAxes: ((KoolContext) -> Unit) = {
        checkIsNotReleased()
        for (i in axesList.indices) {
            axesList[i].updateAxisState(Time.deltaT)
        }
    }

    init {
        ctx.onRender += updateAxes
        if (fixedController == null) {
            ControllerInput.connectionListeners += this
            controller = ControllerInput.findDefaultController()
        }
    }

    fun registerAxis(name: String, vararg keyCodes: KeyCode): Axis = registerAxis(name) {
        setPositiveKeys(*keyCodes)
    }

    fun registerAxis(name: String, posKeyCodes: Set<KeyCode>, negKeyCodes: Set<KeyCode>): Axis = registerAxis(name) {
        this.posKeyCodes += posKeyCodes
        this.negKeyCodes += negKeyCodes
    }

    fun registerAxis(name: String, block: AxisBuilder.() -> Unit): Axis {
        val builder = AxisBuilder(name)
        builder.block()
        val axis = builder.build()
        axes[name] = axis
        axesList += axis
        return axis
    }

    operator fun get(name: String): Axis? = axes[name]

    fun analog(name: String): Float = axes[name]?.analog ?: 0f

    fun digital(name: String): Boolean = axes[name]?.isPositive == true

    override fun release() {
        KoolSystem.requireContext().onRender -= updateAxes
        ControllerInput.connectionListeners -= this
        axesList.forEach { ax -> ax.release() }
        axesList.clear()
        axes.clear()
        super.release()
    }

    override fun onControllerConnected(controller: Controller) {
        val current = this.controller
        var newController = current
        if (current == null) {
            // anything is better than nothing
            newController = controller
        } else if (current.isGamepad) {
            // existing controller already offers standardized input, won't get better than this
            newController = current
        } else if (controller.isGamepad) {
            // new controller offers standardized input, won't get better than this
            newController = controller
        } else if (controller.axisStates.size >= current.axisStates.size && controller.buttonStates.size >= current.buttonStates.size) {
            // more inputs are probably better?
            newController = controller
        }

        if (newController != current) {
            this.controller = newController
            axesList.forEach { it.setController(newController) }
        }
    }

    override fun onControllerDisconnected(controller: Controller) {
        if (this.controller == controller) {
            this.controller = ControllerInput.findDefaultController()
            axesList.forEach { it.setController(this.controller) }
        }
    }

    inner class AxisBuilder(var name: String) {
        var maxValue = 1f
        var centerValue = 0f
        var minValue = -1f

        var deadZone = 0.05f
        var analogRiseTime = 0.1f
        var analogFallTime = 0.1f
        var buttonRiseTime = 0.3f
        var buttonFallTime = 0.3f
        var buttonMin = -1f
        var buttonMax = 1f

        val posKeyCodes = mutableSetOf<KeyCode>()
        val negKeyCodes = mutableSetOf<KeyCode>()
        val posControllerButtons = mutableSetOf<ControllerButton>()
        val negControllerButtons = mutableSetOf<ControllerButton>()
        val controllerAxes = mutableSetOf<ControllerAxisMapping>()

        fun setControllerAxes(vararg axes: ControllerAxisMapping) {
            controllerAxes.clear()
            controllerAxes += axes
        }

        fun addControllerAxis(
            axis: ControllerAxis,
            isInverted: Boolean = false,
            deadZone: Float = 0.025f,
            axisIdleValue: Float = 0f,
            easing: Easing.Easing = Easing.linear
        ) {
            controllerAxes += ControllerAxisMapping(axis, isInverted, deadZone, axisIdleValue, easing)
        }

        fun setAnalogRiseFallTime(time: Float) {
            buttonRiseTime = time
            buttonFallTime = time
        }

        fun setPositiveKeys(vararg keys: KeyCode) {
            posKeyCodes.clear()
            posKeyCodes += keys
        }

        fun setNegativeKeys(vararg keys: KeyCode) {
            negKeyCodes.clear()
            negKeyCodes += keys
        }

        fun setPositiveControllerButtons(vararg buttons: ControllerButton) {
            posControllerButtons.clear()
            posControllerButtons += buttons
        }

        fun setNegativeControllerButtons(vararg buttons: ControllerButton) {
            negControllerButtons.clear()
            negControllerButtons += buttons
        }

        fun setButtonRiseFallTime(time: Float) {
            buttonRiseTime = time
            buttonFallTime = time
        }

        fun setButtonRange(min: Float, max: Float) {
            buttonMin = min
            buttonMax = max
        }

        fun build(): Axis = Axis(name, this).apply {
            controller?.let { setController(it) }
        }
    }

    data class ControllerAxisMapping(
        val axis: ControllerAxis,
        val isInverted: Boolean = false,
        val deadZone: Float = 0.05f,
        val axisIdleValue: Float = 0f,
        val easing: Easing.Easing = Easing.linear
    ) {
        fun getMappedValue(inputVal: Float): Float {
            val posRange = 1f - axisIdleValue
            val negRange = 1f + axisIdleValue

            var mapped = when {
                inputVal > axisIdleValue -> (inputVal - axisIdleValue) / posRange
                inputVal < axisIdleValue -> (inputVal - axisIdleValue) / negRange
                else -> 0f
            }
            if (mapped > deadZone) {
                mapped = easing.eased((mapped - deadZone) / (1f - deadZone))
            } else if (mapped < -deadZone) {
                mapped = -easing.eased(-((mapped + deadZone) / (1f - deadZone)))
            } else {
                mapped = 0f
            }
            return if (isInverted) -mapped else mapped
        }
    }

    inner class Axis(val name: String, builder: AxisBuilder) {
        private var emulatedAnalog = 0f
        var analog: Float = 0f
            private set

        val isPositive: Boolean
            get() = analog > 0f
        val isNegative: Boolean
            get() = analog < 0f
        val isCenter: Boolean
            get() = analog == 0f

        var isPositiveButtonPressed = false; private set
        var isNegativeButtonPressed = false; private set

        val controllerAxes = builder.controllerAxes.toList()
        val posKeyCodes = builder.posKeyCodes.toList()
        val negKeyCodes = builder.negKeyCodes.toList()
        val posControllerButtons = builder.posControllerButtons.toList()
        val negControllerButtons = builder.negControllerButtons.toList()

        var analogRiseTime = builder.analogRiseTime
        var analogFallTime = builder.analogFallTime
        var buttonRiseTime = builder.buttonRiseTime
        var buttonFallTime = builder.buttonFallTime
        var buttonMin = builder.buttonMin
        var buttonMax = builder.buttonMax

        private var usedController: Controller? = null
        private val keyListeners = mutableListOf<InputStack.SimpleKeyListener>()
        private val controllerButtonListenerPos = Controller.ButtonListener { _, newState -> isPositiveButtonPressed = newState }
        private val controllerButtonListenerNeg = Controller.ButtonListener { _, newState -> isNegativeButtonPressed = newState }

        init {
            for (key in posKeyCodes) {
                keyListeners += inputHandler.addKeyListener(key, name, InputStack.KEY_FILTER_ALL, this::processPositiveKeyInputEvent)
            }
            for (key in negKeyCodes) {
                keyListeners += inputHandler.addKeyListener(key, name, InputStack.KEY_FILTER_ALL, this::processNegativeKeyInputEvent)
            }
        }

        fun release() {
            keyListeners.forEach { inputHandler.removeKeyListener(it) }
            usedController?.let {
                it.removeButtonListener(controllerButtonListenerPos)
                it.removeButtonListener(controllerButtonListenerNeg)
            }
        }

        internal fun setController(controller: Controller?) {
            usedController?.let {
                it.removeButtonListener(controllerButtonListenerPos)
                it.removeButtonListener(controllerButtonListenerNeg)
            }
            usedController = controller
            usedController?.let {
                for (button in posControllerButtons) {
                    it.addButtonListener(button, controllerButtonListenerPos)
                }
                for (button in negControllerButtons) {
                    it.addButtonListener(button, controllerButtonListenerNeg)
                }
            }
        }

        internal fun processPositiveKeyInputEvent(ev: KeyEvent) {
            if (ev.isPressed || ev.isRepeated) {
                isPositiveButtonPressed = true
            } else if (ev.isReleased) {
                isPositiveButtonPressed = false
            }
        }

        internal fun processNegativeKeyInputEvent(ev: KeyEvent) {
            if (ev.isPressed || ev.isRepeated) {
                isNegativeButtonPressed = true
            } else if (ev.isReleased) {
                isNegativeButtonPressed = false
            }
        }

        internal fun updateAxisState(deltaT: Float) {
            val change = when {
                isPositiveButtonPressed -> deltaT / buttonRiseTime
                isNegativeButtonPressed -> deltaT / -buttonFallTime
                emulatedAnalog > 0f -> deltaT / -buttonFallTime
                emulatedAnalog < 0f -> deltaT / buttonRiseTime
                else -> 0f
            }
            emulatedAnalog = if (!isPositiveButtonPressed && !isNegativeButtonPressed && abs(change) > abs(emulatedAnalog)) {
                0f
            } else {
                (emulatedAnalog + change).clamp(buttonMin, buttonMax)
            }

            var output = emulatedAnalog
            usedController?.let { ctrl ->
                for (i in controllerAxes.indices) {
                    val axis = controllerAxes[i]
                    val axisVal = axis.getMappedValue(ctrl.getAxisState(axis.axis))
                    if (abs(axisVal) > abs(output)) {
                        output = axisVal
                    }
                }
            }

            var delta = output - analog
            if (delta < 0f && analogFallTime > 0f) {
                delta = delta.clamp(deltaT / -analogFallTime, 0f)
            }
            if (delta > 0f && analogRiseTime > 0f) {
                delta = delta.clamp(0f, deltaT / analogRiseTime)
            }
            analog += delta
        }
    }
}

class DriveAxes(
    ctx: KoolContext,
    inputHandler: InputStack.InputHandler = InputStack.defaultInputHandler
) : InputAxes(ctx, inputHandler) {

    val steerAx: Axis
    val throttleAx: Axis
    val brakeAx: Axis

    val leftRight: Float
        get() = steerAx.analog

    val throttle: Float
        get() = throttleAx.analog
    val brake: Float
        get() = brakeAx.analog
    val left: Float
        get() = max(0f, -steerAx.analog)
    val right: Float
        get() = max(0f, steerAx.analog)

    val recoverAx: Axis
    val isRecover: Boolean get() = recoverAx.isPositiveButtonPressed

    init {
        throttleAx = registerAxis("throttle") {
            addControllerAxis(ControllerAxis.TRIGGER_RIGHT)
            setPositiveKeys(KeyboardInput.KEY_CURSOR_UP, UniversalKeyCode('w'))
            setAnalogRiseFallTime(0f)
            setButtonRiseFallTime(0.2f)
        }
        brakeAx = registerAxis("brake") {
            addControllerAxis(ControllerAxis.TRIGGER_LEFT)
            setPositiveKeys(KeyboardInput.KEY_CURSOR_DOWN, UniversalKeyCode('s'))
            setAnalogRiseFallTime(0f)
            setButtonRiseFallTime(0.2f)
            setButtonRange(-1f, 0.75f)
        }
        steerAx = registerAxis("left / right") {
            addControllerAxis(ControllerAxis.LEFT_X, easing = Easing.sqr)
            addControllerAxis(ControllerAxis.RIGHT_X, easing = Easing.sqr)
            setPositiveKeys(KeyboardInput.KEY_CURSOR_RIGHT, UniversalKeyCode('d'))
            setNegativeKeys(KeyboardInput.KEY_CURSOR_LEFT, UniversalKeyCode('a'))
            setAnalogRiseFallTime(0.1f)
            setButtonRiseFallTime(0.5f)
        }
        recoverAx = registerAxis("recover vehicle") {
            setPositiveKeys(LocalKeyCode('r'))
            setPositiveControllerButtons(ControllerButton.X)
        }
    }
}

class WalkAxes(
    ctx: KoolContext,
    inputHandler: InputStack.InputHandler = InputStack.defaultInputHandler
) : InputAxes(ctx, inputHandler) {

    val forwardBackwardAx: Axis
    val leftRightAx: Axis

    val jumpAx: Axis
    val runAx: Axis
    val crouchAx: Axis

    val forwardBackward: Float
        get() = forwardBackwardAx.analog
    val leftRight: Float
        get() = leftRightAx.analog

    val forward: Float
        get() = max(0f, forwardBackwardAx.analog)
    val backward: Float
        get() = max(0f, -forwardBackwardAx.analog)
    val left: Float
        get() = max(0f, -leftRightAx.analog)
    val right: Float
        get() = max(0f, leftRightAx.analog)

    val isJump: Boolean
        get() = jumpAx.isPositiveButtonPressed
    val isRun: Boolean
        get() = runAx.isPositiveButtonPressed
    val isCrouch: Boolean
        get() = crouchAx.isPositiveButtonPressed

    val runFactor: Float
        get() = runAx.analog
    val crouchFactor: Float
        get() = crouchAx.analog

    init {
        forwardBackwardAx = registerAxis("forward / backward") {
            addControllerAxis(ControllerAxis.LEFT_Y, isInverted = true)
            setPositiveKeys(KeyboardInput.KEY_CURSOR_UP, UniversalKeyCode('w'))
            setNegativeKeys(KeyboardInput.KEY_CURSOR_DOWN, UniversalKeyCode('s'))
            setButtonRiseFallTime(0.15f)
        }
        leftRightAx = registerAxis("left / right") {
            addControllerAxis(ControllerAxis.LEFT_X)
            setPositiveKeys(KeyboardInput.KEY_CURSOR_RIGHT, UniversalKeyCode('d'))
            setNegativeKeys(KeyboardInput.KEY_CURSOR_LEFT, UniversalKeyCode('a'))
            setButtonRiseFallTime(0.15f)
        }

        jumpAx = registerAxis("jump") {
            setPositiveControllerButtons(ControllerButton.A)
            setPositiveKeys(UniversalKeyCode(' '))
            setButtonRiseFallTime(0.01f)
        }
        runAx = registerAxis("run") {
            setPositiveControllerButtons(ControllerButton.SHOULDER_RIGHT)
            setPositiveKeys(KeyboardInput.KEY_SHIFT_LEFT, KeyboardInput.KEY_SHIFT_RIGHT)
            setButtonRiseFallTime(0.5f)
        }
        crouchAx = registerAxis("crouch") {
            setPositiveControllerButtons(ControllerButton.X)
            setPositiveKeys(KeyboardInput.KEY_CTRL_LEFT, KeyboardInput.KEY_CTRL_RIGHT)
            setButtonRiseFallTime(0.5f)
        }
    }
}
