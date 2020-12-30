package de.fabmax.kool.physics

import ammo.Ammo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job
    private val loadingDeferred = CompletableDeferred<Unit>(job)

    actual val isLoaded: Boolean
        get() = Ammo.isInitialized

    actual fun loadPhysics() {
        Ammo.initAmmo()
        Ammo.onLoad {
            loadingDeferred.complete(Unit)
        }
    }

    actual suspend fun awaitLoaded() {
        loadingDeferred.await()
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Physics subsystem is not loaded. Call loadPhysics() first and wait for loading to be finished.")
        }
    }
}