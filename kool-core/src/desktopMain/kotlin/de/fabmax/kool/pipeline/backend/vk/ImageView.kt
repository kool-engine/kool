package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D

class ImageView(
    val logicalDevice: LogicalDevice,
    image: VkImage,
    format: Int,
    aspectMask: Int,
    mipLevels: Int,
    viewType: Int,
    layerCount: Int
) : VkResource() {

    val vkImageView: VkImageView = logicalDevice.createImageView {
        image(image.handle)
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

    override fun freeResources() {
        logicalDevice.destroyImageView(vkImageView)
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