package de.fabmax.kool.platform.webgl

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCodeImpl
import de.fabmax.kool.pipeline.backend.gl.GlImpl
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.logE
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.COMPILE_STATUS
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAGMENT_SHADER
import org.khronos.webgl.WebGLRenderingContext.Companion.LINK_STATUS
import org.khronos.webgl.WebGLRenderingContext.Companion.VERTEX_SHADER

class ShaderManager(val ctx: JsContext) {
    private val gl: WebGLRenderingContext
        get() = GlImpl.gl

    val shaders = mutableMapOf<Pipeline, CompiledShader>()
    var currentShader: CompiledShader? = null

    fun setupShader(cmd: DrawCommand): CompiledShader.ShaderInstance? {
        val pipeline = cmd.pipeline!!
        val shader = shaders.getOrPut(pipeline) { CompiledShader(compileShader(pipeline.shaderCode as ShaderCodeImpl), pipeline, ctx) }
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

    private fun compileShader(code: ShaderCodeImpl): WebGLProgram? {
        val vert = gl.createShader(VERTEX_SHADER)
        gl.shaderSource(vert, code.vertexSrc)
        gl.compileShader(vert)
        val vsStatus = gl.getShaderParameter(vert, COMPILE_STATUS)
        if (vsStatus != true) {
            val log = gl.getShaderInfoLog(vert)
            logE { "Vertex shader compilation failed:\n$log" }
            logE { "Shader source: \n${formatShaderSrc(code.vertexSrc)}" }
            throw KoolException("Vertex shader compilation failed: $log")
        }

        val frag = gl.createShader(FRAGMENT_SHADER)
        gl.shaderSource(frag, code.fragmentSrc)
        gl.compileShader(frag)
        val fsStatus = gl.getShaderParameter(frag, COMPILE_STATUS)
        if (fsStatus != true) {
            val log = gl.getShaderInfoLog(frag)
            logE { "Fragment shader compilation failed:\n$log" }
            logE { "Shader source: \n${formatShaderSrc(code.fragmentSrc)}" }
            throw KoolException("Fragment shader compilation failed: $log")
        }

        val prog = gl.createProgram()
        gl.attachShader(prog, vert)
        gl.attachShader(prog, frag)
        gl.linkProgram(prog)
        gl.deleteShader(vert)
        gl.deleteShader(frag)
        if (gl.getProgramParameter(prog, LINK_STATUS) != true) {
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