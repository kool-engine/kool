package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.RenderPass
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

interface PassEncoderState {
    val frameIndex: Int
    val renderPass: RenderPass
    val stack: MemoryStack
    fun setBindGroup(group: Int, bindGroupData: BindGroupDataVk, pipelineLayout: VkPipelineLayout)
}

class RenderPassEncoderState(val backend: RenderBackendVk): PassEncoderState {
    private var _gpuRenderPass: RenderPassVk? = null
    private var _renderPass: RenderPass? = null
    private var _commandBuffer: VkCommandBuffer? = null
    private var _stack: MemoryStack? = null

    val commandBuffer: VkCommandBuffer get() = _commandBuffer!!
    val gpuRenderPass: RenderPassVk get() = _gpuRenderPass!!
    override val renderPass: RenderPass get() = _renderPass!!
    override val stack: MemoryStack get() = _stack!!
    override val frameIndex: Int get() = backend.swapchain.currentFrameIndex

    var isPassActive = false
        private set
    var mipLevel = 0
        private set
    var layer = 0
        private set
    private var activePipeline: VkGraphicsPipeline = VkGraphicsPipeline(0L)
    private val bindGroups = Array<BindGroupDataVk?>(4) { null }

    fun beginFrame(stack: MemoryStack) {
        _stack = stack

        _commandBuffer = backend.commandBuffers[frameIndex]
        commandBuffer.reset()
        commandBuffer.begin(stack) { }
    }

    fun endFrame() {
        if (isPassActive) {
            ensureRenderPassInactive()
        }

        with(stack) {
            commandBuffer.end()
            backend.device.graphicsQueue.submit(backend.swapchain.inFlightFence, this) {
                pWaitDstStageMask(ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                pCommandBuffers(pointers(commandBuffer))
                pSignalSemaphores(longs(backend.swapchain.renderFinishedSema.handle))
                pWaitSemaphores(longs(backend.swapchain.imageAvailableSema.handle))
                waitSemaphoreCount(1)
            }
            _commandBuffer = null
        }
    }

    fun beginRenderPass(
        renderPass: RenderPass,
        gpuRenderPass: RenderPassVk,
        mipLevel: Int,
        layer: Int = 0,
        //timestampWrites: GPURenderPassTimestampWrites? = null,
        forceLoad: Boolean = false
    ) {
        if (isPassActive) {
            if (gpuRenderPass === _gpuRenderPass && mipLevel == this.mipLevel && layer == this.layer && renderPass.clearColors.size == 1) {
                _renderPass = renderPass
                if (renderPass.clearDepth || renderPass.clearColor != null) {
                    backend.clearHelper.clear(this)
                    activePipeline = VkGraphicsPipeline(0L)
                    for (i in bindGroups.indices) {
                        bindGroups[i] = null
                    }
                }
                return
            }
            ensureRenderPassInactive()
        }

        isPassActive = true
        this.mipLevel = mipLevel
        this.layer = layer
        _gpuRenderPass = gpuRenderPass
        _renderPass = renderPass
        gpuRenderPass.beginRenderPass(this)
    }

    fun ensureRenderPassInactive() {
        if (isPassActive) {
            vkCmdEndRenderPass(commandBuffer)
            isPassActive = false
            _gpuRenderPass = null
            _renderPass = null
            mipLevel = 0
            layer = 0
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

    override fun setBindGroup(group: Int, bindGroupData: BindGroupDataVk, pipelineLayout: VkPipelineLayout) {
        val bindGroup = bindGroupData.bindGroup
        if (bindGroups[group] !== bindGroupData && bindGroup != null) {
            bindGroups[group] = bindGroupData
            vkCmdBindDescriptorSets(
                commandBuffer,
                VK_PIPELINE_BIND_POINT_GRAPHICS,
                pipelineLayout.handle,
                group,
                stack.longs(bindGroup.getDescriptorSet(frameIndex).handle),
                null
            )
        }
    }
}