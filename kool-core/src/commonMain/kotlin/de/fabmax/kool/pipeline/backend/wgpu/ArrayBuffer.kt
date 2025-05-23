package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import io.ygdrasil.webgpu.ArrayBuffer

expect fun Float32Buffer.asArrayBuffer(): ArrayBuffer
expect fun Int32Buffer.asArrayBuffer(): ArrayBuffer
expect fun MixedBuffer.asArrayBuffer(): ArrayBuffer