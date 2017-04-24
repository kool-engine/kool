package de.fabmax.kool.gl

import de.fabmax.kool.TextureProps
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext

class TextureResource private constructor(glRef: Any, val target: Int, val props: TextureProps, ctx: RenderContext) :
        GlResource(glRef, Type.TEXTURE, ctx) {

    companion object {
        fun create(target: Int, props: TextureProps, ctx: RenderContext): TextureResource {
            return TextureResource(GL.createTexture(), target, props, ctx)
        }
    }

    var isLoaded = false

    var texUnit = -1
        internal set

    init {
        GL.bindTexture(GL.TEXTURE_2D, this)
        GL.texParameteri(target, GL.TEXTURE_MIN_FILTER, props.minFilter)
        GL.texParameteri(target, GL.TEXTURE_MAG_FILTER, props.magFilter)
        GL.texParameteri(target, GL.TEXTURE_WRAP_S, props.xWrapping)
        GL.texParameteri(target, GL.TEXTURE_WRAP_T, props.yWrapping)
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteTexture(this)
        super.delete(ctx)
    }
}