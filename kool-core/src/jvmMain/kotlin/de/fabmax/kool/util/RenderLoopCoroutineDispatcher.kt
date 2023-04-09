package de.fabmax.kool.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

actual object RenderLoopCoroutineDispatcher : CoroutineDispatcher() {

    private val dispatchedTasks = mutableListOf<Runnable>()
    private val dispatchedTasksCopy = mutableListOf<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        synchronized(dispatchedTasks) {
            dispatchedTasks += block
        }
    }

    internal fun executeDispatchedTasks() {
        if (dispatchedTasks.isNotEmpty()) {
            synchronized(dispatchedTasks) {
                dispatchedTasksCopy += dispatchedTasks
                dispatchedTasks.clear()
            }
            dispatchedTasksCopy.forEach { it.run() }
            dispatchedTasksCopy.clear()
        }
    }
}