package de.fabmax.kool.gl

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext

class FramebufferResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.FRAMEBUFFER) {
    companion object {
        fun create(ctx: RenderContext): FramebufferResource {
            return FramebufferResource(GL.Companion.createFramebuffer(), ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteFramebuffer(this)
        super.delete(ctx)
    }
}