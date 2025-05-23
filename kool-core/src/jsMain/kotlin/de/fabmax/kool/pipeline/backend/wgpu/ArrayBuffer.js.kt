package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.MixedBufferImpl
import io.ygdrasil.webgpu.ArrayBuffer

actual fun Float32Buffer.asArrayBuffer(): ArrayBuffer = (this as Float32BufferImpl).buffer.buffer
actual fun Int32Buffer.asArrayBuffer(): ArrayBuffer = (this as Int32BufferImpl).buffer.buffer
actual fun MixedBuffer.asArrayBuffer(): ArrayBuffer = (this as MixedBufferImpl).buffer.buffer