package de.fabmax.kool.modules.ui2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Runs a coroutine when entering the composition and relaunches it when [key1] changes.
 * Useful for triggering suspend operations (e.g. animations) in response to state updates.
 */
fun UiScope.LaunchedEffect(key1: Any?, block: suspend CoroutineScope.() -> Unit) {
    val holder = remember { LaunchedEffectHolder(key1) }
    if (holder.keysChanged(key1)) {
        holder.job?.cancel()
        holder.job = coroutineScope.launch { block() }
    }
}

fun UiScope.LaunchedEffect(key1: Any?, key2: Any?, block: suspend CoroutineScope.() -> Unit) {
    val holder = remember { LaunchedEffectHolder(key1, key2) }
    if (holder.keysChanged(key1, key2)) {
        holder.job?.cancel()
        holder.job = coroutineScope.launch { block() }
    }
}

fun UiScope.LaunchedEffect(key1: Any?, key2: Any?, key3: Any?, block: suspend CoroutineScope.() -> Unit) {
    val holder = remember { LaunchedEffectHolder(key1, key2, key3) }
    if (holder.keysChanged(key1, key2, key3)) {
        holder.job?.cancel()
        holder.job = coroutineScope.launch { block() }
    }
}

internal class LaunchedEffectHolder(vararg initialKeys: Any?) {
    private var keys: Array<out Any?> = initialKeys
    var job: Job? = null

    /**
     * Returns true if tracked keys change or the job is no longer active.
     */
    fun keysChanged(vararg newKeys: Any?): Boolean {
        return if (!arrayContentsEquals(keys, newKeys)) {
            keys = newKeys
            true
        } else {
            job == null || !job!!.isActive
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
