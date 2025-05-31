package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.math.float32ToFloat16
import de.fabmax.kool.pipeline.BufferedImageData
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.pipeline.isF16
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint16BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import io.ygdrasil.webgpu.Extent3D
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUOrigin3D
import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.Queue
import io.ygdrasil.webgpu.TexelCopyTextureInfo
import io.ygdrasil.webgpu.Texture
import io.ygdrasil.webgpu.WGPUCopyExternalImageDestInfo
import io.ygdrasil.webgpu.WGPUCopyExternalImageSourceInfo
import io.ygdrasil.webgpu.WGPUExtent3D
import io.ygdrasil.webgpu.WGPUOrigin3D
import io.ygdrasil.webgpu.createJsObject
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

internal actual fun copyNativeTextureData(
    src: ImageData,
    dst: GPUTexture,
    size: Extent3D,
    dstOrigin: GPUOrigin3D,
    device: GPUDevice
): Unit = when (src) {
    is BufferedImageData -> {
        device.queue.writeTexture(
            data = src.arrayBufferView.unsafeCast<ArrayBuffer>(),
            destination = TexelCopyTextureInfo(dst, origin = dstOrigin),
            dataLayout = src.gpuImageDataLayout,
            size = size
        )
    }
    is ImageTextureData -> copyTextureData(src, dst, size, dstOrigin, device)
    else -> error("Not implemented: ${src::class.simpleName}")
}

internal fun copyTextureData(
    src: ImageTextureData,
    dst: GPUTexture,
    size: Extent3D,
    dstOrigin: GPUOrigin3D,
    device: GPUDevice
) {
    val queue = (device.queue as Queue).handler
    queue.copyExternalImageToTexture(
        source = createJsObject<WGPUCopyExternalImageSourceInfo>().apply {
            source = src.data.asDynamic()
        },
        destination = createJsObject<WGPUCopyExternalImageDestInfo>().apply {
            texture = (dst as Texture).handler.asDynamic()
            mipLevel = 0.asDynamic()
            origin = createJsObject<WGPUOrigin3D>().apply {
                x = dstOrigin.x.asDynamic()
                y = dstOrigin.y.asDynamic()
                z = dstOrigin.z.asDynamic()
            }
        },
        copySize = createJsObject<WGPUExtent3D>().apply {
            width = size.width.asDynamic()
            height = size.height.asDynamic()
            depthOrArrayLayers = size.depthOrArrayLayers.asDynamic()
        }
    )
}

private val ImageData.arrayBufferView: ArrayBufferView get() {
    check(this is BufferedImageData)

    val bufData = data
    return when {
        format.isF16 && bufData is Float32BufferImpl -> {
            val f32Array = bufData.buffer
            val f16Buffer = Uint8Array(f32Array.length * 2)
            for (i in 0 until f32Array.length) {
                f16Buffer.putF16(i, f32Array[i])
            }
            f16Buffer
        }
        bufData is Uint8BufferImpl -> bufData.buffer
        bufData is Uint16BufferImpl -> bufData.buffer
        bufData is Float32BufferImpl -> bufData.buffer
        else -> throw IllegalArgumentException("Unsupported buffer type")
    }
}

private fun Uint8Array.putF16(index: Int, f32: Float) {
    float32ToFloat16(f32) { high, low ->
        val byteI = index * 2
        set(byteI, low)
        set(byteI+1, high)
    }
}