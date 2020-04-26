package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL11.glDeleteTextures

class LoadedTextureGl(val ctx: Lwjgl3Context, val texture: Int, estimatedSize: Int) : LoadedTexture {

    val texId = nextTexId++
    var isDestroyed = false
        private set

    init {
        ctx.engineStats.textureAllocated(texId, estimatedSize)
    }

    override fun dispose() {
        isDestroyed = true
        glDeleteTextures(texture)
        ctx.engineStats.textureDeleted(texId)
    }

    companion object {
        private var nextTexId = 1L
    }
}