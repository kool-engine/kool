package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.pipeline.ClearDepthFill
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.Viewport
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkRect2D
import org.lwjgl.vulkan.VkViewport

class PassEncoderState(val backend: RenderBackendVk) {
    private var _gpuRenderPass: RenderPassVk? = null
    private var _renderPass: RenderPass? = null
    private var _commandBuffer: VkCommandBuffer? = null
    private var _stack: MemoryStack? = null

    val commandBuffer: VkCommandBuffer get() = _commandBuffer!!
    val gpuRenderPass: RenderPassVk get() = _gpuRenderPass!!
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
            if (gpuRenderPass === _gpuRenderPass && mipLevel == this.mipLevel && layer == this.layer && renderPass.colorAttachments.size == 1) {
                _renderPass = renderPass
                if (renderPass.colorAttachments[0].clearColor is ClearColorFill || renderPass.depthAttachment?.clearDepth == ClearDepthFill) {
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
        gpuRenderPass.beginRenderPass(this, forceLoad)

        val width = (renderPass.width shr mipLevel).coerceAtLeast(1)
        val height = (renderPass.height shr mipLevel).coerceAtLeast(1)
        scissorBuffer[0].apply {
            extent { it.set(width, height) }
        }
        vkCmdSetScissor(commandBuffer, 0, scissorBuffer)
    }

    fun ensureRenderPassInactive() {
        if (isPassActive) {
            gpuRenderPass.endRenderPass(this)
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

    fun setViewport(viewport: Viewport, mipLevel: Int) {
        val x = viewport.x shr mipLevel
        val y = viewport.y shr mipLevel
        val width = (viewport.width shr mipLevel).coerceAtLeast(1)
        val height = (viewport.height shr mipLevel).coerceAtLeast(1)
        setViewport(x, y, width, height)
    }

    fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        viewportBuffer[0].set(x.toFloat(), (height + y).toFloat(), width.toFloat(), (-height).toFloat(), 0f, 1f)
        vkCmdSetViewport(commandBuffer, 0, viewportBuffer)
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
        descriptorSets.put(viewIdx, viewGroup.getDescriptorSet(frameIndex).handle)
        descriptorSets.put(pipelineIdx, pipelineGroup.getDescriptorSet(frameIndex).handle)
        descriptorSets.put(meshIdx, meshGroup.getDescriptorSet(frameIndex).handle)

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
        descriptorSets.put(0, bindGroup.getDescriptorSet(frameIndex).handle)
        vkCmdBindDescriptorSets(
            commandBuffer,
            bindPoint.vk,
            pipelineLayout.handle,
            groupIndex,
            descriptorSets,
            null
        )
    }

    companion object {
        private val viewportBuffer = VkViewport.calloc(1)
        private val scissorBuffer = VkRect2D.calloc(1)
    }
}

enum class BindPoint(val vk: Int) {
    Graphics(VK_PIPELINE_BIND_POINT_GRAPHICS),
    Compute(VK_PIPELINE_BIND_POINT_COMPUTE),
}