package de.fabmax.kool.util

import de.fabmax.kool.ApplicationScope
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Super-interface for any resource that has to be released after usage (such as GPU resources, e.g. buffers,
 * shaders, etc. or native physics objects, e.g. bodies, materials, etc.). Once released, resources cannot be reused
 * and trying to do so should result in an exception.
 */
interface Releasable {
    val isReleased: Boolean

    /**
     * Releases all resources associated with this object. Usually, resources are released immediately by calling this
     * method. However, some resources can only be released at certain times. In that case, release is done as soon as
     * possible after calling this method.
     * Idempotent operation: Calling this method on an already released object has no effect.
     */
    fun release()
}

/**
 * Attaches this [Releasable] to the given [baseReleasable], so that this object is automatically released when
 * [baseReleasable] is released.
 */
fun <T: Releasable> T.releaseWith(baseReleasable: BaseReleasable): T {
    baseReleasable.addDependingReleasable(this)
    return this
}

/**
 * Removes this [Releasable] from the given [baseReleasable], so that it is no longer automatically released when
 * [baseReleasable] is released.
 */
fun <T: Releasable> T.cancelReleaseWith(baseReleasable: BaseReleasable): T {
    baseReleasable.removeDependingReleasable(this)
    return this
}

fun Releasable.checkIsNotReleased() = check(!isReleased) {
    "$this is already released."
}

fun Releasable.checkIsReleased() = check(isReleased) {
    "$this is not released."
}

/**
 * Schedules release of this [Releasable]. The release operation is performed in the context of the backend render
 * thread after *at least* [numFrames] frames. However, depending on where / when this function is called, it might
 * also be [numFrames] + 1 frames.
 */
fun Releasable.releaseDelayed(numFrames: Int = 1) {
    ApplicationScope.launch {
        withContext(RenderLoopCoroutineDispatcher) {
            delayFrames(numFrames)
            release()
        }
    }
}

abstract class BaseReleasable : Releasable {
    private val _isReleased = atomic(false)
    override val isReleased: Boolean get() = _isReleased.value

    private val onReleaseCallbacks: MutableList<() -> Unit> = mutableListOf()
    private val dependingReleasables = linkedSetOf<Releasable>()

    fun onRelease(block: () -> Unit) {
        onReleaseCallbacks += block
    }

    fun addDependingReleasable(releasable: Releasable) {
        dependingReleasables += releasable
    }

    fun removeDependingReleasable(releasable: Releasable) {
        dependingReleasables -= releasable
    }

    override fun release() {
        if (!_isReleased.getAndSet(true)) {
            dependingReleasables.reversed().toList().forEach { it.release() }
            onReleaseCallbacks.forEach { it() }
            doRelease()
        }
    }

    protected abstract fun doRelease()
}
