package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.ComputeRenderPass

class WgpuComputePipeline(
    val computePipeline: ComputePipeline,
    private val computeShaderModule: GPUShaderModule,
    backend: RenderBackendWebGpu,
): WgpuPipeline(computePipeline, backend) {

    private val gpuComputePipeline = createComputePipeline()
    private val users = mutableSetOf<ComputeRenderPass.Task>()

    override fun removeUser(user: Any) {
        (user as? ComputeRenderPass.Task)?.let { users.remove(it) }
        if (users.isEmpty()) {
            release()
        }
    }

    private fun createComputePipeline(): GPUComputePipeline {
        val compute = GPUProgrammableStage(
            module = computeShaderModule,
            entryPoint = "computeMain"
        )
        return device.createComputePipeline(
            label = "${computePipeline.name}-layout",
            layout = pipelineLayout,
            compute = compute
        )
    }

    fun bind(task: ComputeRenderPass.Task, passEncoderState: ComputePassEncoderState): Boolean {
        users += task
        computePipeline.update(task.pass)

        val pipelineData = computePipeline.pipelineData
        if (!pipelineData.checkBindings(backend)) {
            return false
        }

        passEncoderState.setPipeline(gpuComputePipeline)
        pipelineData.getOrCreateWgpuData().bind(passEncoderState, task.pass, 0)
        return true
    }
}