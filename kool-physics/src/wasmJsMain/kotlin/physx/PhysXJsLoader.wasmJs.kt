/*
 * Generated from WebIDL by webidl-util
 */

package physx

import kotlinx.coroutines.await
import kotlin.js.Promise

@JsModule("physx-js-webidl")
private external val PhysX: () -> Promise<JsAny>

actual object PhysXJsLoader {
    private var physXJsModule: JsAny? = null
    private val physXJsPromise = PhysX()
    private var _isLoaded = false
    actual val isLoaded: Boolean get() = _isLoaded

    internal actual val physXJs: JsAny get() = requireNotNull(physXJsModule) {
        "Module 'physx-js-webidl' is not loaded. Call loadModule() first"
    }

    actual suspend fun loadModule() {
        if (!isLoaded) {
            physXJsPromise.then<JsAny> { physXJsModule = it; it }
        }
        physXJsPromise.await<JsAny>()
        _isLoaded = true
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Module 'physx-js-webidl' is not loaded. Call loadModule() first.")
        }
    }
}

private fun destroyNative(module: JsAny, obj: DestroyableNative): Unit = js("module.destroy(obj)")
actual fun DestroyableNative.destroy() = destroyNative(PhysXJsLoader.physXJs, this)