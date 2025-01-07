package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.TexFormat
import org.lwjgl.vulkan.VK10.*

// fixme: changed maxWidth and maxHeight
abstract class RenderPassVk(val backend: RenderBackendVk, val maxWidth: Int, val maxHeight: Int, val colorFormats: List<Int>) : VkResource() {

    var triFrontDirection = VK_FRONT_FACE_COUNTER_CLOCKWISE

    abstract val vkRenderPass: VkRenderPass

    val nColorAttachments: Int
        get() = colorFormats.size

    val texFormat: TexFormat
        get() = getTexFormat(0)

    val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    val logicalDevice: LogicalDevice get() = backend.logicalDevice

    fun getTexFormat(attachment: Int): TexFormat {
        @Suppress("DEPRECATION")
        return when(colorFormats[attachment]) {
            VK_FORMAT_R8_UNORM -> TexFormat.R
            VK_FORMAT_R8G8_UNORM -> TexFormat.RG
            VK_FORMAT_R8G8B8_UNORM -> TexFormat.RGB
            VK_FORMAT_R8G8B8A8_UNORM -> TexFormat.RGBA

            VK_FORMAT_R16_SFLOAT -> TexFormat.R_F16
            VK_FORMAT_R16G16_SFLOAT -> TexFormat.RG_F16
            VK_FORMAT_R16G16B16_SFLOAT -> TexFormat.RGB_F16
            VK_FORMAT_R16G16B16A16_SFLOAT -> TexFormat.RGBA_F16

            else -> error("Unmapped format: ${colorFormats[attachment]}")
        }
    }
}