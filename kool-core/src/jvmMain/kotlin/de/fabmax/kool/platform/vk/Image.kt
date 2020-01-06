package de.fabmax.kool.platform.vk

import de.fabmax.kool.KoolException
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

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

    fun transitionLayout(oldLayout: Int, newLayout: Int, baseArrayLayer: Int = -1) {
        sys.commandPool.singleTimeCommands { commandBuffer ->
            transitionLayout(this, commandBuffer, oldLayout, newLayout, baseArrayLayer)
        }
    }

    fun transitionLayout(stack: MemoryStack, commandBuffer: VkCommandBuffer, oldLayout: Int, newLayout: Int, baseArrayLayer: Int = -1) {
        val srcStage: Int
        val dstStage: Int
        val srcAccessMask: Int
        val dstAccessMask: Int

        val aspectMask = if (newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
            if (hasStencilComponent()) {
                VK_IMAGE_ASPECT_DEPTH_BIT or VK_IMAGE_ASPECT_STENCIL_BIT
            } else {
                VK_IMAGE_ASPECT_DEPTH_BIT
            }
        } else {
            VK_IMAGE_ASPECT_COLOR_BIT
        }

        srcStage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT
        dstStage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT

        when (oldLayout) {
            VK_IMAGE_LAYOUT_UNDEFINED -> {
                srcAccessMask = 0
                //srcStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
            }
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> {
                srcAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT
                //srcStage = VK_PIPELINE_STAGE_TRANSFER_BIT
            }
            VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> {
                srcAccessMask = VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
            }
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> {
                srcAccessMask = VK_ACCESS_TRANSFER_READ_BIT
            }
            VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> {
                srcAccessMask = VK_ACCESS_SHADER_READ_BIT
            }
            else -> throw KoolException("Source layout not supported: $oldLayout")
        }

        when (newLayout) {
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL -> {
                dstAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT
                //dstStage = VK_PIPELINE_STAGE_TRANSFER_BIT
            }
            VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL -> {
                dstAccessMask = VK_ACCESS_SHADER_READ_BIT
                //dstStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT
            }
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL -> {
                dstAccessMask = VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT or VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
                //dstStage = VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT
            }
            VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL -> {
                dstAccessMask = VK_ACCESS_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
                //dstStage = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT
            }
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL -> {
                dstAccessMask = VK_ACCESS_TRANSFER_READ_BIT
            }
            else -> throw KoolException("Destination layout not supported: $newLayout")
        }

        val nLayers = if (baseArrayLayer < 0) { arrayLayers } else { 1 }
        val barrier = stack.callocVkImageMemoryBarrierN(nLayers) {
            for (i in 0 until nLayers) {
                this[i].apply {
                    sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                    oldLayout(oldLayout)
                    newLayout(newLayout)
                    srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    image(vkImage)
                    srcAccessMask(srcAccessMask)
                    dstAccessMask(dstAccessMask)
                    subresourceRange {
                        it.baseMipLevel(0)
                        it.levelCount(mipLevels)
                        it.baseArrayLayer(if (baseArrayLayer < 0) { i } else { baseArrayLayer })
                        it.layerCount(1)
                        it.aspectMask(aspectMask)
                    }
                }
            }
        }

        vkCmdPipelineBarrier(commandBuffer, srcStage, dstStage,0, null, null, barrier)
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