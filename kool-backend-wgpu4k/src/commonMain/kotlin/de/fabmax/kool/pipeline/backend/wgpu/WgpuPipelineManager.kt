package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.*
import io.ygdrasil.webgpu.GPUErrorFilter
import io.ygdrasil.webgpu.GPUShaderModule
import io.ygdrasil.webgpu.ShaderModuleDescriptor

class WgpuPipelineManager(val backend: RenderBackendWgpu4k) {

    private val vertexShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val fragmentShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val computeShaderModules = mutableMapOf<String, UsedShaderModule>()

    suspend fun prepareDrawPipeline(cmd: DrawCommand) {
        val wgpuPipeline = cmd.pipeline.getWgpuPipeline()
        cmd.pipeline.update(cmd)
        wgpuPipeline.updateGeometry(cmd)
    }

    suspend fun bindDrawPipeline(cmd: DrawCommand, passEncoderState: RenderPassEncoderState): Boolean {
        return cmd.pipeline.getWgpuPipeline().bind(cmd, passEncoderState)
    }

    suspend fun bindComputePipeline(task: ComputePass.Task, passEncoderState: ComputePassEncoderState): Boolean {
        val gpuPipeline = task.pipeline.getWgpuPipeline()
        task.pipeline.update(task.pass)
        return gpuPipeline.bind(task, passEncoderState)
    }

    private suspend fun DrawPipeline.getWgpuPipeline(): WgpuDrawPipeline {
        (pipelineBackend as WgpuDrawPipeline?)?.let { return it }

        val vertexShader = getOrCreateVertexShaderModule(this)
        val fragmentShader = getOrCreateFragmentShaderModule(this)
        return WgpuDrawPipeline(this, vertexShader, fragmentShader, backend).also { pipelineBackend = it }
    }

    private suspend fun ComputePipeline.getWgpuPipeline(): WgpuComputePipeline {
        (pipelineBackend as WgpuComputePipeline?)?.let { return it }

        val computeShader = getOrCreateComputeShaderModule(this)
        return WgpuComputePipeline(this, computeShader, backend).also { pipelineBackend = it }
    }

    private suspend fun getOrCreateVertexShaderModule(pipeline: DrawPipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWgpu4k.WebGpuShaderCode
        val usedModule = vertexShaderModules.getOrPut(shaderCode.vertexSrc) {
            val desc = ShaderModuleDescriptor(
                label = "${pipeline.name} vertex shader",
                code = shaderCode.vertexSrc
            )
            backend.device.pushErrorScope(GPUErrorFilter.Validation)
            val module = backend.device.createShaderModule(desc)
            module.checkErrors("vertex-shader", shaderCode.vertexSrc, pipeline)
            UsedShaderModule(module)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private suspend fun getOrCreateFragmentShaderModule(pipeline: DrawPipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWgpu4k.WebGpuShaderCode
        val usedModule = fragmentShaderModules.getOrPut(shaderCode.fragmentSrc) {
            val desc = ShaderModuleDescriptor(
                label = "${pipeline.name} fragment shader",
                code = shaderCode.fragmentSrc
            )
            val module = backend.device.createShaderModule(desc)
            backend.device.pushErrorScope(GPUErrorFilter.Validation)
            module.checkErrors("fragment-shader", shaderCode.fragmentSrc, pipeline)
            UsedShaderModule(module)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private suspend fun getOrCreateComputeShaderModule(pipeline: ComputePipeline): GPUShaderModule {
        val shaderCode = pipeline.shaderCode as RenderBackendWgpu4k.WebGpuComputeShaderCode
        val usedModule = fragmentShaderModules.getOrPut(shaderCode.computeSrc) {
            val desc = ShaderModuleDescriptor(
                label = "${pipeline.name} compute shader",
                code = shaderCode.computeSrc
            )
            val module = backend.device.createShaderModule(desc)
            backend.device.pushErrorScope(GPUErrorFilter.Validation)
            module.checkErrors("compute-shader", shaderCode.computeSrc, pipeline)
            UsedShaderModule(module)
        }
        usedModule.users += pipeline
        return usedModule.shaderModule
    }

    private suspend fun GPUShaderModule.checkErrors(stage: String, code: String, pipeline: PipelineBase) {
        // fixme: this produces some weird coroutine handler exceptions now
        //backend.device.popErrorScope().onSuccess { info ->
        //    info?.message?.let {
        //        logE { "Errors occurred on compilation of shader ${pipeline.name}:$stage:" }
        //        logE { it }
        //    }
        //}
    }

    internal fun removeDrawPipeline(pipeline: WgpuDrawPipeline) {
        pipeline.drawPipeline.pipelineBackend = null
        val shaderCode = pipeline.drawPipeline.shaderCode as RenderBackendWgpu4k.WebGpuShaderCode

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
        val shaderCode = pipeline.computePipeline.shaderCode as RenderBackendWgpu4k.WebGpuComputeShaderCode

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