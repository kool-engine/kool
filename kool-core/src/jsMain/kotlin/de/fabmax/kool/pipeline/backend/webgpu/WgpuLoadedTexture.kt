package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TextureData

class WgpuLoadedTexture(
    val texture: WgpuTextureResource,
): LoadedTexture {

    override val width: Int = texture.gpuTexture.width
    override val height: Int = texture.gpuTexture.height
    override val depth: Int = texture.gpuTexture.depthOrArrayLayers

    override var isReleased: Boolean = false
        private set

    override fun readTexturePixels(targetData: TextureData) {
        TODO("Not yet implemented")
    }

    override fun release() {
        if (!isReleased) {
            isReleased = true
            texture.release()
        }
    }
}