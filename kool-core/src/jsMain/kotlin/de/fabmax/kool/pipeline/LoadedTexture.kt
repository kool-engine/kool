package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.JsContext
import org.khronos.webgl.WebGLTexture

actual class LoadedTexture(val ctx: JsContext, val texture: WebGLTexture?) {
    var isDestroyed = false
        private set

    actual fun dispose() {
        isDestroyed = true
        ctx.gl.deleteTexture(texture)
    }
}