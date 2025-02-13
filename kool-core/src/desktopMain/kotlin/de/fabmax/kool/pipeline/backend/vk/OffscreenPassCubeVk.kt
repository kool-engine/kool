package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logT
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR
import org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_STORE

class OffscreenPassCubeVk(
    val parentPass: OffscreenPassCube,
    backend: RenderBackendVk
) : RenderPassVk(
    hasDepth = parentPass.hasDepth,
    numSamples = parentPass.numSamples,
    backend = backend
), OffscreenPassCubeImpl {

    override val colorTargetFormats: List<Int> = parentPass.colorAttachments.map { it.texture.format.vk }
    private var attachments = createAttachments()

    private fun createAttachments(): Attachments {
        val isCopy = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val isCopySrc = isCopy || isGenMipMaps
        val isCopyDst = isGenMipMaps

        val attachments = Attachments(
            colorFormats = colorTargetFormats,
            depthFormat = if (hasDepth) backend.physicalDevice.depthFormat else null,
            layers = 6,
            isCopySrc = isCopySrc,
            isCopyDst = isCopyDst,
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
        logT { "Resize offscreen cube pass ${parentPass.name} to $width x $height" }
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

    override fun toString(): String {
        return "OffscreenPassCubeVk:${parentPass.name}"
    }

    fun draw(passEncoderState: PassEncoderState) {
        val isCopy = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val isCopySrc = isCopy || isGenMipMaps
        val isCopyDst = isGenMipMaps
        if (isCopySrc != attachments.isCopySrc || isCopyDst != attachments.isCopyDst) {
            logD { "Offscreen pass ${parentPass.name} copy requirements changed: copy src: $isCopySrc, copy dst: $isCopyDst" }
            attachments.release()
            attachments = createAttachments()
        }
        render(parentPass, passEncoderState)
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val mipLevel = passEncoderState.mipLevel
        val layer = passEncoderState.layer
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
            colorImageViews = attachments.getColorViews(mipLevel, layer),
            colorStoreOp = VK_ATTACHMENT_STORE_OP_STORE,
            depthImageView = attachments.getDepthView(mipLevel, layer),
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