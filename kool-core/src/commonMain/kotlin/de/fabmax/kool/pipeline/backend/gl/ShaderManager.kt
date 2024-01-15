package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.logE

class ShaderManager(val backend: RenderBackendGl) {
    private val gl: GlApi = backend.gl

    private val shaders = mutableMapOf<PipelineBase, CompiledShader>()
    private var boundShader: CompiledShader? = null

    private val glShaderPrograms = mutableMapOf<ShaderCodeGl, UsedGlProgram>()
    private val glComputePrograms = mutableMapOf<ComputeShaderCodeGl, UsedGlProgram>()

    fun bindShader(cmd: DrawCommand): CompiledDrawShader.ShaderMeshInstance? {
        val shader = bindShader(cmd.pipeline!!)
        return shader.bindMesh(cmd)
    }

    fun setupComputeShader(computePipeline: ComputePipeline, computePass: ComputeRenderPass): Boolean {
        val sz = computePipeline.workGroupSize
        val maxSz = backend.gl.capabilities.maxWorkGroupSize
        if (sz.x > maxSz.x || sz.y > maxSz.y || sz.z > maxSz.z) {
            logE { "Maximum compute shader workgroup size exceeded: max size = $maxSz, requested size: $sz" }
            return false
        }

        return false
//        fixme: val shader = bindShader(computePipeline)
//        return shader.bindComputeInstance(computePipeline, computePass) != null
    }

    private fun bindShader(pipeline: Pipeline): CompiledDrawShader {
        val shader = shaders.getOrPut(pipeline) {
            val usedProgram = getCompiledGlProgram(pipeline.shaderCode)
            usedProgram.users += pipeline
            CompiledDrawShader(pipeline, usedProgram.glProgram, backend)
        } as CompiledDrawShader

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
        return shader
    }

    private fun CompiledDrawShader.isSameVertexLayout(other: CompiledDrawShader): Boolean {
        return pipeline.vertexLayout.hash == other.pipeline.vertexLayout.hash
    }

    private fun getCompiledGlProgram(code: ShaderCode): UsedGlProgram {
        return when (code) {
            is ShaderCodeGl -> glShaderPrograms.getOrPut(code) { UsedGlProgram(compileShader(code)) }
            is ComputeShaderCodeGl -> glComputePrograms.getOrPut(code) { UsedGlProgram(compileComputeShader(code)) }
            else -> error("Invalid ShaderCode: $code (must be either ShaderCodeGl or ComputeShaderCodeGl)")
        }
    }

    fun deleteShader(pipeline: Pipeline) {
        // todo
//        val shader = shaders[pipeline]
//        if (shader != null) {
//            shader.destroyInstance(pipeline)
//            if (shader.isEmpty()) {
//                if (shader == boundShader) {
//                    shader.unbindVertexLayout()
//                    boundShader = null
//                }
//                shader.release()
//                shaders.remove(pipeline)
//            }
//        }
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

    private class UsedGlProgram(val glProgram: GlProgram) {
        val users = mutableSetOf<PipelineBase>()
    }
}