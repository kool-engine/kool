package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.PassData
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.releaseDelayed

class WgpuOffscreenPassCube(
    val parentPass: OffscreenPassCube,
    backend: RenderBackendWebGpu
) : WgpuRenderPass(GPUTextureFormat.depth32float, parentPass.numSamples, backend), OffscreenPassCubeImpl {

    override val colorTargetFormats = parentPass.colorAttachments.map { it.texture.format.wgpu }
    private var attachments: Attachments? = null
    private var applyResize: Vec2i? = Vec2i(parentPass.width, parentPass.height)

    override fun doRelease() {
        attachments?.release()
        parentPass.colorTextures.forEach {
            it.gpuTexture = null
        }
        parentPass.depthTexture?.let {
            it.gpuTexture = null
        }
    }

    fun draw(passData: PassData, passEncoderState: RenderPassEncoderState) {
        applyResize?.let {
            doResize(it)
            applyResize = null
        }
        val attachments = checkNotNull(this.attachments)
        val isCopySrc = passData.isCopySource
        if (isCopySrc != attachments.isCopySrc) {
            logD { "Offscreen pass ${parentPass.name} copy requirements changed: copy src: $isCopySrc" }
            val size = attachments.size
            attachments.release()
            this.attachments = createAttachments(isCopySrc, size)
        }
        render(passData, passEncoderState)
    }

    override fun applySize(width: Int, height: Int) {
        applyResize = Vec2i(width, height)
    }

    private fun doResize(newSize: Vec2i) {
        logT { "Resize offscreen cube pass ${parentPass.name} to ${newSize.x} x ${newSize.y}" }
        val wasCopySource = attachments?.isCopySrc == true
        attachments?.releaseDelayed(1)
        attachments = createAttachments(wasCopySource, newSize)
    }

    override fun generateMipLevels(encoder: GPUCommandEncoder) {
        val attachments = checkNotNull(this.attachments)
        for (i in attachments.colorImages.indices) {
            val image = attachments.colorImages[i]
            backend.textureLoader.mipmapGenerator.generateMipLevels(image.imageInfo, image.gpuTexture, encoder)
        }
    }

    override fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder) {
        val attachments = checkNotNull(this.attachments)
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                attachments.copyColorToTexture(i, frameCopy.colorCopy[i] as TextureCube, encoder)
            }
        }
        if (frameCopy.isCopyDepth) {
            attachments.copyDepthToTexture(frameCopy.depthCopyCube, encoder)
        }
    }

    override fun beginRenderPass(
        passEncoderState: RenderPassEncoderState,
        forceLoad: Boolean,
        timestampWrites: GPURenderPassTimestampWrites?
    ): GPURenderPassEncoder {
        val attachments = checkNotNull(this.attachments)
        val renderPass = passEncoderState.renderPass
        val mipLevel = passEncoderState.mipLevel
        val layer = passEncoderState.layer
        val views = attachments.getColorViews(mipLevel, layer)
        val colors = views.mapIndexed { i, colorView ->
            val colorLoadOp = when {
                forceLoad -> GPULoadOp.load
                renderPass.colorAttachments[i].clearColor is ClearColorLoad -> GPULoadOp.load
                else -> GPULoadOp.clear
            }
            val clearColor = if (colorLoadOp == GPULoadOp.load) null else {
                (parentPass.colorAttachments[i].clearColor as? ClearColorFill)?.let { GPUColorDict(it.clearColor) }
            }

            GPURenderPassColorAttachment(
                view = colorView,
                loadOp = colorLoadOp,
                clearValue = clearColor,
                resolveTarget = if (isMultiSampled) attachments.getResolveColorView(i, mipLevel, layer) else null,
            )
        }
        val depthLoadOp = when {
            forceLoad -> GPULoadOp.load
            renderPass.depthAttachment?.clearDepth == ClearDepthLoad -> GPULoadOp.load
            else -> GPULoadOp.clear
        }
        val depth = attachments.getDepthView(mipLevel)?.let { depthView ->
            GPURenderPassDepthStencilAttachment(
                view = depthView,
                depthLoadOp = depthLoadOp,
                depthStoreOp = GPUStoreOp.store,
                depthClearValue = renderPass.depthMode.far
            )
        }
        return passEncoderState.encoder.beginRenderPass(colors, depth, timestampWrites, renderPass.name)
    }

    override fun endRenderPass(passEncoderState: RenderPassEncoderState) {
        super.endRenderPass(passEncoderState)
        val attachments = checkNotNull(this.attachments)
        attachments.resolveDepthImage?.let { copyDst ->
            backend.textureLoader.resolveMultiSampledDepthTexture(
                src = attachments.depthImage!!.gpuTexture,
                dst = copyDst.gpuTexture,
                mipLevel = passEncoderState.mipLevel,
                layer = passEncoderState.layer,
                encoder = passEncoderState.encoder
            )
        }
    }

    private fun createAttachments(isCopySource: Boolean, size: Vec2i): Attachments {
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val attachments = Attachments(
            colorFormats = colorTargetFormats,
            depthFormat = if (parentPass.hasDepth) GPUTextureFormat.depth32float else null,
            layers = 6,
            isCopySrc = isCopySource || isGenMipMaps,
            parentPass = parentPass,
            size = size,
        )
        parentPass.colorTextures.forEachIndexed { i, attachment ->
            if (isMultiSampled) {
                attachment.gpuTexture = attachments.resolveColorImages[i]
            } else {
                attachment.gpuTexture = attachments.colorImages[i]
            }
        }
        parentPass.depthTexture?.let { attachment ->
            if (isMultiSampled) {
                attachment.gpuTexture = attachments.resolveDepthImage
            } else {
                attachment.gpuTexture = attachments.depthImage
            }
        }
        return attachments
    }
}