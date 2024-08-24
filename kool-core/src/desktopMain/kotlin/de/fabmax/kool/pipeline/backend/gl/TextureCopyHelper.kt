package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_TEXTURE_3D

internal object TextureCopyHelper {

    fun readTexturePixels(src: LoadedTextureGl, dst: BufferedImageData): Boolean {
        return when {
            src.target == GL_TEXTURE_1D && dst is BufferedImageData1d -> readPixels(src, dst.data, dst.format)
            src.target == GL_TEXTURE_2D && dst is BufferedImageData2d -> readPixels(src, dst.data, dst.format)
            src.target == GL_TEXTURE_3D && dst is BufferedImageData3d -> readPixels(src, dst.data, dst.format)
            else -> false
        }
    }

    private fun readPixels(src: LoadedTextureGl, buf: Buffer, format: TexFormat): Boolean {
        glBindTexture(src.target, src.glTexture.handle)
        when (buf) {
            is Uint8BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, format.glFormat(GlImpl), format.glType(GlImpl), it)
            }
            is Int32BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, format.glFormat(GlImpl), format.glType(GlImpl), it)
            }
            is Float32BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, format.glFormat(GlImpl), format.glType(GlImpl), it)
            }
            else -> return false
        }
        return true
    }
}
