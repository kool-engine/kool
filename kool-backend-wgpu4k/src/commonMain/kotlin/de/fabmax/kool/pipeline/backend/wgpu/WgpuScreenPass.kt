package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.pipeline.ClearColorLoad
import de.fabmax.kool.pipeline.ClearDepthLoad
import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.launchDelayed
import io.ygdrasil.webgpu.*

class WgpuScreenPass(backend: RenderBackendWgpu4k, numSamples: Int) :
    WgpuRenderPass(GPUTextureFormat.Depth32Float, numSamples, backend)
{
    private val surface: WgpuSurface
        get() = backend.surface

    override val colorTargetFormats: List<GPUTextureFormat>
        get() = listOf(backend.canvasFormat)

    private var colorTexture: GPUTexture? = null
    private var colorTextureView: GPUTextureView? = null

    private var depthAttachment: GPUTexture? = null
    private var depthAttachmentView: GPUTextureView? = null

    fun applySize(width: Int, height: Int) {
        updateRenderTextures(width.toUInt(), height.toUInt())
    }

    suspend fun renderScene(scenePass: Scene.ScreenPass, passEncoderState: RenderPassEncoderState) {
        if (depthAttachment == null || colorTexture == null) {
            updateRenderTextures(surface.width, surface.height)
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
            if (copyDstC == null || copyDstC.width.toUInt() != width || copyDstC.height.toUInt() != height) {
                copyDstC?.let {
                    launchDelayed(1) { it.release() }
                }

                val descriptor = TextureDescriptor(
                    label = colorDst.name,
                    size = Extent3D(width, height),
                    format = backend.canvasFormat,
                    usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding, GPUTextureUsage.RenderAttachment),
                )
                val texResource = backend.createTexture(descriptor)
                copyDstC = texResource
                dst.gpuTexture = copyDstC
            }
            colorDstWgpu = copyDstC
        }

        depthDst?.let { dst ->
            var copyDstD = (dst.gpuTexture as WgpuTextureResource?)
            if (copyDstD == null || copyDstD.width.toUInt() != width || copyDstD.height.toUInt() != height) {
                copyDstD?.let {
                    launchDelayed(1) { it.release() }
                }

                val descriptor = TextureDescriptor(
                    label = dst.name,
                    size = Extent3D(width, height),
                    format = depthFormat!!,
                    usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding, GPUTextureUsage.RenderAttachment),
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
                colorAttachments = listOf(
                    RenderPassColorAttachment(
                        view = colorTextureView!!,
                        loadOp = GPULoadOp.Load,
                        storeOp = GPUStoreOp.Store,
                        resolveTarget = colorDstWgpu.gpuTexture.createView(),
                    )
                ),
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
            forceLoad -> GPULoadOp.Load
            renderPass.colorAttachments[0].clearColor is ClearColorLoad -> GPULoadOp.Load
            else -> GPULoadOp.Clear
        }
        val clearColor = if (colorLoadOp == GPULoadOp.Load) null else {
            (renderPass.colorAttachments[0].clearColor as? ClearColorFill)?.let { Color(it.clearColor.r.toDouble(), it.clearColor.g.toDouble(), it.clearColor.b.toDouble(), it.clearColor.a.toDouble()) }
        }

        val depthLoadOp = when {
            forceLoad -> GPULoadOp.Load
            renderPass.depthAttachment?.clearDepth == ClearDepthLoad -> GPULoadOp.Load
            else -> GPULoadOp.Clear
        }

        val colors = listOf(
            RenderPassColorAttachment(
                view = colorTextureView!!,
                loadOp = colorLoadOp,
                storeOp = GPUStoreOp.Store,
                clearValue = clearColor,
                resolveTarget = surface.getCurrentTextureView()
            )
        )
        val depth = RenderPassDepthStencilAttachment(
            view = depthAttachmentView!!,
            depthLoadOp = depthLoadOp,
            depthStoreOp = GPUStoreOp.Store,
            depthClearValue = renderPass.depthMode.far
        )
        return passEncoderState.encoder.beginRenderPass(colors, depth, timestampWrites, renderPass.name)
    }

    private fun updateRenderTextures(width: UInt, height: UInt) {
        colorTexture?.close()
        depthAttachment?.close()

        val colorDescriptor = TextureDescriptor(
            size = Extent3D(width, height),
            format = backend.canvasFormat,
            usage = setOf(GPUTextureUsage.RenderAttachment, GPUTextureUsage.TextureBinding),
            sampleCount = numSamples.toUInt()
        )
        colorTexture = device.createTexture(colorDescriptor).also {
            colorTextureView = it.createView()
        }

        val depthDescriptor = TextureDescriptor(
            size = Extent3D(width, height),
            format = depthFormat!!,
            usage = setOf(GPUTextureUsage.RenderAttachment, GPUTextureUsage.TextureBinding),
            sampleCount = numSamples.toUInt()
        )
        depthAttachment = device.createTexture(depthDescriptor).also {
            depthAttachmentView = it.createView()
        }
    }
}