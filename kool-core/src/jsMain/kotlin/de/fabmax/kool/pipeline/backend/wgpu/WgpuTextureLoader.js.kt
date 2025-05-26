package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.platform.ImageTextureData
import io.ygdrasil.webgpu.Extent3D
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.Origin3D
import io.ygdrasil.webgpu.Queue
import io.ygdrasil.webgpu.Texture
import io.ygdrasil.webgpu.WGPUCopyExternalImageDestInfo
import io.ygdrasil.webgpu.WGPUCopyExternalImageSourceInfo
import io.ygdrasil.webgpu.WGPUExtent3D
import io.ygdrasil.webgpu.WGPUOrigin3D
import io.ygdrasil.webgpu.createJsObject

internal actual fun copyNativeTextureData(
    src: ImageData,
    dst: GPUTexture,
    size: Extent3D,
    device: GPUDevice
): Unit = when (src) {
    // Unsupported
    is ImageTextureData -> copyTextureData(src, dst, size, Origin3D(0u, 0u, 0u), device)
    else -> error("Not implemented: ${src::class.simpleName}")
}

private fun copyTextureData(
    src: ImageTextureData,
    dst: GPUTexture,
    size: Extent3D,
    dstOrigin: Origin3D,
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
