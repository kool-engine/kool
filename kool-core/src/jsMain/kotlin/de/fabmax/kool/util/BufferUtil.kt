package de.fabmax.kool.util

import de.fabmax.kool.platform.Pako
import kotlin.io.encoding.Base64

actual object BufferUtil {
    actual fun inflate(zipData: Uint8Buffer): Uint8Buffer {
        val uint8Data = (zipData as Uint8BufferImpl).buffer
        return Uint8BufferImpl(Pako.inflate(uint8Data))
    }

    actual fun deflate(data: Uint8Buffer): Uint8Buffer {
        val uint8Data = (data as Uint8BufferImpl).buffer
        return Uint8BufferImpl(Pako.gzip(uint8Data))
    }

    actual fun encodeBase64(data: Uint8Buffer) = Base64.encode(data.toArray())

    actual fun decodeBase64(base64: String): Uint8Buffer = Base64.decode(base64).toBuffer()
}