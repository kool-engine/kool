package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_TEXTURE_3D

internal object TextureCopyHelper {

    fun readTexturePixels(src: LoadedTextureGl, dst: TextureData): Boolean {
        if (src.target != GL_TEXTURE_1D && src.target != GL_TEXTURE_2D && src.target != GL_TEXTURE_3D) {
            return false
        }

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
            else -> return false
        }
        return true
    }
}
