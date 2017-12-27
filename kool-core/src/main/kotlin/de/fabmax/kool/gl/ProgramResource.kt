package de.fabmax.kool.gl

import de.fabmax.kool.RenderContext

class ProgramResource private constructor(glRef: Any, ctx: RenderContext) : GlResource(glRef, Type.PROGRAM, ctx) {
    companion object {
        fun create(ctx: RenderContext): ProgramResource {
            return ProgramResource(glCreateProgram(), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: RenderContext) {
        glDeleteProgram(this)
        super.delete(ctx)
    }

    fun attachShader(shader: ShaderResource, ctx: RenderContext) {
        glAttachShader(this, shader)
    }

    fun link(ctx: RenderContext): Boolean {
        glLinkProgram(this)
        return glGetProgrami(this, GL_LINK_STATUS) == GL_TRUE
    }

    fun getInfoLog(ctx: RenderContext): String {
        return glGetProgramInfoLog(this)
    }
}