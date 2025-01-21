package de.fabmax.kool.pipeline.backend.vk

internal object DeferredRelease {
    private val deferredTasks = mutableListOf<DeferredTask>()

    fun defer(ticks: Int = 1, task: () -> Unit) {
        deferredTasks += DeferredTask(ticks, task)
    }

    fun processTasks(force: Boolean = false) {
        if (deferredTasks.isNotEmpty()) {
            if (force) {
                deferredTasks.forEach { it.task() }
                deferredTasks.clear()
            } else {
                deferredTasks.forEach {
                    if (--it.delay <= 0) {
                        it.task()
                    }
                }
                deferredTasks.removeIf { it.delay <= 0 }
            }
        }
    }

    private class DeferredTask(var delay: Int, val task: () -> Unit)
}