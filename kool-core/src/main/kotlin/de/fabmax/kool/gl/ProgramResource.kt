package de.fabmax.kool.gl

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext

class ProgramResource private constructor(glRef: Any, ctx: RenderContext) : GlResource(glRef, Type.PROGRAM) {
    companion object {
        fun create(ctx: RenderContext): ProgramResource {
            return ProgramResource(GL.createProgram(), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteProgram(this)
        super.delete(ctx)
    }

    fun attachShader(shader: ShaderResource, ctx: RenderContext) {
        GL.attachShader(this, shader)
    }

    fun link(ctx: RenderContext): Boolean {
        GL.linkProgram(this)
        return GL.getProgrami(this, GL.LINK_STATUS) == GL.TRUE
    }

    fun getInfoLog(ctx: RenderContext): String {
        return GL.getProgramInfoLog(this)
    }
}