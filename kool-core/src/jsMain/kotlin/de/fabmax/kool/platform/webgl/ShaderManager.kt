package de.fabmax.kool.platform.webgl

import de.fabmax.kool.KoolException
import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.ShaderCode
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
        get() = ctx.gl

    val shaders = mutableMapOf<ULong, CompiledShader>()
    var currentShader: CompiledShader? = null

    fun setupShader(cmd: DrawCommand): CompiledShader.ShaderInstance {
        val pipeline = cmd.pipeline!!
        val shader = shaders.getOrPut(pipeline.shaderCode.longHash) { CompiledShader(compileShader(pipeline.shaderCode), pipeline, ctx) }
        if (shader !== currentShader) {
            currentShader = shader
            shader.use()
        }
        return shader.bindInstance(cmd)
    }

    private fun compileShader(code: ShaderCode): WebGLProgram? {
        val vert = gl.createShader(VERTEX_SHADER)
        gl.shaderSource(vert, code.vertexSrc)
        gl.compileShader(vert)
        val vsStatus = gl.getShaderParameter(vert, COMPILE_STATUS)
        if (vsStatus != true) {
            val log = gl.getShaderInfoLog(vert)
            logE { "Vertex shader compilation failed: $vsStatus\n$log" }
            logE { "Shader source: \n${code.vertexSrc}" }
            throw KoolException("Vertex shader compilation failed: $log")
        }

        val frag = gl.createShader(FRAGMENT_SHADER)
        gl.shaderSource(frag, code.fragmentSrc)
        gl.compileShader(frag)
        val fsStatus = gl.getShaderParameter(frag, COMPILE_STATUS)
        if (fsStatus != true) {
            val log = gl.getShaderInfoLog(frag)
            logE { "Fragment shader compilation failed: $fsStatus\n$log" }
            logE { "Shader source: \n${code.fragmentSrc}" }
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
            logE { "Shader linkage failed: $log" }
            throw KoolException("Shader linkage failed: $log")
        }
        return prog
    }
}