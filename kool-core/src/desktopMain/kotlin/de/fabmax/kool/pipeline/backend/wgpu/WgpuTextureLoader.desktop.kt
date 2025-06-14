package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.math.float32ToFloat16
import de.fabmax.kool.pipeline.BufferedImageData
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.pipeline.isF16
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint16BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import io.ygdrasil.webgpu.Extent3D
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUOrigin3D
import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.TexelCopyTextureInfo
import kotlin.IllegalArgumentException

internal actual fun copyNativeTextureData(
    src: ImageData,
    dst: GPUTexture,
    size: Extent3D,
    dstOrigin: GPUOrigin3D,
    device: GPUDevice
) {
    when (src) {
        is BufferedImageData -> {
            src.arrayBufferView.asArrayBuffer { arrayBuffer ->
                device.queue.writeTexture(
                    data = arrayBuffer,
                    destination = TexelCopyTextureInfo(dst, origin = dstOrigin),
                    dataLayout = src.gpuImageDataLayout,
                    size = size
                )
            }
        }
        else -> error("Not implemented: ${src::class.simpleName}")
    }

}

private val ImageData.arrayBufferView: java.nio.Buffer
    get() {
    check(this is BufferedImageData)

    val bufData = data
    return when {
        format.isF16 && bufData is Float32BufferImpl -> {
            val f32Array = bufData
            val f16Buffer = Uint8BufferImpl(f32Array.capacity * 2)

            for (i in 0 until f32Array.capacity) {
                f16Buffer.putF16(i, f32Array[i])
            }
            f16Buffer.getRawBuffer()
        }
        bufData is Uint8BufferImpl -> bufData.getRawBuffer()
        bufData is Uint16BufferImpl -> bufData.getRawBuffer()
        bufData is Float32BufferImpl -> bufData.getRawBuffer()
        else -> throw IllegalArgumentException("Unsupported buffer type")
    }
}

private fun Uint8BufferImpl.putF16(index: Int, f32: Float) {
    float32ToFloat16(f32) { high, low ->
        val byteI = index * 2
        set(byteI, low.toUByte())
        set(byteI+1, high.toUByte())
    }
}