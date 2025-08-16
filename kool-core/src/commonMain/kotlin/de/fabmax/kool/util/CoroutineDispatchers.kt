package de.fabmax.kool.util

import de.fabmax.kool.ApplicationScope
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.takeWhile
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Coroutine dispatcher for executing coroutines in the frontend render loop. Use this if you want to
 * add / remove scenes, scene nodes, modify meshes, etc.
 */
object FrontendCoroutineDispatcher : TriggeredCoroutineDispatcher()
@Suppress("UnusedReceiverParameter")
val Dispatchers.Frontend: CoroutineDispatcher
    get() = FrontendCoroutineDispatcher

/**
 * Coroutine dispatcher for executing coroutines on the backend render thread. This is only needed if you want
 * to directly interact with GPU resources. The backend render thread runs in parallel to frontend update, so
 * it is not safe to modify scene data from within coroutines using this dispatcher.
 */
object BackendCoroutineDispatcher : TriggeredCoroutineDispatcher()
@Suppress("UnusedReceiverParameter")
val Dispatchers.Backend: CoroutineDispatcher
    get() = BackendCoroutineDispatcher


abstract class TriggeredCoroutineDispatcher : CoroutineDispatcher() {
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
    val targetFrame = Time.frameCount + numFrames
    Time.frameFlow.takeWhile { it < targetFrame }.count()
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
 * ApplicationScope.launch(Dispatchers.Frontend) {
 *     block()
 * }
 * ```
 */
@Deprecated("Launching stand-alone coroutines is somewhat bad practice.")
inline fun launchOnMainThread(crossinline block: suspend () -> Unit) = ApplicationScope.launch(Dispatchers.Frontend) {
    block()
}
