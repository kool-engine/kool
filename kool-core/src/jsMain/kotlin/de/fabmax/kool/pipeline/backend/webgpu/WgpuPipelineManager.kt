package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

class WgpuPipelineManager(val backend: RenderBackendWebGpu) {

    private val vertexShaderModules = mutableMapOf<String, UsedShaderModule>()
    private val fragmentShaderModules = mutableMapOf<String, UsedShaderModule>()

    fun bindDrawPipeline(cmd: DrawCommand, passEncoderState: PassEncoderState, renderPass: WgpuRenderPass<*>): Boolean {
        val drawPipeline = cmd.pipeline!!
        val gpuPipeline = drawPipeline.getWgpuPipeline(renderPass)
        drawPipeline.update(cmd)
        return gpuPipeline.bind(cmd, passEncoderState)
    }

    private fun DrawPipeline.getWgpuPipeline(renderPass: WgpuRenderPass<*>): WgpuDrawPipeline {
        (pipelineBackend as WgpuDrawPipeline?)?.let { return it }

        val vertexShader = getOrCreateVertexShaderModule(this)
        val fragmentShader = getOrCreateFragmentShaderModule(this)
        return WgpuDrawPipeline(this, vertexShader, fragmentShader, renderPass, backend).also { pipelineBackend = it }
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

    private class UsedShaderModule(val shaderModule: GPUShaderModule) {
        val users = mutableSetOf<PipelineBase>()
    }
}