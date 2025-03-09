package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.util.memStack

class ComputePipelineVk(
    val computePipeline: ComputePipeline,
    private val computeShaderModule: VkShaderModule,
    backend: RenderBackendVk,
) : PipelineVk(computePipeline, backend) {

    private val vkComputePipeline: VkComputePipeline = createComputePipelineVk()
    private val users = mutableSetOf<ComputePass.Task>()

    override fun removeUser(user: Any) {
        (user as? ComputePass.Task)?.let { users.remove(it) }
        if (users.isEmpty()) {
            release()
        }
    }

    fun bind(task: ComputePass.Task, passEncoderState: PassEncoderState): Boolean {
        users += task
        computePipeline.update(task.pass)

        val pipelineData = computePipeline.pipelineData
        if (!pipelineData.checkBindings()) {
            return false
        }

        passEncoderState.setComputePipeline(vkComputePipeline)
        val computeData = pipelineData.getOrCreateVkData(passEncoderState.commandBuffer)
        computeData.updateBuffers(passEncoderState)
        computeData.prepareBind(passEncoderState)
        passEncoderState.setBindGroup(computeData, pipelineLayout, BindPoint.Compute, groupIndex = 0)
        return true
    }

    private fun createComputePipelineVk(): VkComputePipeline = memStack {
        device.createComputePipeline(stack = this) {
            layout(pipelineLayout.handle)

            val code = computePipeline.shaderCode as ShaderCodeVk
            val stage = code.computeStage!!
            stage {
                it.`sType$Default`()
                it.stage(stage.stage)
                it.module(computeShaderModule.handle)
                it.pName(ASCII(stage.entryPoint))
            }
        }
    }

    override fun release() {
        super.release()
        backend.pipelineManager.removeComputePipeline(this)
        backend.device.destroyComputePipeline(vkComputePipeline)
    }
}