package de.fabmax.kool.pipeline

import org.khronos.webgl.WebGLTexture

actual class LoadedTexture(val texture: WebGLTexture?) {
    var isDestroyed = false
        private set

    actual fun dispose() {
    }
}