package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.checkIsNotReleased
import de.fabmax.kool.util.logE

class ShaderManager(val backend: RenderBackendGl) {
    private val gl: GlApi = backend.gl

    private var boundShader: CompiledShader? = null

    private val glDrawPrograms = mutableMapOf<ShaderCodeGl, UsedGlProgram>()
    private val glComputePrograms = mutableMapOf<ComputeShaderCodeGl, UsedGlProgram>()

    fun bindDrawShader(cmd: DrawCommand): CompiledDrawShader.DrawInfo {
        val shader = cmd.pipeline.getCompiledShader()
        val current = boundShader as? CompiledDrawShader

        if (shader.program != current?.program) {
            current?.disableVertexLayout()
            gl.useProgram(shader.program)
            shader.enableVertexLayout()

        } else if (!shader.isSameVertexLayout(current)) {
            current.disableVertexLayout()
            shader.enableVertexLayout()
        }

        boundShader = shader
        return shader.bindMesh(cmd)
    }

    fun bindComputeShader(pipeline: ComputePipeline, task: ComputePass.Task): Boolean {
        val sz = pipeline.workGroupSize
        val maxSz = backend.gl.capabilities.maxWorkGroupSize
        if (sz.x > maxSz.x || sz.y > maxSz.y || sz.z > maxSz.z) {
            logE { "Maximum compute shader workgroup size exceeded: max size = $maxSz, requested size: $sz" }
            return false
        }
        if (sz.x * sz.y * sz.z > backend.gl.capabilities.maxWorkGroupInvocations) {
            logE { "Maximum compute shader workgroup invocations exceeded: max invocations = ${backend.gl.capabilities.maxWorkGroupInvocations}, " +
                    "requested invocations: ${sz.x} x ${sz.y} x ${sz.z} = ${sz.x * sz.y * sz.z}" }
            return false
        }

        val shader = pipeline.getCompiledShader()
        val current = boundShader as? CompiledComputeShader
        if (shader.program != current?.program) {
            gl.useProgram(shader.program)
        }

        boundShader = shader
        return shader.bindComputePass(task)
    }

    private fun DrawPipeline.getCompiledShader(): CompiledDrawShader {
        checkIsNotReleased()
        (pipelineBackend as CompiledDrawShader?)?.let { return it }

        val usedProgram = getCompiledGlProgram(shaderCode)
        usedProgram.users += this
        return CompiledDrawShader(this, usedProgram.glProgram, backend).also { pipelineBackend = it }
    }

    private fun ComputePipeline.getCompiledShader(): CompiledComputeShader {
        (pipelineBackend as CompiledComputeShader?)?.let { return it }

        val usedProgram = getCompiledGlProgram(shaderCode)
        usedProgram.users += this
        return CompiledComputeShader(this, usedProgram.glProgram, backend).also { pipelineBackend = it }
    }

    private fun CompiledDrawShader.isSameVertexLayout(other: CompiledDrawShader): Boolean {
        return pipeline.vertexLayout.hash == other.pipeline.vertexLayout.hash
    }

    private fun getCompiledGlProgram(code: ShaderCode): UsedGlProgram {
        return when (code) {
            is ShaderCodeGl -> glDrawPrograms.getOrPut(code) { UsedGlProgram(compileShader(code)) }
            is ComputeShaderCodeGl -> glComputePrograms.getOrPut(code) { UsedGlProgram(compileComputeShader(code)) }
            else -> error("Invalid ShaderCode: $code (must be either ShaderCodeGl or ComputeShaderCodeGl)")
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
            error("Vertex shader compilation failed: $log")
        }

        val frag = gl.createShader(gl.FRAGMENT_SHADER)
        gl.shaderSource(frag, code.fragmentSrc)
        gl.compileShader(frag)
        val fsStatus = gl.getShaderParameter(frag, gl.COMPILE_STATUS)
        if (fsStatus != gl.TRUE) {
            val log = gl.getShaderInfoLog(frag)
            logE { "Fragment shader compilation failed:\n$log" }
            logE { "Fragment shader source: \n${formatShaderSrc(code.fragmentSrc)}" }
            error("Fragment shader compilation failed: $log")
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
            error("Shader linkage failed: $log")
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
            error("Compute shader compilation failed: $log")
        }

        val prog = gl.createProgram()
        gl.attachShader(prog, comp)
        gl.linkProgram(prog)
        gl.deleteShader(comp)
        if (gl.getProgramParameter(prog, gl.LINK_STATUS) != gl.TRUE) {
            val log = gl.getProgramInfoLog(prog)
            logE { "Compute shader linkage failed:\n$log" }
            logE { "Compute shader source: \n${formatShaderSrc(code.computeSrc)}" }
            error("Compute shader linkage failed: $log")
        }
        return prog
    }

    internal fun removeDrawShader(shader: CompiledDrawShader) {
        shader.pipeline.pipelineBackend = null
        glDrawPrograms[shader.pipeline.shaderCode]?.let { usedProgram ->
            usedProgram.users -= shader.pipeline
            if (usedProgram.users.isEmpty()) {
                gl.deleteProgram(usedProgram.glProgram)
                glDrawPrograms.remove(shader.pipeline.shaderCode)
            }
        }
        if (shader == boundShader) {
            shader.disableVertexLayout()
            boundShader = null
        }
    }

    internal fun removeComputeShader(shader: CompiledComputeShader) {
        shader.pipeline.pipelineBackend = null
        glComputePrograms[shader.pipeline.shaderCode]?.let { usedProgram ->
            usedProgram.users -= shader.pipeline
            if (usedProgram.users.isEmpty()) {
                gl.deleteProgram(usedProgram.glProgram)
                glComputePrograms.remove(shader.pipeline.shaderCode)
            }
        }
        if (shader == boundShader) {
            boundShader = null
        }
    }

    private fun formatShaderSrc(src: String): String {
        val srcBuilder = StringBuilder()
        src.lines().forEachIndexed { ln, line -> srcBuilder.append("${ln+1} $line\n") }
        return srcBuilder.toString()
    }

    private class UsedGlProgram(val glProgram: GlProgram) {
        val users = mutableSetOf<PipelineBase>()
    }
}