package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Buffer
import io.ygdrasil.webgpu.ArrayBuffer


expect fun Buffer.asArrayBuffer(): ArrayBuffer
expect fun ArrayBuffer.writeInto(target: Buffer)
expect fun ArrayBuffer.asUInt32Array(): UIntArray
