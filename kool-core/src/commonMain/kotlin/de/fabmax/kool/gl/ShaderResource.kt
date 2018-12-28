package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException

class ShaderResource private constructor(glRef: Any, ctx: KoolContext) :
        GlResource(glRef, Type.SHADER, ctx) {
    companion object {
        fun createFragmentShader(ctx: KoolContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_FRAGMENT_SHADER), ctx)
        }

        fun createGeometryShader(ctx: KoolContext): ShaderResource {
            if (!ctx.glCapabilities.geometryShader) {
                throw KoolException("Geometry shaders are not supported on this implementation")
            }
            return ShaderResource(glCreateShader(GL_GEOMETRY_SHADER), ctx)
        }

        fun createVertexShader(ctx: KoolContext): ShaderResource {
            return ShaderResource(glCreateShader(GL_VERTEX_SHADER), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: KoolContext) {
        ctx.checkIsGlThread()
        glDeleteShader(this)
        super.delete(ctx)
    }

    fun shaderSource(source: String, ctx: KoolContext) {
        ctx.checkIsGlThread()
        glShaderSource(this, source)
    }

    fun compile(ctx: KoolContext): Boolean {
        ctx.checkIsGlThread()
        glCompileShader(this)
        return glGetShaderi(this, GL_COMPILE_STATUS) == GL_TRUE
    }

    fun getInfoLog(ctx: KoolContext): String {
        ctx.checkIsGlThread()
        return glGetShaderInfoLog(this)
    }
}