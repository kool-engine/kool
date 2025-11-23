package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Easing

/**
 * Base interface for animation specifications. Defines how a value evolves over time.
 */
interface AnimationSpec<T>

/**
 * Creates a tween (in-between) animation spec using the given [duration] and [easing] curve.
 */
data class TweenSpec<T>(
    val duration: Float = 0.3f,
    val easing: Easing.Easing = Easing.smooth
) : AnimationSpec<T>

/**
 * Specification for snapping the value instantly without any animation.
 */
class SnapSpec<T> : AnimationSpec<T>

/**
 * Convenience builder for [TweenSpec].
 */
fun <T> tween(duration: Float = 0.3f, easing: Easing.Easing = Easing.smooth): TweenSpec<T> =
    TweenSpec(duration, easing)

/**
 * Convenience builder for [SnapSpec].
 */
fun <T> snap(): SnapSpec<T> = SnapSpec()