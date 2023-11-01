package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

@Suppress("UnusedReceiverParameter")
val Dispatchers.RenderLoop: CoroutineDispatcher
    get() = RenderLoopCoroutineDispatcher

object RenderLoopCoroutineDispatcher : CoroutineDispatcher() {

    private val queueLock = SynchronizedObject()

    private val dispatchedTasks = mutableListOf<Runnable>()
    private val dispatchedTasksCopy = mutableListOf<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        synchronized(queueLock) {
            dispatchedTasks += block
        }
    }

    internal fun executeDispatchedTasks() {
        if (dispatchedTasks.isNotEmpty()) {
            synchronized(queueLock) {
                dispatchedTasksCopy += dispatchedTasks
                dispatchedTasks.clear()
            }
            dispatchedTasksCopy.forEach { it.run() }
            dispatchedTasksCopy.clear()
        }
    }
}

suspend fun delayFrames(numFrames: Int) {
    if (numFrames <= 0) {
        return
    }

    withContext(Dispatchers.RenderLoop) {
        var delayCallback: ((KoolContext) -> Unit)? = null
        suspendCancellableCoroutine { continuation ->
            var counter = numFrames
            delayCallback = {
                if (--counter <= 0) {
                    continuation.resume(Unit)
                }
            }
            delayCallback?.let { KoolSystem.requireContext().onRender += it }
        }
        delayCallback?.let { KoolSystem.requireContext().onRender -= it }
    }
}

/**
 * Executes the given [block] after [frames] frames on the render-loop thread. This is equivalent to launching
 * a coroutine and using the [delayFrames] suspending function before executing the block:
 * ```
 * CoroutineScope(Dispatchers.RenderLoop).launch {
 *     delayFrames(frames)
 *     block()
 * }
 * ```
 */
inline fun launchDelayed(frames: Int, crossinline block: suspend () -> Unit) = CoroutineScope(Dispatchers.RenderLoop).launch {
    delayFrames(frames)
    block()
}

/**
 * Executes the given [block] on the render-loop thread. This is equivalent to launching
 * a coroutine in the RenderLoop context and calling block:
 * ```
 * CoroutineScope(Dispatchers.RenderLoop).launch {
 *     block()
 * }
 * ```
 */
inline fun launchOnMainThread(crossinline block: suspend () -> Unit) = CoroutineScope(Dispatchers.RenderLoop).launch {
    block()
}
