package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCodeGl
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.logE

class ShaderManager(val backend: RenderBackendGl) {
    val shaders = mutableMapOf<Pipeline, CompiledShader>()
    var currentShader: CompiledShader? = null

    private val gl: GlApi = backend.gl

    fun setupShader(cmd: DrawCommand): CompiledShader.ShaderInstance? {
        val pipeline = cmd.pipeline!!
        val shader = shaders.getOrPut(pipeline) {
            val code = pipeline.shaderCode as ShaderCodeGl
            CompiledShader(compileShader(code), pipeline, backend)
        }
        if (shader !== currentShader) {
            currentShader?.unUse()
            currentShader = shader
            shader.use()
        }
        return shader.bindInstance(cmd)
    }

    fun deleteShader(pipeline: Pipeline) {
        val shader = shaders[pipeline]
        if (shader != null) {
            shader.destroyInstance(pipeline)
            if (shader.isEmpty()) {
                if (shader == currentShader) {
                    shader.unUse()
                    currentShader = null
                }
                shader.destroy()
                shaders.remove(pipeline)
            }
        }
    }

    private fun compileShader(code: ShaderCodeGl): GlProgram {
        val vert = gl.createShader(gl.VERTEX_SHADER)
        gl.shaderSource(vert, code.vertexSrc)
        gl.compileShader(vert)
        val vsStatus = gl.getShaderParameter(vert, gl.COMPILE_STATUS)
        if (vsStatus != gl.TRUE) {
            val log = gl.getShaderInfoLog(vert)
            logE { "Vertex shader compilation failed:\n$log" }
            logE { "Vertex shader source: \n${formatShaderSrc(code.vertexSrc)}" }
            throw KoolException("Vertex shader compilation failed: $log")
        }

        val frag = gl.createShader(gl.FRAGMENT_SHADER)
        gl.shaderSource(frag, code.fragmentSrc)
        gl.compileShader(frag)
        val fsStatus = gl.getShaderParameter(frag, gl.COMPILE_STATUS)
        if (fsStatus != gl.TRUE) {
            val log = gl.getShaderInfoLog(frag)
            logE { "Fragment shader compilation failed:\n$log" }
            logE { "Fragment shader source: \n${formatShaderSrc(code.fragmentSrc)}" }
            throw KoolException("Fragment shader compilation failed: $log")
        }

        val prog = gl.createProgram()
        gl.attachShader(prog, vert)
        gl.attachShader(prog, frag)
        gl.linkProgram(prog)
        gl.deleteShader(vert)
        gl.deleteShader(frag)
        if (gl.getProgramParameter(prog, gl.LINK_STATUS) != gl.TRUE) {
            val log = gl.getProgramInfoLog(prog)
            logE { "Shader linkage failed:\n$log" }
            logE { "Vertex shader source: \n${formatShaderSrc(code.vertexSrc)}" }
            logE { "Fragment shader source: \n${formatShaderSrc(code.fragmentSrc)}" }
            throw KoolException("Shader linkage failed: $log")
        }
        return prog
    }

    private fun formatShaderSrc(src: String): String {
        val srcBuilder = StringBuilder()
        src.lines().forEachIndexed { ln, line -> srcBuilder.append("${ln+1} $line\n") }
        return srcBuilder.toString()
    }
}