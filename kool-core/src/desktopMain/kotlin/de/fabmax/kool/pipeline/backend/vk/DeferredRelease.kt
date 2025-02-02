package de.fabmax.kool.pipeline.backend.vk

internal object DeferredRelease {
    private val deferredTasks = mutableListOf<DeferredTask>()

    fun defer(ticks: Int = Swapchain.MAX_FRAMES_IN_FLIGHT, task: () -> Unit) {
        deferredTasks += DeferredTask(ticks, task)
    }

    fun processTasks(force: Boolean = false) {
        if (deferredTasks.isNotEmpty()) {
            if (force) {
                deferredTasks.sortedBy { it.delay }.forEach { it.task() }
                deferredTasks.clear()
            } else {
                for (i in deferredTasks.indices) {
                    val task = deferredTasks[i]
                    if (--task.delay <= 0) {
                        task.task()
                    }
                }
                deferredTasks.removeIf { it.delay <= 0 }
            }
        }
    }

    private class DeferredTask(var delay: Int, val task: () -> Unit)
}