package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logT
import io.ygdrasil.webgpu.*

class WgpuOffscreenPass2d(
    val parentPass: OffscreenPass2d,
    backend: RenderBackendWgpu4k
) : WgpuRenderPass(GPUTextureFormat.Depth32Float, parentPass.numSamples, backend), OffscreenPass2dImpl {

    override val colorTargetFormats = parentPass.colorAttachments.map { it.texture.format.wgpu }
    private var attachments = createAttachments()

    private fun createAttachments(): Attachments {
        val isCopy = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val isCopySrc = isCopy || isGenMipMaps

        val attachments = Attachments(
            colorFormats = colorTargetFormats,
            depthFormat = if (parentPass.hasDepth) GPUTextureFormat.Depth32Float else null,
            layers = 1,
            isCopySrc = isCopySrc,
            parentPass = parentPass,
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

    override fun applySize(width: Int, height: Int) {
        logT { "Resize offscreen 2d pass ${parentPass.name} to $width x $height" }
        attachments.release()
        attachments = createAttachments()
    }

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            attachments.release()
            parentPass.colorTextures.forEach {
                it.gpuTexture = null
            }
            parentPass.depthTexture?.let {
                it.gpuTexture = null
            }
        }
    }

    suspend fun draw(passEncoderState: RenderPassEncoderState) {
        val isCopySrc = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        if (isCopySrc != attachments.isCopySrc) {
            logD { "Offscreen pass ${parentPass.name} copy requirements changed: copy src: $isCopySrc" }
            attachments.release()
            attachments = createAttachments()
        }
        render(parentPass, passEncoderState)
    }

    override fun generateMipLevels(encoder: GPUCommandEncoder) {
        for (i in attachments.colorImages.indices) {
            val image = attachments.colorImages[i]
            backend.textureLoader.mipmapGenerator.generateMipLevels(image.imageInfo, image.gpuTexture, encoder)
        }
    }

    override fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder) {
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                attachments.copyColorToTexture(i, frameCopy.colorCopy[i] as Texture2d, encoder)
            }
        }
        if (frameCopy.isCopyDepth) {
            attachments.copyDepthToTexture(frameCopy.depthCopy2d, encoder)
        }
    }

    override fun beginRenderPass(
        passEncoderState: RenderPassEncoderState,
        forceLoad: Boolean,
        timestampWrites: GPURenderPassTimestampWrites?
    ): GPURenderPassEncoder {
        val renderPass = passEncoderState.renderPass
        val mipLevel = passEncoderState.mipLevel
        val views = attachments.getColorViews(mipLevel)
        val colors = views.mapIndexed { i, colorView ->
            val colorLoadOp = when {
                forceLoad -> GPULoadOp.Load
                renderPass.colorAttachments[i].clearColor is ClearColorLoad -> GPULoadOp.Load
                else -> GPULoadOp.Clear
            }
            val clearColor = if (colorLoadOp == GPULoadOp.Load) null else {
                (parentPass.colorAttachments[i].clearColor as? ClearColorFill)?.let { Color(it.clearColor.r.toDouble(), it.clearColor.g.toDouble(), it.clearColor.b.toDouble(), it.clearColor.a.toDouble()) }
            }

            io.ygdrasil.webgpu.RenderPassColorAttachment(
                view = colorView,
                loadOp = colorLoadOp,
                storeOp = GPUStoreOp.Store,
                clearValue = clearColor,
                resolveTarget = if (isMultiSampled) attachments.getResolveColorView(i, mipLevel) else null,
            )
        }

        val depthLoadOp = when {
            forceLoad -> GPULoadOp.Load
            renderPass.depthAttachment?.clearDepth == ClearDepthLoad -> GPULoadOp.Load
            else -> GPULoadOp.Clear
        }
        val depth = attachments.getDepthView(mipLevel)?.let { depthView ->
            RenderPassDepthStencilAttachment(
                view = depthView,
                depthLoadOp = depthLoadOp,
                depthStoreOp = GPUStoreOp.Store,
                depthClearValue = renderPass.depthMode.far
            )
        }
        return passEncoderState.encoder.beginRenderPass(colors, depth, timestampWrites, renderPass.name)
    }

    override fun endRenderPass(passEncoderState: RenderPassEncoderState) {
        super.endRenderPass(passEncoderState)
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
}