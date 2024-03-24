package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkFormatProperties
import kotlin.math.max

class Image(val sys: VkSystem, config: Config) : VkResource() {
    val width = config.width
    val height = config.height
    val depth = config.depth
    val mipLevels = config.mipLevels
    val numSamples = config.numSamples
    val format = config.format
    val arrayLayers = config.arrayLayers

    val vkImage: Long
    val allocation: Long

    var layout = VK_IMAGE_LAYOUT_UNDEFINED

    init {
        memStack {
            val w = max(1, config.width)
            val h = max(1, config.height)
            val d = max(1, config.depth)

            if (w > config.width) logW { "Invalid image width requested: ${config.width}" }
            if (h > config.height) logW { "Invalid image height requested: ${config.height}" }
            if (d > config.depth) logW { "Invalid image depth requested: ${config.depth}" }

            val imageInfo = callocVkImageCreateInfo {
                sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                imageType(if (depth > 1) VK_IMAGE_TYPE_3D else VK_IMAGE_TYPE_2D)
                extent {
                    it.width(w)
                    it.height(h)
                    it.depth(d)
                }
                mipLevels(mipLevels)
                format(format)
                tiling(config.tiling)
                initialLayout(layout)
                usage(config.usage)
                sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                samples(numSamples)
                arrayLayers(config.arrayLayers)
                flags(config.flags)
            }

            val pBuffer = mallocLong(1)
            val pAllocation = mallocPointer(1)
            checkVk(sys.memManager.createImage(imageInfo, config.allocUsage, pBuffer, pAllocation)) { "Image creation failed with code: $it" }
            vkImage = pBuffer[0]
            allocation = pAllocation[0]
        }
    }

    fun generateMipmaps(sys: VkSystem, dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
        sys.commandPool.singleTimeCommands { commandBuffer ->
            generateMipmaps(this, commandBuffer, dstLayout)
        }
    }

    fun generateMipmaps(stack: MemoryStack, commandBuffer: VkCommandBuffer, dstLayout: Int = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
        if (mipLevels <= 1) {
            // not much to generate...
            transitionLayout(dstLayout)
            return
        }

        val formatProperties = VkFormatProperties.malloc(stack)
        vkGetPhysicalDeviceFormatProperties(sys.physicalDevice.vkPhysicalDevice, format, formatProperties)
        if (formatProperties.optimalTilingFeatures() and VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT == 0) {
            logW { "Texture image format does not support linear blitting!" }
            transitionLayout(dstLayout)
            return
        }

        val barrier = stack.callocVkImageMemoryBarrierN(1) {
            sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
            image(vkImage)
            srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
            subresourceRange {
                it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                it.baseArrayLayer(0)
                it.layerCount(arrayLayers)
                it.levelCount(1)
            }
        }

        val srcAccessMaskSrcOpt = srcAccessMask(layout)
        val dstAccessMaskSrcOpt = dstAccessMask(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
        val srcAccessMaskDst = srcAccessMask(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
        val dstAccessMaskDst = dstAccessMask(dstLayout)

        var mipWidth = width
        var mipHeight = height
        for (i in 1 until mipLevels) {
            barrier
                    .subresourceRange { it.baseMipLevel(i - 1) }
                    .oldLayout(layout)
                    .newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                    .srcAccessMask(srcAccessMaskSrcOpt)
                    .dstAccessMask(dstAccessMaskSrcOpt)

            vkCmdPipelineBarrier(commandBuffer,
                    VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT, 0,
                    null, null, barrier)

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
                dstOffsets(1)
                        .x(if (mipWidth > 1) mipWidth / 2 else 1)
                        .y(if (mipHeight > 1) mipHeight / 2 else 1)
                        .z(1)
                dstSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(i)
                    it.baseArrayLayer(0)
                    it.layerCount(arrayLayers)
                }
            }
            vkCmdBlitImage(commandBuffer,
                    vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                    vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                    blit, VK_FILTER_LINEAR)

            barrier
                    .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                    .newLayout(dstLayout)
                    .srcAccessMask(srcAccessMaskDst)
                    .dstAccessMask(dstAccessMaskDst)

            vkCmdPipelineBarrier(commandBuffer,
                    VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0,
                    null, null, barrier)

            if (mipWidth > 1) { mipWidth /= 2 }
            if (mipHeight > 1) { mipHeight /= 2 }
        }

        barrier.subresourceRange { it.baseMipLevel(mipLevels - 1) }
                .oldLayout(layout)
                .newLayout(dstLayout)
                .srcAccessMask(srcAccessMask(layout))
                .dstAccessMask(dstAccessMask(dstLayout))

        vkCmdPipelineBarrier(commandBuffer,
                VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0,
                null, null, barrier)

        layout = dstLayout
    }

    fun transitionLayout(newLayout: Int) {
        sys.commandPool.singleTimeCommands { commandBuffer ->
            transitionLayout(this, commandBuffer, newLayout)
        }
    }

    fun transitionLayout(stack: MemoryStack, commandBuffer: VkCommandBuffer, newLayout: Int) {
        val aspectMask = if (newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
            if (hasStencilComponent()) {
                VK_IMAGE_ASPECT_DEPTH_BIT or VK_IMAGE_ASPECT_STENCIL_BIT
            } else {
                VK_IMAGE_ASPECT_DEPTH_BIT
            }
        } else {
            VK_IMAGE_ASPECT_COLOR_BIT
        }

        val srcStage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT
        val dstStage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT

        val srcAccessMask = srcAccessMask(layout)
        val dstAccessMask = dstAccessMask(newLayout)

        val barrier = stack.callocVkImageMemoryBarrierN(arrayLayers) {
            for (i in 0 until arrayLayers) {
                this[i].apply {
                    sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                    oldLayout(layout)
                    newLayout(newLayout)
                    srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    image(vkImage)
                    srcAccessMask(srcAccessMask)
                    dstAccessMask(dstAccessMask)
                    subresourceRange {
                        it.baseMipLevel(0)
                        it.levelCount(mipLevels)
                        it.baseArrayLayer(i)
                        it.layerCount(1)
                        it.aspectMask(aspectMask)
                    }
                }
            }
        }

        vkCmdPipelineBarrier(commandBuffer, srcStage, dstStage, 0, null, null, barrier)
        layout = newLayout
    }

    private fun srcAccessMask(srcLayout: Int): Int = when (srcLayout) {
        VK_IMAGE_LAYOUT_UNDEFINED -> 0
        VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> VK_ACCESS_TRANSFER_WRITE_BIT
        VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
        VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> VK_ACCESS_TRANSFER_READ_BIT
        VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> VK_ACCESS_SHADER_READ_BIT
        else -> error("Layout not supported / implemented: $srcLayout")
    }

    private fun dstAccessMask(dstLayout: Int): Int = when (dstLayout) {
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

    override fun freeResources() {
        sys.memManager.freeImage(vkImage, allocation)
    }

    class Config {
        var width: Int = 0
        var height: Int = 0
        var depth: Int = 1
        var mipLevels: Int = 1
        var numSamples: Int = VK_SAMPLE_COUNT_1_BIT
        var format: Int = 0
        var tiling: Int = VK_IMAGE_TILING_OPTIMAL
        var usage: Int = 0
        var allocUsage: Int = 0
        var arrayLayers: Int = 1
        var flags: Int = 0
    }
}