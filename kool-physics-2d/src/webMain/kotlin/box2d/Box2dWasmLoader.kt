/*
 * Generated from WebIDL by webidl-util
 */

package box2d

import kotlin.js.JsAny

internal expect object Box2dWasmLoader {
    internal val box2dWasm: JsAny
    suspend fun loadModule()
    fun destroy(nativeObject: JsAny)
}