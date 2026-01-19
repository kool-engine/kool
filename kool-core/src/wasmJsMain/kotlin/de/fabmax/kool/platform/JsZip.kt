package de.fabmax.kool.platform

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.w3c.files.Blob
import kotlin.js.Promise

@JsModule("jszip")
external class JsZip : JsAny {
    fun file(name: String, data: Uint8Array): JsZip
    fun folder(name: String): JsZip
    fun forEach(callback: (relativePath: String, file: ZipObject) -> Unit)
    fun loadAsync(data: Blob): Promise<JsZip>
    fun loadAsync(data: Uint8Array): Promise<JsZip>
    fun generateAsync(options: JsAny): Promise<Uint8Array>
}

suspend fun JsZip.generate(): Uint8Buffer {
    val o = JsZipGenOptions("uint8array", "DEFLATE")
    val data = generateAsync(o).await<Uint8Array>()
    return Uint8BufferImpl(data)
}

private fun JsZipGenOptions(type: String, compression: String): JsAny = js("({ type: type, compression: compression })")

@JsModule("jszip")
external interface ZipObject {
    val name: String
    val dir: Boolean

    fun async(type: String): Promise<JsAny>
}

fun ZipObject.asyncU8(): Promise<Uint8Array> {
    return async("uint8array").unsafeCast()
}

fun ZipObject.asyncText(): Promise<JsString> {
    return async("text").unsafeCast()
}


external interface User {
    val name: String
    val age: Int
    // You can use nullable types to declare a property as optional
    val email: String?
}

