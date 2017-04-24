package de.fabmax.kool.gl

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext

class RenderbufferResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.RENDERBUFFER, ctx) {
    companion object {
        fun create(ctx: RenderContext): RenderbufferResource {
            return RenderbufferResource(GL.createRenderbuffer(), ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteRenderbuffer(this)
        super.delete(ctx)
    }
}