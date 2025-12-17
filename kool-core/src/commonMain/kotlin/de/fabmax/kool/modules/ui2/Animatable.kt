package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.lerp
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
    abstract fun setLerp(start: T, target: T, progress: Float)
}

/**
 * An animatable Float value.
 */
class AnimatableFloat(initialValue: Float) : Animatable<Float>(initialValue) {
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
            set(startValue.lerp(targetValue, easing.eased(progress)))
            progress < 1f
        }.count()
    }

    override fun setLerp(start: Float, target: Float, progress: Float) {
        set(start.lerp(target, progress))
    }
}

/**
 * An animatable Color value. Colors are correctly interpolated in linear space.
 */
class AnimatableColor(initialValue: Color) : Animatable<Color>(initialValue) {
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

    override fun setLerp(start: Color, target: Color, progress: Float) {
        set(start.mix(target, progress))
    }
}

/**
 * An animatable Color value. Colors are correctly interpolated in linear space.
 */
fun ColorAnimatable(initialHex: String): AnimatableColor = AnimatableColor(Color(initialHex))

suspend fun AnimatableColor.animateTo(targetHex: String, duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth) =
    animateTo(Color(targetHex), duration, easing)

fun UiScope.rememberAnimatableFloat(initialValue: Float) = remember { AnimatableFloat(initialValue) }
fun UiScope.rememberAnimatableColor(initialValue: Color) = remember { AnimatableColor(initialValue) }
fun UiScope.rememberAnimatableColor(initialHex: String) = remember { ColorAnimatable(initialHex) }

/**
 * Wrapper class for animating Animatable values in a non-suspending manner. Can also be used outside of UI contexts.
 */
class Animator<T: Any, A: Animatable<T>>(
    val animatable: A,
    val duration: Float,
    val easing: Easing.Easing,
    val onFinished: (Animator<T, A>.() -> Unit)? = null
) {
    val value: T get() = animatable.value

    private var startValue = animatable.value
    private var targetValue = animatable.value
    private var progress = 1f

    val isActive: Boolean get() = progress < 1f
    val isFinished: Boolean get() = !isActive

    fun set(value: T) {
        startValue = value
        targetValue = value
        animatable.set(value)
        progress = 1f
    }

    fun start(target: T, startFrom: T = animatable.value) {
        animatable.value = startFrom
        startValue = startFrom
        targetValue = target
        progress = 0f
    }

    fun update(deltaT: Float = Time.deltaT): T {
        if (isActive) {
            progress = (progress + deltaT / duration).clamp()
            animatable.setLerp(startValue, targetValue, easing.eased(progress))
            if (isFinished) {
                onFinished?.invoke(this)
            }
        }
        return value
    }
}

context(ui: UiScope)
fun <T: Any, A: Animatable<T>> Animator<T, A>.updateUsing(deltaT: Float = Time.deltaT): T {
    with(ui) { animatable.use() }
    return update(deltaT)
}

typealias FloatAnimator = Animator<Float, AnimatableFloat>

fun FloatAnimator(
    duration: Float,
    easing: Easing.Easing = Easing.smooth,
    initial: Float = 0f,
    onFinished: (FloatAnimator.() -> Unit)? = null
): FloatAnimator = Animator(AnimatableFloat(initial), duration, easing, onFinished)

typealias ColorAnimator = Animator<Color, AnimatableColor>

fun ColorAnimator(
    duration: Float,
    easing: Easing.Easing = Easing.smooth,
    initial: Color = Color.BLACK,
    onFinished: (ColorAnimator.() -> Unit)? = null
): ColorAnimator = Animator(AnimatableColor(initial), duration, easing, onFinished)
