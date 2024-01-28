package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TextureData

class WgpuLoadedTexture(
    val texture: WgpuTextureResource,
    override val width: Int,
    override val height: Int,
    override val depth: Int
): LoadedTexture {

    override var isReleased: Boolean = false
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