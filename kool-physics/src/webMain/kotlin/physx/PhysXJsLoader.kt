/*
 * Generated from WebIDL by webidl-util
 */

package physx

import kotlin.js.JsAny

internal expect object PhysXJsLoader {
    internal val physXJs: JsAny
    val isLoaded: Boolean
    suspend fun loadModule()
}

sealed external interface DestroyableNative : JsAny

expect fun DestroyableNative.destroy()