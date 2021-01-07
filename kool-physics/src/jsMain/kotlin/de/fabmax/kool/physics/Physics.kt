package de.fabmax.kool.physics

import de.fabmax.kool.util.logD
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import physx.PhysX
import kotlin.coroutines.CoroutineContext

actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job
    private val loadingDeferred = CompletableDeferred<Unit>(job)

    private var isLoading = false
    actual val isLoaded: Boolean
        get() = PhysX.isInitialized

    actual fun loadPhysics() {
        if (!isLoading) {
            logD { "Loading physx-js..." }
            isLoading = true
            PhysX.initPhysX()
            PhysX.onLoadListener {
                loadingDeferred.complete(Unit)
            }
        }
    }

    actual suspend fun awaitLoaded() {
        if (!isLoading) {
            loadPhysics()
        }
        loadingDeferred.await()
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Physics subsystem is not loaded. Call loadPhysics() first and wait for loading to be finished.")
        }
    }
}