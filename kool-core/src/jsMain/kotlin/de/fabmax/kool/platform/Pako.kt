package de.fabmax.kool.platform

import org.khronos.webgl.Uint8Array

@JsModule("pako")
external object Pako {
    fun inflate(data: Uint8Array): Uint8Array
    fun gzip(data: Uint8Array): Uint8Array
}