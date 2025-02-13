package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.pipeline.backend.stats.TextureInfo
import de.fabmax.kool.pipeline.backend.vk.ImageVk.Companion.dstAccessMaskForLayout
import de.fabmax.kool.pipeline.backend.vk.ImageVk.Companion.dstStageMaskForLayout
import de.fabmax.kool.pipeline.backend.vk.ImageVk.Companion.srcAccessMaskForLayout
import de.fabmax.kool.pipeline.backend.vk.ImageVk.Companion.srcStageMaskForLayout
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRCopyCommands2.vkCmdBlitImage2KHR
import org.lwjgl.vulkan.KHRSynchronization2.vkCmdPipelineBarrier2KHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VK13.*
import org.lwjgl.vulkan.VkCommandBuffer
import kotlin.math.max

class ImageVk(
    val backend: RenderBackendVk,
    val imageInfo: ImageInfo
) : BaseReleasable(), GpuTexture {

    override val width: Int get() = imageInfo.width
    override val height: Int get() = imageInfo.height
    override val depth: Int get() = imageInfo.depth
    val arrayLayers: Int get() = imageInfo.arrayLayers
    val mipLevels: Int get() = imageInfo.mipLevels
    val samples: Int get() = imageInfo.samples
    val format: Int get() = imageInfo.format

    val vkImage: VkImage

    var lastKnownLayout = VK_IMAGE_LAYOUT_UNDEFINED

    private val textureInfo = TextureInfo(
        texture = null,
        size = (width * height * depth * arrayLayers * imageInfo.bytesPerPx * imageInfo.mipMapFactor).toLong(),
        name = imageInfo.label
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

    private val ImageInfo.bytesPerPx: Int get() = when(format) {
        VK_FORMAT_R8_UNORM -> 1
        VK_FORMAT_R8G8_UNORM -> 2
        VK_FORMAT_R8G8B8A8_UNORM -> 4

        VK_FORMAT_R16_SFLOAT -> 2
        VK_FORMAT_R16G16_SFLOAT -> 4
        VK_FORMAT_R16G16B16A16_SFLOAT -> 8

        VK_FORMAT_R32_SFLOAT -> 4
        VK_FORMAT_R32G32_SFLOAT -> 8
        VK_FORMAT_R32G32B32A32_SFLOAT -> 16

        VK_FORMAT_R32_SINT -> 4
        VK_FORMAT_R32G32_SINT -> 8
        VK_FORMAT_R32G32B32A32_SINT -> 16

        VK_FORMAT_R32_UINT -> 4
        VK_FORMAT_R32G32_UINT -> 8
        VK_FORMAT_R32G32B32A32_UINT -> 16

        else -> 1
    }

    private val ImageInfo.mipMapFactor: Double get() = if (mipLevels > 1) { 1.333 } else 1.0

    override fun toString(): String {
        return "ImageVK[${vkImage.handle.toHexString()}]:\"${imageInfo.label}\""
    }

    fun copyFromBuffer(
        buffer: VkBuffer,
        commandBuffer: VkCommandBuffer,
        dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
        stack: MemoryStack? = null
    ) {
        memStack(stack) {
            transitionLayout(
                VK_IMAGE_LAYOUT_UNDEFINED,
                VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                commandBuffer,
                stack = this
            )
            val region = callocVkBufferImageCopyN(1) {
                bufferOffset(0)
                bufferRowLength(0)
                bufferImageHeight(0)
                imageSubresource().set(imageInfo.aspectMask, 0, 0, arrayLayers)
                imageOffset().set(0, 0, 0)
                imageExtent().set(width, height, depth)
            }
            vkCmdCopyBufferToImage(commandBuffer, buffer.handle, vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region)
            transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, dstLayout, commandBuffer, stack = this)
        }
    }

    fun copyToBuffer(
        buffer: VkBuffer,
        commandBuffer: VkCommandBuffer,
        stack: MemoryStack? = null
    ) {
        memStack(stack) {
            val prevLayout = lastKnownLayout
            transitionLayout(
                VK_IMAGE_LAYOUT_UNDEFINED,
                VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                commandBuffer,
                stack = this
            )
            val region = callocVkBufferImageCopyN(1) {
                bufferOffset(0)
                bufferRowLength(0)
                bufferImageHeight(0)
                imageSubresource().set(imageInfo.aspectMask, 0, 0, arrayLayers)
                imageOffset().set(0, 0, 0)
                imageExtent().set(width, height, depth)
            }
            vkCmdCopyImageToBuffer(commandBuffer, vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, buffer.handle, region)
            transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, prevLayout, commandBuffer, stack = this)
        }
    }

    fun copyFromImage(
        src: ImageVk,
        commandBuffer: VkCommandBuffer,
        dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
        stack: MemoryStack? = null
    ) {
        memStack(stack) {
            val srcLayout = src.lastKnownLayout
            src.transitionLayout(srcLayout, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, commandBuffer, stack = this)
            transitionLayout(
                VK_IMAGE_LAYOUT_UNDEFINED,
                VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                commandBuffer,
                stack = this
            )
            val region = callocVkImageCopyN(1) { }
            for (level in 0 until src.mipLevels) {
                region[0].apply {
                    srcSubresource().set(src.imageInfo.aspectMask, level, 0, arrayLayers)
                    srcOffset().set(0, 0, 0)
                    dstSubresource().set(imageInfo.aspectMask, level, 0, arrayLayers)
                    dstOffset().set(0, 0, 0)
                    extent().set(
                        (width shr level).coerceAtLeast(1),
                        (height shr level).coerceAtLeast(1),
                        (depth shr level).coerceAtLeast(1)
                    )
                }
                vkCmdCopyImage(commandBuffer, src.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region)
            }
            src.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, src.lastKnownLayout, commandBuffer, stack = this)
            transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, dstLayout, commandBuffer, stack = this)
        }
    }

    fun transitionLayout(
        oldLayout: Int,
        newLayout: Int,
        commandBuffer: VkCommandBuffer,
        baseMipLevel: Int = 0,
        mipLevels: Int = this.mipLevels,
        baseArrayLayer: Int = 0,
        arrayLayers: Int = this.arrayLayers,
        stack: MemoryStack? = null
    ) {
        if (newLayout == oldLayout) return
        vkImage.transitionLayout(oldLayout, newLayout, imageInfo.aspectMask, baseMipLevel, mipLevels, baseArrayLayer, arrayLayers, commandBuffer, stack)
        lastKnownLayout = newLayout
    }

    override fun release() {
        val wasReleased = isReleased
        super.release()
        if (!wasReleased) {
            backend.memManager.freeImage(vkImage)
            textureInfo.deleted()
        }
    }

    fun generateMipmaps(stack: MemoryStack, commandBuffer: VkCommandBuffer, dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
        if (mipLevels <= 1) {
            // not much to generate...
            transitionLayout(lastKnownLayout, dstLayout, commandBuffer)
            return
        }
        if (!backend.physicalDevice.isImageFormatSupportingBlitting(format)) {
            logE { "Unable to generate mip maps: Texture image format does not support linear blitting!" }
            transitionLayout(lastKnownLayout, dstLayout, commandBuffer)
            return
        }

        if (imageInfo.usage and VK_IMAGE_USAGE_TRANSFER_SRC_BIT == 0) {
            logE("ImageVk#generateMipMaps") { "Image ${imageInfo.label} misses transfer src usage flag" }
        }
        if (imageInfo.usage and VK_IMAGE_USAGE_TRANSFER_DST_BIT == 0) {
            logE("ImageVk#generateMipMaps") { "Image ${imageInfo.label} misses transfer dst usage flag" }
        }

        val barrier = stack.callocVkImageMemoryBarrier2N(1) {
            srcStageMask(srcStageMaskForLayout(VK_IMAGE_LAYOUT_UNDEFINED))
            srcAccessMask(srcAccessMaskForLayout(VK_IMAGE_LAYOUT_UNDEFINED))
            dstStageMask(dstStageMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL))
            dstAccessMask(dstAccessMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL))
            oldLayout(VK_IMAGE_LAYOUT_UNDEFINED)
            newLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            subresourceRange().set(imageInfo.aspectMask, 1, mipLevels - 1, 0, arrayLayers)
            srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            image(vkImage.handle)
        }
        val barrierDep = stack.callocVkDependencyInfo {
            pImageMemoryBarriers(barrier)
        }
        vkCmdPipelineBarrier2KHR(commandBuffer, barrierDep)

        var mipWidth = width
        var mipHeight = height
        for (i in 1 until mipLevels) {
            val srcLayout = if (i == 1) lastKnownLayout else VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL

            // transition previous mip level to TRANSFER_SRC layout
            barrier
                .subresourceRange {
                    it.baseMipLevel(i - 1)
                    it.levelCount(1)
                }
                .oldLayout(srcLayout)
                .newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                .srcStageMask(srcStageMaskForLayout(srcLayout))
                .srcAccessMask(srcAccessMaskForLayout(srcLayout))
                .dstStageMask(dstStageMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL))
                .dstAccessMask(dstAccessMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL))
            vkCmdPipelineBarrier2KHR(commandBuffer, barrierDep)



            val blit = stack.callocVkBlitImageInfo2 {
                val region = stack.callocVkImageBlit2N(1) {
                    srcOffsets(1).set(mipWidth, mipHeight, 1)
                    dstOffsets(1).set((mipWidth shr 1).coerceAtLeast(1), (mipHeight shr 1).coerceAtLeast(1), 1)
                    srcSubresource {
                        it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        it.mipLevel(i - 1)
                        it.layerCount(arrayLayers)
                    }
                    dstSubresource {
                        it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        it.mipLevel(i)
                        it.layerCount(arrayLayers)
                    }
                }
                srcImage(vkImage.handle)
                srcImageLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                dstImage(vkImage.handle)
                dstImageLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                filter(VK_FILTER_LINEAR)
                pRegions(region)
            }
            vkCmdBlitImage2KHR(commandBuffer, blit)

            // transition previous mip level to final layout
            barrier
                .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                .newLayout(dstLayout)
                .srcStageMask(srcStageMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL))
                .srcAccessMask(srcAccessMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL))
                .dstStageMask(dstStageMaskForLayout(dstLayout))
                .dstAccessMask(dstAccessMaskForLayout(dstLayout))

            vkCmdPipelineBarrier2KHR(commandBuffer, barrierDep)

            if (mipWidth > 1) { mipWidth /= 2 }
            if (mipHeight > 1) { mipHeight /= 2 }
        }

        // transition last mip level to final layout
        barrier
            .subresourceRange { it.baseMipLevel(mipLevels - 1) }
            .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            .newLayout(dstLayout)
            .srcStageMask(srcStageMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL))
            .srcAccessMask(srcAccessMaskForLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL))
            .dstStageMask(dstStageMaskForLayout(dstLayout))
            .dstAccessMask(dstAccessMaskForLayout(dstLayout))

        vkCmdPipelineBarrier2KHR(commandBuffer, barrierDep)
        lastKnownLayout = dstLayout
    }

    fun imageView2d(device: Device): VkImageView {
        require(depth == 1)
        return device.createImageView(
            image = vkImage,
            viewType = VK_IMAGE_VIEW_TYPE_2D,
            format = format,
            aspectMask = imageInfo.aspectMask,
            levelCount = mipLevels,
            layerCount = 1
        )
    }

    companion object {
        fun srcAccessMaskForLayout(layout: Int): Long = when (layout) {
            VK_IMAGE_LAYOUT_UNDEFINED -> 0
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_ACCESS_2_TRANSFER_WRITE_BIT
            VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_ACCESS_2_TRANSFER_READ_BIT
            VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_ACCESS_2_SHADER_READ_BIT
            else -> VK_ACCESS_2_MEMORY_WRITE_BIT
        }

        fun dstAccessMaskForLayout(layout: Int): Long = when (layout) {
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_ACCESS_2_TRANSFER_WRITE_BIT
            VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_ACCESS_2_SHADER_READ_BIT
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_READ_BIT or VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
            VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_ACCESS_2_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_ACCESS_2_TRANSFER_READ_BIT
            else -> VK_ACCESS_2_MEMORY_WRITE_BIT or VK_ACCESS_2_MEMORY_READ_BIT
        }

        fun srcStageMaskForLayout(layout: Int): Long = when (layout) {
            VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT
            VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_PIPELINE_STAGE_2_FRAGMENT_SHADER_BIT
            VK_IMAGE_LAYOUT_UNDEFINED -> VK_PIPELINE_STAGE_2_TOP_OF_PIPE_BIT
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT.toLong()
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT.toLong()
            else -> VK_PIPELINE_STAGE_2_ALL_COMMANDS_BIT
        }

        fun dstStageMaskForLayout(layout: Int): Long = when (layout) {
            VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT
            VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_PIPELINE_STAGE_2_FRAGMENT_SHADER_BIT
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT
            else -> VK_PIPELINE_STAGE_2_ALL_COMMANDS_BIT
        }
    }
}

fun VkImage.transitionLayout(
    oldLayout: Int,
    newLayout: Int,
    aspectMask: Int,
    baseMipLevel: Int,
    mipLevels: Int,
    baseArrayLayer: Int,
    arrayLayers: Int,
    commandBuffer: VkCommandBuffer,
    stack: MemoryStack? = null
) = memStack(stack) {
    val dependecy = callocVkDependencyInfo {
        val barrier = callocVkImageMemoryBarrier2N(1) {
            srcStageMask(srcStageMaskForLayout(oldLayout))
            srcAccessMask(srcAccessMaskForLayout(oldLayout))
            dstStageMask(dstStageMaskForLayout(newLayout))
            dstAccessMask(dstAccessMaskForLayout(newLayout))

            oldLayout(oldLayout)
            newLayout(newLayout)

            subresourceRange().set(aspectMask, baseMipLevel, mipLevels, baseArrayLayer, arrayLayers)

            srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            image(handle)
        }
        pImageMemoryBarriers(barrier)
    }
    vkCmdPipelineBarrier2KHR(commandBuffer, dependecy)
}