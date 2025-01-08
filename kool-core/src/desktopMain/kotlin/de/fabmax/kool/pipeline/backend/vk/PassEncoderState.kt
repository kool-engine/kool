package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.RenderPass
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

interface PassEncoderState {
    fun setBindGroup(group: Int, bindGroupData: BindGroupDataVk)
}

//class ComputePassEncoderState: PassEncoderState {
//    private var _encoder: VkCommandBuffer? = null
//    //private var _passEncoder: GPUComputePassEncoder? = null
//
//    val encoder: VkCommandBuffer
//        get() = _encoder!!
////    val passEncoder: GPUComputePassEncoder
////        get() = _passEncoder!!
//    var isPassActive = false
//        private set
//
//    private var computePipeline: GPUComputePipeline? = null
//    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }
//
//    fun setup(encoder: GPUCommandEncoder, passEncoder: GPUComputePassEncoder) {
//        check(!isPassActive)
//        _encoder = encoder
//        _passEncoder = passEncoder
//        isPassActive = true
//    }
//
//    fun end() {
//        if (isPassActive) {
//            passEncoder.end()
//            isPassActive = false
//
//            computePipeline = null
//            for (i in bindGroups.indices) {
//                bindGroups[i] = null
//            }
//        }
//    }
//
//    fun setPipeline(computePipeline: GPUComputePipeline) {
//        if (this.computePipeline !== computePipeline) {
//            this.computePipeline = computePipeline
//            passEncoder.setPipeline(computePipeline)
//        }
//    }
//
//    override fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData) {
//        if (bindGroups[group] !== bindGroupData) {
//            bindGroups[group] = bindGroupData
//            passEncoder.setBindGroup(group, bindGroupData.bindGroup!!)
//        }
//    }
//}

class RenderPassEncoderState<T: RenderPass>(val renderPassVk: RenderPassVk<T>): PassEncoderState {
    private var isPassActive = false
    private var _commandBuffer: VkCommandBuffer? = null
    private var _renderPass: T? = null
    private var _stack: MemoryStack? = null

    val commandBuffer: VkCommandBuffer get() = _commandBuffer!!
    val renderPass: T get() = _renderPass!!
    val stack: MemoryStack get() = _stack!!

    private var activePipeline: VkGraphicsPipeline = VkGraphicsPipeline(0L)
    private val bindGroups = Array<BindGroupDataVk?>(4) { null }

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

    override fun setBindGroup(group: Int, bindGroupData: BindGroupDataVk) {
        if (bindGroups[group] !== bindGroupData) {
            bindGroups[group] = bindGroupData
            //vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, bindGroupData.gpuLayout.handle, 0, ...)
        }
    }
}