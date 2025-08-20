package de.fabmax.kool.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.takeWhile
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object KoolDispatchers {
    /**
     * Coroutine dispatcher for executing coroutines in the frontend render loop. Use this if you want to
     * add / remove scenes, scene nodes, modify meshes, etc.
     */
    val Frontend = TriggeredCoroutineDispatcher("Frontend")

    /**
     * Coroutine dispatcher for executing coroutines on the backend render thread. This is only needed if you want
     * to directly interact with GPU resources. The backend render thread runs in parallel to frontend update, so
     * it is not safe to modify scene data from within coroutines using this dispatcher.
     */
    val Backend = TriggeredCoroutineDispatcher("Backend")

    /**
     * Coroutine dispatcher for executing coroutines during sync-time, i.e., it is safe to access frontend and backend
     * components.
     */
    val Synced = TriggeredCoroutineDispatcher("Synced")
}

object ApplicationScope : CoroutineScope {
    val job = Job()
    override val coroutineContext: CoroutineContext = job
}

object FrontendScope : CoroutineScope {
    val job = Job(ApplicationScope.job)
    override val coroutineContext: CoroutineContext = job + KoolDispatchers.Frontend
}

object BackendScope : CoroutineScope {
    val job = Job(ApplicationScope.job)
    override val coroutineContext: CoroutineContext = job + KoolDispatchers.Backend
}

object SyncedScope : CoroutineScope {
    val job = Job(ApplicationScope.job)
    override val coroutineContext: CoroutineContext = job + KoolDispatchers.Synced
}

class TriggeredCoroutineDispatcher(val name: String) : CoroutineDispatcher() {
    private val queueLock = SynchronizedObject()
    private val taskQueue = mutableListOf<Runnable>()
    private val taskQueueCopy = mutableListOf<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        synchronized(queueLock) {
            taskQueue += block
        }
    }

    internal fun executeDispatchedTasks() {
        if (taskQueue.isNotEmpty()) {
            synchronized(queueLock) {
                taskQueueCopy += taskQueue
                taskQueue.clear()
            }
            taskQueueCopy.forEach { it.run() }
            taskQueueCopy.clear()
        }
    }
}

suspend fun delayFrames(numFrames: Int) {
    if (numFrames > 0) {
        val targetFrame = Time.frameCount + numFrames
        Time.frameFlow.takeWhile { it < targetFrame }.count()
    }
}

/**
 * Executes the given [block] after [frames] frames in the current [CoroutineScope]. This is equivalent to launching
 * a coroutine and using the [delayFrames] suspending function before executing the block:
 * ```
 * launch {
 *     delayFrames(frames)
 *     block()
 * }
 * ```
 */
inline fun CoroutineScope.launchDelayed(
    frames: Int,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend () -> Unit
) = launch(context) {
    delayFrames(frames)
    block()
}

/**
 * Executes the given [block] on the frontend dispatcher. This is equivalent to:
 * ```
 * FrontendScope.launch {
 *     block()
 * }
 * ```
 */
@Deprecated("There really isn't a main thread anymore. Use the appropriate CoroutineScope (typically FrontendScope)")
inline fun launchOnMainThread(crossinline block: suspend () -> Unit) = FrontendScope.launch {
    block()
}
