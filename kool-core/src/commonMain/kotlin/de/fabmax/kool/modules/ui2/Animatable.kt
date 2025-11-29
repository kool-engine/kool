package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.takeWhile

/**
 * A value holder that can be animated by calling suspend functions like [animateTo].
 * When the value changes, dependent UI elements are recomposed.
 */
abstract class Animatable<T: Any>(initialValue: T) : MutableStateValue<T>(initialValue) {
    abstract suspend fun animateTo(targetValue: T, duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth)
}

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
    override suspend fun animateTo(targetValue: Float, duration: Float, easing: Easing.Easing) {
        if (duration <= 0f) {
            set(targetValue)
            return
        }
        val startTime = Time.gameTime
        val startValue = value

        Time.frameFlow.takeWhile {
            val elapsed = Time.gameTime - startTime
            val progress = (elapsed / duration).toFloat().clamp()
            set(lerp(startValue, targetValue, easing.eased(progress)))
            progress < 1f
        }.count()
    }
}

/**
 * An animatable Color value. Colors are correctly interpolated in linear space.
 */
class ColorAnimatable(initialValue: Color) : Animatable<Color>(initialValue) {
    /**
     * Animates the color from its current value to the [targetValue].
     *
     * @param targetValue The target color of the animation.
     * @param duration The duration of the animation in seconds.
     * @param easing The easing function to use for the animation.
     */
    override suspend fun animateTo(targetValue: Color, duration: Float, easing: Easing.Easing) {
        if (duration <= 0f) {
            set(targetValue)
            return
        }
        val startTime = Time.gameTime
        val startValue: Color = value.toLinear()
        val targetLinear: Color = targetValue.toLinear()

        Time.frameFlow.takeWhile {
            val elapsed = Time.gameTime - startTime
            val progress = (elapsed / duration).toFloat().clamp()
            set(startValue.mix(targetLinear, easing.eased(progress)).toSrgb())
            progress < 1f
        }.count()
    }

    suspend fun animateTo(targetHex: String, duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth) {
        animateTo(Color(targetHex), duration, easing)
    }
}

/**
 * An animatable Color value. Colors are correctly interpolated in linear space.
 */
fun ColorAnimatable(initialHex: String): ColorAnimatable = ColorAnimatable(Color(initialHex))

fun UiScope.rememberAnimatable(initialValue: Float) = remember { AnimatableFloat(initialValue) }
fun UiScope.rememberColorAnimatable(initialValue: Color) = remember { ColorAnimatable(initialValue) }
fun UiScope.rememberColorAnimatable(initialHex: String) = remember { ColorAnimatable(initialHex) }