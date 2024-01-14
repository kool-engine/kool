package de.fabmax.kool.util

import kotlin.io.encoding.Base64

expect fun Uint8Buffer.deflate(): Uint8Buffer
expect fun Uint8Buffer.inflate(): Uint8Buffer

fun String.decodeBase64(): Uint8Buffer {
    return Base64.decode(this).toBuffer()
}

fun Uint8Buffer.encodeBase64(): String {
    return Base64.encode(toArray())
}

fun Uint8Buffer.decodeToString(): String {
    return toArray().decodeToString()
}

fun ByteArray.toBuffer(): Uint8Buffer {
    return Uint8Buffer(size).put(this)
}

inline fun MixedBuffer.positioned(pos: Int, block: (MixedBuffer) -> Unit) {
    position = pos
    block(this)
}