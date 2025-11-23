package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

/**
 * Animates this Float value to [targetValue] using the provided [spec].
 * Suspends until the animation finishes.
 */
suspend fun AnimatableFloat.animateTo(targetValue: Float, spec: AnimationSpec<Float>) {
    when (spec) {
        is TweenSpec -> animateTo(targetValue, spec.duration, spec.easing)
        is SnapSpec -> set(targetValue)
        else -> set(targetValue) // Fallback for unknown specs
    }
}

/**
 * Animates this Color value to [targetValue] using the provided [spec].
 * Suspends until the animation finishes.
 */
suspend fun ColorAnimatable.animateTo(targetValue: Color, spec: AnimationSpec<Color>) {
    when (spec) {
        is TweenSpec -> animateTo(targetValue, spec.duration, spec.easing)
        is SnapSpec -> set(targetValue)
        else -> set(targetValue)
    }
}

/**
 * Fires a Float animation to [targetValue]. When [targetValue] changes, the animation
 * restarts with the current value as start point.
 */
fun UiScope.animateFloatAsState(
    targetValue: Float,
    animationSpec: AnimationSpec<Float> = tween()
): MutableStateValue<Float> {
    val animatable = remember { AnimatableFloat(targetValue) }

    LaunchedEffect(targetValue) {
        animatable.animateTo(targetValue, animationSpec)
    }

    return animatable
}

/**
 * Fires a Color animation to [targetValue]. When [targetValue] changes, the animation
 * restarts with the current value as start point.
 */
fun UiScope.animateColorAsState(
    targetValue: Color,
    animationSpec: AnimationSpec<Color> = tween()
): MutableStateValue<Color> {
    val animatable = remember { ColorAnimatable(targetValue) }

    LaunchedEffect(targetValue) {
        animatable.animateTo(targetValue, animationSpec)
    }

    return animatable
}