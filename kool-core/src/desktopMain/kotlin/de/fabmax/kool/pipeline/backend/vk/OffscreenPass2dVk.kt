package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.PassData
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logT
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR

class OffscreenPass2dVk(
    val parentPass: OffscreenPass2d,
    backend: RenderBackendVk
) : RenderPassVk(
    hasDepth = parentPass.hasDepth,
    numSamples = parentPass.numSamples,
    backend = backend
), OffscreenPass2dImpl {

    override val colorTargetFormats: List<Int> = parentPass.colorAttachments.map { it.texture.format.vk }
    private var attachments = createAttachments(false)

    private fun createAttachments(isCopySource: Boolean): Attachments {
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val attachments = Attachments(
            colorFormats = colorTargetFormats,
            depthFormat = if (hasDepth) backend.physicalDevice.depthFormat else null,
            layers = 1,
            isCopySrc = isCopySource || isGenMipMaps,
            isCopyDst = isGenMipMaps,
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
        val wasCopySource = attachments.isCopySrc
        attachments.release()
        attachments = createAttachments(wasCopySource)
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

    override fun toString(): String {
        return "OffscreenPass2dVk:${parentPass.name}"
    }

    fun draw(passData: PassData, passEncoderState: PassEncoderState) {
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val isCopySrc = passData.isCopySource || isGenMipMaps
        val isCopyDst = isGenMipMaps
        if (isCopySrc != attachments.isCopySrc || isCopyDst != attachments.isCopyDst) {
            logD { "Offscreen pass ${parentPass.name} copy requirements changed: copy src: $isCopySrc, copy dst: $isCopyDst" }
            attachments.release()
            attachments = createAttachments(isCopySrc)
        }
        render(passData, passEncoderState)
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val mipLevel = passEncoderState.mipLevel
        val width = (parentPass.width shr mipLevel).coerceAtLeast(1)
        val height = (parentPass.height shr mipLevel).coerceAtLeast(1)

        val isLoadDepth = forceLoad || parentPass.depthAttachment?.clearDepth == ClearDepthLoad
        var isLoadColor = forceLoad
        for (i in parentPass.colorAttachments.indices) {
            if (parentPass.colorAttachments[i].clearColor == ClearColorLoad) {
                isLoadColor = true
            }
        }

        attachments.transitionToAttachmentLayout(isLoadColor, isLoadDepth, passEncoderState)
        val renderingInfo = setupRenderingInfo(
            width = width,
            height = height,
            renderPass = parentPass,
            forceLoad = forceLoad,
            colorImageViews = attachments.getColorViews(mipLevel),
            resolveColorViews = attachments.getResolveColorViews(mipLevel),
            depthImageView = attachments.getDepthView(mipLevel),
            resolveDepthView = attachments.getResolveDepthView(mipLevel)
        )
        vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
    }

    override fun endRenderPass(passEncoderState: PassEncoderState) {
        super.endRenderPass(passEncoderState)
        attachments.transitionToShaderRead(passEncoderState)
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) {
        for (i in attachments.colorImages.indices) {
            attachments.colorImages[i].generateMipmaps(passEncoderState.stack, passEncoderState.commandBuffer)
        }
    }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                attachments.copyColorToTexture(0, frameCopy.colorCopy[i], passEncoderState)
            }
        }
        if (frameCopy.isCopyDepth) {
            attachments.copyDepthToTexture(frameCopy.depthCopy2d, passEncoderState)
        }
    }
}