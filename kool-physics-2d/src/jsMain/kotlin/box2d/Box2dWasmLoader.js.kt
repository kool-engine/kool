/*
 * Generated from WebIDL by webidl-util
 */

package box2d

import kotlinx.coroutines.await
import kotlin.js.Promise

@JsModule("kool-box2d-wasm")
private external val Box2D: () -> Promise<JsAny>

actual object Box2dWasmLoader {
    private var box2dWasmModule: JsAny? = null
    private val box2dWasmPromise = Box2D()
    private var _isLoaded = false
    actual val isLoaded: Boolean get() = _isLoaded

    internal actual val box2dWasm: JsAny get() = requireNotNull(box2dWasmModule) {
        "Module 'kool-box2d-wasm' is not loaded. Call loadModule() first"
    }

    actual suspend fun loadModule() {
        if (!isLoaded) {
            box2dWasmPromise.then<JsAny> { box2dWasmModule = it; it }
        }
        box2dWasmPromise.await<JsAny>()
        _isLoaded = true
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Module 'kool-box2d-wasm' is not loaded. Call loadModule() first.")
        }
    }
}

private fun destroyNative(module: JsAny, obj: DestroyableNative): Unit = js("module.destroy(obj)")
actual fun DestroyableNative.destroy() = destroyNative(Box2dWasmLoader.box2dWasm, this)