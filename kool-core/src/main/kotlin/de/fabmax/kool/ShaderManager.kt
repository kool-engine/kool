package de.fabmax.kool

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.shading.Shader
import java.util.*

/**
 * @author fabmax
 */
class ShaderManager internal constructor() {

    var boundShader: Shader? = null
        private set

    private val shaderProgramMap: MutableMap<Shader.Source, ShaderReferenceCounter> = mutableMapOf()

    private data class ShaderReferenceCounter(val prog: ProgramResource, var referenceCount: Int)

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

    internal fun deleteShader(shader: Shader, ctx: RenderContext) {
        val ref = shaderProgramMap[shader.source]
        if (ref != null) {
            if (--ref.referenceCount == 0) {
                ref.prog.delete(ctx)
                shaderProgramMap.remove(shader.source)
            }
        }
    }

    internal fun compile(source: Shader.Source, ctx: RenderContext): ProgramResource {
        var res = shaderProgramMap[source]

        if (res == null) {
            // create vertex shader
            val vertShader = ShaderResource.createVertexShader(ctx)
            vertShader.shaderSource(source.vertexSrc, ctx)
            if (!vertShader.compile(ctx)) {
                // compilation failed
                val log = vertShader.getInfoLog(ctx)
                vertShader.delete(ctx)
                throw KogleException("Vertex shader compilation failed: " + log)
            }

            // create fragment shader
            val fragShader = ShaderResource.createFragmentShader(ctx)
            fragShader.shaderSource(source.fragmentSrc, ctx)
            if (!fragShader.compile(ctx)) {
                // compilation failed
                val log = fragShader.getInfoLog(ctx)
                fragShader.delete(ctx)
                throw KogleException("Fragment shader compilation failed: " + log)
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
                throw KogleException("Shader linkage failed: " + log)
            }

            // keep resource for resource sharing of equal shaders
            res = ShaderReferenceCounter(prog, 0)
            shaderProgramMap[source] = res
        }

        res.referenceCount++
        return res.prog
    }
}
