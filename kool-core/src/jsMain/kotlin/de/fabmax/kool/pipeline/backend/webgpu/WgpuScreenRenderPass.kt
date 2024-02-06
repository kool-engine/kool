package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.launchDelayed

class WgpuScreenRenderPass(backend: RenderBackendWebGpu) :
    WgpuRenderPass<Scene.SceneRenderPass>(GPUTextureFormat.depth32float, KoolSystem.configJs.numSamples, backend)
{
    private val canvasContext: GPUCanvasContext
        get() = backend.canvasContext

    override val colorTargetFormats: List<GPUTextureFormat>
        get() = listOf(backend.canvasFormat)

    private var colorTexture: GPUTexture? = null
    private var colorTextureView: GPUTextureView? = null

    private var depthAttachment: GPUTexture? = null
    private var depthAttachmentView: GPUTextureView? = null

    fun applySize(width: Int, height: Int) {
        updateRenderTextures(width, height)
    }

    fun renderScene(scenePass: Scene.SceneRenderPass, encoder: GPUCommandEncoder) {
        if (depthAttachment == null || colorTexture == null) {
            updateRenderTextures(backend.canvas.width, backend.canvas.height)
        }
        render(scenePass, encoder)
    }

    fun captureFramebuffer(scene: Scene, encoder: GPUCommandEncoder) {
        val src = colorTexture!!
        val width = src.width
        val height = src.height

        val target = scene.capturedFramebuffer
        var copyDst = (target.loadedTexture as WgpuLoadedTexture?)
        if (copyDst == null || copyDst.width != width || copyDst.height != height) {
            copyDst?.let {
                launchDelayed(1) { it.release() }
            }

            val descriptor = GPUTextureDescriptor(
                label = "${scene.name}-capturedFramebuffer",
                size = intArrayOf(width, height),
                format = backend.canvasFormat,
                usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
            )
            val texResource = backend.createTexture(descriptor, target)
            copyDst = WgpuLoadedTexture(texResource)
            target.loadedTexture = copyDst
            target.loadingState = Texture.LoadingState.LOADED
        }

        if (numSamples > 1) {
            // run an empty render pass, which resolves the multi-sampled color texture into the target capture texture
            val passEncoder = encoder.beginRenderPass(
                colorAttachments = arrayOf(GPURenderPassColorAttachment(
                    view = src.createView(),
                    resolveTarget = copyDst.texture.gpuTexture.createView(),
                ))
            )
            passEncoder.end()
        } else {
            backend.textureLoader.copyTexture2d(src, copyDst.texture.gpuTexture, 1, encoder)
        }
    }

    override fun getRenderAttachments(renderPass: Scene.SceneRenderPass, viewIndex: Int, mipLevel: Int): RenderAttachments {
        val colors = arrayOf(
            GPURenderPassColorAttachment(
                view = colorTextureView!!,
                clearValue = renderPass.clearColor?.let { GPUColorDict(it) },
                resolveTarget = canvasContext.getCurrentTexture().createView()
            )
        )
        val depth = GPURenderPassDepthStencilAttachment(
            view = depthAttachmentView!!,
            depthLoadOp = if (renderPass.clearDepth) GPULoadOp.clear else GPULoadOp.load,
            depthStoreOp = GPUStoreOp.store,
            depthClearValue = if (renderPass.isReverseDepth) 0f else 1f
        )
        return RenderAttachments(colors, depth)
    }

    private fun updateRenderTextures(width: Int, height: Int) {
        colorTexture?.destroy()
        depthAttachment?.destroy()

        val colorDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = backend.canvasFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT or GPUTextureUsage.TEXTURE_BINDING,
            sampleCount = numSamples
        )
        colorTexture = device.createTexture(colorDescriptor).also {
            colorTextureView = it.createView()
        }

        val depthDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = depthFormat!!,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = numSamples
        )
        depthAttachment = device.createTexture(depthDescriptor).also {
            depthAttachmentView = it.createView()
        }
    }
}