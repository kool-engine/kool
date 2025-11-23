package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

/**
 * Fires a Float animation to [targetValue] using the provided [animationSpec].
 */
fun UiScope.animateFloatAsState(
    targetValue: Float,
    animationSpec: AnimationSpec<Float> = tween()
): MutableStateValue<Float> {
    val animatable = remember { AnimatableFloat(targetValue) }

    LaunchedEffect(targetValue) {
        animationSpec.animateTo(animatable, targetValue)
    }

    return animatable
}

/**
 * Fires a Color animation to [targetValue] using the provided [animationSpec].
 */
fun UiScope.animateColorAsState(
    targetValue: Color,
    animationSpec: AnimationSpec<Color> = tween()
): MutableStateValue<Color> {
    val animatable = remember { ColorAnimatable(targetValue) }

    LaunchedEffect(targetValue) {
        animationSpec.animateTo(animatable, targetValue)
    }

    return animatable
}