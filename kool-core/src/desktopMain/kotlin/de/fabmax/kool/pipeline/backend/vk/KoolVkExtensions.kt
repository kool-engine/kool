package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.VK10.*

fun ShaderStage.bitValue(): Int {
    return when (this) {
        ShaderStage.VERTEX_SHADER -> VK_SHADER_STAGE_VERTEX_BIT
        ShaderStage.TESSELEATION_CTRL -> VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT
        ShaderStage.TESSELATION_EVAL -> VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT
        ShaderStage.GEOMETRY_SHADER -> VK_SHADER_STAGE_GEOMETRY_BIT
        ShaderStage.FRAGMENT_SHADER -> VK_SHADER_STAGE_FRAGMENT_BIT
        ShaderStage.COMPUTE_SHADER -> VK_SHADER_STAGE_COMPUTE_BIT
        ShaderStage.ALL -> VK_SHADER_STAGE_ALL
    }
}

val TexFormat.vk: Int
    get() = when(this) {
        TexFormat.R -> VK_FORMAT_R8_UNORM
        TexFormat.RG -> VK_FORMAT_R8G8_UNORM
        TexFormat.RGBA -> VK_FORMAT_R8G8B8A8_UNORM

        TexFormat.R_F16 -> VK_FORMAT_R16_SFLOAT
        TexFormat.RG_F16 -> VK_FORMAT_R16G16_SFLOAT
        TexFormat.RGBA_F16 -> VK_FORMAT_R16G16B16A16_SFLOAT

        TexFormat.R_F32 -> VK_FORMAT_R32_SFLOAT
        TexFormat.RG_F32 -> VK_FORMAT_R32G32_SFLOAT
        TexFormat.RGBA_F32 -> VK_FORMAT_R32G32B32A32_SFLOAT

        TexFormat.R_I32 -> VK_FORMAT_R32_SINT
        TexFormat.RG_I32 -> VK_FORMAT_R32G32_SINT
        TexFormat.RGBA_I32 -> VK_FORMAT_R32G32B32A32_SINT

        TexFormat.R_U32 -> VK_FORMAT_R32_UINT
        TexFormat.RG_U32 -> VK_FORMAT_R32G32_UINT
        TexFormat.RGBA_U32 -> VK_FORMAT_R32G32B32A32_UINT

        TexFormat.RG11B10_F -> VK_FORMAT_B10G11R11_UFLOAT_PACK32
    }

@Suppress("DEPRECATION")
val TexFormat.vkBytesPerPx: Int
    get() = when(this) {
        TexFormat.R -> 1
        TexFormat.RG -> 2
        TexFormat.RGBA -> 4

        TexFormat.R_F16 -> 2
        TexFormat.RG_F16 -> 4
        TexFormat.RGBA_F16 -> 8

        TexFormat.R_F32 -> 4
        TexFormat.RG_F32 -> 8
        TexFormat.RGBA_F32 -> 16

        TexFormat.R_I32 -> 4
        TexFormat.RG_I32 -> 8
        TexFormat.RGBA_I32 -> 16

        TexFormat.R_U32 -> 4
        TexFormat.RG_U32 -> 8
        TexFormat.RGBA_U32 -> 16

        TexFormat.RG11B10_F -> 4
    }

val CullMethod.vk: Int get() = when (this) {
    CullMethod.CULL_BACK_FACES -> VK_CULL_MODE_BACK_BIT
    CullMethod.CULL_FRONT_FACES -> VK_CULL_MODE_FRONT_BIT
    CullMethod.NO_CULLING -> VK_CULL_MODE_NONE
}

val DepthCompareOp.vk: Int get() = when (this) {
    DepthCompareOp.ALWAYS -> VK_COMPARE_OP_ALWAYS
    DepthCompareOp.NEVER -> VK_COMPARE_OP_NEVER
    DepthCompareOp.LESS -> VK_COMPARE_OP_LESS
    DepthCompareOp.LESS_EQUAL -> VK_COMPARE_OP_LESS_OR_EQUAL
    DepthCompareOp.GREATER -> VK_COMPARE_OP_GREATER
    DepthCompareOp.GREATER_EQUAL -> VK_COMPARE_OP_GREATER_OR_EQUAL
    DepthCompareOp.EQUAL -> VK_COMPARE_OP_EQUAL
    DepthCompareOp.NOT_EQUAL -> VK_COMPARE_OP_NOT_EQUAL
}

val FilterMethod.vk: Int get() = when (this) {
    FilterMethod.LINEAR -> VK_FILTER_LINEAR
    FilterMethod.NEAREST -> VK_FILTER_NEAREST
}

val AddressMode.vk: Int get() = when (this) {
    AddressMode.CLAMP_TO_EDGE -> VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE
    AddressMode.MIRRORED_REPEAT -> VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT
    AddressMode.REPEAT -> VK_SAMPLER_ADDRESS_MODE_REPEAT
}

fun VkClearValue.setColor(color: Color) {
    color {
        it.float32(0, color.r)
        it.float32(1, color.g)
        it.float32(2, color.b)
        it.float32(3, color.a)
    }
}

fun VkCommandBuffer.reset(flags: Int = 0) {
    vkCheck(vkResetCommandBuffer(this, flags)) { "Failed resetting command buffer: $it" }
}

inline fun VkCommandBuffer.begin(stack: MemoryStack, block: VkCommandBufferBeginInfo.() -> Unit) {
    val beginInfo = stack.callocVkCommandBufferBeginInfo(block)
    vkCheck(vkBeginCommandBuffer(this, beginInfo)) { "Failed beginning command buffer: $it" }
}

fun VkCommandBuffer.end() {
    vkCheck(vkEndCommandBuffer(this)) { "Failed ending command buffer: $it" }
}

inline fun VkQueue.submit(fence: VkFence, stack: MemoryStack, block: VkSubmitInfo.() -> Unit) {
    val submitInfo = stack.callocVkSubmitInfo(block)
    vkCheck(vkQueueSubmit(this, submitInfo, fence.handle)) { "Failed submitting queue: $it" }
}
