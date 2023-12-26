package de.fabmax.kool.util

import de.fabmax.kool.platform.Pako

actual fun Uint8Buffer.deflate(): Uint8Buffer {
    val uint8Data = (this as Uint8BufferImpl).buffer
    return Uint8BufferImpl(Pako.gzip(uint8Data))
}

actual fun Uint8Buffer.inflate(): Uint8Buffer {
    val uint8Data = (this as Uint8BufferImpl).buffer
    return Uint8BufferImpl(Pako.inflate(uint8Data))
}
