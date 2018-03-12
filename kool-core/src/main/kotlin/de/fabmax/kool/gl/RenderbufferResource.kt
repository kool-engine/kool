package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext

class RenderbufferResource private constructor(glRef: Any, ctx: KoolContext) :
        GlResource(glRef, Type.RENDERBUFFER, ctx) {
    companion object {
        fun create(ctx: KoolContext): RenderbufferResource {
            return RenderbufferResource(glCreateRenderbuffer(), ctx)
        }
    }

    override fun delete(ctx: KoolContext) {
        glDeleteRenderbuffer(this)
        super.delete(ctx)
    }
}