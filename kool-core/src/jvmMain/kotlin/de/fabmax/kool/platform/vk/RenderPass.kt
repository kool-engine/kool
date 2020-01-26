package de.fabmax.kool.platform.vk

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.TexFormat
import org.lwjgl.vulkan.VK10

abstract class RenderPass(val sys: VkSystem, val maxWidth: Int, val maxHeight: Int, val colorFormat: Int) : VkResource() {

    abstract val vkRenderPass: Long

    val texFormat: TexFormat
        get() = when(colorFormat) {
            VK10.VK_FORMAT_R8_UNORM -> TexFormat.R
            VK10.VK_FORMAT_R8G8_UNORM -> TexFormat.RG
            VK10.VK_FORMAT_R8G8B8_UNORM -> TexFormat.RGB
            VK10.VK_FORMAT_R8G8B8A8_UNORM -> TexFormat.RGBA

            VK10.VK_FORMAT_R16_SFLOAT -> TexFormat.R_F16
            VK10.VK_FORMAT_R16G16_SFLOAT -> TexFormat.RG_F16
            VK10.VK_FORMAT_R16G16B16_SFLOAT -> TexFormat.RGB_F16
            VK10.VK_FORMAT_R16G16B16A16_SFLOAT -> TexFormat.RGBA_F16

            else -> throw KoolException("Unmapped format: $colorFormat")
        }
}