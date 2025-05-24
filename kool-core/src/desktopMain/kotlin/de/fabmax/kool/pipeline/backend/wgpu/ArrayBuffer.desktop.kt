package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import io.ygdrasil.webgpu.ArrayBuffer

fun Float32Buffer.asArrayBuffer(): ArrayBuffer = TODO("Not yet implemented")
fun Int32Buffer.asArrayBuffer(): ArrayBuffer = TODO("Not yet implemented")
fun MixedBuffer.asArrayBuffer(): ArrayBuffer = TODO("Not yet implemented")
actual fun ArrayBuffer.writeInto(target: Buffer): Unit = TODO("Not yet implemented")
actual fun Buffer.asArrayBuffer(): ArrayBuffer = TODO("Not yet implemented")