package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logT
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR
import org.lwjgl.vulkan.VK10.*

class OffscreenPassCubeVk(
    val parentPass: OffscreenPassCube,
    numSamples: Int,
    backend: RenderBackendVk
) : RenderPassVk(false, numSamples, backend), OffscreenPassCubeImpl {

    override val colorTargetFormats: List<Int> = parentPass.colorTextures.map { it.props.format.vk }

    private val colorAttachments = List(parentPass.colorTextures.size) {
        RenderAttachment(parentPass.colorTextures[it], false, "${parentPass.name}.color[$it]")
    }
    private val depthAttachment: RenderAttachment?

    private var copySrcFlag = 0
    private var copyDstFlag = 0

    init {
        val depthTex = when (parentPass.depthAttachment) {
            OffscreenPass.DepthAttachmentRender -> TextureCube(
                TextureProps(generateMipMaps = false, defaultSamplerSettings = SamplerSettings().clamped()),
                "${parentPass.name}:render-depth"
            )
            else -> parentPass.depthTexture
        }
        depthAttachment = depthTex?.let { RenderAttachment(it, true,  it.name) }
    }

    override fun applySize(width: Int, height: Int) {
        logT { "Resize offscreen cube pass ${parentPass.name} to $width x $height" }
        colorAttachments.forEach { it.recreate(width, height) }
        depthAttachment?.recreate(width, height)
    }

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            colorAttachments.forEach { it.release() }
            depthAttachment?.release()
        }
    }

    override fun toString(): String {
        return "OffscreenPassCubeVk:${parentPass.name}"
    }

    fun draw(passEncoderState: PassEncoderState) {
        val isCopySrc = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        val isCopyDst = parentPass.mipMode == RenderPass.MipMode.Generate
        if ((isCopySrc && copySrcFlag == 0) || (isCopyDst && copyDstFlag == 0)) {
            copySrcFlag = VK_IMAGE_USAGE_TRANSFER_SRC_BIT
            if (isCopyDst) {
                copyDstFlag = VK_IMAGE_USAGE_TRANSFER_DST_BIT
            }
            colorAttachments.forEach { it.recreate(parentPass.width, parentPass.height) }
            depthAttachment?.recreate(parentPass.width, parentPass.height)
        }
        render(parentPass, passEncoderState)
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val isLoadColor = forceLoad || parentPass.clearColor == null
        val isLoadDepth = forceLoad || !parentPass.clearDepth

        val mipLevel = passEncoderState.mipLevel
        val layer = passEncoderState.layer
        val width = (parentPass.width shr mipLevel).coerceAtLeast(1)
        val height = (parentPass.height shr mipLevel).coerceAtLeast(1)
        val colorLoadOp = if (isLoadColor) VK_ATTACHMENT_LOAD_OP_LOAD else VK_ATTACHMENT_LOAD_OP_CLEAR
        val depthLoadOp = if (isLoadDepth) VK_ATTACHMENT_LOAD_OP_LOAD else VK_ATTACHMENT_LOAD_OP_CLEAR

        for (i in colorTargetFormats.indices) {
            val srcLayout = if (isLoadColor) colorAttachments[i].gpuTexture.lastKnownLayout else VK_IMAGE_LAYOUT_UNDEFINED
            colorAttachments[i].gpuTexture.transitionLayout(
                oldLayout = srcLayout,
                newLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )
        }
        depthAttachment?.let { depth ->
            val srcLayout = if (isLoadColor) depth.gpuTexture.lastKnownLayout else VK_IMAGE_LAYOUT_UNDEFINED
            depth.gpuTexture.transitionLayout(
                oldLayout = srcLayout,
                newLayout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )
        }

        val renderingInfo = setupRenderingInfo(
            width = width,
            height = height,
            colorImageViews = colorAttachments.map { it.mipViews[mipLevel][layer] },
            colorLoadOp = colorLoadOp,
            colorStoreOp = VK_ATTACHMENT_STORE_OP_STORE,
            clearColors = parentPass.clearColors.mapNotNull { it },
            depthImageView = depthAttachment?.mipViews[mipLevel][layer],
            depthLoadOp = depthLoadOp,
            isReverseDepth = parentPass.isReverseDepth,
        )
        vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
    }

    override fun endRenderPass(passEncoderState: PassEncoderState) {
        super.endRenderPass(passEncoderState)
        for (i in colorAttachments.indices) {
            colorAttachments[i].gpuTexture.transitionLayout(
                oldLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                newLayout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )
        }
        depthAttachment?.gpuTexture?.transitionLayout(
            oldLayout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
            newLayout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) {
        TODO("Not yet implemented")
    }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                colorAttachments[i].copyToTexture(frameCopy.colorCopy[i] as TextureCube, passEncoderState)
            }
        }
        if (frameCopy.isCopyDepth) {
            depthAttachment?.copyToTexture(frameCopy.depthCopyCube, passEncoderState)
        }
    }

    private inner class RenderAttachment(val texture: TextureCube, val isDepth: Boolean, val name: String) : BaseReleasable() {
        var descriptor: ImageInfo
        var gpuTexture: ImageVk
        val mipViews = mutableListOf<List<VkImageView>>()

        init {
            val attachmentUsage = if (isDepth) VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT else VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
            val (desc, tex) = createTexture(
                width = parentPass.width,
                height = parentPass.height,
                usage = VK_IMAGE_USAGE_SAMPLED_BIT or attachmentUsage or copySrcFlag or copyDstFlag
            )
            descriptor = desc
            gpuTexture = tex

            texture.gpuTexture = gpuTexture
            texture.loadingState = Texture.LoadingState.LOADED
            createViews()
        }

        fun recreate(width: Int, height: Int) {
            val attachmentUsage = if (isDepth) VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT else VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
            val (desc, tex) = createTexture(
                width = width,
                height = height,
                usage = VK_IMAGE_USAGE_SAMPLED_BIT or attachmentUsage or copySrcFlag or copyDstFlag
            )
            descriptor = desc
            gpuTexture.release()
            gpuTexture = tex
            texture.gpuTexture = gpuTexture
            createViews()
        }

        private fun createTexture(
            width: Int,
            height: Int,
            usage: Int,
            texture: Texture<*> = this.texture
        ): Pair<ImageInfo, ImageVk> {
            val aspectMask = if (isDepth) VK_IMAGE_ASPECT_DEPTH_BIT else VK_IMAGE_ASPECT_COLOR_BIT
            val descriptor = ImageInfo(
                imageType = VK_IMAGE_TYPE_2D,
                format = if (isDepth) backend.physicalDevice.depthFormat else texture.props.format.vk,
                width = width,
                height = height,
                depth = 1,
                arrayLayers = 6,
                mipLevels = parentPass.numTextureMipLevels,
                samples = numSamples,
                usage = usage,
                flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT,
                aspectMask = aspectMask,
                label = texture.name
            )
            val tex = ImageVk(backend, descriptor, name)
            return descriptor to tex
        }

        private fun createViews() {
            mipViews.flatMap { it }.forEach { backend.device.destroyImageView(it) }
            mipViews.clear()
            for (i in 0 until parentPass.numRenderMipLevels) {
                mipViews += List<VkImageView>(6) { face ->
                    backend.device.createImageView(
                        image = gpuTexture.vkImage,
                        viewType = VK_IMAGE_VIEW_TYPE_2D,
                        format = gpuTexture.format,
                        aspectMask = if (isDepth) VK_IMAGE_ASPECT_DEPTH_BIT else VK_IMAGE_ASPECT_COLOR_BIT,
                        levelCount = 1,
                        baseArrayLayer = face,
                        baseMipLevel = i
                    )
                }
            }
        }

        fun copyToTexture(target: TextureCube, passEncoderState: PassEncoderState) {
            var copyDst = (target.gpuTexture as ImageVk?)
            if (copyDst == null || copyDst.width != parentPass.width || copyDst.height != parentPass.height) {
                copyDst?.release()
                val (_, gpuTex) = createTexture(
                    width = parentPass.width,
                    height = parentPass.height,
                    usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT,
                    texture = target
                )
                copyDst = gpuTex
                target.gpuTexture = copyDst
                target.loadingState = Texture.LoadingState.LOADED
            }
            copyDst.copyFromImage(gpuTexture, passEncoderState.commandBuffer)
        }

        override fun release() {
            super.release()
            gpuTexture.release()
            texture.gpuTexture = null
            mipViews.flatMap { it }.forEach { backend.device.destroyImageView(it) }
        }
    }
}