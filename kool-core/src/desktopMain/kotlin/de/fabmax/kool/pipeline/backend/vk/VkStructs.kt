package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Struct
import org.lwjgl.system.StructBuffer
import org.lwjgl.vulkan.*
import java.nio.IntBuffer
import java.nio.LongBuffer

inline fun <T: Struct<T>> MemoryStack.allocStruct(
    factory: (MemoryStack) -> T,
    block: T.() -> Unit
): T = factory(this).apply(block)

inline fun <T: StructBuffer<S, T>, S: Struct<S>> MemoryStack.allocStructBuffer(
    sz: Int,
    factory: (Int, MemoryStack) -> T,
    block: T.() -> Unit
): T = factory(sz, this).apply(block)

inline fun <T: StructBuffer<S, T>, S: Struct<S>> MemoryStack.allocStructBufferItems(
    sz: Int,
    factory: (Int, MemoryStack) -> T,
    block: T.() -> Unit,
    itemInit: T.(Int) -> Unit
): T {
    val buffer = factory(sz, this)
    for (i in 0 until sz) {
        buffer.itemInit(i)
    }
    buffer.block()
    return buffer
}

inline fun MemoryStack.callocVkApplicationInfo(block: VkApplicationInfo.() -> Unit): VkApplicationInfo =
    allocStruct(VkApplicationInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkBlitImageInfo2(block: VkBlitImageInfo2.() -> Unit): VkBlitImageInfo2 =
    allocStruct(VkBlitImageInfo2::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkBufferCreateInfo(block: VkBufferCreateInfo.() -> Unit): VkBufferCreateInfo =
    allocStruct(VkBufferCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkCommandBufferAllocateInfo(block: VkCommandBufferAllocateInfo.() -> Unit): VkCommandBufferAllocateInfo =
    allocStruct(VkCommandBufferAllocateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkCommandBufferBeginInfo(block: VkCommandBufferBeginInfo.() -> Unit): VkCommandBufferBeginInfo =
    allocStruct(VkCommandBufferBeginInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkCommandPoolCreateInfo(block: VkCommandPoolCreateInfo.() -> Unit): VkCommandPoolCreateInfo =
    allocStruct(VkCommandPoolCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkDebugUtilsMessengerCreateInfoEXT(block: VkDebugUtilsMessengerCreateInfoEXT.() -> Unit): VkDebugUtilsMessengerCreateInfoEXT =
    allocStruct(VkDebugUtilsMessengerCreateInfoEXT::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkDependencyInfo(block: VkDependencyInfo.() -> Unit): VkDependencyInfo =
    allocStruct(VkDependencyInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkDescriptorPoolCreateInfo(block: VkDescriptorPoolCreateInfo.() -> Unit): VkDescriptorPoolCreateInfo =
    allocStruct(VkDescriptorPoolCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkDescriptorSetAllocateInfo(block: VkDescriptorSetAllocateInfo.() -> Unit): VkDescriptorSetAllocateInfo =
    allocStruct(VkDescriptorSetAllocateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkDescriptorSetLayoutCreateInfo(block: VkDescriptorSetLayoutCreateInfo.() -> Unit): VkDescriptorSetLayoutCreateInfo =
    allocStruct(VkDescriptorSetLayoutCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkDeviceCreateInfo(block: VkDeviceCreateInfo.() -> Unit): VkDeviceCreateInfo =
    allocStruct(VkDeviceCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkFenceCreateInfo(block: VkFenceCreateInfo.() -> Unit): VkFenceCreateInfo =
    allocStruct( VkFenceCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkFramebufferCreateInfo(block: VkFramebufferCreateInfo.() -> Unit): VkFramebufferCreateInfo =
    allocStruct(VkFramebufferCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkImageCreateInfo(block: VkImageCreateInfo.() -> Unit): VkImageCreateInfo =
    allocStruct(VkImageCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkImageViewCreateInfo(block: VkImageViewCreateInfo.() -> Unit): VkImageViewCreateInfo =
    allocStruct(VkImageViewCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkInstanceCreateInfo(block: VkInstanceCreateInfo.() -> Unit): VkInstanceCreateInfo =
    allocStruct(VkInstanceCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkMemoryAllocateInfo(block: VkMemoryAllocateInfo.() -> Unit): VkMemoryAllocateInfo =
    allocStruct(VkMemoryAllocateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPhysicalDeviceFeatures(block: VkPhysicalDeviceFeatures.() -> Unit): VkPhysicalDeviceFeatures =
    allocStruct(VkPhysicalDeviceFeatures::calloc, block)

inline fun MemoryStack.callocVkPipelineColorBlendStateCreateInfo(block: VkPipelineColorBlendStateCreateInfo.() -> Unit): VkPipelineColorBlendStateCreateInfo =
    allocStruct(VkPipelineColorBlendStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineDepthStencilStateCreateInfo(block: VkPipelineDepthStencilStateCreateInfo.() -> Unit): VkPipelineDepthStencilStateCreateInfo =
    allocStruct(VkPipelineDepthStencilStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineDynamicStateCreateInfo(block: VkPipelineDynamicStateCreateInfo.() -> Unit): VkPipelineDynamicStateCreateInfo =
    allocStruct(VkPipelineDynamicStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineInputAssemblyStateCreateInfo(block: VkPipelineInputAssemblyStateCreateInfo.() -> Unit): VkPipelineInputAssemblyStateCreateInfo =
    allocStruct(VkPipelineInputAssemblyStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineLayoutCreateInfo(block: VkPipelineLayoutCreateInfo.() -> Unit): VkPipelineLayoutCreateInfo =
    allocStruct(VkPipelineLayoutCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineMultisampleStateCreateInfo(block: VkPipelineMultisampleStateCreateInfo.() -> Unit): VkPipelineMultisampleStateCreateInfo =
    allocStruct(VkPipelineMultisampleStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineRasterizationStateCreateInfo(block: VkPipelineRasterizationStateCreateInfo.() -> Unit): VkPipelineRasterizationStateCreateInfo =
    allocStruct(VkPipelineRasterizationStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineRenderingCreateInfo(block: VkPipelineRenderingCreateInfo.() -> Unit): VkPipelineRenderingCreateInfo =
    allocStruct(VkPipelineRenderingCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineVertexInputStateCreateInfo(block: VkPipelineVertexInputStateCreateInfo.() -> Unit): VkPipelineVertexInputStateCreateInfo =
    allocStruct(VkPipelineVertexInputStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPipelineViewportStateCreateInfo(block: VkPipelineViewportStateCreateInfo.() -> Unit): VkPipelineViewportStateCreateInfo =
    allocStruct(VkPipelineViewportStateCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkPresentInfoKHR(block: VkPresentInfoKHR.() -> Unit): VkPresentInfoKHR =
    allocStruct(VkPresentInfoKHR::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkQueryPoolCreateInfo(block: VkQueryPoolCreateInfo.() -> Unit): VkQueryPoolCreateInfo =
    allocStruct(VkQueryPoolCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkRenderingAttachmentInfo(block: VkRenderingAttachmentInfo.() -> Unit): VkRenderingAttachmentInfo =
    allocStruct(VkRenderingAttachmentInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkRenderingInfo(block: VkRenderingInfo.() -> Unit): VkRenderingInfo =
    allocStruct(VkRenderingInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkRenderPassBeginInfo(block: VkRenderPassBeginInfo.() -> Unit): VkRenderPassBeginInfo =
    allocStruct(VkRenderPassBeginInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkRenderPassCreateInfo(block: VkRenderPassCreateInfo.() -> Unit): VkRenderPassCreateInfo =
    allocStruct(VkRenderPassCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkSamplerCreateInfo(block: VkSamplerCreateInfo.() -> Unit): VkSamplerCreateInfo =
    allocStruct(VkSamplerCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkSemaphoreCreateInfo(block: VkSemaphoreCreateInfo.() -> Unit): VkSemaphoreCreateInfo =
    allocStruct(VkSemaphoreCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkShaderModuleCreateInfo(block: VkShaderModuleCreateInfo.() -> Unit): VkShaderModuleCreateInfo =
    allocStruct(VkShaderModuleCreateInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkSubmitInfo(block: VkSubmitInfo.() -> Unit): VkSubmitInfo =
    allocStruct(VkSubmitInfo::calloc) {
        `sType$Default`()
        block()
    }

inline fun MemoryStack.callocVkSwapchainCreateInfoKHR(block: VkSwapchainCreateInfoKHR.() -> Unit): VkSwapchainCreateInfoKHR =
    allocStruct(VkSwapchainCreateInfoKHR::calloc) {
        `sType$Default`()
        block()
    }


inline fun MemoryStack.callocVkAttachmentDescriptionN(n: Int, block: VkAttachmentDescription.Buffer.() -> Unit): VkAttachmentDescription.Buffer =
    allocStructBuffer(n, VkAttachmentDescription::calloc, block)

inline fun MemoryStack.callocVkAttachmentReferenceN(n: Int, block: VkAttachmentReference.Buffer.() -> Unit): VkAttachmentReference.Buffer =
    allocStructBuffer(n, VkAttachmentReference::calloc, block)

inline fun MemoryStack.callocVkBufferCopyN(n: Int, block: VkBufferCopy.Buffer.() -> Unit): VkBufferCopy.Buffer =
    allocStructBuffer(n, VkBufferCopy::calloc, block)

inline fun MemoryStack.callocVkBufferImageCopyN(n: Int, block: VkBufferImageCopy.Buffer.() -> Unit): VkBufferImageCopy.Buffer =
    allocStructBuffer(n, VkBufferImageCopy::calloc, block)

inline fun MemoryStack.callocVkClearValueN(n: Int, block: VkClearValue.Buffer.() -> Unit): VkClearValue.Buffer =
    allocStructBuffer(n, VkClearValue::calloc, block)

inline fun MemoryStack.callocVkComputePipelineCreateInfoN(n: Int, block: VkComputePipelineCreateInfo.Buffer.() -> Unit): VkComputePipelineCreateInfo.Buffer =
    allocStructBufferItems(n, VkComputePipelineCreateInfo::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkDescriptorBufferInfoN(n: Int, block: VkDescriptorBufferInfo.Buffer.() -> Unit): VkDescriptorBufferInfo.Buffer =
    allocStructBuffer(n, VkDescriptorBufferInfo::calloc, block)

inline fun MemoryStack.callocVkDescriptorImageInfoN(n: Int, block: VkDescriptorImageInfo.Buffer.() -> Unit): VkDescriptorImageInfo.Buffer =
    allocStructBuffer(n, VkDescriptorImageInfo::calloc, block)

inline fun MemoryStack.callocVkDescriptorPoolSizeN(n: Int, block: VkDescriptorPoolSize.Buffer.() -> Unit): VkDescriptorPoolSize.Buffer =
    allocStructBuffer(n, VkDescriptorPoolSize::calloc, block)

inline fun MemoryStack.callocVkDescriptorSetLayoutBindingN(n: Int, block: VkDescriptorSetLayoutBinding.Buffer.() -> Unit): VkDescriptorSetLayoutBinding.Buffer =
    allocStructBuffer(n, VkDescriptorSetLayoutBinding::calloc, block)

inline fun MemoryStack.callocVkDeviceQueueCreateInfoN(n: Int, block: VkDeviceQueueCreateInfo.Buffer.() -> Unit): VkDeviceQueueCreateInfo.Buffer =
    allocStructBufferItems(n, VkDeviceQueueCreateInfo::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkGraphicsPipelineCreateInfoN(n: Int, block: VkGraphicsPipelineCreateInfo.Buffer.() -> Unit): VkGraphicsPipelineCreateInfo.Buffer =
    allocStructBufferItems(n, VkGraphicsPipelineCreateInfo::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkImageBlitN(n: Int, block: VkImageBlit.Buffer.() -> Unit): VkImageBlit.Buffer =
    allocStructBuffer(n, VkImageBlit::calloc, block)

inline fun MemoryStack.callocVkImageBlit2N(n: Int, block: VkImageBlit2.Buffer.() -> Unit): VkImageBlit2.Buffer =
    allocStructBufferItems(n, VkImageBlit2::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkImageCopyN(n: Int, block: VkImageCopy.Buffer.() -> Unit): VkImageCopy.Buffer =
    allocStructBuffer(n, VkImageCopy::calloc, block)

inline fun MemoryStack.callocVkImageMemoryBarrierN(n: Int, block: VkImageMemoryBarrier.Buffer.() -> Unit): VkImageMemoryBarrier.Buffer =
    allocStructBufferItems(n, VkImageMemoryBarrier::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkImageMemoryBarrier2N(n: Int, block: VkImageMemoryBarrier2.Buffer.() -> Unit): VkImageMemoryBarrier2.Buffer =
    allocStructBufferItems(n, VkImageMemoryBarrier2::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkPipelineColorBlendAttachmentStateN(n: Int, block: VkPipelineColorBlendAttachmentState.Buffer.() -> Unit): VkPipelineColorBlendAttachmentState.Buffer =
    allocStructBuffer(n, VkPipelineColorBlendAttachmentState::calloc, block)

inline fun MemoryStack.callocVkPipelineShaderStageCreateInfoN(n: Int, block: VkPipelineShaderStageCreateInfo.Buffer.() -> Unit): VkPipelineShaderStageCreateInfo.Buffer =
    allocStructBufferItems(n, VkPipelineShaderStageCreateInfo::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkPushConstantRangeN(n: Int, block: VkPushConstantRange.Buffer.() -> Unit): VkPushConstantRange.Buffer =
    allocStructBuffer(n, VkPushConstantRange::calloc, block)

inline fun MemoryStack.callocVkRect2DN(n: Int, block: VkRect2D.Buffer.() -> Unit): VkRect2D.Buffer =
    allocStructBuffer(n, VkRect2D::calloc, block)

inline fun MemoryStack.callocVkRenderingAttachmentInfoN(n: Int, block: VkRenderingAttachmentInfo.Buffer.() -> Unit): VkRenderingAttachmentInfo.Buffer =
    allocStructBufferItems(n, VkRenderingAttachmentInfo::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkSubmitInfoN(n: Int, block: VkSubmitInfo.Buffer.() -> Unit): VkSubmitInfo.Buffer =
    allocStructBufferItems(n, VkSubmitInfo::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun MemoryStack.callocVkSubpassDependencyN(n: Int, block: VkSubpassDependency.Buffer.() -> Unit): VkSubpassDependency.Buffer =
    allocStructBuffer(n, VkSubpassDependency::calloc, block)

inline fun MemoryStack.callocVkSubpassDescriptionN(n: Int, block: VkSubpassDescription.Buffer.() -> Unit): VkSubpassDescription.Buffer =
    allocStructBuffer(n, VkSubpassDescription::calloc, block)

inline fun MemoryStack.callocVkVertexInputBindingDescriptionN(n: Int, block: VkVertexInputBindingDescription.Buffer.() -> Unit): VkVertexInputBindingDescription.Buffer =
    allocStructBuffer(n, VkVertexInputBindingDescription::calloc, block)

inline fun MemoryStack.callocVkVertexInputAttributeDescriptionN(n: Int, block: VkVertexInputAttributeDescription.Buffer.() -> Unit): VkVertexInputAttributeDescription.Buffer =
    allocStructBuffer(n, VkVertexInputAttributeDescription::calloc, block)

inline fun MemoryStack.callocVkViewportN(n: Int, block: VkViewport.Buffer.() -> Unit): VkViewport.Buffer =
    allocStructBuffer(n, VkViewport::calloc, block)

inline fun MemoryStack.callocVkWriteDescriptorSetN(n: Int, block: VkWriteDescriptorSet.Buffer.() -> Unit): VkWriteDescriptorSet.Buffer =
    allocStructBufferItems(n, VkWriteDescriptorSet::calloc, block) {
        get(it).`sType$Default`()
    }

inline fun <T> MemoryStack.enumerateBuffer(createBuffer: (Int) -> T, block: (IntBuffer, T?) -> Unit): T {
    val ip = mallocInt(1)
    block(ip, null)
    val buffer = createBuffer(ip[0])
    block(ip, buffer)
    return buffer
}

inline fun MemoryStack.enumerateExtensionProperties(block: (IntBuffer, VkExtensionProperties.Buffer?) -> Unit): VkExtensionProperties.Buffer =
    enumerateBuffer(VkExtensionProperties::malloc, block)

inline fun MemoryStack.enumerateLayerProperties(block: (IntBuffer, VkLayerProperties.Buffer?) -> Unit): VkLayerProperties.Buffer =
    enumerateBuffer(VkLayerProperties::malloc, block)

inline fun MemoryStack.enumerateQueueProperties(block: (IntBuffer, VkQueueFamilyProperties.Buffer?) -> Unit): VkQueueFamilyProperties.Buffer =
    enumerateBuffer(VkQueueFamilyProperties::malloc, block)

inline fun MemoryStack.enumerateLongs(block: (IntBuffer, LongBuffer?) -> Unit): LongBuffer =
    enumerateBuffer(this::mallocLong, block)

inline fun MemoryStack.enumeratePointers(block: (IntBuffer, PointerBuffer?) -> Unit): PointerBuffer =
    enumerateBuffer(this::mallocPointer, block)
