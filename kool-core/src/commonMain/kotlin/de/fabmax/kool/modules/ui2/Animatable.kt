package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.delayFrames
import kotlin.math.abs

/**
 * A value holder that can be animated by calling suspend functions like [animateTo].
 * When the value changes, dependent UI elements are recomposed.
 */
abstract class Animatable<T: Any>(initialValue: T) : MutableStateValue<T>(initialValue)

/**
 * An animatable Float value.
 */
class AnimatableFloat(initialValue: Float) : Animatable<Float>(initialValue) {
    private fun lerp(a: Float, b: Float, l: Float) = a * (1 - l) + b * l
    /**
     * Animates the value from its current value to the [targetValue].
     *
     * @param targetValue The target value of the animation.
     * @param duration The duration of the animation in seconds.
     * @param easing The easing function to use for the animation.
     */
    suspend fun animateTo(targetValue: Float, duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth) {
        if (duration <= 0f) {
            set(targetValue)
            return
        }
        val startTime = Time.gameTime
        val startValue = value
        while (true) {
            delayFrames(1)
            val elapsed = Time.gameTime - startTime
            val progress = (elapsed / duration).toFloat().clamp()
            val easedProgress = easing.eased(progress)
            val newValue = lerp(startValue, targetValue, easedProgress)

            if (progress >= 1f || abs(value - targetValue) < 0.0001f) {
                set(targetValue)
                break
            } else {
                set(newValue)
            }
        }
    }
}

/**
 * An animatable Color value. Colors are correctly interpolated in linear space.
 */
class ColorAnimatable(initialValue: Color) : Animatable<Color>(initialValue) {
    constructor(initialHex: String) : this(Color(initialHex))

    /**
     * Animates the color from its current value to the [targetValue].
     *
     * @param targetValue The target color of the animation.
     * @param duration The duration of the animation in seconds.
     * @param easing The easing function to use for the animation.
     */
    suspend fun animateTo(targetValue: Color, duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth) {
        if (duration <= 0f) {
            set(targetValue)
            return
        }
        val startTime = Time.gameTime
        val startValue = value.toLinear()
        val targetLinear = targetValue.toLinear()
        val animatedColor = MutableColor()
        while (true) {
            delayFrames(1)
            val elapsed = Time.gameTime - startTime
            val progress = (elapsed / duration).toFloat().clamp()
            val easedProgress = easing.eased(progress)

            startValue.mix(targetLinear, easedProgress, animatedColor)

            if (progress >= 1f) {
                set(targetValue)
                break
            } else {
                set(animatedColor.toSrgb())
            }
        }
    }

    suspend fun animateTo(targetHex: String, duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth) {
        animateTo(Color(targetHex), duration, easing)
    }
}

fun UiScope.rememberAnimatable(initialValue: Float) = remember { AnimatableFloat(initialValue) }
fun UiScope.rememberColorAnimatable(initialValue: Color) = remember { ColorAnimatable(initialValue) }
fun UiScope.rememberColorAnimatable(initialHex: String) = remember { ColorAnimatable(initialHex) }