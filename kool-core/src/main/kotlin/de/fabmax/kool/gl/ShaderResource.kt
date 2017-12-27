package de.fabmax.kool.gl

import de.fabmax.kool.RenderContext

class ShaderResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.SHADER, ctx) {
    companion object {
        fun createFragmentShader(ctx: RenderContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_FRAGMENT_SHADER), ctx)
        }

        fun createVertexShader(ctx: RenderContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_VERTEX_SHADER), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: RenderContext) {
        glDeleteShader(this)
        super.delete(ctx)
    }

    fun shaderSource(source: String, ctx: RenderContext) {
        glShaderSource(this, source)
    }

    fun compile(ctx: RenderContext): Boolean {
        glCompileShader(this)
        return glGetShaderi(this, GL_COMPILE_STATUS) == GL_TRUE
    }

    fun getInfoLog(ctx: RenderContext): String {
        return glGetShaderInfoLog(this)
    }
}