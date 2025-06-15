package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

class WgpuPipelineManager(val backend: RenderBackendWebGpu) {

    private val vertexShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val fragmentShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val computeShaderModules = mutableMapOf<String, UsedShaderModule>()

    fun prepareDrawPipeline(cmd: DrawCommand) {
        val wgpuPipeline = cmd.pipeline.getWgpuPipeline()
        cmd.pipeline.update(cmd)
        wgpuPipeline.updateGeometry(cmd)
    }

    fun bindDrawPipeline(cmd: DrawCommand, passEncoderState: RenderPassEncoderState): Boolean {
        return cmd.pipeline.getWgpuPipeline().bind(cmd, passEncoderState)
    }

    fun bindComputePipeline(task: ComputePass.Task, passEncoderState: ComputePassEncoderState): Boolean {
        val gpuPipeline = task.pipeline.getWgpuPipeline()
        task.pipeline.update(task.pass)
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
                code = shaderCode.vertexSrc
            )
            val module = backend.device.createShaderModule(desc)
            module.checkErrors("vertex-shader", shaderCode.vertexSrc, pipeline)
            UsedShaderModule(module)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private fun getOrCreateFragmentShaderModule(pipeline: DrawPipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
        val usedModule = fragmentShaderModules.getOrPut(shaderCode.fragmentSrc) {
            val desc = GPUShaderModuleDescriptor(
                label = "${pipeline.name} fragment shader",
                code = shaderCode.fragmentSrc
            )
            val module = backend.device.createShaderModule(desc)
            module.checkErrors("fragment-shader", shaderCode.fragmentSrc, pipeline)
            UsedShaderModule(module)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private fun getOrCreateComputeShaderModule(pipeline: ComputePipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuComputeShaderCode
        val usedModule = fragmentShaderModules.getOrPut(shaderCode.computeSrc) {
            val desc = GPUShaderModuleDescriptor(
                label = "${pipeline.name} compute shader",
                code = shaderCode.computeSrc
            )
            val module = backend.device.createShaderModule(desc)
            module.checkErrors("compute-shader", shaderCode.computeSrc, pipeline)
            UsedShaderModule(module)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private fun GPUShaderModule.checkErrors(stage: String, code: String, pipeline: PipelineBase) {
        getCompilationInfo().then { info ->
            val messages = info.messages
            if (messages.any { it.type == "error" }) {
                logE { "Errors occurred on compilation of shader ${pipeline.name}:$stage:" }
                messages.filter { it.type == "error" }.forEach {
                    logE { it.message }
                }
                logE { code }
            }
            if (messages.any { it.type == "warning" }) {
                logW { "Warnings occurred on compilation of fragment shader ${pipeline.name}:" }
                messages.filter { it.type == "warning" }.forEach {
                    logW { it.message }
                }
            }
        }
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