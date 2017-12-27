package de.fabmax.kool.gl

import de.fabmax.kool.RenderContext

class RenderbufferResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.RENDERBUFFER, ctx) {
    companion object {
        fun create(ctx: RenderContext): RenderbufferResource {
            return RenderbufferResource(glCreateRenderbuffer(), ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        glDeleteRenderbuffer(this)
        super.delete(ctx)
    }
}