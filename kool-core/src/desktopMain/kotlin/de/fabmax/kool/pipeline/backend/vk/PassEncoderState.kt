package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.RenderPass
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class PassEncoderState(val backend: RenderBackendVk) {
    private var _gpuRenderPass: RenderPassVk? = null
    private var _renderPass: RenderPass? = null
    private var _commandBuffer: VkCommandBuffer? = null
    private var _stack: MemoryStack? = null

    val commandBuffer: VkCommandBuffer get() = _commandBuffer!!
    val gpuRenderPass: RenderPassVk get() = _gpuRenderPass!!
    var vkRenderPass: VkRenderPass = VkRenderPass(0); private set
    val renderPass: RenderPass get() = _renderPass!!
    val stack: MemoryStack get() = _stack!!
    val frameIndex: Int get() = backend.swapchain.currentFrameIndex

    var isPassActive = false
        private set
    var mipLevel = 0
        private set
    var layer = 0
        private set
    private var activePipeline: Long = 0L
    private var currentViewGroup: BindGroupDataVk? = null
    private var currentPipelineGroup: BindGroupDataVk? = null

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
                    activePipeline = 0L
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
            activePipeline = 0L
            currentViewGroup = null
            currentPipelineGroup = null
        }
    }

    fun setPipeline(renderPipeline: VkGraphicsPipeline) {
        if (activePipeline != renderPipeline.handle) {
            activePipeline = renderPipeline.handle
            vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, renderPipeline.handle)
        }
    }

    fun setComputePipeline(computePipeline: VkComputePipeline) {
        if (activePipeline != computePipeline.handle) {
            activePipeline = computePipeline.handle
            vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_COMPUTE, computePipeline.handle)
        }
    }

    fun setBindGroups(
        viewGroup: BindGroupDataVk,
        pipelineGroup: BindGroupDataVk,
        meshGroup: BindGroupDataVk,
        pipelineLayout: VkPipelineLayout,
        bindPoint: BindPoint
    ) {
        if (viewGroup == currentViewGroup && pipelineGroup == currentPipelineGroup) {
            setBindGroup(meshGroup, pipelineLayout, bindPoint)
            return
        }
        currentViewGroup = viewGroup
        currentPipelineGroup = pipelineGroup

        val viewIdx = viewGroup.data.layout.group
        val pipelineIdx = pipelineGroup.data.layout.group
        val meshIdx = meshGroup.data.layout.group

        descriptorSets.limit(3)
        viewGroup.bindGroup?.let { descriptorSets.put(viewIdx, it.getDescriptorSet(frameIndex).handle) }
        pipelineGroup.bindGroup?.let { descriptorSets.put(pipelineIdx, it.getDescriptorSet(frameIndex).handle) }
        meshGroup.bindGroup?.let { descriptorSets.put(meshIdx, it.getDescriptorSet(frameIndex).handle) }
        vkCmdBindDescriptorSets(
            commandBuffer,
            bindPoint.vk,
            pipelineLayout.handle,
            0,
            descriptorSets,
            null
        )
    }

    fun setBindGroup(
        bindGroup: BindGroupDataVk,
        pipelineLayout: VkPipelineLayout,
        bindPoint: BindPoint,
        groupIndex: Int = bindGroup.data.layout.group
    ) {
        descriptorSets.limit(1)
        bindGroup.bindGroup?.let { descriptorSets.put(0, it.getDescriptorSet(frameIndex).handle) }
        vkCmdBindDescriptorSets(
            commandBuffer,
            bindPoint.vk,
            pipelineLayout.handle,
            groupIndex,
            descriptorSets,
            null
        )
    }
}

enum class BindPoint(val vk: Int) {
    Graphics(VK_PIPELINE_BIND_POINT_GRAPHICS),
    Compute(VK_PIPELINE_BIND_POINT_COMPUTE),
}