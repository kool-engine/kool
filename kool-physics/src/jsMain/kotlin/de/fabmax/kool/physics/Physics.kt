package de.fabmax.kool.physics

import ammo.Ammo
import de.fabmax.kool.util.logD
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job
    private val loadingDeferred = CompletableDeferred<Unit>(job)

    private var isLoading = false
    actual val isLoaded: Boolean
        get() = Ammo.isInitialized

    actual fun loadPhysics() {
        if (!isLoading) {
            logD { "Loading ammo.js..." }
            isLoading = true
            Ammo.initAmmo()
            Ammo.onLoad {
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