package de.fabmax.kool.platform.gl

import de.fabmax.kool.KoolException
import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.logE
import org.lwjgl.opengl.GL20.*

class ShaderManager(val renderBackend: GlRenderBackend, val ctx: Lwjgl3Context) {
    val shaders = mutableMapOf<Pipeline, CompiledShader>()
    var currentShader: CompiledShader? = null

    fun setupShader(cmd: DrawCommand): CompiledShader.ShaderInstance? {
        val pipeline = cmd.pipeline!!
        val shader = shaders.getOrPut(pipeline) { CompiledShader(compileShader(pipeline.shaderCode), pipeline, renderBackend, ctx) }
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

    private fun compileShader(code: ShaderCode): Int {
        val vert = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vert, code.glVertexSrc)
        glCompileShader(vert)
        val vsStatus = glGetShaderi(vert, GL_COMPILE_STATUS)
        if (vsStatus != GL_TRUE) {
            val log = glGetShaderInfoLog(vert)
            logE { "Vertex shader compilation failed:\n$log" }
            logE { "Shader source: \n${formatShaderSrc(code.glVertexSrc)}" }
            throw KoolException("Vertex shader compilation failed: $log")
        }

        val frag = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(frag, code.glFragmentSrc)
        glCompileShader(frag)
        val fsStatus = glGetShaderi(frag, GL_COMPILE_STATUS)
        if (fsStatus != GL_TRUE) {
            val log = glGetShaderInfoLog(frag)
            logE { "Fragment shader compilation failed:\n$log" }
            logE { "Shader source: \n${formatShaderSrc(code.glFragmentSrc)}" }
            throw KoolException("Fragment shader compilation failed: $log")
        }

        val prog = glCreateProgram()
        glAttachShader(prog, vert)
        glAttachShader(prog, frag)
        glLinkProgram(prog)
        glDeleteShader(vert)
        glDeleteShader(frag)
        if (glGetProgrami(prog, GL_LINK_STATUS) != GL_TRUE) {
            val log = glGetProgramInfoLog(prog)
            logE { "Shader linkage failed:\n$log" }
            logE { "Vertex shader source: \n${formatShaderSrc(code.glVertexSrc)}" }
            logE { "Fragment shader source: \n${formatShaderSrc(code.glFragmentSrc)}" }
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