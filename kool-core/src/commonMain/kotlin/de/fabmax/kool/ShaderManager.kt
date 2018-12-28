package de.fabmax.kool

import de.fabmax.kool.gl.ProgramResource
import de.fabmax.kool.gl.ShaderResource
import de.fabmax.kool.gl.glUseProgram
import de.fabmax.kool.scene.Node
import de.fabmax.kool.shading.Shader
import de.fabmax.kool.util.logE

/**
 * @author fabmax
 */
class ShaderManager internal constructor() : SharedResManager<Shader.Source, ProgramResource>() {

    var boundShader: Shader? = null
        private set

    fun onNewFrame(ctx: KoolContext) {
        // force re-binding shader on new frame, otherwise delayed loaded
        // resources (e.g. textures) might not be loaded at all
        clearShader(ctx)
    }

    fun clearShader(ctx: KoolContext) {
        ctx.checkIsGlThread()
        if (boundShader != null) {
            // clear used shader
            glUseProgram(null)
            boundShader = null
        }
    }

    fun bindShader(shader: Shader, toNode: Node, ctx: KoolContext) {
        if (!shader.isValid) {
            shader.onLoad(toNode, ctx)
        }
        if (!shader.isBound(ctx)) {
            if (shader.res?.glRef != boundShader?.res?.glRef) {
                glUseProgram(shader.res)
            }
            boundShader = shader
            shader.onBind(toNode, ctx)
        }
    }

    internal fun onRenderingHintsChanged(ctx: KoolContext) {
        // invalidate all shaders
        deleteAllResources(ctx)
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

        // optionally create geometry shader
        var geomShader: ShaderResource? = null
        if (key.geometrySrc.isNotEmpty()) {
            geomShader = ShaderResource.createGeometryShader(ctx)
            geomShader.shaderSource(key.geometrySrc, ctx)
            if (!fragShader.compile(ctx)) {
                // compilation failed
                val log = geomShader.getInfoLog(ctx)
                geomShader.delete(ctx)
                logE { "Geometry shader compilation failed: $log" }
                logE { "Shader source: \n${key.geometrySrc}" }
                throw KoolException("Geometry shader compilation failed: $log")
            }
        }

        // link shader
        val prog = ProgramResource.create(ctx)
        prog.attachShader(vertShader, ctx)
        prog.attachShader(fragShader, ctx)
        geomShader?.let { prog.attachShader(it, ctx) }
        val success = prog.link(ctx)

        // after linkage fragment and vertex shader are no longer needed
        vertShader.delete(ctx)
        fragShader.delete(ctx)
        geomShader?.delete(ctx)
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
