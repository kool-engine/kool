package de.fabmax.kool.platform.vk

import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*

inline fun <T> MemoryStack.allocStruct(block: T.() -> Unit, factory: (MemoryStack) -> T): T {
    val struct = factory(this)
    struct.block()
    return struct
}

inline fun MemoryStack.callocVkApplicationInfo(block: VkApplicationInfo.() -> Unit): VkApplicationInfo =
    allocStruct(block) { VkApplicationInfo.calloc(it) }

inline fun MemoryStack.callocVkAttachmentDescriptionN(n: Int, block: VkAttachmentDescription.Buffer.() -> Unit): VkAttachmentDescription.Buffer =
    allocStruct(block) { VkAttachmentDescription.calloc(n, it) }

inline fun MemoryStack.callocVkAttachmentReferenceN(n: Int, block: VkAttachmentReference.Buffer.() -> Unit): VkAttachmentReference.Buffer =
    allocStruct(block) { VkAttachmentReference.calloc(n, it) }

inline fun MemoryStack.callocVkBufferCopyN(n: Int, block: VkBufferCopy.Buffer.() -> Unit): VkBufferCopy.Buffer =
    allocStruct(block) { VkBufferCopy.calloc(n, it) }

inline fun MemoryStack.callocVkBufferCreateInfo(block: VkBufferCreateInfo.() -> Unit): VkBufferCreateInfo =
    allocStruct(block) { VkBufferCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkBufferImageCopyN(n: Int, block: VkBufferImageCopy.Buffer.() -> Unit): VkBufferImageCopy.Buffer =
    allocStruct(block) { VkBufferImageCopy.calloc(n, it) }

inline fun MemoryStack.callocVkClearValueN(n: Int, block: VkClearValue.Buffer.() -> Unit): VkClearValue.Buffer =
    allocStruct(block) { VkClearValue.calloc(n, it) }

inline fun MemoryStack.callocVkCommandBufferAllocateInfo(block: VkCommandBufferAllocateInfo.() -> Unit): VkCommandBufferAllocateInfo =
    allocStruct(block) { VkCommandBufferAllocateInfo.calloc(it) }

inline fun MemoryStack.callocVkCommandBufferBeginInfo(block: VkCommandBufferBeginInfo.() -> Unit): VkCommandBufferBeginInfo =
    allocStruct(block) { VkCommandBufferBeginInfo.calloc(it) }

inline fun MemoryStack.callocVkCommandPoolCreateInfo(block: VkCommandPoolCreateInfo.() -> Unit): VkCommandPoolCreateInfo =
    allocStruct(block) { VkCommandPoolCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkDebugUtilsMessengerCreateInfoEXT(block: VkDebugUtilsMessengerCreateInfoEXT.() -> Unit): VkDebugUtilsMessengerCreateInfoEXT =
    allocStruct(block) { VkDebugUtilsMessengerCreateInfoEXT.calloc(it) }

inline fun MemoryStack.callocVkDescriptorBufferInfoN(n: Int, block: VkDescriptorBufferInfo.Buffer.() -> Unit): VkDescriptorBufferInfo.Buffer =
    allocStruct(block) { VkDescriptorBufferInfo.calloc(n, it) }

inline fun MemoryStack.callocVkDescriptorImageInfoN(n: Int, block: VkDescriptorImageInfo.Buffer.() -> Unit): VkDescriptorImageInfo.Buffer =
    allocStruct(block) { VkDescriptorImageInfo.calloc(n, it) }

inline fun MemoryStack.callocVkDescriptorPoolCreateInfo(block: VkDescriptorPoolCreateInfo.() -> Unit): VkDescriptorPoolCreateInfo =
    allocStruct(block) { VkDescriptorPoolCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkDescriptorPoolSizeN(n: Int, block: VkDescriptorPoolSize.Buffer.() -> Unit): VkDescriptorPoolSize.Buffer =
    allocStruct(block) { VkDescriptorPoolSize.calloc(n, it) }

inline fun MemoryStack.callocVkDescriptorSetAllocateInfo(block: VkDescriptorSetAllocateInfo.() -> Unit): VkDescriptorSetAllocateInfo =
    allocStruct(block) { VkDescriptorSetAllocateInfo.calloc(it) }

inline fun MemoryStack.callocVkDescriptorSetLayoutBindingN(n: Int, block: VkDescriptorSetLayoutBinding.Buffer.() -> Unit): VkDescriptorSetLayoutBinding.Buffer =
    allocStruct(block) { VkDescriptorSetLayoutBinding.calloc(n, it) }

inline fun MemoryStack.callocVkDescriptorSetLayoutCreateInfo(block: VkDescriptorSetLayoutCreateInfo.() -> Unit): VkDescriptorSetLayoutCreateInfo =
    allocStruct(block) { VkDescriptorSetLayoutCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkDeviceCreateInfo(block: VkDeviceCreateInfo.() -> Unit): VkDeviceCreateInfo =
    allocStruct(block) { VkDeviceCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkDeviceQueueCreateInfoN(n: Int, block: VkDeviceQueueCreateInfo.Buffer.() -> Unit): VkDeviceQueueCreateInfo.Buffer =
    allocStruct(block) { VkDeviceQueueCreateInfo.calloc(n, it) }

inline fun MemoryStack.callocVkFenceCreateInfo(block: VkFenceCreateInfo.() -> Unit): VkFenceCreateInfo =
    allocStruct(block) { VkFenceCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkFramebufferCreateInfo(block: VkFramebufferCreateInfo.() -> Unit): VkFramebufferCreateInfo =
    allocStruct(block) { VkFramebufferCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkGraphicsPipelineCreateInfoN(n: Int, block: VkGraphicsPipelineCreateInfo.Buffer.() -> Unit): VkGraphicsPipelineCreateInfo.Buffer =
    allocStruct(block) { VkGraphicsPipelineCreateInfo.calloc(n, it) }

inline fun MemoryStack.callocVkImageBlitN(n: Int, block: VkImageBlit.Buffer.() -> Unit): VkImageBlit.Buffer =
    allocStruct(block) { VkImageBlit.calloc(n, it) }

inline fun MemoryStack.callocVkImageCreateInfo(block: VkImageCreateInfo.() -> Unit): VkImageCreateInfo =
    allocStruct(block) { VkImageCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkImageCopyN(n: Int, block: VkImageCopy.Buffer.() -> Unit): VkImageCopy.Buffer =
        allocStruct(block) { VkImageCopy.calloc(n, it) }

inline fun MemoryStack.callocVkImageMemoryBarrierN(n: Int, block: VkImageMemoryBarrier.Buffer.() -> Unit): VkImageMemoryBarrier.Buffer =
    allocStruct(block) { VkImageMemoryBarrier.calloc(n, it) }

inline fun MemoryStack.callocVkImageViewCreateInfo(block: VkImageViewCreateInfo.() -> Unit): VkImageViewCreateInfo =
    allocStruct(block) { VkImageViewCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkInstanceCreateInfo(block: VkInstanceCreateInfo.() -> Unit): VkInstanceCreateInfo =
    allocStruct(block) { VkInstanceCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkMemoryAllocateInfo(block: VkMemoryAllocateInfo.() -> Unit): VkMemoryAllocateInfo =
    allocStruct(block) { VkMemoryAllocateInfo.calloc(it) }

inline fun MemoryStack.callocVkPhysicalDeviceFeatures(block: VkPhysicalDeviceFeatures.() -> Unit): VkPhysicalDeviceFeatures =
    allocStruct(block) { VkPhysicalDeviceFeatures.calloc(it) }

inline fun MemoryStack.callocVkPipelineColorBlendAttachmentStateN(n: Int, block: VkPipelineColorBlendAttachmentState.Buffer.() -> Unit): VkPipelineColorBlendAttachmentState.Buffer =
    allocStruct(block) { VkPipelineColorBlendAttachmentState.calloc(n, it) }

inline fun MemoryStack.callocVkPipelineColorBlendStateCreateInfo(block: VkPipelineColorBlendStateCreateInfo.() -> Unit): VkPipelineColorBlendStateCreateInfo =
    allocStruct(block) { VkPipelineColorBlendStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineDepthStencilStateCreateInfo(block: VkPipelineDepthStencilStateCreateInfo.() -> Unit): VkPipelineDepthStencilStateCreateInfo =
    allocStruct(block) { VkPipelineDepthStencilStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineDynamicStateCreateInfo(block: VkPipelineDynamicStateCreateInfo.() -> Unit): VkPipelineDynamicStateCreateInfo =
        allocStruct(block) { VkPipelineDynamicStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineInputAssemblyStateCreateInfo(block: VkPipelineInputAssemblyStateCreateInfo.() -> Unit): VkPipelineInputAssemblyStateCreateInfo =
    allocStruct(block) { VkPipelineInputAssemblyStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineLayoutCreateInfo(block: VkPipelineLayoutCreateInfo.() -> Unit): VkPipelineLayoutCreateInfo =
    allocStruct(block) { VkPipelineLayoutCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineMultisampleStateCreateInfo(block: VkPipelineMultisampleStateCreateInfo.() -> Unit): VkPipelineMultisampleStateCreateInfo =
    allocStruct(block) { VkPipelineMultisampleStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineRasterizationStateCreateInfo(block: VkPipelineRasterizationStateCreateInfo.() -> Unit): VkPipelineRasterizationStateCreateInfo =
    allocStruct(block) { VkPipelineRasterizationStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineShaderStageCreateInfoN(n: Int, block: VkPipelineShaderStageCreateInfo.Buffer.() -> Unit): VkPipelineShaderStageCreateInfo.Buffer =
    allocStruct(block) { VkPipelineShaderStageCreateInfo.calloc(n, it) }

inline fun MemoryStack.callocVkPipelineVertexInputStateCreateInfo(block: VkPipelineVertexInputStateCreateInfo.() -> Unit): VkPipelineVertexInputStateCreateInfo =
    allocStruct(block) { VkPipelineVertexInputStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPipelineViewportStateCreateInfo(block: VkPipelineViewportStateCreateInfo.() -> Unit): VkPipelineViewportStateCreateInfo =
    allocStruct(block) { VkPipelineViewportStateCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkPresentInfoKHR(block: VkPresentInfoKHR.() -> Unit): VkPresentInfoKHR =
    allocStruct(block) { VkPresentInfoKHR.calloc(it) }

inline fun MemoryStack.callocVkPushConstantRangeN(n: Int, block: VkPushConstantRange.Buffer.() -> Unit): VkPushConstantRange.Buffer =
        allocStruct(block) { VkPushConstantRange.calloc(n, it) }

inline fun MemoryStack.callocVkRect2DN(n: Int, block: VkRect2D.Buffer.() -> Unit): VkRect2D.Buffer =
    allocStruct(block) { VkRect2D.calloc(n, it) }

inline fun MemoryStack.callocVkRenderPassBeginInfo(block: VkRenderPassBeginInfo.() -> Unit): VkRenderPassBeginInfo =
    allocStruct(block) { VkRenderPassBeginInfo.calloc(it) }

inline fun MemoryStack.callocVkRenderPassCreateInfo(block: VkRenderPassCreateInfo.() -> Unit): VkRenderPassCreateInfo =
    allocStruct(block) { VkRenderPassCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkSamplerCreateInfo(block: VkSamplerCreateInfo.() -> Unit): VkSamplerCreateInfo =
    allocStruct(block) { VkSamplerCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkSemaphoreCreateInfo(block: VkSemaphoreCreateInfo.() -> Unit): VkSemaphoreCreateInfo =
    allocStruct(block) { VkSemaphoreCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkShaderModuleCreateInfo(block: VkShaderModuleCreateInfo.() -> Unit): VkShaderModuleCreateInfo =
    allocStruct(block) { VkShaderModuleCreateInfo.calloc(it) }

inline fun MemoryStack.callocVkSubmitInfo(block: VkSubmitInfo.() -> Unit): VkSubmitInfo =
    allocStruct(block) { VkSubmitInfo.calloc(it) }

inline fun MemoryStack.callocVkSubmitInfoN(n: Int, block: VkSubmitInfo.Buffer.() -> Unit): VkSubmitInfo.Buffer =
    allocStruct(block) { VkSubmitInfo.calloc(n, it) }

inline fun MemoryStack.callocVkSubpassDependencyN(n: Int, block: VkSubpassDependency.Buffer.() -> Unit): VkSubpassDependency.Buffer =
    allocStruct(block) { VkSubpassDependency.calloc(n, it) }

inline fun MemoryStack.callocVkSubpassDescriptionN(n: Int, block: VkSubpassDescription.Buffer.() -> Unit): VkSubpassDescription.Buffer =
    allocStruct(block) { VkSubpassDescription.calloc(n, it) }

inline fun MemoryStack.callocVkSwapchainCreateInfoKHR(block: VkSwapchainCreateInfoKHR.() -> Unit): VkSwapchainCreateInfoKHR =
    allocStruct(block) { VkSwapchainCreateInfoKHR.calloc(it) }

inline fun MemoryStack.callocVkVertexInputBindingDescriptionN(n: Int, block: VkVertexInputBindingDescription.Buffer.() -> Unit): VkVertexInputBindingDescription.Buffer =
    allocStruct(block) { VkVertexInputBindingDescription.calloc(n, it) }

inline fun MemoryStack.callocVkVertexInputAttributeDescriptionN(n: Int, block: VkVertexInputAttributeDescription.Buffer.() -> Unit): VkVertexInputAttributeDescription.Buffer =
    allocStruct(block) { VkVertexInputAttributeDescription.calloc(n, it) }

inline fun MemoryStack.callocVkViewportN(n: Int, block: VkViewport.Buffer.() -> Unit): VkViewport.Buffer =
    allocStruct(block) { VkViewport.calloc(n, it) }

inline fun MemoryStack.callocVkWriteDescriptorSetN(n: Int, block: VkWriteDescriptorSet.Buffer.() -> Unit): VkWriteDescriptorSet.Buffer =
    allocStruct(block) { VkWriteDescriptorSet.calloc(n, it) }
