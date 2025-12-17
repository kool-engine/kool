package de.fabmax.kool.modules.ui2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Runs a coroutine when entering the composition and relaunches it when [keys] change.
 * Useful for triggering suspend operations (e.g. animations) in response to state updates.
 */
fun UiScope.LaunchedEffect(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit) {
    val holder = remember { LaunchedEffectHolder() }
    if (holder.keysChanged(*keys)) {
        holder.job?.cancel()
        holder.job = coroutineScope.launch { block() }
    }
}

private class LaunchedEffectHolder(vararg initialKeys: Any?) {
    private var keys: Array<out Any?> = initialKeys
    var job: Job? = null

    /**
     * Returns true if tracked keys change
     */
    fun keysChanged(vararg newKeys: Any?): Boolean {
        return (!arrayContentsEquals(keys, newKeys)).also { changed ->
            if (changed) keys = newKeys
        }
    }

    private fun arrayContentsEquals(a1: Array<out Any?>, a2: Array<out Any?>): Boolean {
        if (a1.size != a2.size) return false
        for (i in a1.indices) {
            if (a1[i] != a2[i]) return false
        }
        return true
    }
}
