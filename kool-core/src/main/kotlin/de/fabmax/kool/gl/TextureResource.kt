package de.fabmax.kool.gl

import de.fabmax.kool.RenderContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.glCapabilities
import kotlin.math.max

class TextureResource private constructor(glRef: Any, val target: Int, val props: TextureProps, ctx: RenderContext) :
        GlResource(glRef, Type.TEXTURE, ctx) {

    companion object {
        fun create(target: Int, props: TextureProps, ctx: RenderContext): TextureResource {
            return TextureResource(glCreateTexture(), target, props, ctx)
        }
    }

    var isLoaded = false

    var texUnit = -1
        internal set

    init {
        glBindTexture(GL_TEXTURE_2D, this)
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, props.minFilter)
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, props.magFilter)
        glTexParameteri(target, GL_TEXTURE_WRAP_S, props.xWrapping)
        glTexParameteri(target, GL_TEXTURE_WRAP_T, props.yWrapping)

        if (props.anisotropy > 1 && glCapabilities.anisotropicTexFilterInfo.isSupported) {
            val anisotropy = max(glCapabilities.anisotropicTexFilterInfo.maxAnisotropy.toInt(), props.anisotropy)
            glTexParameteri(target, glCapabilities.anisotropicTexFilterInfo.TEXTURE_MAX_ANISOTROPY_EXT, anisotropy)
        }
    }

    override fun delete(ctx: RenderContext) {
        glDeleteTexture(this)
        super.delete(ctx)
    }
}