package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.PassData
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.releaseDelayed
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
    private var attachments: Attachments? = null
    private var applyResize: Vec2i? = Vec2i(parentPass.width, parentPass.height)

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            attachments?.release()
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
        applyResize?.let {
            doResize(it)
            applyResize = null
        }
        val attachments = checkNotNull(this.attachments)
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val isCopySrc = passData.isCopySource || isGenMipMaps
        val isCopyDst = isGenMipMaps
        if (isCopySrc != attachments.isCopySrc || isCopyDst != attachments.isCopyDst) {
            logD { "Offscreen pass ${parentPass.name} copy requirements changed: copy src: $isCopySrc, copy dst: $isCopyDst" }
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
        logT { "Resize offscreen 2d pass ${parentPass.name} to ${newSize.x} x ${newSize.y}" }
        val wasCopySource = attachments?.isCopySrc == true
        attachments?.releaseDelayed(1)
        attachments = createAttachments(wasCopySource, newSize)
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val attachments = checkNotNull(this.attachments)
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
        val attachments = checkNotNull(this.attachments)
        attachments.transitionToShaderRead(passEncoderState)
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) {
        val attachments = checkNotNull(this.attachments)
        for (i in attachments.colorImages.indices) {
            attachments.colorImages[i].generateMipmaps(passEncoderState.stack, passEncoderState.commandBuffer)
        }
    }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        val attachments = checkNotNull(this.attachments)
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                attachments.copyColorToTexture(0, frameCopy.colorCopy[i], passEncoderState)
            }
        }
        if (frameCopy.isCopyDepth) {
            attachments.copyDepthToTexture(frameCopy.depthCopy2d, passEncoderState)
        }
    }

    private fun createAttachments(isCopySource: Boolean, size: Vec2i): Attachments {
        val isGenMipMaps = parentPass.mipMode == RenderPass.MipMode.Generate
        val attachments = Attachments(
            colorFormats = colorTargetFormats,
            depthFormat = if (hasDepth) backend.physicalDevice.depthFormat else null,
            layers = 1,
            isCopySrc = isCopySource || isGenMipMaps,
            isCopyDst = isGenMipMaps,
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