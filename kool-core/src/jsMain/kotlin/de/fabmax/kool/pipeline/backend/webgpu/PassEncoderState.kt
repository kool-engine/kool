package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.RenderPass

interface PassEncoderState {
    fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData)
}

class ComputePassEncoderState: PassEncoderState {
    private var _encoder: GPUCommandEncoder? = null
    private var _passEncoder: GPUComputePassEncoder? = null

    val encoder: GPUCommandEncoder
        get() = _encoder!!
    val passEncoder: GPUComputePassEncoder
        get() = _passEncoder!!
    var isPassActive = false
        private set

    private var computePipeline: GPUComputePipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    fun setup(encoder: GPUCommandEncoder, passEncoder: GPUComputePassEncoder) {
        check(!isPassActive)
        _encoder = encoder
        _passEncoder = passEncoder
        isPassActive = true
    }

    fun end() {
        if (isPassActive) {
            passEncoder.end()
            isPassActive = false

            computePipeline = null
            for (i in bindGroups.indices) {
                bindGroups[i] = null
            }
        }
    }

    fun setPipeline(computePipeline: GPUComputePipeline) {
        if (this.computePipeline !== computePipeline) {
            this.computePipeline = computePipeline
            passEncoder.setPipeline(computePipeline)
        }
    }

    override fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData) {
        if (bindGroups[group] !== bindGroupData) {
            bindGroups[group] = bindGroupData
            passEncoder.setBindGroup(group, bindGroupData.bindGroup!!)
        }
    }
}

class RenderPassEncoderState<T: RenderPass>(val gpuRenderPass: WgpuRenderPass<T>): PassEncoderState {
    private var isPassActive = false
    private var _encoder: GPUCommandEncoder? = null
    private var _renderPass: T? = null
    private var _passEncoder: GPURenderPassEncoder? = null

    val encoder: GPUCommandEncoder get() = _encoder!!
    val passEncoder: GPURenderPassEncoder get() = _passEncoder!!
    val renderPass: T get() = _renderPass!!

    private var activePipeline: GPURenderPipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    fun setup(
        encoder: GPUCommandEncoder,
        renderPass: T
    ) {
        _encoder = encoder
        _renderPass = renderPass
    }

    fun begin(viewIndex: Int, mipLevel: Int, timestampWrites: GPURenderPassTimestampWrites? = null, forceLoad: Boolean = false) {
        check(!isPassActive)

        val (colorAttachments, depthAttachment) = gpuRenderPass.getRenderAttachments(renderPass, viewIndex, mipLevel, forceLoad)
        _passEncoder = encoder.beginRenderPass(colorAttachments, depthAttachment, timestampWrites, renderPass.name)
        isPassActive = true
    }

    fun end() {
        if (isPassActive) {
            passEncoder.end()
            isPassActive = false

            activePipeline = null
            for (i in bindGroups.indices) {
                bindGroups[i] = null
            }
        }
    }

    fun setPipeline(renderPipeline: GPURenderPipeline) {
        if (this.activePipeline !== renderPipeline) {
            this.activePipeline = renderPipeline
            passEncoder.setPipeline(renderPipeline)
        }
    }

    override fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData) {
        if (bindGroups[group] !== bindGroupData) {
            bindGroups[group] = bindGroupData
            passEncoder.setBindGroup(group, bindGroupData.bindGroup!!)
        }
    }
}