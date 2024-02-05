package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time

abstract class WgpuRenderPass<T: RenderPass>(
    val depthFormat: GPUTextureFormat?,
    val numSamples: Int,
    val backend: RenderBackendWebGpu
) : BaseReleasable() {

    private val passEncoderState = PassEncoderState(this)

    protected val device: GPUDevice
        get() = backend.device

    abstract val colorTargetFormats: List<GPUTextureFormat>

    protected fun render(renderPass: T, encoder: GPUCommandEncoder) {
        val t = if (renderPass.isProfileTimes) Time.precisionTime else 0.0

        val mipLevels = if (renderPass.drawMipLevels) renderPass.mipLevels else 1
        for (mipLevel in 0 until mipLevels) {
            renderPass.setupMipLevel(mipLevel)

            when (renderPass.viewRenderMode) {
                RenderPass.ViewRenderMode.SINGLE_RENDER_PASS -> {
                    val (colorAttachments, depthAttachment) = getRenderAttachments(renderPass, 0, mipLevel)
                    passEncoderState.reset(encoder.beginRenderPass(colorAttachments, depthAttachment, renderPass.name), renderPass)
                    for (viewIndex in renderPass.views.indices) {
                        renderView(viewIndex, mipLevel, passEncoderState)
                    }
                    passEncoderState.passEncoder.end()
                }

                RenderPass.ViewRenderMode.MULTI_RENDER_PASS -> {
                    for (viewIndex in renderPass.views.indices) {
                        val (colorAttachments, depthAttachment) = getRenderAttachments(renderPass, viewIndex, mipLevel)
                        passEncoderState.reset(encoder.beginRenderPass(colorAttachments, depthAttachment, renderPass.name), renderPass)
                        renderView(viewIndex, mipLevel, passEncoderState)
                        passEncoderState.passEncoder.end()
                    }
                }
            }
        }

        if (renderPass.isProfileTimes) {
            renderPass.tDraw = Time.precisionTime - t
        }
    }

    private fun renderView(viewIndex: Int, mipLevel: Int, passEncoderState: PassEncoderState) {
        val passEncoder = passEncoderState.passEncoder
        val view = passEncoderState.renderPass.views[viewIndex]

        passEncoderState.renderPass.setupView(viewIndex)

        val viewport = view.viewport
        val x = (viewport.x shr mipLevel).toFloat()
        val y = (viewport.y shr mipLevel).toFloat()
        val w = (viewport.width shr mipLevel).toFloat()
        val h = (viewport.height shr mipLevel).toFloat()
        passEncoder.setViewport(x, y, w, h, 0f, 1f)

        for (cmd in view.drawQueue.commands) {
            val isCmdValid = cmd.pipeline != null && cmd.geometry.numIndices > 0
            if (isCmdValid && backend.pipelineManager.bindDrawPipeline(cmd, passEncoderState)) {
                val insts = cmd.mesh.instances
                if (insts == null) {
                    passEncoder.drawIndexed(cmd.geometry.numIndices)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                } else if (insts.numInstances > 0) {
                    passEncoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                }
            }
        }
    }

    protected abstract fun getRenderAttachments(renderPass: T, viewIndex: Int, mipLevel: Int): RenderAttachments

    protected data class RenderAttachments(
        val colorAttachments: Array<GPURenderPassColorAttachment>,
        val depthAttachment: GPURenderPassDepthStencilAttachment?
    )
}

class PassEncoderState(val gpuRenderPass: WgpuRenderPass<*>) {
    private var _renderPass: RenderPass? = null
    private var _passEncoder: GPURenderPassEncoder? = null

    val passEncoder: GPURenderPassEncoder
        get() = _passEncoder!!
    val renderPass: RenderPass
        get() = _renderPass!!

    private var renderPipeline: GPURenderPipeline? = null
    private val bindGroups = Array<WgpuBindGroupData?>(4) { null }

    fun reset(passEncoder: GPURenderPassEncoder, renderPass: RenderPass) {
        _renderPass = renderPass
        _passEncoder = passEncoder
        renderPipeline = null
        for (i in bindGroups.indices) {
            bindGroups[i] = null
        }
    }

    fun setPipeline(renderPipeline: GPURenderPipeline) {
        if (this.renderPipeline !== renderPipeline) {
            this.renderPipeline = renderPipeline
            passEncoder.setPipeline(renderPipeline)
        }
    }

    fun setBindGroup(group: Int, bindGroupData: WgpuBindGroupData) {
        if (bindGroups[group] !== bindGroupData) {
            bindGroups[group] = bindGroupData
            passEncoder.setBindGroup(group, bindGroupData.bindGroup!!)
        }
    }
}
