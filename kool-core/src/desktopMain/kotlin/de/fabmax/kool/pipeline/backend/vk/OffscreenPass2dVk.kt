package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logT
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR
import org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_STORE

class OffscreenPass2dVk(
    val parentPass: OffscreenPass2d,
    numSamples: Int,
    backend: RenderBackendVk
) : RenderPassVk(
    hasDepth = parentPass.depthAttachment != OffscreenPass.DepthAttachmentNone,
    numSamples = numSamples,
    backend = backend
), OffscreenPass2dImpl {

    override val colorTargetFormats: List<Int> = parentPass.colorTextures.map { it.props.format.vk }
    private var attachments = createAttachments(false, false)

    private fun createAttachments(isCopySrc: Boolean, isCopyDst: Boolean): Attachments {
        val attachments = Attachments(
            colorFormats = colorTargetFormats,
            depthFormat = if (hasDepth) backend.physicalDevice.depthFormat else null,
            layers = 1,
            isCopySrc = isCopySrc,
            isCopyDst = isCopyDst,
            parentPass = parentPass,
            backend = backend
        )
        parentPass.colorTextures.forEachIndexed { i, tex ->
            tex.gpuTexture = attachments.colorImages[i]
            tex.loadingState = Texture.LoadingState.LOADED
        }
        parentPass.depthTexture?.let { tex ->
            tex.gpuTexture = attachments.depthImage
            tex.loadingState = Texture.LoadingState.LOADED
        }
        return attachments
    }

    override fun applySize(width: Int, height: Int) {
        logT { "Resize offscreen 2d pass ${parentPass.name} to $width x $height" }
        attachments.release()
        attachments = createAttachments(attachments.isCopySrc, attachments.isCopyDst)
    }

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            attachments.release()
            parentPass.colorTextures.forEach {
                it.gpuTexture = null
                it.loadingState = Texture.LoadingState.NOT_LOADED
            }
            parentPass.depthTexture?.let {
                it.gpuTexture = null
                it.loadingState = Texture.LoadingState.NOT_LOADED
            }
        }
    }

    override fun toString(): String {
        return "OffscreenPass2dVk:${parentPass.name}"
    }

    fun draw(passEncoderState: PassEncoderState) {
        val isCopy = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val isCopySrc = isCopy || isGenMipMaps
        val isCopyDst = isGenMipMaps
        if (isCopySrc != attachments.isCopySrc || isCopyDst != attachments.isCopyDst) {
            attachments.release()
            attachments = createAttachments(isCopySrc, isCopyDst)
        }
        render(parentPass, passEncoderState)
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val mipLevel = passEncoderState.mipLevel
        val width = (parentPass.width shr mipLevel).coerceAtLeast(1)
        val height = (parentPass.height shr mipLevel).coerceAtLeast(1)

        val isLoadDepth = forceLoad || parentPass.clearDepth == ClearDepthLoad
        var isLoadColor = forceLoad
        for (i in parentPass.clearColors.indices) {
            if (parentPass.clearColors[i] == ClearColorLoad) {
                isLoadColor = true
            }
        }

        attachments.transitionToAttachmentLayout(isLoadColor, isLoadDepth, passEncoderState)
        val renderingInfo = setupRenderingInfo(
            width = width,
            height = height,
            forceLoad = forceLoad,
            colorImageViews = attachments.getColorViews(mipLevel),
            colorClearModes = parentPass.clearColors,
            colorStoreOp = VK_ATTACHMENT_STORE_OP_STORE,
            depthImageView = attachments.getDepthView(mipLevel),
            depthClearMode = parentPass.clearDepth,
            isReverseDepth = parentPass.isReverseDepth,
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
        //attachments.depthImage?.generateMipmaps(passEncoderState.stack, passEncoderState.commandBuffer)
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