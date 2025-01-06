package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*

class ImageView(val logicalDevice: LogicalDevice, image: Long, format: Int, aspectMask: Int, mipLevels: Int, viewType: Int, layerCount: Int) : VkResource() {
    val vkImageView: Long

    init {
        memStack {
            val createInfo = callocVkImageViewCreateInfo {
                image(image)
                viewType(viewType)
                format(format)
                subresourceRange {
                    it.aspectMask(aspectMask)
                    it.baseMipLevel(0)
                    it.levelCount(mipLevels)
                    it.baseArrayLayer(0)
                    it.layerCount(layerCount)
                }
            }
            vkImageView = checkCreateLongPtr { vkCreateImageView(logicalDevice.vkDevice, createInfo, null, it) }
        }
        logD { "Created image view" }
    }

    override fun freeResources() {
        vkDestroyImageView(logicalDevice.vkDevice, vkImageView, null)
        logD { "Destroyed image view" }
    }

    companion object {
        fun imageView2d(logicalDevice: LogicalDevice, image: Image, aspectMask: Int): ImageView {
            require(image.depth == 1)
            return ImageView(
                logicalDevice,
                image.vkImage,
                image.format,
                aspectMask,
                image.mipLevels,
                VK_IMAGE_VIEW_TYPE_2D,
                image.arrayLayers
            )
        }
    }
}