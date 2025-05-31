package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.Uint16BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import io.ygdrasil.webgpu.ArrayBuffer
import java.lang.foreign.MemorySegment

actual fun ArrayBuffer.writeInto(target: Buffer): Unit {
    target.asNioBuffer()
        .let(MemorySegment::ofBuffer)
        .copyFrom(asMemorySegment())
}

actual fun Buffer.asArrayBuffer(block: (ArrayBuffer) -> Unit) {
    this.asNioBuffer()
        .asArrayBuffer(block)
}

fun java.nio.Buffer.asArrayBuffer(block: (ArrayBuffer) -> Unit) {
    this.let(MemorySegment::ofBuffer)
        .let { ArrayBuffer(it.address().toULong(), it.byteSize().toULong()) }
        .also { block(it) }
}

@OptIn(ExperimentalUnsignedTypes::class)
actual fun ArrayBuffer.asUIntArray(): UIntArray = TODO("Not yet implemented")

private fun Buffer.asNioBuffer(): java.nio.Buffer = when (this) {
    is Float32BufferImpl -> this.getRawBuffer()
    is Int32BufferImpl -> this.getRawBuffer()
    is MixedBufferImpl -> this.getRawBuffer()
    is Uint16BufferImpl -> this.getRawBuffer()
    is Uint8BufferImpl -> this.getRawBuffer()
    else -> error("Unsupported buffer type ${this::class.simpleName}")
}

private fun ArrayBuffer.asMemorySegment() = MemorySegment
    .ofAddress(rawPointer.toLong())
    .reinterpret(size.toLong())