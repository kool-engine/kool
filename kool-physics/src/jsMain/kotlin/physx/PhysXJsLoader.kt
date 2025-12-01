/*
 * Generated from WebIDL by webidl-util
 */

package physx

import kotlinx.coroutines.asDeferred             
import kotlin.js.Promise

@JsModule("physx-js-webidl")
private external val PhysX: () -> Promise<dynamic>

object PhysXJsLoader {
    @JsName("physXJs")
    internal var physXJs: dynamic = null
    private val physXJsPromise = PhysX()
    internal var physXJsDeferred = physXJsPromise.asDeferred()

    val isLoaded: Boolean get() = physXJsDeferred.isCompleted

    suspend fun loadModule() {
        if (!isLoaded) {
            physXJsPromise.then { module: dynamic -> physXJs = module }
        }
        physXJsDeferred.await()
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Module 'physx-js-webidl' is not loaded. Call loadModule() first and wait for loading to be finished.")
        }
    }

    fun destroy(nativeObject: Any) {
        physXJs.destroy(nativeObject)
    }
}