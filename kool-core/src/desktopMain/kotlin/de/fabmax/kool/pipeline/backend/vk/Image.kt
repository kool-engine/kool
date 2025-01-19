package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.stats.TextureInfo
import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkFormatProperties
import kotlin.math.max

class Image(
    val backend: RenderBackendVk,
    val imageInfo: ImageInfo,
    label: String = UniqueId.nextId("Image")
) : BaseReleasable() {
    val width: Int get() = imageInfo.width
    val height: Int get() = imageInfo.height
    val depth: Int get() = imageInfo.depth
    val mipLevels: Int get() = imageInfo.mipLevels
    val samples: Int get() = imageInfo.samples
    val format: Int get() = imageInfo.format
    val arrayLayers: Int get() = imageInfo.arrayLayers

    val vkImage: VkImage

    var layout = imageInfo.initialLayout
        private set

    private val textureInfo = TextureInfo(
        texture = null,
        size = (width * height * depth * arrayLayers /* texture.bytePerPx * texture.mipMapFactor*/).toLong(),
        label
    )

    init {
        memStack {
            val w = max(1, width)
            val h = max(1, height)
            val d = max(1, depth)

            if (w > width) logW { "Invalid image width requested: $width" }
            if (h > height) logW { "Invalid image height requested: $height" }
            if (d > depth) logW { "Invalid image depth requested: $depth" }

            vkImage = backend.memManager.createImage(imageInfo)
        }
    }

    fun copyFromBuffer(
        buffer: VkBuffer,
        commandBuffer: VkCommandBuffer,
        dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
        stack: MemoryStack? = null
    ) {
        memStack(stack) {
            transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, commandBuffer, stack = this)
            val region = callocVkBufferImageCopyN(1) {
                bufferOffset(0)
                bufferRowLength(0)
                bufferImageHeight(0)
                imageSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1)
                imageOffset().set(0, 0, 0)
                imageExtent().set(width, height, depth)
            }
            vkCmdCopyBufferToImage(commandBuffer, buffer.handle, vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region)
            transitionLayout(dstLayout, commandBuffer, stack = this)
        }
    }

    fun copyFromImage(
        src: Image,
        commandBuffer: VkCommandBuffer,
        dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
        stack: MemoryStack? = null
    ) {
        memStack(stack) {
            src.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, commandBuffer, stack = this)
            transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, commandBuffer, stack = this)
            val region = callocVkImageCopyN(1) {
                srcSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1)
                srcOffset().set(0, 0, 0)
                dstSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1)
                dstOffset().set(0, 0, 0)
                extent().set(width, height, depth)
            }
            vkCmdCopyImage(commandBuffer, src.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region)
            transitionLayout(dstLayout, commandBuffer, stack = this)
        }
    }

    fun transitionLayout(newLayout: Int, commandBuffer: VkCommandBuffer, stack: MemoryStack? = null) {
        if (newLayout == layout) {
            return
        }

        memStack(stack) {
            val oldLayout = layout
            val aspectMask = when (newLayout) {
                VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL if (hasStencilComponent()) -> VK_IMAGE_ASPECT_DEPTH_BIT or VK_IMAGE_ASPECT_STENCIL_BIT
                VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> VK_IMAGE_ASPECT_DEPTH_BIT
                else -> VK_IMAGE_ASPECT_COLOR_BIT
            }

            val barrier = callocVkImageMemoryBarrierN(1) {
                oldLayout(oldLayout)
                newLayout(newLayout)
                srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                srcAccessMask(getSrcAccessMask(oldLayout))
                dstAccessMask(getDstAccessMask(newLayout))
                image(vkImage.handle)
                subresourceRange().set(aspectMask, 0, mipLevels, 0, arrayLayers)
            }

            val srcStage: Int
            val dstStage: Int
            when {
                oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> {
                    srcStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                    dstStage = VK_PIPELINE_STAGE_TRANSFER_BIT
                }
                oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> {
                    srcStage = VK_PIPELINE_STAGE_TRANSFER_BIT
                    dstStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT
                }
                else -> {
                    srcStage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT
                    dstStage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT
                }
            }
            vkCmdPipelineBarrier(commandBuffer, srcStage, dstStage, 0, null, null, barrier)
            layout = newLayout
        }
    }

    private fun getSrcAccessMask(srcLayout: Int): Int = when (srcLayout) {
        VK_IMAGE_LAYOUT_UNDEFINED -> 0
        VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_ACCESS_TRANSFER_WRITE_BIT
        VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
        VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_ACCESS_TRANSFER_READ_BIT
        VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_ACCESS_SHADER_READ_BIT
        else -> error("Layout not supported / implemented: $srcLayout")
    }

    private fun getDstAccessMask(dstLayout: Int): Int = when (dstLayout) {
        VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_ACCESS_TRANSFER_WRITE_BIT
        VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_ACCESS_SHADER_READ_BIT
        VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT or VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
        VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_ACCESS_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
        VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_ACCESS_TRANSFER_READ_BIT
        else -> error("Destination layout not supported: $dstLayout")
    }

    private fun hasStencilComponent(): Boolean {
        return format == VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK_FORMAT_D24_UNORM_S8_UINT
    }

    override fun release() {
        super.release()
        backend.memManager.freeImage(vkImage)
        textureInfo.deleted()
    }

    fun generateMipmaps(dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
        backend.commandPool.singleShotCommands { commandBuffer ->
            generateMipmaps(this, commandBuffer, dstLayout)
        }
    }

    fun generateMipmaps(stack: MemoryStack, commandBuffer: VkCommandBuffer, dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
        if (mipLevels <= 1) {
            // not much to generate...
            transitionLayout(dstLayout, commandBuffer)
            return
        }

        val formatProperties = VkFormatProperties.malloc(stack)
        vkGetPhysicalDeviceFormatProperties(backend.physicalDevice.vkPhysicalDevice, format, formatProperties)
        if (formatProperties.optimalTilingFeatures() and VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT == 0) {
            logE { "Unable to generate mip maps: Texture image format does not support linear blitting!" }
            transitionLayout(dstLayout, commandBuffer)
            return
        }

        val barrier = stack.callocVkImageMemoryBarrierN(1) {
            sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
            image(vkImage.handle)
            srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            subresourceRange {
                it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                it.baseArrayLayer(0)
                it.layerCount(arrayLayers)
                it.levelCount(1)
            }
        }

        val srcAccessMaskSrcOpt = getSrcAccessMask(layout)
        val dstAccessMaskSrcOpt = getDstAccessMask(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
        val srcAccessMaskDst = getSrcAccessMask(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
        val dstAccessMaskDst = getDstAccessMask(dstLayout)

        var mipWidth = width
        var mipHeight = height
        for (i in 1 until mipLevels) {
            barrier
                .subresourceRange { it.baseMipLevel(i - 1) }
                .oldLayout(layout)
                .newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                .srcAccessMask(srcAccessMaskSrcOpt)
                .dstAccessMask(dstAccessMaskSrcOpt)

            vkCmdPipelineBarrier(commandBuffer, VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT, 0, null, null, barrier)

            val blit = stack.callocVkImageBlitN(1) {
                srcOffsets(0).set(0, 0, 0)
                srcOffsets(1).set(mipWidth, mipHeight, 1)
                srcSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(i - 1)
                    it.baseArrayLayer(0)
                    it.layerCount(arrayLayers)
                }
                dstOffsets(0).set(0, 0, 0)
                dstOffsets(1).set((mipWidth shr 1).coerceAtLeast(1), (mipHeight shr 1).coerceAtLeast(1), 1)
                dstSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(i)
                    it.baseArrayLayer(0)
                    it.layerCount(arrayLayers)
                }
            }
            vkCmdBlitImage(commandBuffer,
                vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                blit, VK_FILTER_LINEAR
            )

            barrier
                .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                .newLayout(dstLayout)
                .srcAccessMask(srcAccessMaskDst)
                .dstAccessMask(dstAccessMaskDst)

            vkCmdPipelineBarrier(commandBuffer, VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0, null, null, barrier)

            if (mipWidth > 1) { mipWidth /= 2 }
            if (mipHeight > 1) { mipHeight /= 2 }
        }

        barrier
            .subresourceRange { it.baseMipLevel(mipLevels - 1) }
            .oldLayout(layout)
            .newLayout(dstLayout)
            .srcAccessMask(getSrcAccessMask(layout))
            .dstAccessMask(getDstAccessMask(dstLayout))

        vkCmdPipelineBarrier(commandBuffer, VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0, null, null, barrier)
        layout = dstLayout
    }

    companion object {
        fun imageView2d(device: Device, image: Image, aspectMask: Int): VkImageView {
            require(image.depth == 1)
            return device.createImageView(
                image = image.vkImage,
                viewType = VK_IMAGE_VIEW_TYPE_2D,
                format = image.format,
                aspectMask = aspectMask,
                levelCount = image.mipLevels,
                layerCount = 1
            )
        }
    }
}