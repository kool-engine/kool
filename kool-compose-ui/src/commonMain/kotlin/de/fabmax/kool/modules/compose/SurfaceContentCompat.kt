package de.fabmax.kool.modules.compose

import de.fabmax.kool.modules.ui2.UiScope

/**
 * Allows adding callbacks for surface redraws to modify [de.fabmax.kool.modules.ui2.UiScope].
 *
 * Based on NavigationEventDispatcher from Compose for desktop.
 * [addCallback] and [removeCallback] must be called from main thread, which [CompatUiScope] ensures.
 *
 * @see de.fabmax.kool.modules.compose.state.CompatUiScope
 */
class SurfaceContentCompat {
    private val onUpdateUi = mutableSetOf<(UiScope) -> Unit>()

    internal fun addCallback(block: (UiScope) -> Unit) {
        onUpdateUi.add(block)
    }

    internal fun removeCallback(block: (UiScope) -> Unit) {
        onUpdateUi.remove(block)
    }

    fun content(scope: UiScope) {
        onUpdateUi.forEach { it(scope) }
    }
}