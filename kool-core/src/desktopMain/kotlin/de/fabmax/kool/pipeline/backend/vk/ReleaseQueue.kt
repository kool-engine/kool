package de.fabmax.kool.pipeline.backend.vk

internal object ReleaseQueue {
    private val queuedReleases = mutableListOf<QueuedRelease>()

    fun enqueue(ticks: Int = Swapchain.MAX_FRAMES_IN_FLIGHT, task: () -> Unit) {
        queuedReleases += QueuedRelease(ticks, task)
    }

    fun processQueue(force: Boolean = false) {
        if (queuedReleases.isNotEmpty()) {
            if (force) {
                queuedReleases.sortedBy { it.delay }.forEach { it.task() }
                queuedReleases.clear()
            } else {
                for (i in queuedReleases.indices) {
                    val task = queuedReleases[i]
                    if (--task.delay <= 0) {
                        task.task()
                    }
                }
                queuedReleases.removeIf { it.delay <= 0 }
            }
        }
    }

    private class QueuedRelease(var delay: Int, val task: () -> Unit)
}