package de.fabmax.kool.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

actual object RenderLoopCoroutineDispatcher : CoroutineDispatcher() {

    private val dispatchedTasks = mutableListOf<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatchedTasks += block
    }

    internal fun executeDispatchedTasks() {
        if (dispatchedTasks.isNotEmpty()) {
            dispatchedTasks.forEach { it.run() }
            dispatchedTasks.clear()
        }
    }
}