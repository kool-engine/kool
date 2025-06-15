package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import io.ygdrasil.webgpu.ArrayBuffer

actual fun ArrayBuffer.writeInto(target: Buffer): Unit = TODO("Not yet implemented")
@OptIn(ExperimentalUnsignedTypes::class)
actual fun ArrayBuffer.asUIntArray(): UIntArray = TODO("Not yet implemented")
actual fun Buffer.asArrayBuffer(block: (ArrayBuffer) -> Unit): Unit = TODO("Not yet implemented")