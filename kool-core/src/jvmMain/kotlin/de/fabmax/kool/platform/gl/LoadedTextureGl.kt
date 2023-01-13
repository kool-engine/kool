package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.AddressMode
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logW
import org.lwjgl.opengl.GL12.*
import org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT
import java.awt.image.BufferedImage
import kotlin.math.min

class LoadedTextureGl(val ctx: Lwjgl3Context, val target: Int, val texture: Int, estimatedSize: Int) : LoadedTexture {

    val texId = nextTexId++
    var isDestroyed = false
        private set

    override var width = 0
    override var height = 0
    override var depth = 0

    init {
        ctx.engineStats.textureAllocated(texId, estimatedSize)
    }

    fun setSize(width: Int, height: Int, depth: Int) {
        this.width = width
        this.height = height
        this.depth = depth
    }

    fun applySamplerProps(props: TextureProps) {
        glBindTexture(target, texture)

        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, props.minFilter.glMinFilterMethod(props.mipMapping))
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, props.magFilter.glMagFilterMethod())
        glTexParameteri(target, GL_TEXTURE_WRAP_S, props.addressModeU.glAddressMode())
        glTexParameteri(target, GL_TEXTURE_WRAP_T, props.addressModeV.glAddressMode())
        if (target == GL_TEXTURE_3D) {
            glTexParameteri(target, GL_TEXTURE_WRAP_R, props.addressModeW.glAddressMode())
        }

        val backend = ctx.renderBackend as GlRenderBackend
        val anisotropy = min(props.maxAnisotropy, backend.glCapabilities.maxAnisotropy)
        if (anisotropy > 1) {
            glTexParameteri(target, backend.glCapabilities.glTextureMaxAnisotropyExt, anisotropy)
        }

        if (anisotropy > 1 && (props.minFilter == FilterMethod.NEAREST || props.magFilter == FilterMethod.NEAREST)) {
            logW { "Texture filtering is NEAREST but anisotropy is $anisotropy (> 1)" }
        }
    }

    fun copyToBufferedImage(): BufferedImage {
        glBindTexture(target, texture)
        val pixels = createUint8Buffer(width * height * 4)
        glGetTexImage(target, 0, GL_RGBA, GL_UNSIGNED_BYTE, (pixels as Uint8BufferImpl).buffer)

        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (i in 0 until width * height) {
            // swap byte order (rgba -> abgr)
            val bi = i * 4
            val r = pixels[bi].toUByte().toInt()
            val g = pixels[bi+1].toUByte().toInt()
            val b = pixels[bi+2].toUByte().toInt()
            val a = pixels[bi+3].toUByte().toInt()
            val rgba = (a shl 24) or (r shl 16) or (g shl 8) or b
            // todo: setting individual pixels is rather slow
            img.setRGB(i % width, height - 1 - i / width, rgba)
        }
        return img
    }

    override fun dispose() {
        if (!isDestroyed) {
            isDestroyed = true
            glDeleteTextures(texture)
            ctx.engineStats.textureDeleted(texId)
        }
    }

    private fun FilterMethod.glMinFilterMethod(mipMapping: Boolean): Int {
        return when(this) {
            FilterMethod.NEAREST -> if (mipMapping) GL_NEAREST_MIPMAP_NEAREST else GL_NEAREST
            FilterMethod.LINEAR -> if (mipMapping) GL_LINEAR_MIPMAP_LINEAR else GL_LINEAR
        }
    }

    private fun FilterMethod.glMagFilterMethod(): Int {
        return when (this) {
            FilterMethod.NEAREST -> GL_NEAREST
            FilterMethod.LINEAR -> GL_LINEAR
        }
    }

    private fun AddressMode.glAddressMode(): Int {
        return when(this) {
            AddressMode.CLAMP_TO_EDGE -> GL_CLAMP_TO_EDGE
            AddressMode.MIRRORED_REPEAT -> GL_MIRRORED_REPEAT
            AddressMode.REPEAT -> GL_REPEAT
        }
    }

    companion object {
        private var nextTexId = 1L
    }
}