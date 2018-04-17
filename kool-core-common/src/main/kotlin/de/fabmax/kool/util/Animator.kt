package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.isFuzzyZero
import kotlin.math.PI
import kotlin.math.cos

/**
 * @author fabmax
 */
abstract class Animator<V, out T: InterpolatedValue<V>>(val value: T) {
    companion object {
        val ONCE = 1
        val REPEAT = 2
        val REPEAT_TOGGLE_DIR = 3
    }

    var duration = 1f
    var speed = 1f
    var repeating = ONCE
    var progress = 0f

    open fun tick(ctx: KoolContext): V {
        if (!speed.isFuzzyZero()) {
            progress += ctx.deltaT * speed / duration
            if (progress >= 1f && speed > 0) {
                when (repeating) {
                    ONCE -> {
                        // animation is done
                        progress = 1f
                        speed = 0f
                    }
                    REPEAT -> {
                        // repeat animation from beginning
                        progress = 0f
                    }
                    REPEAT_TOGGLE_DIR -> {
                        // invert speed to play animation backwards
                        progress = 1f
                        speed = -speed
                    }

                }
            } else if (progress <= 0f && speed < 0) {
                when (repeating) {
                    ONCE -> {
                        // animation is done
                        progress = 0f
                        speed = 0f
                    }
                    REPEAT -> {
                        // repeat animation from end
                        progress = 1f
                    }
                    REPEAT_TOGGLE_DIR -> {
                        // invert speed to play animation backwards
                        progress = 0f
                        speed = -speed
                    }

                }
            }

            progress = progress.clamp(0f, 1f)
            value.interpolate(interpolate(progress))
        }
        return value.value
    }

    protected abstract fun interpolate(progress: Float): Float
}

class LinearAnimator<V, out T: InterpolatedValue<V>>(value: T) : Animator<V, T>(value) {
    override fun interpolate(progress: Float): Float {
        return progress
    }
}

class CosAnimator<V, out T: InterpolatedValue<V>>(value: T) : Animator<V, T>(value) {
    override fun interpolate(progress: Float): Float {
        return 0.5f - cos(progress * PI).toFloat() * 0.5f
    }
}

abstract class InterpolatedValue<T>(initial: T) {
    var value = initial

    var onUpdate: ((T) -> Unit)? = null

    open fun interpolate(progress: Float) {
        updateValue(progress)
        onUpdate?.invoke(value)
    }

    protected abstract fun updateValue(interpolationPos: Float)
}

class InterpolatedFloat(var from: Float, var to: Float) : InterpolatedValue<Float>(from) {
    override fun updateValue(interpolationPos: Float) {
        value = from + (to - from) * interpolationPos
    }
}

class InterpolatedColor(var from: MutableColor, var to: MutableColor) : InterpolatedValue<MutableColor>(MutableColor()) {
    init {
        value.set(from)
    }

    override fun updateValue(interpolationPos: Float) {
        value.set(to).subtract(from).scale(interpolationPos).add(from)
    }
}
