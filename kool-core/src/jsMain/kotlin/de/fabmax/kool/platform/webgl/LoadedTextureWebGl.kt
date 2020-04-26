package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.platform.JsContext
import org.khronos.webgl.WebGLTexture

class LoadedTextureWebGl(val ctx: JsContext, val texture: WebGLTexture?, estimatedSize: Int) : LoadedTexture {

    val texId = nextTexId++
    var isDestroyed = false
        private set

    init {
        ctx.engineStats.textureAllocated(texId, estimatedSize)
    }

    override fun dispose() {
        isDestroyed = true
        ctx.gl.deleteTexture(texture)
        ctx.engineStats.textureDeleted(texId)
    }

    companion object {
        private var nextTexId = 1L
    }
}