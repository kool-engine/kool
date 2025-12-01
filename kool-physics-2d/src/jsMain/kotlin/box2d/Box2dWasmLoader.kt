/*
 * Generated from WebIDL by webidl-util
 */

package box2d

import kotlinx.coroutines.asDeferred             
import kotlin.js.Promise

@JsModule("kool-box2d-wasm")
private external val Box2D: () -> Promise<dynamic>

object Box2dWasmLoader {
    @JsName("box2dWasm")
    internal var box2dWasm: dynamic = null
    private val box2dWasmPromise = Box2D()
    internal var box2dWasmDeferred = box2dWasmPromise.asDeferred()

    val isLoaded: Boolean get() = box2dWasmDeferred.isCompleted

    suspend fun loadModule() {
        if (!isLoaded) {
            box2dWasmPromise.then { module: dynamic -> box2dWasm = module }
        }
        box2dWasmDeferred.await()
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Module 'kool-box2d-wasm' is not loaded. Call loadModule() first and wait for loading to be finished.")
        }
    }

    fun destroy(nativeObject: Any) {
        box2dWasm.destroy(nativeObject)
    }
}