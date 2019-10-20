package de.fabmax.kool.platform.vk

import org.lwjgl.vulkan.VK10.*

class Image(val sys: VkSystem, val width: Int, val height: Int, val mipLevels: Int, val numSamples: Int, val format: Int, tiling: Int, usage: Int, allocUsage: Int) : VkResource() {

    val vkImage: Long
    val allocation: Long

    init {
        memStack {
            val imageInfo = callocVkImageCreateInfo {
                sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                imageType(VK_IMAGE_TYPE_2D)
                extent {
                    it.width(width)
                    it.height(height)
                    it.depth(1)
                }
                mipLevels(mipLevels)
                arrayLayers(1)
                format(format)
                tiling(tiling)
                initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                usage(usage)
                sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                samples(numSamples)
                flags(0)
            }

            val pBuffer = mallocLong(1)
            val pAllocation = mallocPointer(1)
            sys.memManager.createImage(imageInfo, allocUsage, pBuffer, pAllocation)
            vkImage = pBuffer[0]
            allocation = pAllocation[0]
        }
    }

    fun transitionLayout(oldLayout: Int, newLayout: Int) {
        sys.commandPool.singleTimeCommands { commandBuffer ->
            val barrier = callocVkImageMemoryBarrierN(1) {
                sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                oldLayout(oldLayout)
                newLayout(newLayout)
                srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                image(vkImage)
                subresourceRange {
                    it.baseMipLevel(0)
                    it.levelCount(mipLevels)
                    it.baseArrayLayer(0)
                    it.layerCount(1)
                }
            }

            val sourceStage: Int
            val destinationStage: Int

            if (newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
                barrier.subresourceRange {
                    if (hasStencilComponent()) {
                        it.aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT or VK_IMAGE_ASPECT_STENCIL_BIT)
                    } else {
                        it.aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT)
                    }
                }
            } else {
                barrier.subresourceRange {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                }
            }

            // fixme: this is ugly...
            if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
                barrier.srcAccessMask(0)
                barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT

            } else if (oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
                barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
                barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT)
                sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT
                destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT

            } else if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
                barrier.srcAccessMask(0)
                barrier.dstAccessMask(VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT or VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT)
                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                destinationStage = VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT

            } else if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL) {
                barrier.srcAccessMask(0)
                barrier.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                destinationStage = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT

            } else {
                throw RuntimeException("Unsupported layout transition")
            }

            vkCmdPipelineBarrier(commandBuffer, sourceStage, destinationStage,0, null, null, barrier)
        }
    }

    private fun hasStencilComponent(): Boolean {
        return format == VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK_FORMAT_D24_UNORM_S8_UINT
    }

    override fun freeResources() {
        sys.memManager.freeImage(vkImage, allocation)
    }
}