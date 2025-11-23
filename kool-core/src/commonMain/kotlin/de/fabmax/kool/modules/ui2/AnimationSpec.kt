package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Easing

/**
 * Defines an animation specification that holds the logic of how an [Animatable]
 * should evolve towards a target value over time.
 */
interface AnimationSpec<T: Any> {
    /**
     * Executes the animation. This function should suspend until the animation is finished.
     */
    suspend fun animateTo(animatable: Animatable<T>, targetValue: T)
}

/**
 * Implementation of a Tween (in-between) animation.
 */
data class TweenSpec<T: Any>(
    val duration: Float = 0.3f,
    val easing: Easing.Easing = Easing.smooth
) : AnimationSpec<T> {
    override suspend fun animateTo(animatable: Animatable<T>, targetValue: T) =
        animatable.animateTo(targetValue, duration, easing)
}

/**
 * Implementation of a Snap (instant) animation.
 */
class SnapSpec<T: Any> : AnimationSpec<T> {
    override suspend fun animateTo(animatable: Animatable<T>, targetValue: T) = animatable.set(targetValue)
}

fun <T: Any> tween(duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth) = TweenSpec<T>(duration, easing)
fun <T: Any> snap() = SnapSpec<T>()