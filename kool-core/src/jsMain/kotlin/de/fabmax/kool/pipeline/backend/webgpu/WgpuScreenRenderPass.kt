package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Time

class WgpuScreenRenderPass(val backend: RenderBackendWebGpu, val multiSamples: Int = 4) {

    val depthFormat = GPUTextureFormat.depth32float

    private val device: GPUDevice
        get() = backend.device
    private val canvasContext: GPUCanvasContext
        get() = backend.canvasContext

    private var colorAttachment: GPUTexture? = null
    private var colorAttachmentView: GPUTextureView? = null

    private var depthAttachment: GPUTexture? = null
    private var depthAttachmentView: GPUTextureView? = null

    fun onCanvasResized(newWidth: Int, newHeight: Int) {
        createRenderAttachments(newWidth, newHeight)
    }

    fun renderViewsPass(renderPass: RenderPass) {
        check(renderPass is Scene.OnscreenSceneRenderPass)

        if (depthAttachment == null || colorAttachment == null) {
            createRenderAttachments(backend.canvas.width, backend.canvas.height)
        }

        device.queue.submit(renderPass.views.map { it.encodeQueue() }.toTypedArray())
    }

    private fun RenderPass.View.encodeQueue(): GPUCommandBuffer {
        val commandEncoder = device.createCommandEncoder()

        val colorAttachments = clearColors.map { clearColor ->
            GPURenderPassColorAttachment(
                view = colorAttachmentView!!,
                clearValue = clearColor?.let { GPUColorDict(it) },
                resolveTarget = canvasContext.getCurrentTexture().createView()
            )
        }.toTypedArray()

        val depthAttachment = GPURenderPassDepthStencilAttachment(
            view = depthAttachmentView!!,
            depthLoadOp = if (clearDepth) GPULoadOp.clear else GPULoadOp.load,
            depthStoreOp = GPUStoreOp.store,
            depthClearValue = 1f
        )

        val encoder = commandEncoder.beginRenderPass(GPURenderPassDescriptor(colorAttachments, depthAttachment))
        encoder.setViewport(viewport.x.toFloat(), viewport.y.toFloat(), viewport.width.toFloat(), viewport.height.toFloat(), 0f, 1f)
        encoder.setScissorRect(viewport.x, viewport.y, viewport.width, viewport.height)

        for (cmd in drawQueue.commands) {
            val t = Time.precisionTime

            if (cmd.geometry.numIndices == 0) continue
            if (cmd.pipeline == null) continue

            if (backend.pipelineManager.bindDrawPipeline(cmd, encoder, this@WgpuScreenRenderPass)) {
                val insts = cmd.mesh.instances
                if (insts == null) {
                    encoder.drawIndexed(cmd.geometry.numIndices)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                } else {
                    encoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                }
                cmd.mesh.drawTime = Time.precisionTime - t
            }
        }

        encoder.end()
        return commandEncoder.finish()
    }

    private fun createRenderAttachments(width: Int, height: Int) {
        colorAttachment?.destroy()
        depthAttachment?.destroy()

        val colorDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = backend.canvasFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = multiSamples
        )
        colorAttachment = device.createTexture(colorDescriptor).also {
            colorAttachmentView = it.createView()
        }

        val depthDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = depthFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = multiSamples
        )
        depthAttachment = device.createTexture(depthDescriptor).also {
            depthAttachmentView = it.createView()
        }
    }

}