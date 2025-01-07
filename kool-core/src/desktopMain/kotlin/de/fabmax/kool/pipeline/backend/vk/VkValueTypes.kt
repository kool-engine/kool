package de.fabmax.kool.pipeline.backend.vk

@JvmInline
value class VkCommandPool(val handle: Long)

@JvmInline
value class VkFramebuffer(val handle: Long)

data class VkImage(val handle: Long, val allocation: Long)

@JvmInline
value class VkImageView(val handle: Long)

@JvmInline
value class VkFence(val handle: Long)

@JvmInline
value class VkRenderPass(val handle: Long)

@JvmInline
value class VkSemaphore(val handle: Long)

@JvmInline
value class VkSwapchain(val handle: Long)
