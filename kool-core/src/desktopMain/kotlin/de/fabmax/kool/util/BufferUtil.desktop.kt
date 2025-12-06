package de.fabmax.kool.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

actual fun Uint8Buffer.deflate(): Uint8Buffer {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).use { it.write(toArray()) }
    return Uint8BufferImpl(bos.toByteArray())
}

actual fun Uint8Buffer.inflate(): Uint8Buffer {
    return Uint8BufferImpl(GZIPInputStream(ByteArrayInputStream(toArray())).readBytes())
}

fun String.toByteBuffer(): ByteBuffer {
    val bytes = toByteArray()
    val buffer = ByteBuffer.allocateDirect(bytes.size + 1)
    buffer.put(bytes)
    buffer.put(0)
    buffer.flip()
    return buffer
}