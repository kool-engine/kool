package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D

class ImageView(
    val device: Device,
    image: VkImage,
    format: Int,
    aspectMask: Int,
    viewType: Int,
    mipLevels: Int,
    layerCount: Int
) : BaseReleasable() {

    val vkImageView: VkImageView = device.createImageView {
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

    override fun release() {
        super.release()
        device.destroyImageView(vkImageView)
    }

    companion object {
        fun imageView2d(device: Device, image: Image, aspectMask: Int): ImageView {
            require(image.depth == 1)
            return ImageView(
                device,
                image.vkImage,
                image.format,
                aspectMask,
                VK_IMAGE_VIEW_TYPE_2D,
                image.mipLevels,
                image.arrayLayers
            )
        }
    }
}