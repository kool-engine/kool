package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.RenderPass
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

interface PassEncoderState {
    val frameIndex: Int
    val renderPass: RenderPass
    val stack: MemoryStack
    fun setBindGroup(group: Int, bindGroupData: BindGroupDataVk, pipeline: VkPipeline)
}

class RenderPassEncoderState<T: RenderPass>(val renderPassVk: RenderPassVk<T>): PassEncoderState {
    private var isPassActive = false
    private var _commandBuffer: VkCommandBuffer? = null
    private var _renderPass: T? = null
    private var _stack: MemoryStack? = null

    val commandBuffer: VkCommandBuffer get() = _commandBuffer!!
    override val stack: MemoryStack get() = _stack!!
    override val renderPass: T get() = _renderPass!!

    private var activePipeline: VkGraphicsPipeline = VkGraphicsPipeline(0L)
    private val bindGroups = Array<BindGroupDataVk?>(4) { null }

    override val frameIndex: Int get() = renderPassVk.backend.swapchain.currentFrameIndex

    fun setup(
        commandBuffer: VkCommandBuffer,
        renderPass: T,
        stack: MemoryStack
    ) {
        _commandBuffer = commandBuffer
        _renderPass = renderPass
        _stack = stack
    }

    fun begin(viewIndex: Int, mipLevel: Int, /*timestampWrites: GPURenderPassTimestampWrites? = null,*/ forceLoad: Boolean = false) {
        check(!isPassActive)
        isPassActive = true
        renderPassVk.beginRenderPass(this, viewIndex, mipLevel)
    }

    fun end() {
        if (isPassActive) {
            vkCmdEndRenderPass(commandBuffer)
            isPassActive = false
            activePipeline = VkGraphicsPipeline(0L)
            for (i in bindGroups.indices) {
                bindGroups[i] = null
            }
        }
    }

    fun setPipeline(renderPipeline: VkGraphicsPipeline) {
        if (this.activePipeline != renderPipeline) {
            this.activePipeline = renderPipeline
            vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, renderPipeline.handle)
        }
    }

    override fun setBindGroup(group: Int, bindGroupData: BindGroupDataVk, pipeline: VkPipeline) {
        val bindGroup = bindGroupData.bindGroup
        if (bindGroups[group] !== bindGroupData && bindGroup != null) {
            bindGroups[group] = bindGroupData
            vkCmdBindDescriptorSets(
                commandBuffer,
                VK_PIPELINE_BIND_POINT_GRAPHICS,
                pipeline.pipelineLayout.handle,
                group,
                stack.longs(bindGroup.getDescriptorSet(frameIndex).handle),
                null
            )
        }
    }
}