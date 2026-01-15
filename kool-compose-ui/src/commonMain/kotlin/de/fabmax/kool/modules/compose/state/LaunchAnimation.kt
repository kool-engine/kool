package de.fabmax.kool.modules.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import de.fabmax.kool.modules.ui2.Animator
import de.fabmax.kool.util.KoolDispatchers
import de.fabmax.kool.util.Time
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Sends updates to a Kool [animator] as a [LaunchedEffect].
 *
 * Once this node leaves the composition, the animator will stop receiving updates.
 */
@Suppress("NOTHING_TO_INLINE") // Inline necessary to correctly trigger recompositions when collecting animated value
@Composable
inline fun LaunchAnimation(animator: Animator<*, *>) {
    LaunchedEffect(animator.isActive) {
        if (animator.isActive) launch(KoolDispatchers.Frontend) {
            while (animator.isActive) {
                animator.update(Time.deltaT)
                yield()
            }
        }
    }
}