package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.JsContext
import org.khronos.webgl.WebGLTexture

actual class LoadedTexture(val ctx: JsContext, val texture: WebGLTexture?, estimatedSize: Int) {

    val texId = nextTexId++
    var isDestroyed = false
        private set

    init {
        ctx.engineStats.textureAllocated(texId, estimatedSize)
    }

    actual fun dispose() {
        isDestroyed = true
        ctx.gl.deleteTexture(texture)
        ctx.engineStats.textureDeleted(texId)
    }

    companion object {
        private var nextTexId = 1L
    }
}