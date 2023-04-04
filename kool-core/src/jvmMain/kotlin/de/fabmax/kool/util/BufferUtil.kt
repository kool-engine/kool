package de.fabmax.kool.util

import de.fabmax.kool.use
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

actual object BufferUtil {
    actual fun inflate(zipData: Uint8Buffer): Uint8Buffer =
        Uint8BufferImpl(GZIPInputStream(ByteArrayInputStream(zipData.toArray())).readBytes())

    actual fun deflate(data: Uint8Buffer): Uint8Buffer {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).use { it.write(data.toArray()) }
        return Uint8BufferImpl(bos.toByteArray())
    }

    actual fun encodeBase64(data: Uint8Buffer) = kotlin.io.encoding.Base64.encode(data.toArray())

    actual fun decodeBase64(base64: String): Uint8Buffer = kotlin.io.encoding.Base64.decode(base64).toBuffer()
}