package de.fabmax.kool.util

import kotlin.io.encoding.Base64
import kotlin.math.max

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

internal fun increaseBufferSize(currentSize: Int, requiredInc: Int, elemSizeBytes: Int, growFactor: Float = 2f): Int {
    val memLimitMax = Int.MAX_VALUE.toLong() / elemSizeBytes
    val increased = max(currentSize + requiredInc.toLong(), (currentSize * growFactor).toLong())
        .coerceAtMost(memLimitMax).toInt()
    check(increased - currentSize >= requiredInc) {
        "Unable to increase buffer to requested size of ${currentSize + requiredInc} elements. Underlying buffer " +
        "size is limited to $memLimitMax elements (one element is $elemSizeBytes bytes and total buffer must not " +
        "exceed 2 GB)."
    }
    return increased
}
