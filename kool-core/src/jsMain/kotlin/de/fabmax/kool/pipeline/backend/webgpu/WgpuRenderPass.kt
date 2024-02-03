package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time

abstract class WgpuRenderPass<T: RenderPass>(
    val depthFormat: GPUTextureFormat?,
    val numSamples: Int,
    val backend: RenderBackendWebGpu
) : BaseReleasable() {

    protected val device: GPUDevice
        get() = backend.device

    abstract val colorTargetFormats: List<GPUTextureFormat>

    protected fun render(renderPass: T, mipLevels: Int, encoder: GPUCommandEncoder) {
        val t = if (renderPass.isProfileTimes) Time.precisionTime else 0.0

        for (mipLevel in 0 until mipLevels) {
            if (renderPass is OffscreenRenderPass) {
                renderPass.setupMipLevel(mipLevel)
                for (i in renderPass.views.indices) {
                    renderPass.views[i].viewport.set(0, 0, renderPass.getMipWidth(mipLevel), renderPass.getMipHeight(mipLevel))
                }
            }

            for (viewIndex in renderPass.views.indices) {
                renderPass.setupView(viewIndex)

                val (colorAttachments, depthAttachment) = getRenderAttachments(renderPass, viewIndex, mipLevel)
                val view = renderPass.views[viewIndex]
                val viewport = view.viewport
                val x = viewport.x.toFloat()
                val y = viewport.y.toFloat()
                val w = viewport.width.toFloat()
                val h = viewport.height.toFloat()

                val passEncoder = encoder.beginRenderPass(colorAttachments, depthAttachment, renderPass.name)
                passEncoder.setViewport(x, y, w, h, 0f, 1f)

                for (cmd in view.drawQueue.commands) {
                    val isCmdValid = cmd.pipeline != null && cmd.geometry.numIndices > 0
                    if (isCmdValid && backend.pipelineManager.bindDrawPipeline(cmd, passEncoder, this@WgpuRenderPass)) {
                        val insts = cmd.mesh.instances
                        if (insts == null) {
                            passEncoder.drawIndexed(cmd.geometry.numIndices)
                            BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                        } else {
                            passEncoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
                            BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                        }
                    }
                }
                passEncoder.end()
            }
        }

        if (renderPass.isProfileTimes) {
            renderPass.tDraw = Time.precisionTime - t
        }
    }

    protected abstract fun getRenderAttachments(renderPass: T, viewIndex: Int, mipLevel: Int): RenderAttachments

    protected data class RenderAttachments(
        val colorAttachments: Array<GPURenderPassColorAttachment>,
        val depthAttachment: GPURenderPassDepthStencilAttachment?
    )
}