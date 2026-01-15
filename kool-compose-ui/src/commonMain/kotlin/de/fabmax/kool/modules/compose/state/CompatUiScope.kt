package de.fabmax.kool.modules.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import de.fabmax.kool.modules.compose.LocalSurfaceContentCompat
import de.fabmax.kool.modules.ui2.UiScope

/**
 * Compatibility interop with Kool's UI system.
 *
 * The passed [block] will be called whenever content would normally be recalculated in Kool.
 * This allows adding Surface callbacks like onEachFrame which can't be set via Modifiers.
 *
 * Note: do not make changes to node Modifiers in this scope,
 * this is currently managed fully by the Compose implementation and modifiers may be cleared.
 */
@Composable
fun CompatUiScope(block: UiScope.() -> Unit) {
    val compat = LocalSurfaceContentCompat.current

    DisposableEffect(compat, block) {
        compat.addCallback(block)
        onDispose {
            compat.removeCallback(block)
        }
    }
}
