package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.pipeline.ClearColorLoad
import de.fabmax.kool.pipeline.ClearDepthLoad
import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.launchDelayed

class WgpuScreenPass(backend: RenderBackendWebGpu) :
    WgpuRenderPass(GPUTextureFormat.depth32float, KoolSystem.configJs.numSamples, backend)
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

    fun renderScene(scenePass: Scene.ScreenPass, passEncoderState: RenderPassEncoderState) {
        if (depthAttachment == null || colorTexture == null) {
            updateRenderTextures(backend.ctx.canvas.width, backend.ctx.canvas.height)
        }
        render(scenePass, passEncoderState)
    }

    override fun generateMipLevels(encoder: GPUCommandEncoder) { }

    override fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder) {
        val colorDst = if (frameCopy.isCopyColor) frameCopy.colorCopy2d else null
        val depthDst = if (frameCopy.isCopyDepth) frameCopy.depthCopy2d else null

        val colorSrc = colorTexture!!
        val width = colorSrc.width
        val height = colorSrc.height

        var colorDstWgpu: WgpuTextureResource? = null
        var depthDstWgpu: WgpuTextureResource? = null

        colorDst?.let { dst ->
            var copyDstC = (dst.gpuTexture as WgpuTextureResource?)
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
                val texResource = backend.createTexture(descriptor)
                copyDstC = texResource
                dst.gpuTexture = copyDstC
            }
            colorDstWgpu = copyDstC
        }

        depthDst?.let { dst ->
            var copyDstD = (dst.gpuTexture as WgpuTextureResource?)
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
                val texResource = backend.createTexture(descriptor)
                copyDstD = texResource
                dst.gpuTexture = copyDstD
            }
            depthDstWgpu = copyDstD
        }

        if (numSamples > 1 && colorDstWgpu != null) {
            // run an empty render pass, which resolves the multi-sampled color texture into the target capture texture
            val passEncoder = encoder.beginRenderPass(
                colorAttachments = arrayOf(GPURenderPassColorAttachment(
                    view = colorTextureView!!,
                    resolveTarget = colorDstWgpu.gpuTexture.createView(),
                )),
            )
            passEncoder.end()

        } else {
            colorDstWgpu?.let { copyDst ->
                backend.textureLoader.copyTexture2d(colorSrc, copyDst.gpuTexture, 1, encoder)
            }
        }
        depthDstWgpu?.let { copyDst ->
            backend.textureLoader.resolveMultiSampledDepthTexture(depthAttachment!!, copyDst.gpuTexture, encoder)
        }
    }

    override fun beginRenderPass(
        passEncoderState: RenderPassEncoderState,
        forceLoad: Boolean,
        timestampWrites: GPURenderPassTimestampWrites?
    ): GPURenderPassEncoder {
        val renderPass = passEncoderState.renderPass
        val colorLoadOp = when {
            forceLoad -> GPULoadOp.load
            renderPass.colorAttachments[0].clearColor is ClearColorLoad -> GPULoadOp.load
            else -> GPULoadOp.clear
        }
        val clearColor = if (colorLoadOp == GPULoadOp.load) null else {
            (renderPass.colorAttachments[0].clearColor as? ClearColorFill)?.let { GPUColorDict(it.clearColor) }
        }

        val depthLoadOp = when {
            forceLoad -> GPULoadOp.load
            renderPass.depthAttachment?.clearDepth == ClearDepthLoad -> GPULoadOp.load
            else -> GPULoadOp.clear
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
            depthClearValue = renderPass.depthMode.far
        )
        return passEncoderState.encoder.beginRenderPass(colors, depth, timestampWrites, renderPass.name)
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