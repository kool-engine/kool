package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.FrameCopy
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

    override fun generateMipLevels(encoder: GPUCommandEncoder) { }

    override fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder) {
        val colorDst = if (frameCopy.isCopyColor) frameCopy.colorCopy2d else null
        val depthDst = if (frameCopy.isCopyDepth) frameCopy.depthCopy2d else null

        val colorSrc = colorTexture!!
        val width = colorSrc.width
        val height = colorSrc.height

        var colorDstWgpu: WgpuLoadedTexture? = null
        var depthDstWgpu: WgpuLoadedTexture? = null

        colorDst?.let { dst ->
            var copyDstC = (dst.gpuTexture as WgpuLoadedTexture?)
            if (copyDstC == null || copyDstC.width != width || copyDstC.height != height) {
                copyDstC?.let {
                    launchDelayed(1) { it.release() }
                }

                val descriptor = GPUTextureDescriptor(
                    label = colorDst.name,
                    size = intArrayOf(width, height),
                    format = backend.canvasFormat,
                    usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
                )
                val texResource = backend.createTexture(descriptor, dst)
                copyDstC = WgpuLoadedTexture(texResource)
                dst.gpuTexture = copyDstC
                dst.loadingState = Texture.LoadingState.LOADED
            }
            colorDstWgpu = copyDstC
        }

        depthDst?.let { dst ->
            var copyDstD = (dst.gpuTexture as WgpuLoadedTexture?)
            if (copyDstD == null || copyDstD.width != width || copyDstD.height != height) {
                copyDstD?.let {
                    launchDelayed(1) { it.release() }
                }

                val descriptor = GPUTextureDescriptor(
                    label = dst.name,
                    size = intArrayOf(width, height),
                    format = depthFormat!!,
                    usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
                )
                val texResource = backend.createTexture(descriptor, dst)
                copyDstD = WgpuLoadedTexture(texResource)
                dst.gpuTexture = copyDstD
                dst.loadingState = Texture.LoadingState.LOADED
            }
            depthDstWgpu = copyDstD
        }

        if (numSamples > 1 && colorDstWgpu != null) {
            // run an empty render pass, which resolves the multi-sampled color texture into the target capture texture
            val passEncoder = encoder.beginRenderPass(
                colorAttachments = arrayOf(GPURenderPassColorAttachment(
                    view = colorTextureView!!,
                    resolveTarget = colorDstWgpu!!.texture.gpuTexture.createView(),
                )),
            )
            passEncoder.end()

        } else {
            colorDstWgpu?.let { copyDst ->
                backend.textureLoader.copyTexture2d(colorSrc, copyDst.texture.gpuTexture, 1, encoder)
            }
        }
        depthDstWgpu?.let { copyDst ->
            backend.textureLoader.resolveMultiSampledDepthTexture(depthAttachment!!, copyDst.texture.gpuTexture, encoder)
        }
    }

    override fun getRenderAttachments(renderPass: Scene.SceneRenderPass, viewIndex: Int, mipLevel: Int, forceLoad: Boolean): RenderAttachments {
        val colorLoadOp = when {
            forceLoad -> GPULoadOp.load
            renderPass.clearColor == null -> GPULoadOp.load
            else -> GPULoadOp.clear
        }
        val clearColor = if (colorLoadOp == GPULoadOp.load) null else renderPass.clearColor?.let { GPUColorDict(it) }

        val depthLoadOp = when {
            forceLoad -> GPULoadOp.load
            renderPass.clearDepth -> GPULoadOp.clear
            else -> GPULoadOp.load
        }

        val colors = arrayOf(
            GPURenderPassColorAttachment(
                view = colorTextureView!!,
                loadOp = colorLoadOp,
                clearValue = clearColor,
                resolveTarget = canvasContext.getCurrentTexture().createView()
            )
        )
        val depth = GPURenderPassDepthStencilAttachment(
            view = depthAttachmentView!!,
            depthLoadOp = depthLoadOp,
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
            usage = GPUTextureUsage.RENDER_ATTACHMENT or GPUTextureUsage.TEXTURE_BINDING,
            sampleCount = numSamples
        )
        depthAttachment = device.createTexture(depthDescriptor).also {
            depthAttachmentView = it.createView()
        }
    }
}