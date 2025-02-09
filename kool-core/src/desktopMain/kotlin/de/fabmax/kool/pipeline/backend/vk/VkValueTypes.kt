package de.fabmax.kool.pipeline.backend.vk

import java.nio.ByteBuffer

class VkBuffer(val handle: Long, val allocation: Long, val bufferSize: Long, val mapped: ByteBuffer?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return handle == (other as VkBuffer).handle
    }

    override fun hashCode(): Int {
        return handle.hashCode()
    }
}

@JvmInline
value class VkCommandPool(val handle: Long)

@JvmInline
value class VkComputePipeline(val handle: Long)

@JvmInline
value class VkDescriptorPool(val handle: Long)

@JvmInline
value class VkDescriptorSet(val handle: Long)

@JvmInline
value class VkDescriptorSetLayout(val handle: Long)

@JvmInline
value class VkFramebuffer(val handle: Long)

@JvmInline
value class VkGraphicsPipeline(val handle: Long)

data class VkImage(val handle: Long, val allocation: Long)

data class VkImageView(val handle: Long)

@JvmInline
value class VkFence(val handle: Long)

@JvmInline
value class VkPipelineLayout(val handle: Long)

@JvmInline
value class VkQueryPool(val handle: Long)

@JvmInline
value class VkRenderPass(val handle: Long)

@JvmInline
value class VkSampler(val handle: Long)

@JvmInline
value class VkSemaphore(val handle: Long)

@JvmInline
value class VkShaderModule(val handle: Long)

@JvmInline
value class VkSwapchain(val handle: Long)
