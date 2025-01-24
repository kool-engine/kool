package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.RenderPass
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

interface PassEncoderState {
    val frameIndex: Int
    val renderPass: RenderPass
    val stack: MemoryStack
}

class RenderPassEncoderState(val backend: RenderBackendVk): PassEncoderState {
    private var _gpuRenderPass: RenderPassVk? = null
    private var _renderPass: RenderPass? = null
    private var _commandBuffer: VkCommandBuffer? = null
    private var _stack: MemoryStack? = null

    val commandBuffer: VkCommandBuffer get() = _commandBuffer!!
    val gpuRenderPass: RenderPassVk get() = _gpuRenderPass!!
    var vkRenderPass: VkRenderPass = VkRenderPass(0); private set
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

    private val descriptorSets = MemoryUtil.memAllocLong(3)

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
        forceLoad: Boolean = false
    ) {
        if (isPassActive) {
            if (gpuRenderPass === _gpuRenderPass && mipLevel == this.mipLevel && layer == this.layer && renderPass.clearColors.size == 1) {
                _renderPass = renderPass
                if (renderPass.clearDepth || renderPass.clearColor != null) {
                    backend.clearHelper.clear(this)
                    activePipeline = VkGraphicsPipeline(0L)
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
        vkRenderPass = gpuRenderPass.beginRenderPass(this, forceLoad)
    }

    fun ensureRenderPassInactive() {
        if (isPassActive) {
            vkCmdEndRenderPass(commandBuffer)
            vkRenderPass = VkRenderPass(0L)
            isPassActive = false
            _gpuRenderPass = null
            _renderPass = null
            mipLevel = 0
            layer = 0
            activePipeline = VkGraphicsPipeline(0L)
        }
    }

    fun setPipeline(renderPipeline: VkGraphicsPipeline) {
        if (this.activePipeline != renderPipeline) {
            this.activePipeline = renderPipeline
            vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, renderPipeline.handle)
        }
    }

    fun setBindGroups(
        viewGroup: BindGroupDataVk,
        pipelineGroup: BindGroupDataVk,
        meshGroup: BindGroupDataVk,
        pipelineLayout: VkPipelineLayout
    ) {
        val viewIdx = viewGroup.data.layout.group
        val pipelineIdx = pipelineGroup.data.layout.group
        val meshIdx = meshGroup.data.layout.group

        descriptorSets.limit(3)
        viewGroup.bindGroup?.let { descriptorSets.put(viewIdx, it.getDescriptorSet(frameIndex).handle) }
        pipelineGroup.bindGroup?.let { descriptorSets.put(pipelineIdx, it.getDescriptorSet(frameIndex).handle) }
        meshGroup.bindGroup?.let { descriptorSets.put(meshIdx, it.getDescriptorSet(frameIndex).handle) }
        vkCmdBindDescriptorSets(
            commandBuffer,
            VK_PIPELINE_BIND_POINT_GRAPHICS,
            pipelineLayout.handle,
            0,
            descriptorSets,
            null
        )
    }

    fun setBindGroup(
        bindGroup: BindGroupDataVk,
        pipelineLayout: VkPipelineLayout
    ) {
        descriptorSets.limit(1)
        bindGroup.bindGroup?.let { descriptorSets.put(0, it.getDescriptorSet(frameIndex).handle) }
        vkCmdBindDescriptorSets(
            commandBuffer,
            VK_PIPELINE_BIND_POINT_GRAPHICS,
            pipelineLayout.handle,
            bindGroup.data.layout.group,
            descriptorSets,
            null
        )
    }
}