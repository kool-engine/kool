package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@Suppress("UnusedReceiverParameter")
val Dispatchers.RenderLoop: CoroutineDispatcher
    get() = RenderLoopCoroutineDispatcher

expect object RenderLoopCoroutineDispatcher : CoroutineDispatcher

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
            delayCallback?.let { KoolContext.requireContext().onRender += it }
        }
        delayCallback?.let { KoolContext.requireContext().onRender -= it }
    }
}