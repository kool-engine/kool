package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGetTexImage

internal object TextureCopyHelper {

    fun readTexturePixels(src: LoadedTextureGl, dst: TextureData) {
        glBindTexture(src.target, src.glTexture.handle)
        when (val buf = dst.data) {
            is Uint8BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, dst.format.glFormat(GlImpl), dst.format.glType(GlImpl), it)
            }
            is Int32BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, dst.format.glFormat(GlImpl), dst.format.glType(GlImpl), it)
            }
            is Float32BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, dst.format.glFormat(GlImpl), dst.format.glType(GlImpl), it)
            }
            else -> throw IllegalArgumentException("Unsupported target buffer type")
        }
    }
}
