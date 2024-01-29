package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.Time

abstract class WgpuRenderPass(
    val depthFormat: GPUTextureFormat?,
    val multiSamples: Int,
    val backend: RenderBackendWebGpu
) {

    protected val device: GPUDevice
        get() = backend.device

    protected fun render(
        renderPass: RenderPass,
        colorAttachments: Array<GPURenderPassColorAttachment>,
        depthView: GPUTextureView?
    ) {
        val t = if (renderPass.isProfileTimes) Time.precisionTime else 0.0

        val commandEncoder = device.createCommandEncoder()
        for (view in renderPass.views) {
            val viewport = view.viewport
            val depthAttachment = depthView?.let { depth ->
                GPURenderPassDepthStencilAttachment(
                    view = depth,
                    depthLoadOp = if (view.clearDepth) GPULoadOp.clear else GPULoadOp.load,
                    depthStoreOp = GPUStoreOp.store,
                    depthClearValue = 1f
                )
            }

            val encoder = commandEncoder.beginRenderPass(GPURenderPassDescriptor(colorAttachments, depthAttachment))
            encoder.setViewport(viewport.x.toFloat(), viewport.y.toFloat(), viewport.width.toFloat(), viewport.height.toFloat(), 0f, 1f)
            encoder.setScissorRect(viewport.x, viewport.y, viewport.width, viewport.height)
            for (cmd in view.drawQueue.commands) {
                if (cmd.geometry.numIndices == 0) continue
                if (cmd.pipeline == null) continue

                if (backend.pipelineManager.bindDrawPipeline(cmd, encoder, this@WgpuRenderPass)) {
                    val insts = cmd.mesh.instances
                    if (insts == null) {
                        encoder.drawIndexed(cmd.geometry.numIndices)
                        BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                    } else {
                        encoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
                        BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                    }
                }
            }
            encoder.end()
        }
        device.queue.submit(arrayOf(commandEncoder.finish()))

        if (renderPass.isProfileTimes) {
            renderPass.tDraw = Time.precisionTime - t
        }
    }
}