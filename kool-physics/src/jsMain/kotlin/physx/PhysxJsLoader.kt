/*
 * Generated from WebIDL by webidl-util
 */

package physx

import kotlin.js.Promise

object PhysXJsLoader {
    @JsName("physXJs")
    internal var physXJs: dynamic = null
    @Suppress("UnsafeCastFromDynamic")
    private val physXJsPromise: Promise<dynamic> = js("require('physx-js-webidl')")()

    private var isLoading = false
    private var isLoaded = false

    private val onLoadListeners = mutableListOf<() -> Unit>()

    fun loadModule() {
        if (!isLoading) {
            isLoading = true
            physXJsPromise.then { module: dynamic ->
                physXJs = module
                isLoaded = true
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
    
    fun destroy(vararg nativeObjects: Any) {
        for (obj in nativeObjects) {
            physXJs.destroy(obj)
        }
    }
}