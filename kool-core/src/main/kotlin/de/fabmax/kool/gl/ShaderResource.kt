package de.fabmax.kool.gl

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext

class ShaderResource private constructor(glRef: Any, ctx: RenderContext) :
        GlResource(glRef, Type.SHADER, ctx) {
    companion object {
        fun createFragmentShader(ctx: RenderContext): ShaderResource {
            return ShaderResource(GL.createShader(GL.Companion.FRAGMENT_SHADER), ctx)
        }

        fun createVertexShader(ctx: RenderContext): ShaderResource {
            return ShaderResource(GL.createShader(GL.Companion.VERTEX_SHADER), ctx)
        }
    }

    init {
        ctx.memoryMgr.memoryAllocated(this, 1)
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteShader(this)
        super.delete(ctx)
    }

    fun shaderSource(source: String, ctx: RenderContext) {
        GL.shaderSource(this, source)
    }

    fun compile(ctx: RenderContext): Boolean {
        GL.compileShader(this)
        return GL.getShaderi(this, GL.COMPILE_STATUS) == GL.TRUE
    }

    fun getInfoLog(ctx: RenderContext): String {
        return GL.getShaderInfoLog(this)
    }
}