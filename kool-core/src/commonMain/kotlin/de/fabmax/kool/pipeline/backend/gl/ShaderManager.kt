package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.logE

class ShaderManager(val backend: RenderBackendGl) {
    val shaders = mutableMapOf<PipelineBase, CompiledShader>()
    var currentShader: CompiledShader? = null

    private val gl: GlApi = backend.gl

    fun setupShader(cmd: DrawCommand): CompiledShader.ShaderInstance? {
        val shader = setupShader(cmd.pipeline!!)
        return shader.bindInstance(cmd)
    }

    fun setupComputeShader(computePipeline: ComputePipeline, computePass: ComputeRenderPass): Boolean {
        val sz = computePipeline.workGroupSize
        val maxSz = backend.gl.capabilities.maxWorkGroupSize
        if (sz.x > maxSz.x || sz.y > maxSz.y || sz.z > maxSz.z) {
            logE { "Maximum compute shader workgroup size exceeded: max size = $maxSz, requested size: $sz" }
            return false
        }

        val shader = setupShader(computePipeline)
        return shader.bindComputeInstance(computePipeline, computePass) != null
    }

    private fun setupShader(pipeline: PipelineBase): CompiledShader {
        val shader = shaders.getOrPut(pipeline) {
            val glProgram = when (pipeline) {
                is Pipeline -> {
                    val code = pipeline.shaderCode as ShaderCodeGl
                    compileShader(code)
                }
                is ComputePipeline -> {
                    val code = pipeline.shaderCode as ComputeShaderCodeGl
                    compileComputeShader(code)
                }
                else -> throw IllegalStateException()
            }
            CompiledShader(glProgram, pipeline, backend)
        }
        if (shader !== currentShader) {
            currentShader?.unUse()
            currentShader = shader
            shader.use()
        }
        return shader
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

    private fun compileComputeShader(code: ComputeShaderCodeGl): GlProgram {
        val comp = gl.createShader(gl.COMPUTE_SHADER)
        gl.shaderSource(comp, code.computeSrc)
        gl.compileShader(comp)
        val status = gl.getShaderParameter(comp, gl.COMPILE_STATUS)
        if (status != gl.TRUE) {
            val log = gl.getShaderInfoLog(comp)
            logE { "Compute shader compilation failed:\n$log" }
            logE { "Compute shader source: \n${formatShaderSrc(code.computeSrc)}" }
            throw KoolException("Compute shader compilation failed: $log")
        }

        val prog = gl.createProgram()
        gl.attachShader(prog, comp)
        gl.linkProgram(prog)
        gl.deleteShader(comp)
        if (gl.getProgramParameter(prog, gl.LINK_STATUS) != gl.TRUE) {
            val log = gl.getProgramInfoLog(prog)
            logE { "Compute shader linkage failed:\n$log" }
            logE { "Compute shader source: \n${formatShaderSrc(code.computeSrc)}" }
            throw KoolException("Compute shader linkage failed: $log")
        }
        return prog
    }

    private fun formatShaderSrc(src: String): String {
        val srcBuilder = StringBuilder()
        src.lines().forEachIndexed { ln, line -> srcBuilder.append("${ln+1} $line\n") }
        return srcBuilder.toString()
    }
}