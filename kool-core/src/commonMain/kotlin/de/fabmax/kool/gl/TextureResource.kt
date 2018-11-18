package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps

class TextureResource private constructor(glRef: Any, val props: TextureProps, ctx: KoolContext) :
        GlResource(glRef, Type.TEXTURE, ctx) {

    val target: Int get() = props.target

    var isLoaded = false

    var texUnit = -1
        internal set

    override fun delete(ctx: KoolContext) {
        glDeleteTexture(this)
        super.delete(ctx)
    }

    companion object {
        fun create(props: TextureProps, ctx: KoolContext): TextureResource {
            return TextureResource(glCreateTexture(), props, ctx)
        }
    }
}