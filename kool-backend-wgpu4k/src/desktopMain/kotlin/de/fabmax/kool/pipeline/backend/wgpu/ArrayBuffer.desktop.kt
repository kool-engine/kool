package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.*
import io.ygdrasil.webgpu.ArrayBuffer
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

actual fun ArrayBuffer.writeInto(target: Buffer) {
    target.asNioBuffer()
        .let(MemorySegment::ofBuffer)
        .copyFrom(asArrayBuffer())
}

actual fun Buffer.asArrayBuffer(block: (ArrayBuffer) -> Unit) {
    this.asNioBuffer()
        .asArrayBuffer(block)
}

fun java.nio.Buffer.asArrayBuffer(block: (ArrayBuffer) -> Unit) {
    asArrayBuffer()
        .also { block(it) }
}

@OptIn(ExperimentalUnsignedTypes::class)
actual fun ArrayBuffer.asUIntArray(): UIntArray = asMemorySegment()
    .toArray(ValueLayout.JAVA_INT)
    .asUIntArray()

private fun ArrayBuffer.asMemorySegment(): MemorySegment = MemorySegment.ofAddress(rawPointer.toLong())
    .reinterpret(size.toLong())

private fun java.nio.Buffer.asArrayBuffer(): ArrayBuffer = this.let(MemorySegment::ofBuffer)
    .let { ArrayBuffer(it.address().toULong(), it.byteSize().toULong()) }

private fun Buffer.asNioBuffer(): java.nio.Buffer = when (this) {
    is Float32BufferImpl -> this.getRawBuffer()
    is Int32BufferImpl -> this.getRawBuffer()
    is MixedBufferImpl -> this.getRawBuffer()
    is Uint16BufferImpl -> this.getRawBuffer()
    is Uint8BufferImpl -> this.getRawBuffer()
    else -> error("Unsupported buffer type ${this::class.simpleName}")
}

private fun ArrayBuffer.asArrayBuffer() = MemorySegment
    .ofAddress(rawPointer.toLong())
    .reinterpret(size.toLong())