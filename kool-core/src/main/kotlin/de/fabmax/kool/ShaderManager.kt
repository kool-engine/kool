package de.fabmax.kool

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.shading.Shader

/**
 * @author fabmax
 */
class ShaderManager internal constructor() : SharedResManager<Shader.Source, ProgramResource>() {

    var boundShader: Shader? = null
        private set

    fun bindShader(shader: Shader?, ctx: RenderContext) {
        if (shader != null) {
            if (!shader.isValid) {
                shader.onLoad(ctx)
            }
            if (shader != boundShader) {
                if (shader.res?.glRef != boundShader?.res?.glRef) {
                    GL.useProgram(shader.res)
                }
                boundShader = shader
                shader.onBind(ctx)
            }

        } else if (boundShader != null) {
            // clear used shader
            GL.useProgram(null)
            boundShader = null
        }
    }

    internal fun createShader(source: Shader.Source, ctx: RenderContext): ProgramResource {
        return createResource(source, ctx)
    }

    internal fun deleteShader(shader: Shader, ctx: RenderContext) {
        val res = shader.res
        if (res != null) {
            deleteResource(shader.source, res, ctx)
        }
    }

    override fun createResource(key: Shader.Source, ctx: RenderContext): ProgramResource {
        // create vertex shader
        val vertShader = ShaderResource.createVertexShader(ctx)
        vertShader.shaderSource(key.vertexSrc, ctx)
        if (!vertShader.compile(ctx)) {
            // compilation failed
            val log = vertShader.getInfoLog(ctx)
            vertShader.delete(ctx)
            throw KoolException("Vertex shader compilation failed: " + log)
        }

        // create fragment shader
        val fragShader = ShaderResource.createFragmentShader(ctx)
        fragShader.shaderSource(key.fragmentSrc, ctx)
        if (!fragShader.compile(ctx)) {
            // compilation failed
            val log = fragShader.getInfoLog(ctx)
            fragShader.delete(ctx)
            throw KoolException("Fragment shader compilation failed: " + log)
        }

        // link shader
        val prog = ProgramResource.create(ctx)
        prog.attachShader(vertShader, ctx)
        prog.attachShader(fragShader, ctx)
        val success = prog.link(ctx)
        // after linkage fragment and vertex shader are no longer needed
        vertShader.delete(ctx)
        fragShader.delete(ctx)
        if (!success) {
            // linkage failed
            val log = prog.getInfoLog(ctx)
            prog.delete(ctx)
            throw KoolException("Shader linkage failed: " + log)
        }

        return prog
    }

    override fun deleteResource(key: Shader.Source, res: ProgramResource, ctx: RenderContext) {
        res.delete(ctx)
    }
}
