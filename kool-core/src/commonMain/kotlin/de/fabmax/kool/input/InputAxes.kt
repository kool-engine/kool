package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Disposable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.runDelayed
import kotlin.math.abs
import kotlin.math.max

open class InputAxes(ctx: KoolContext) : Disposable {
    private val axesList = mutableListOf<Axis>()
    private val axes = mutableMapOf<String, Axis>()

    private val updateAxes: ((KoolContext) -> Unit) = {
        for (i in axesList.indices) {
            axesList[i].updateAxisState(Time.deltaT)
        }
    }

    init {
        ctx.onRender += updateAxes
    }

    fun registerAxis(name: String, vararg keyCodes: KeyCode): Axis {
        val posKeys = mutableSetOf<KeyCode>()
        keyCodes.forEach { posKeys += it }
        return registerAxis(name, posKeys, emptySet())
    }

    fun registerAxis(name: String, posKeyCodes: Set<KeyCode>, negKeyCodes: Set<KeyCode>): Axis {
        val axis = Axis(name)
        for (key in posKeyCodes) {
            axis.keyListeners += InputStack.defaultKeyboardListener.registerKeyListener(key, name, callback = axis::processPositiveKeyInputEvent)
        }
        for (key in negKeyCodes) {
            axis.keyListeners += InputStack.defaultKeyboardListener.registerKeyListener(key, name, callback = axis::processNegativeKeyInputEvent)
        }
        axes[name] = axis
        axesList += axis
        return axis
    }

    operator fun get(name: String): Axis? = axes[name]

    fun analog(name: String): Float = axes[name]?.analog ?: 0f

    fun digital(name: String): Boolean = axes[name]?.digital == true

    override fun dispose(ctx: KoolContext) {
        runDelayed(1) { ctx.onRender -= updateAxes }
        axesList.forEach { ax ->
            ax.keyListeners.forEach { InputStack.defaultKeyboardListener.removeKeyListener(it) }
        }
        axesList.clear()
        axes.clear()
    }

    class Axis(val name: String) {
        internal val keyListeners = mutableListOf<InputStack.SimpleKeyListener>()

        var analog: Float = 0f
            private set
        val digital: Boolean
            get() = abs(analog) > digitalOnThreshold
        val positive: Boolean
            get() = analog > digitalOnThreshold
        val negative: Boolean
            get() = analog < -digitalOnThreshold

        var digitalOnThreshold = 0.01f
        var analogRiseTime = 1f
        var analogFallTime = 1f

        private var isPositiveKeyPressed = false
        private var isNegativeKeyPressed = false

        fun setRiseFallTime(time: Float) {
            analogRiseTime = time
            analogFallTime = time
        }

        internal fun processPositiveKeyInputEvent(ev: KeyEvent) {
            if (ev.isPressed || ev.isRepeated) {
                isPositiveKeyPressed = true
            } else if (ev.isReleased) {
                isPositiveKeyPressed = false
            }
        }

        internal fun processNegativeKeyInputEvent(ev: KeyEvent) {
            if (ev.isPressed || ev.isRepeated) {
                isNegativeKeyPressed = true
            } else if (ev.isReleased) {
                isNegativeKeyPressed = false
            }
        }

        internal fun updateAxisState(deltaT: Float) {
            val change = when {
                isPositiveKeyPressed -> deltaT / analogRiseTime
                isNegativeKeyPressed -> deltaT / -analogFallTime
                analog > 0f -> deltaT / -analogFallTime
                analog < 0f -> deltaT / analogRiseTime
                else -> 0f
            }
            analog = if (!isPositiveKeyPressed && !isNegativeKeyPressed && abs(change) > abs(analog)) {
                0f
            } else {
                (analog + change).clamp(-1f, 1f)
            }
        }
    }
}

class DriveAxes(ctx: KoolContext) : InputAxes(ctx) {
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

    init {
        throttleAx = registerAxis("throttle", KeyboardInput.KEY_CURSOR_UP, UniversalKeyCode('w'))
            .apply { setRiseFallTime(0.2f) }
        brakeAx = registerAxis("brake", KeyboardInput.KEY_CURSOR_DOWN, UniversalKeyCode('s'))
            .apply { setRiseFallTime(0.2f) }
        steerAx = registerAxis("left / right",
            setOf(KeyboardInput.KEY_CURSOR_RIGHT, UniversalKeyCode('d')),
            setOf(KeyboardInput.KEY_CURSOR_LEFT, UniversalKeyCode('a')),
        ).apply { setRiseFallTime(0.5f) }
    }
}

class WalkAxes(ctx: KoolContext) : InputAxes(ctx) {
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
        get() = jumpAx.digital
    val isRun: Boolean
        get() = runAx.digital
    val isCrouch: Boolean
        get() = crouchAx.digital

    val runFactor: Float
        get() = runAx.analog
    val crouchFactor: Float
        get() = crouchAx.analog

    init {
        forwardBackwardAx = registerAxis("forward / backward",
            setOf(KeyboardInput.KEY_CURSOR_UP, UniversalKeyCode('w')),
            setOf(KeyboardInput.KEY_CURSOR_DOWN, UniversalKeyCode('s')),
        ).apply { setRiseFallTime(0.15f) }
        leftRightAx = registerAxis("left / right",
            setOf(KeyboardInput.KEY_CURSOR_RIGHT, UniversalKeyCode('d')),
            setOf(KeyboardInput.KEY_CURSOR_LEFT, UniversalKeyCode('a')),
        ).apply { setRiseFallTime(0.15f) }

        jumpAx = registerAxis("jump", UniversalKeyCode(' ')).apply { setRiseFallTime(0.01f) }
        runAx = registerAxis("run", KeyboardInput.KEY_SHIFT_LEFT, KeyboardInput.KEY_SHIFT_RIGHT).apply { setRiseFallTime(0.5f) }
        crouchAx = registerAxis("crouch", KeyboardInput.KEY_CTRL_LEFT, KeyboardInput.KEY_CTRL_RIGHT).apply { setRiseFallTime(0.5f) }
    }
}
