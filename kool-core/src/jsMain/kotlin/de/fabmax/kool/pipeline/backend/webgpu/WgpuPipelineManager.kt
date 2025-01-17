package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*

class WgpuPipelineManager(val backend: RenderBackendWebGpu) {

    private val vertexShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val fragmentShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val computeShaderModules = mutableMapOf<String, UsedShaderModule>()

    fun bindDrawPipeline(cmd: DrawCommand, passEncoderState: RenderPassEncoderState): Boolean {
        val gpuPipeline = cmd.pipeline.getWgpuPipeline()
        cmd.pipeline.update(cmd)
        return gpuPipeline.bind(cmd, passEncoderState)
    }

    fun bindComputePipeline(task: ComputeRenderPass.Task, passEncoderState: ComputePassEncoderState): Boolean {
        val computePipeline = task.pipeline
        val gpuPipeline = computePipeline.getWgpuPipeline()
        computePipeline.update(task.pass)
        return gpuPipeline.bind(task, passEncoderState)
    }

    private fun DrawPipeline.getWgpuPipeline(): WgpuDrawPipeline {
        (pipelineBackend as WgpuDrawPipeline?)?.let { return it }

        val vertexShader = getOrCreateVertexShaderModule(this)
        val fragmentShader = getOrCreateFragmentShaderModule(this)
        return WgpuDrawPipeline(this, vertexShader, fragmentShader, backend).also { pipelineBackend = it }
    }

    private fun ComputePipeline.getWgpuPipeline(): WgpuComputePipeline {
        (pipelineBackend as WgpuComputePipeline?)?.let { return it }

        val computeShader = getOrCreateComputeShaderModule(this)
        return WgpuComputePipeline(this, computeShader, backend).also { pipelineBackend = it }
    }

    private fun getOrCreateVertexShaderModule(pipeline: DrawPipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
        val usedModule = vertexShaderModules.getOrPut(shaderCode.vertexSrc) {
            val desc = GPUShaderModuleDescriptor(
                label = "${pipeline.name} vertex shader",
                code = pipeline.shaderCode.vertexSrc
            )
            UsedShaderModule(backend.device.createShaderModule(desc))
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private fun getOrCreateFragmentShaderModule(pipeline: DrawPipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
        val usedModule = fragmentShaderModules.getOrPut(shaderCode.fragmentSrc) {
            val desc = GPUShaderModuleDescriptor(
                label = "${pipeline.name} fragment shader",
                code = pipeline.shaderCode.fragmentSrc
            )
            UsedShaderModule(backend.device.createShaderModule(desc))
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private fun getOrCreateComputeShaderModule(pipeline: ComputePipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuComputeShaderCode
        val usedModule = fragmentShaderModules.getOrPut(shaderCode.computeSrc) {
            val desc = GPUShaderModuleDescriptor(
                label = "${pipeline.name} compute shader",
                code = pipeline.shaderCode.computeSrc
            )
            UsedShaderModule(backend.device.createShaderModule(desc))
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    internal fun removeDrawPipeline(pipeline: WgpuDrawPipeline) {
        pipeline.drawPipeline.pipelineBackend = null
        val shaderCode = pipeline.drawPipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode

        vertexShaderModules[shaderCode.vertexSrc]?.let { usedModule ->
            usedModule.users -= pipeline.drawPipeline
            if (usedModule.users.isEmpty()) {
                vertexShaderModules.remove(shaderCode.vertexSrc)
            }
        }
        fragmentShaderModules[shaderCode.fragmentSrc]?.let { usedModule ->
            usedModule.users -= pipeline.drawPipeline
            if (usedModule.users.isEmpty()) {
                fragmentShaderModules.remove(shaderCode.fragmentSrc)
            }
        }
    }

    internal fun removeComputePipeline(pipeline: WgpuComputePipeline) {
        pipeline.computePipeline.pipelineBackend = null
        val shaderCode = pipeline.computePipeline.shaderCode as RenderBackendWebGpu.WebGpuComputeShaderCode

        computeShaderModules[shaderCode.computeSrc]?.let { usedModule ->
            usedModule.users -= pipeline.computePipeline
            if (usedModule.users.isEmpty()) {
                vertexShaderModules.remove(shaderCode.computeSrc)
            }
        }
    }

    private class UsedShaderModule(val shaderModule: GPUShaderModule) {
        val users = mutableSetOf<PipelineBase>()
    }
}