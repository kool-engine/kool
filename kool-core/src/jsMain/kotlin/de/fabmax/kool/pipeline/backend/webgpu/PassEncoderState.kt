package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.pipeline.RenderPass

interface PassEncoderState {
    val renderPass: RenderPass
    fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData)
}

class ComputePassEncoderState: PassEncoderState {
    private var isPassActive = false
    private var _renderPass: ComputeRenderPass? = null
    private var _encoder: GPUCommandEncoder? = null
    private var _passEncoder: GPUComputePassEncoder? = null

    val encoder: GPUCommandEncoder get() = _encoder!!
    val passEncoder: GPUComputePassEncoder get() = _passEncoder!!
    override val renderPass: ComputeRenderPass get() = _renderPass!!

    private var computePipeline: GPUComputePipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    fun setup(encoder: GPUCommandEncoder, passEncoder: GPUComputePassEncoder, renderPass: ComputeRenderPass) {
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
            passEncoder.setBindGroup(group, bindGroupData.bindGroup!!)
        }
    }
}

class RenderPassEncoderState(val backend: RenderBackendWebGpu): PassEncoderState {
    private var _gpuRenderPass: WgpuRenderPass? = null
    private var _renderPass: RenderPass? = null
    private var _encoder: GPUCommandEncoder? = null
    private var _passEncoder: GPURenderPassEncoder? = null

    val encoder: GPUCommandEncoder get() = _encoder!!
    val passEncoder: GPURenderPassEncoder get() = _passEncoder!!
    val gpuRenderPass: WgpuRenderPass get() = _gpuRenderPass!!
    override val renderPass: RenderPass get() = _renderPass!!

    private var activePipeline: GPURenderPipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    private var isPassActive = false
    private var currentLevel = 0
    private var currentLayer = 0

    fun beginFrame() {
        _encoder = backend.device.createCommandEncoder()
    }

    fun endFrame(): GPUCommandBuffer {
        if (isPassActive) {
            ensureRenderPassInactive()
        }
        val cmdBuffer = encoder.finish()
        _encoder = null
        return cmdBuffer
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
            if (gpuRenderPass === _gpuRenderPass && currentLevel == mipLevel && currentLayer == layer && renderPass.clearColors.size == 1) {
                _renderPass = renderPass
                if (renderPass.clearDepth || renderPass.clearColor != null) {
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

        val attachments = gpuRenderPass.getRenderAttachments(renderPass, mipLevel, layer, forceLoad)
        _passEncoder = encoder.beginRenderPass(attachments.colorAttachments, attachments.depthAttachment, timestampWrites, renderPass.name)
        isPassActive = true
        currentLevel = mipLevel
        currentLayer = layer
        _gpuRenderPass = gpuRenderPass
        _renderPass = renderPass
    }

    fun ensureRenderPassInactive() {
        if (isPassActive) {
            passEncoder.end()
            isPassActive = false
            _gpuRenderPass = null
            _renderPass = null
            _passEncoder = null
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