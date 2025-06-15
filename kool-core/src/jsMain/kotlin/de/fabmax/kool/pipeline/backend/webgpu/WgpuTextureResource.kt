package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.pipeline.backend.stats.TextureInfo
import de.fabmax.kool.util.BaseReleasable

class WgpuTextureResource(
    val imageInfo: GPUTextureDescriptor,
    val gpuTexture: GPUTexture
) : BaseReleasable(), GpuTexture {
    override val width: Int get() = gpuTexture.width
    override val height: Int get() = gpuTexture.height
    override val depth: Int get() = gpuTexture.depthOrArrayLayers

    private val textureInfo = TextureInfo(
        texture = null,
        size = (gpuTexture.width * gpuTexture.height * gpuTexture.depthOrArrayLayers * imageInfo.bytesPerPx * imageInfo.mipMapFactor).toLong()
    )

    private val GPUTextureDescriptor.bytesPerPx: Int get() {
        val channels = when {
            "rgba" in format.enumValue -> 4
            "rg" in format.enumValue -> 2
            "r" in format.enumValue -> 1
            else -> 1
        }
        return when {
            "8" in format.enumValue -> 1 * channels
            "16" in format.enumValue -> 2 * channels
            "32" in format.enumValue -> 4 * channels
            else -> 4
        }
    }

    private val GPUTextureDescriptor.mipMapFactor: Double get() = if (mipLevelCount > 1) { 1.333 } else 1.0

    override fun release() {
        super.release()
        gpuTexture.destroy()
        textureInfo.deleted()
    }
}