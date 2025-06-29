package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.*
import io.ygdrasil.webgpu.*

interface PassEncoderState {
    val renderPass: GpuPass
    fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData)
}

class ComputePassEncoderState: PassEncoderState {
    private var isPassActive = false
    private var _renderPass: ComputePass? = null
    private var _encoder: GPUCommandEncoder? = null
    private var _passEncoder: GPUComputePassEncoder? = null

    val encoder: GPUCommandEncoder get() = _encoder!!
    val passEncoder: GPUComputePassEncoder get() = _passEncoder!!
    override val renderPass: ComputePass get() = _renderPass!!

    private var computePipeline: GPUComputePipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    fun setup(encoder: GPUCommandEncoder, passEncoder: GPUComputePassEncoder, renderPass: ComputePass) {
        check(!isPassActive)
        _encoder = encoder
        _passEncoder = passEncoder
        _renderPass = renderPass
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
            passEncoder.setBindGroup(group.toUInt(), bindGroupData.bindGroup!!)
        }
    }
}

class RenderPassEncoderState(val backend: RenderBackendWgpu4k): PassEncoderState {
    private var _gpuRenderPass: WgpuRenderPass? = null
    private var _renderPass: RenderPass? = null
    private var _encoder: GPUCommandEncoder? = null
    private var _passEncoder: GPURenderPassEncoder? = null

    val encoder: GPUCommandEncoder get() = _encoder!!
    val passEncoder: GPURenderPassEncoder get() = _passEncoder!!
    val gpuRenderPass: WgpuRenderPass get() = _gpuRenderPass!!
    override val renderPass: RenderPass get() = _renderPass!!

    var isPassActive = false
        private set
    var mipLevel = 0
        private set
    var layer = 0
        private set
    private var activePipeline: GPURenderPipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    fun beginFrame() {
        _encoder = backend.device.createCommandEncoder()
    }

    fun endFrame() {
        if (isPassActive) {
            ensureRenderPassInactive()
        }
        val cmdBuffer = encoder.finish()
        _encoder = null
        backend.device.queue.submit(listOf(cmdBuffer))
    }

    fun beginRenderPass(
        renderPass: RenderPass,
        gpuRenderPass: WgpuRenderPass,
        mipLevel: Int,
        layer: Int = 0,
        timestampWrites: GPURenderPassTimestampWrites? = null,
        forceLoad: Boolean = false
    ) {
        if (isPassActive) {
            if (gpuRenderPass === _gpuRenderPass && mipLevel == this.mipLevel && layer == this.layer && renderPass.colorAttachments.size == 1) {
                _renderPass = renderPass
                if (renderPass.colorAttachments[0].clearColor is ClearColorFill || renderPass.depthAttachment?.clearDepth == ClearDepthFill) {
                    backend.clearHelper.clear(this)
                    activePipeline = null
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
        _passEncoder = gpuRenderPass.beginRenderPass(this, forceLoad, timestampWrites)
    }

    fun ensureRenderPassInactive() {
        if (isPassActive) {
            gpuRenderPass.endRenderPass(this)
            isPassActive = false
            _gpuRenderPass = null
            _renderPass = null
            _passEncoder = null
            mipLevel = 0
            layer = 0
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
            passEncoder.setBindGroup(group.toUInt(), bindGroupData.bindGroup!!)
        }
    }
}