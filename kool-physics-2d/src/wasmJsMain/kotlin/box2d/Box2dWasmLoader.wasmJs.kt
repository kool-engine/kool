package box2d

import kotlinx.coroutines.await
import kotlin.js.Promise

@JsModule("kool-box2d-wasm")
private external val Box2D: () -> Promise<JsAny>

internal actual object Box2dWasmLoader {
    private var box2dWasmModule: JsAny? = null
    internal actual val box2dWasm: JsAny get() = requireNotNull(box2dWasmModule) { "Module 'kool-box2d-wasm' is not loaded" }
    private val box2dWasmPromise = Box2D()
    private var isLoaded: Boolean = false

    actual suspend fun loadModule() {
        if (!isLoaded) {
            box2dWasmPromise.then<JsAny> { module ->
                box2dWasmModule = module
                module
            }
        }
        box2dWasmPromise.await<JsAny>()
        isLoaded = true
    }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Module 'kool-box2d-wasm' is not loaded. Call loadModule() first and wait for loading to be finished.")
        }
    }

    actual fun destroy(nativeObject: JsAny) {
        destroyJsNativeAny(box2dWasm, nativeObject)
    }
}

private fun destroyJsNativeAny(module: JsAny, obj: JsAny): Unit = js("module.destroy(obj)")
