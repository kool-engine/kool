package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePipeline
import io.ygdrasil.webgpu.GPUComputePipeline
import io.ygdrasil.webgpu.GPUShaderModule
import io.ygdrasil.webgpu.ProgrammableStage

class WgpuComputePipeline(
    val computePipeline: ComputePipeline,
    private val computeShaderModule: GPUShaderModule,
    backend: RenderBackendWgpu4k,
): WgpuPipeline(computePipeline, backend) {

    private val gpuComputePipeline = createComputePipeline()
    private val users = mutableSetOf<ComputePass.Task>()

    override fun removeUser(user: Any) {
        (user as? ComputePass.Task)?.let { users.remove(it) }
        if (users.isEmpty()) {
            release()
        }
    }

    private fun createComputePipeline(): GPUComputePipeline {
        return device.createComputePipeline(
            label = "${computePipeline.name}-layout",
            layout = pipelineLayout,
            compute = ProgrammableStage(
                module = computeShaderModule,
                entryPoint = "computeMain"
            )
        )
    }

    fun bind(task: ComputePass.Task, passEncoderState: ComputePassEncoderState): Boolean {
        users += task
        computePipeline.update(task.pass)

        val pipelineData = computePipeline.pipelineData
        if (!pipelineData.checkBindings()) {
            return false
        }

        passEncoderState.setPipeline(gpuComputePipeline)
        pipelineData.getOrCreateWgpuData().bind(passEncoderState, 0)
        return true
    }
}