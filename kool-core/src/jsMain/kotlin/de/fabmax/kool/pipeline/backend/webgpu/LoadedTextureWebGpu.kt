package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TextureData

class LoadedTextureWebGpu(
    val texture: GPUTexture,
    override val width: Int,
    override val height: Int,
    override val depth: Int
): LoadedTexture {

    override val isReleased: Boolean = false
    override fun readTexturePixels(targetData: TextureData) {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}