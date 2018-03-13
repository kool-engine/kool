package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps
import kotlin.math.max

class TextureResource private constructor(glRef: Any, val target: Int, val props: TextureProps, ctx: KoolContext) :
        GlResource(glRef, Type.TEXTURE, ctx) {

    companion object {
        fun create(target: Int, props: TextureProps, ctx: KoolContext): TextureResource {
            return TextureResource(glCreateTexture(), target, props, ctx)
        }
    }

    var isLoaded = false

    var texUnit = -1
        internal set

    init {
        // target == GL_TEXTURE_2D
        glBindTexture(target, this)
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, props.minFilter)
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, props.magFilter)
        glTexParameteri(target, GL_TEXTURE_WRAP_S, props.xWrapping)
        glTexParameteri(target, GL_TEXTURE_WRAP_T, props.yWrapping)

        if (props.anisotropy > 1 && ctx.glCapabilities.anisotropicTexFilterInfo.isSupported) {
            val anisotropy = max(ctx.glCapabilities.anisotropicTexFilterInfo.maxAnisotropy.toInt(), props.anisotropy)
            glTexParameteri(target, ctx.glCapabilities.anisotropicTexFilterInfo.TEXTURE_MAX_ANISOTROPY_EXT, anisotropy)
        }
    }

    override fun delete(ctx: KoolContext) {
        glDeleteTexture(this)
        super.delete(ctx)
    }
}