package de.fabmax.kool

import de.fabmax.kool.gl.ProgramResource
import de.fabmax.kool.gl.ShaderResource
import de.fabmax.kool.gl.glUseProgram
import de.fabmax.kool.shading.PreferredLightModel
import de.fabmax.kool.shading.PreferredShadowMethod
import de.fabmax.kool.shading.Shader
import de.fabmax.kool.shading.ShadingHints
import de.fabmax.kool.util.logE

/**
 * @author fabmax
 */
class ShaderManager internal constructor() : SharedResManager<Shader.Source, ProgramResource>() {

    var boundShader: Shader? = null
        private set

    var shadingHints = ShadingHints(PreferredLightModel.PHONG, PreferredShadowMethod.NO_SHADOW)
        // TODO: set(value) { updateAllShaders() }

    fun onNewFrame(ctx: KoolContext) {
        // force re-binding shader on new frame, otherwise delayed loaded
        // resources (e.g. textures) might not be loaded at all
        bindShader(null, ctx)
    }

    fun bindShader(shader: Shader?, ctx: KoolContext) {
        if (shader != null) {
            if (!shader.isValid) {
                shader.onLoad(ctx)
            }
            if (!shader.isBound(ctx)) {
                if (shader.res?.glRef != boundShader?.res?.glRef) {
                    glUseProgram(shader.res)
                }
                boundShader = shader
                shader.onBind(ctx)
            }

        } else if (boundShader != null) {
            // clear used shader
            glUseProgram(null)
            boundShader = null
        }
    }

    internal fun createShader(source: Shader.Source, ctx: KoolContext): ProgramResource {
        return addReference(source, ctx)
    }

    internal fun deleteShader(shader: Shader, ctx: KoolContext) {
        val res = shader.res
        if (res != null) {
            removeReference(shader.source, ctx)
        }
    }

    override fun createResource(key: Shader.Source, ctx: KoolContext): ProgramResource {
        // create vertex shader
        val vertShader = ShaderResource.createVertexShader(ctx)
        vertShader.shaderSource(key.vertexSrc, ctx)
        if (!vertShader.compile(ctx)) {
            // compilation failed
            val log = vertShader.getInfoLog(ctx)
            vertShader.delete(ctx)
            logE { "Vertex shader compilation failed: $log" }
            logE { "Shader source: \n${key.vertexSrc}" }
            throw KoolException("Vertex shader compilation failed: $log")
        }

        // create fragment shader
        val fragShader = ShaderResource.createFragmentShader(ctx)
        fragShader.shaderSource(key.fragmentSrc, ctx)
        if (!fragShader.compile(ctx)) {
            // compilation failed
            val log = fragShader.getInfoLog(ctx)
            fragShader.delete(ctx)
            logE { "Fragment shader compilation failed: $log" }
            logE { "Shader source: \n${key.fragmentSrc}" }
            throw KoolException("Fragment shader compilation failed: $log")
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
            logE { "Shader linkage failed: $log" }
            throw KoolException("Shader linkage failed: $log")
        }

        return prog
    }

    override fun deleteResource(key: Shader.Source, res: ProgramResource, ctx: KoolContext) {
        res.delete(ctx)
    }
}
