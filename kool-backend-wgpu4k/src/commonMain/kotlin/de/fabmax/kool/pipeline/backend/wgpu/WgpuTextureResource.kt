package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.pipeline.backend.stats.TextureInfo
import de.fabmax.kool.util.BaseReleasable
import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.GPUTextureDescriptor

class WgpuTextureResource(
    val imageInfo: GPUTextureDescriptor,
    val gpuTexture: GPUTexture
) : BaseReleasable(), GpuTexture {
    override val width: Int get() = gpuTexture.width.toInt()
    override val height: Int get() = gpuTexture.height.toInt()
    override val depth: Int get() = gpuTexture.depthOrArrayLayers.toInt()

    private val textureInfo = TextureInfo(
        texture = null,
        size = (gpuTexture.width.toInt() * gpuTexture.height.toInt() * gpuTexture.depthOrArrayLayers.toInt() * imageInfo.bytesPerPx * imageInfo.mipMapFactor).toLong()
    )

    private val GPUTextureDescriptor.bytesPerPx: Int get() {
        val channels = when {
            "rgba" in format.name.lowercase() -> 4
            "rg" in format.name.lowercase() -> 2
            "r" in format.name.lowercase() -> 1
            else -> 1
        }
        return when {
            "8" in format.name.lowercase() -> 1 * channels
            "16" in format.name.lowercase() -> 2 * channels
            "32" in format.name.lowercase() -> 4 * channels
            else -> 4
        }
    }

    private val GPUTextureDescriptor.mipMapFactor: Double get() = if (mipLevelCount > 1u) { 1.333 } else 1.0

    override fun release() {
        super.release()
        gpuTexture.close()
        textureInfo.deleted()
    }
}