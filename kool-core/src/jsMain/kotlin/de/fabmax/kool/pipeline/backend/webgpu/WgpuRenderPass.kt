package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Time

class WgpuRenderPass(val backend: RenderBackendWebGpu, val multiSamples: Int = 4) {

    val depthFormat = GPUTextureFormat.depth32float

    private val device: GPUDevice
        get() = backend.device
    private val gpuContext: GPUCanvasContext
        get() = backend.gpuContext

    private var colorAttachment: GPUTexture? = null
    private var colorAttachmentView: GPUTextureView? = null

    private var depthAttachment: GPUTexture? = null
    private var depthAttachmentView: GPUTextureView? = null

    fun createRenderAttachments(
        width: Int = backend.canvas.width,
        height: Int = backend.canvas.height,
    ) {
        colorAttachment?.destroy()
        depthAttachment?.destroy()

        val depthDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = depthFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = multiSamples
        )
        depthAttachment = device.createTexture(depthDescriptor).also {
            depthAttachmentView = it.createView()
        }

        val colorDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = backend.canvasFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = multiSamples
        )
        colorAttachment = device.createTexture(colorDescriptor).also {
            colorAttachmentView = it.createView()
        }
    }

    fun doForegroundPass(scene: Scene) {
        if (depthAttachment == null || colorAttachment == null) {
            createRenderAttachments()
        }

        val scenePass = when (val pass = scene.mainRenderPass) {
            is Scene.OnscreenSceneRenderPass -> pass
            else -> TODO()
        }

        device.queue.submit(scenePass.views.map { it.encodeQueue() }.toTypedArray())
    }

    private fun RenderPass.View.encodeQueue(): GPUCommandBuffer {
        val commandEncoder = device.createCommandEncoder()

        val colorAttachments = clearColors.map { clearColor ->
            val resolveTarget = gpuContext.getCurrentTexture().createView()
            clearColor?.let {
                GPURenderPassColorAttachmentClear(colorAttachmentView!!, GPUColorDict(it), resolveTarget)
            } ?: GPURenderPassColorAttachmentLoad(colorAttachmentView!!, resolveTarget)
        }.toTypedArray()

        val depthAttachment = GPURenderPassDepthStencilAttachment(
            view = depthAttachmentView!!,
            depthLoadOp = GPULoadOp.clear,
            depthStoreOp = GPUStoreOp.store,
            depthClearValue = 1f
        )

        val encoder = commandEncoder.beginRenderPass(GPURenderPassDescriptor(colorAttachments, depthAttachment))
        //pass.setViewport()
        //pass.setScissor()

        for (cmd in drawQueue.commands) {
            val t = Time.precisionTime

            if (cmd.geometry.numIndices == 0) continue
            if (cmd.pipeline == null) continue

            if (backend.pipelineManager.bindDrawPipeline(cmd, encoder, this@WgpuRenderPass)) {
                val insts = cmd.mesh.instances
                if (insts == null) {
                    encoder.drawIndexed(cmd.geometry.numIndices)
                } else {
                    encoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
                }
                cmd.mesh.drawTime = Time.precisionTime - t
            }
        }

        encoder.end()
        return commandEncoder.finish()
    }
}