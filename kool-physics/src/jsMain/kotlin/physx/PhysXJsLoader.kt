/*
 * Generated from WebIDL by webidl-util
 */

package physx

import kotlinx.coroutines.asDeferred             
import kotlin.js.Promise

@JsModule("physx-js-webidl")
internal external val PhysX: () -> Promise<dynamic>

object PhysXJsLoader {
    @JsName("physXJs")
    internal var physXJs: dynamic = null
    private val physXJsPromise = PhysX()
    internal var physxDeferred = physXJsPromise.asDeferred()

    val isLoaded: Boolean get() = physxDeferred.isCompleted
    private var isLoading = false
    private val onLoadListeners = mutableListOf<() -> Unit>()

    fun loadModule() {
        if (!isLoading) {
            isLoading = true
            physXJsPromise.then { module: dynamic ->
                physXJs = module
                onLoadListeners.forEach { it() }
            }
        }
    }

    fun addOnLoadListener(listener: () -> Unit) {
        if (isLoaded) {
            listener()
        } else {
            onLoadListeners += listener
        }
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