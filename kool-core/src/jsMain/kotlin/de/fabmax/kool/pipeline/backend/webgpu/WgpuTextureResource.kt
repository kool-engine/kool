package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.pipeline.backend.stats.TextureInfo
import de.fabmax.kool.util.BaseReleasable

class WgpuTextureResource(val gpuTexture: GPUTexture, texture: Texture<*>) : BaseReleasable(), GpuTexture {
    override val width: Int get() = gpuTexture.width
    override val height: Int get() = gpuTexture.height
    override val depth: Int get() = gpuTexture.depthOrArrayLayers

    private val textureInfo = TextureInfo(
        texture = texture,
        size = (gpuTexture.width * gpuTexture.height * gpuTexture.depthOrArrayLayers * texture.bytePerPx * texture.mipMapFactor).toLong()
    )

    private val Texture<*>.bytePerPx: Int get() = props.format.pxSize

    private val Texture<*>.mipMapFactor: Double get() = if (props.isMipMapped) { 1.333 } else 1.0

    override fun release() {
        super.release()
        gpuTexture.destroy()
        textureInfo.deleted()
    }
}