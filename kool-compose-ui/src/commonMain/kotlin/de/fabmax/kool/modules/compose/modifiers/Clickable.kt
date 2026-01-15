package de.fabmax.kool.modules.compose.modifiers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.modules.compose.state.LaunchAnimation
import de.fabmax.kool.modules.compose.state.collectAsState
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import me.dvyy.compose.mini.modifier.Modifier
import me.dvyy.compose.mini.modifier.composed

/**
 * Calls [onClick] when this element is clicked, also adding a ripple effect.
 */
fun Modifier.clickable(
    hoverBackground: UiRenderer<UiNode> = RectBackground(Color.WHITE.withAlpha(0.2f)),
    onClick: (PointerEvent) -> Unit,
) = composed {
    val animator = remember { FloatAnimator(0.3f, Easing.linear) }
    val clickPos = remember { MutableVec2f() }
    var isHovered by remember { mutableStateOf(false) }
    val animatedRippleProgress by animator.animatable.collectAsState()
    LaunchAnimation(animator)

    // Read values to let compose know to recreate modifiers when they change
    animatedRippleProgress; isHovered

    this.onClick {
        clickPos.set(it.position)
        animator.start(1f, startFrom = 0f)
        animator.update(Time.deltaT)
        onClick(it)
    }.draw {
        if (animator.isActive) getUiPrimitives(1).localCircle(
            clickPos.x, clickPos.y,
            animator.value * 128.dp.px,
            Color.WHITE.withAlpha(0.7f - animator.value * 0.5f)
        )
    }.draw { if (isHovered) hoverBackground.renderUi(this) }
        .onEnter { isHovered = true }
        .onExit { isHovered = false }

}
