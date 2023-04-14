package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*

class ImageView(val sys: VkSystem, image: Long, format: Int, aspectFlags: Int, mipLevels: Int, viewType: Int) : VkResource() {

    val vkImageView: Long

    constructor(sys: VkSystem, image: Image, aspectFlags: Int):
            this(sys, image.vkImage, image.format, aspectFlags, image.mipLevels, if (image.depth > 1) VK_IMAGE_VIEW_TYPE_3D else VK_IMAGE_VIEW_TYPE_2D)

    init {
        memStack {
            val createInfo = callocVkImageViewCreateInfo {
                sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                image(image)
                viewType(viewType)
                format(format)
                subresourceRange {
                    it.aspectMask(aspectFlags)
                    it.baseMipLevel(0)
                    it.levelCount(mipLevels)
                    it.baseArrayLayer(0)
                    it.layerCount(1)
                }
            }
            vkImageView = checkCreatePointer { vkCreateImageView(sys.device.vkDevice, createInfo, null, it) }
        }
        logD { "Created image view" }
    }

    override fun freeResources() {
        vkDestroyImageView(sys.device.vkDevice, vkImageView, null)
        logD { "Destroyed image view" }
    }
}