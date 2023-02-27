package de.fabmax.kool.platform.vk.util

import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.pipeline.TexFormat
import org.lwjgl.vulkan.VK10.*

fun ShaderStage.bitValue(): Int {
    return when (this) {
        ShaderStage.VERTEX_SHADER -> VK_SHADER_STAGE_VERTEX_BIT
        ShaderStage.TESSELEATION_CTRL -> VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT
        ShaderStage.TESSELATION_EVAL -> VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT
        ShaderStage.GEOMETRY_SHADER -> VK_SHADER_STAGE_GEOMETRY_BIT
        ShaderStage.FRAGMENT_SHADER -> VK_SHADER_STAGE_FRAGMENT_BIT
        ShaderStage.ALL -> VK_SHADER_STAGE_ALL
    }
}

val TexFormat.vkFormat: Int
    get() = when(this) {
        TexFormat.R -> VK_FORMAT_R8_UNORM
        TexFormat.RG -> VK_FORMAT_R8G8_UNORM
        TexFormat.RGB -> VK_FORMAT_R8G8B8_UNORM
        TexFormat.RGBA -> VK_FORMAT_R8G8B8A8_UNORM

        TexFormat.R_F16 -> VK_FORMAT_R16_SFLOAT
        TexFormat.RG_F16 -> VK_FORMAT_R16G16_SFLOAT
        TexFormat.RGB_F16 -> VK_FORMAT_R16G16B16_SFLOAT
        TexFormat.RGBA_F16 -> VK_FORMAT_R16G16B16A16_SFLOAT

        TexFormat.RI -> VK_FORMAT_R8_SINT
    }

val TexFormat.vkBytesPerPx: Int
    get() = when(this) {
        TexFormat.R -> 1
        TexFormat.RG -> 2
        TexFormat.RGB -> 3
        TexFormat.RGBA -> 4

        TexFormat.R_F16 -> 2
        TexFormat.RG_F16 -> 4
        TexFormat.RGB_F16 -> 6
        TexFormat.RGBA_F16 -> 8

        TexFormat.RI -> 1
    }
