package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logW
import org.lwjgl.opengl.GL14.*
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

        val backend = ctx.backend as GlRenderBackend
        val anisotropy = min(props.maxAnisotropy, backend.glCapabilities.maxAnisotropy)
        if (anisotropy > 1) {
            glTexParameteri(target, backend.glCapabilities.glTextureMaxAnisotropyExt, anisotropy)
        }

        if (anisotropy > 1 && (props.minFilter == FilterMethod.NEAREST || props.magFilter == FilterMethod.NEAREST)) {
            logW { "Texture filtering is NEAREST but anisotropy is $anisotropy (> 1)" }
        }
    }

    override fun readTexturePixels(targetData: TextureData) {
        glBindTexture(target, texture)
        when (val buf = targetData.data) {
            is Uint8BufferImpl -> {
                buf.useRaw {
                    glGetTexImage(target, 0, targetData.format.glFormat, targetData.format.glType, it)
                }
            }
            is Float32BufferImpl -> {
                buf.useRaw {
                    glGetTexImage(target, 0, targetData.format.glFormat, targetData.format.glType, it)
                }
            }
        }
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
