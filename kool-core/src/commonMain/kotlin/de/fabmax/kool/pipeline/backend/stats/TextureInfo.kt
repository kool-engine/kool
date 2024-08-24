package de.fabmax.kool.pipeline.backend.stats

import de.fabmax.kool.pipeline.Texture

class TextureInfo(val texture: Texture<*>, var size: Long = 0L) : ResourceInfo(texture.name) {
    init {
        BackendStats.allocatedTextures[id] = this
        BackendStats.totalTextureSize += size
    }

    override fun deleted() {
        BackendStats.totalTextureSize -= size
        BackendStats.allocatedTextures -= id
    }
}