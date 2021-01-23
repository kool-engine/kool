/*
 * Generated from WebIDL by webidl-util
 */

package physx

import kotlin.js.Promise

object PhysxJsLoader {
    @JsName("physxJs")
    internal var physxJs: dynamic = null
    @Suppress("UnsafeCastFromDynamic")
    private val physxJsPromise: Promise<dynamic> = js("require('physx-js')")()

    private var isLoading = false
    private var isLoaded = false

    private val onLoadListeners = mutableListOf<() -> Unit>()

    fun loadModule() {
        if (!isLoading) {
            isLoading = true
            physxJsPromise.then { module: dynamic ->
                physxJs = module
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
            throw IllegalStateException("Module 'physx-js' is not loaded. Call loadModule() first and wait for loading to be finished.")
        }
    }
    
    fun destroy(nativeObject: Any) = physxJs.destroy(nativeObject)
}