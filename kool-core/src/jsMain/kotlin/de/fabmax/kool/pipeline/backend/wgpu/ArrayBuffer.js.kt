package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.Uint16BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import io.ygdrasil.webgpu.ArrayBuffer
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.Uint8Array

actual fun ArrayBuffer.writeInto(target: Buffer): Unit = when (target) {
    is Float32BufferImpl -> target.buffer.set(Float32Array(this))
    is Int32BufferImpl -> target.buffer.set(Int32Array(this))
    is MixedBufferImpl -> Uint8Array(target.buffer.buffer).set(Uint8Array(this))
    is Uint16BufferImpl -> target.buffer.set(Uint16Array(this))
    is Uint8BufferImpl -> target.buffer.set(Uint8Array(this))
    else -> error("Unsupported buffer type ${target::class.simpleName}")
}

private fun Buffer.asArrayBuffer(): ArrayBuffer = when (this) {
    is Float32BufferImpl -> this.buffer.buffer
    is Int32BufferImpl -> this.buffer.buffer
    is MixedBufferImpl ->  this.buffer.buffer
    is Uint16BufferImpl -> this.buffer.buffer
    is Uint8BufferImpl -> this.buffer.buffer
    else -> error("Unsupported buffer type ${this::class.simpleName}")
}

@OptIn(ExperimentalUnsignedTypes::class)
actual fun ArrayBuffer.asUIntArray(): UIntArray = Int32Array(this)
    .unsafeCast<IntArray>().asUIntArray()

actual fun Buffer.asArrayBuffer(block: (ArrayBuffer) -> Unit) {
    block(asArrayBuffer())
}