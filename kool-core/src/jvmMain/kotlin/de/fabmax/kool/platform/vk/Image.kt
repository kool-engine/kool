package de.fabmax.kool.platform.vk

import org.lwjgl.vulkan.VK10.*

class Image(val sys: VkSystem, config: Config) : VkResource() {
    val width = config.width
    val height = config.height
    val mipLevels = config.mipLevels
    val numSamples = config.numSamples
    val format = config.format
    val arrayLayers = config.arrayLayers

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
                format(format)
                tiling(config.tiling)
                initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
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

    fun transitionLayout(oldLayout: Int, newLayout: Int) {
        sys.commandPool.singleTimeCommands { commandBuffer ->
            val sourceStage: Int
            val destinationStage: Int
            val sourceAccessMask: Int
            val destinationAccessMask: Int

            val aspectMask = if (newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
                if (hasStencilComponent()) {
                    VK_IMAGE_ASPECT_DEPTH_BIT or VK_IMAGE_ASPECT_STENCIL_BIT
                } else {
                    VK_IMAGE_ASPECT_DEPTH_BIT
                }
            } else {
                VK_IMAGE_ASPECT_COLOR_BIT
            }

            // fixme: this is ugly...
            if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
                sourceAccessMask = 0
                destinationAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT
                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT

            } else if (oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
                sourceAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT
                destinationAccessMask = VK_ACCESS_SHADER_READ_BIT
                sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT
                destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT

            } else if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
                sourceAccessMask = 0
                destinationAccessMask = VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT or VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                destinationStage = VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT

            } else if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL) {
                sourceAccessMask = 0
                destinationAccessMask = VK_ACCESS_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                destinationStage = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT

            } else {
                throw RuntimeException("Unsupported layout transition")
            }

            val barrier = callocVkImageMemoryBarrierN(arrayLayers) {
                for (i in 0 until arrayLayers) {
                    this[i].apply {
                        sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                        oldLayout(oldLayout)
                        newLayout(newLayout)
                        srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                        dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                        image(vkImage)
                        srcAccessMask(sourceAccessMask)
                        dstAccessMask(destinationAccessMask)
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

            vkCmdPipelineBarrier(commandBuffer, sourceStage, destinationStage,0, null, null, barrier)
        }
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
        var mipLevels: Int = 0
        var numSamples: Int = VK_SAMPLE_COUNT_1_BIT
        var format: Int = 0
        var tiling: Int = VK_IMAGE_TILING_OPTIMAL
        var usage: Int = 0
        var allocUsage: Int = 0
        var arrayLayers: Int = 1
        var flags: Int = 0
    }
}