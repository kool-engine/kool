package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext

class ShaderResource private constructor(glRef: Any, ctx: KoolContext) :
        GlResource(glRef, Type.SHADER, ctx) {
    companion object {
        fun createFragmentShader(ctx: KoolContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_FRAGMENT_SHADER), ctx)
        }

        fun createVertexShader(ctx: KoolContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_VERTEX_SHADER), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: KoolContext) {
        glDeleteShader(this)
        super.delete(ctx)
    }

    fun shaderSource(source: String, ctx: KoolContext) {
        glShaderSource(this, source)
    }

    fun compile(ctx: KoolContext): Boolean {
        glCompileShader(this)
        return glGetShaderi(this, GL_COMPILE_STATUS) == GL_TRUE
    }

    fun getInfoLog(ctx: KoolContext): String {
        return glGetShaderInfoLog(this)
    }
}