package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR
import org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR
import org.lwjgl.vulkan.VK10.*

class Device(val backend: RenderBackendVk) : BaseReleasable() {

    private val physicalDevice: PhysicalDevice get() = backend.physicalDevice

    val vkDevice: VkDevice
    val graphicsQueue: VkQueue
    val presentQueue: VkQueue
    val transferQueue: VkQueue
    val computeQueue: VkQueue?

    val graphicsQueueProperties: VkQueueFamilyProperties
    val presentQueueProperties: VkQueueFamilyProperties
    val transferQueueProperties: VkQueueFamilyProperties
    val computeQueueProperties: VkQueueFamilyProperties?

    init {
        memStack {
            val uniqueFamilies = physicalDevice.queueFamiliyIndices.uniqueFamilies
            val queueCreateInfo = callocVkDeviceQueueCreateInfoN(uniqueFamilies.size) {
                uniqueFamilies.forEachIndexed { i, famIdx ->
                    this[i].apply {
                        queueFamilyIndex(famIdx)
                        pQueuePriorities(floats(1f))
                    }
                }
            }

            val features = callocVkPhysicalDeviceFeatures {
                samplerAnisotropy(physicalDevice.deviceFeatures.samplerAnisotropy())
            }

            val enableExtensions = backend.setup.requestedDeviceExtensions.filter { it.name in physicalDevice.availableDeviceExtensions }
            val extNames = mallocPointer(enableExtensions.size)
            logD("Device") { "Enabling device extensions:" }
            enableExtensions.forEachIndexed { i, extension ->
                logD("Device") { "  ${extension.name}" }
                extNames.put(i, ASCII(extension.name))
            }

            vkDevice = physicalDevice.createDevice {
                pQueueCreateInfos(queueCreateInfo)
                pEnabledFeatures(features)
                ppEnabledExtensionNames(extNames)
                if (physicalDevice.isPortabilityDevice) {
                    // this enables all available portability features for the device, e.g.
                    // mutableComparisonSamplers which is needed to create samplers for shadow maps
                    pNext(physicalDevice.portabilityFeatures)
                }
            }

            val graphicsQueueIdx = physicalDevice.queueFamiliyIndices.graphicsFamily!!
            val presentQueueIdx = physicalDevice.queueFamiliyIndices.presentFamily!!
            val transferQueueIdx = physicalDevice.queueFamiliyIndices.transferFamily ?: graphicsQueueIdx
            val computeQueueIdx = physicalDevice.queueFamiliyIndices.computeFamily

            graphicsQueue = getQueue(graphicsQueueIdx, 0)
            presentQueue = getQueue(presentQueueIdx, 0)
            transferQueue = getQueue(transferQueueIdx, 0)
            computeQueue = computeQueueIdx?.let { getQueue(it, 0) }

            val queueProperties = enumerateQueueProperties { cnt, buf ->
                vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice.vkPhysicalDevice, cnt, buf)
            }
            graphicsQueueProperties = queueProperties[graphicsQueueIdx]
            presentQueueProperties = queueProperties[presentQueueIdx]
            transferQueueProperties = queueProperties[transferQueueIdx]
            computeQueueProperties = computeQueueIdx?.let { queueProperties[it] }
        }

        releaseWith(backend.instance)
        logD { "Created logical device" }
    }

    fun waitForIdle() {
        vkDeviceWaitIdle(vkDevice)
    }

    override fun release() {
        super.release()
        DeferredRelease.defer {
            vkDestroyDevice(vkDevice, null)
            logD { "Destroyed logical device" }
        }
    }

    private fun MemoryStack.getQueue(queueFamily: Int, queueIndex: Int): VkQueue {
        val ptr = mallocPointer(1)
        vkGetDeviceQueue(vkDevice, queueFamily, queueIndex, ptr)
        return VkQueue(ptr[0], vkDevice)
    }
}

internal inline fun Device.allocateDescriptorSets(stack: MemoryStack? = null, block: VkDescriptorSetAllocateInfo.() -> Unit): List<VkDescriptorSet> {
    logT { "allocate descriptor sets" }
    memStack(stack) {
        val allocInfo = callocVkDescriptorSetAllocateInfo(block)
        val handles = mallocLong(allocInfo.descriptorSetCount())
        checkVk(vkAllocateDescriptorSets(vkDevice, allocInfo, handles)) { "Failed allocating descriptor sets: $it" }
        return buildList { repeat(allocInfo.descriptorSetCount()) { add(VkDescriptorSet(handles[it])) } }
    }
}

internal inline fun Device.createCommandPool(stack: MemoryStack? = null, block: VkCommandPoolCreateInfo.() -> Unit): VkCommandPool {
    logT { "create command pool" }
    memStack(stack) {
        val createInfo = callocVkCommandPoolCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateCommandPool(vkDevice, createInfo, null, handle)) { "Failed creating command pool: $it" }
        return VkCommandPool(handle[0])
    }
}

internal inline fun Device.createDescriptorPool(stack: MemoryStack? = null, block: VkDescriptorPoolCreateInfo.() -> Unit): VkDescriptorPool {
    logT { "create descriptor pool" }
    memStack(stack) {
        val createInfo = callocVkDescriptorPoolCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateDescriptorPool(vkDevice, createInfo, null, handle)) { "Failed creating descriptor pool: $it" }
        return VkDescriptorPool(handle[0])
    }
}

internal inline fun Device.createDescriptorSetLayout(stack: MemoryStack? = null, block: VkDescriptorSetLayoutCreateInfo.() -> Unit): VkDescriptorSetLayout {
    logT { "create descriptor set layout" }
    memStack(stack) {
        val createInfo = callocVkDescriptorSetLayoutCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateDescriptorSetLayout(vkDevice, createInfo, null, handle)) { "Failed creating descriptor set layout: $it" }
        return VkDescriptorSetLayout(handle[0])
    }
}

internal inline fun Device.createFramebuffer(stack: MemoryStack? = null, block: VkFramebufferCreateInfo.() -> Unit): VkFramebuffer {
    logT { "create framebuffer" }
    memStack(stack) {
        val createInfo = callocVkFramebufferCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateFramebuffer(vkDevice, createInfo, null, handle)) { "Failed creating framebuffer: $it" }
        return VkFramebuffer(handle[0])
    }
}

internal inline fun Device.createFence(stack: MemoryStack? = null, block: VkFenceCreateInfo.() -> Unit): VkFence {
    logT { "create fence" }
    memStack(stack) {
        val createInfo = callocVkFenceCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateFence(vkDevice, createInfo, null, handle)) { "Failed creating fence: $it" }
        return VkFence(handle[0])
    }
}

internal fun Device.createGraphicsPipeline(stack: MemoryStack? = null, block: VkGraphicsPipelineCreateInfo.() -> Unit): VkGraphicsPipeline {
    logT { "create graphics pipeline" }
    memStack(stack) {
        val createInfo = callocVkGraphicsPipelineCreateInfoN(1) {
            this[0].block()
        }
        val handle = mallocLong(1)
        checkVk(vkCreateGraphicsPipelines(vkDevice, VK_NULL_HANDLE, createInfo, null, handle)) { "Failed creating graphicsPipeline: $it" }
        return VkGraphicsPipeline(handle[0])
    }
}

internal fun Device.createImageView(
    image: VkImage,
    viewType: Int,
    format: Int,
    aspectMask: Int,
    levelCount: Int,
    baseMipLevel: Int = 0,
    layerCount: Int = 1,
    baseArrayLayer: Int = 0,
    stack: MemoryStack? = null
): VkImageView {
    return createImageView(stack) {
        image(image.handle)
        viewType(viewType)
        format(format)
        subresourceRange {
            it.aspectMask(aspectMask)
            it.baseMipLevel(baseMipLevel)
            it.levelCount(levelCount)
            it.baseArrayLayer(baseArrayLayer)
            it.layerCount(layerCount)
        }
    }
}

internal inline fun Device.createImageView(stack: MemoryStack? = null, block: VkImageViewCreateInfo.() -> Unit): VkImageView {
    logT { "create image view" }
    memStack(stack) {
        val createInfo = callocVkImageViewCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateImageView(vkDevice, createInfo, null, handle)) { "Failed creating image view: $it" }
        return VkImageView(handle[0])
    }
}

internal fun Device.createPipelineLayout(stack: MemoryStack? = null, block: VkPipelineLayoutCreateInfo.() -> Unit): VkPipelineLayout {
    logT { "create pipeline layout" }
    memStack(stack) {
        val createInfo = callocVkPipelineLayoutCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreatePipelineLayout(vkDevice, createInfo, null, handle)) { "Failed creating pipeline layout: $it" }
        return VkPipelineLayout(handle[0])
    }
}

internal fun Device.createQueryPool(stack: MemoryStack? = null, block: VkQueryPoolCreateInfo.() -> Unit): VkQueryPool {
    logT { "create query pool" }
    memStack(stack) {
        val createInfo = callocVkQueryPoolCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateQueryPool(vkDevice, createInfo, null, handle)) { "Failed creating query pool: $it" }
        return VkQueryPool(handle[0])
    }
}

internal fun Device.createRenderPass(stack: MemoryStack? = null, block: VkRenderPassCreateInfo.() -> Unit): VkRenderPass {
    logT { "create render pass" }
    memStack(stack) {
        val createInfo = callocVkRenderPassCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateRenderPass(vkDevice, createInfo, null, handle)) { "Failed creating render pass: $it" }
        return VkRenderPass(handle[0])
    }
}

internal fun Device.createSampler(stack: MemoryStack? = null, block: VkSamplerCreateInfo.() -> Unit): VkSampler {
    logT { "create sampler" }
    memStack(stack) {
        val createInfo = callocVkSamplerCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateSampler(vkDevice, createInfo, null, handle)) { "Failed creating sampler: $it" }
        return VkSampler(handle[0])
    }
}

internal fun Device.createSemaphore(stack: MemoryStack? = null): VkSemaphore {
    logT { "create semaphore" }
    memStack(stack) {
        val createInfo = callocVkSemaphoreCreateInfo { }
        val handle = mallocLong(1)
        checkVk(vkCreateSemaphore(vkDevice, createInfo, null, handle)) { "Failed creating semaphore: $it" }
        return VkSemaphore(handle[0])
    }
}

internal fun Device.createShaderModule(stack: MemoryStack? = null, block: VkShaderModuleCreateInfo.() -> Unit): VkShaderModule {
    logT { "create shader module" }
    memStack(stack) {
        val createInfo = callocVkShaderModuleCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateShaderModule(vkDevice, createInfo, null, handle)) { "Failed creating shader module: $it" }
        return VkShaderModule(handle[0])
    }
}

internal fun Device.createSwapchain(stack: MemoryStack? = null, block: VkSwapchainCreateInfoKHR.() -> Unit): VkSwapchain {
    logT { "create swapchain" }
    memStack(stack) {
        val createInfo = callocVkSwapchainCreateInfoKHR(block)
        val handle = mallocLong(1)
        checkVk(vkCreateSwapchainKHR(vkDevice, createInfo, null, handle)) { "Failed creating swapchain: $it" }
        return VkSwapchain(handle[0])
    }
}

internal fun Device.destroyCommandPool(commandPool: VkCommandPool) {
    DeferredRelease.defer { vkDestroyCommandPool(vkDevice, commandPool.handle, null) }
}

internal fun Device.destroyDescriptorPool(descriptorPool: VkDescriptorPool) {
    DeferredRelease.defer { vkDestroyDescriptorPool(vkDevice, descriptorPool.handle, null) }
}

internal fun Device.destroyDescriptorSetLayout(descriptorSetLayout: VkDescriptorSetLayout) {
    DeferredRelease.defer { vkDestroyDescriptorSetLayout(vkDevice, descriptorSetLayout.handle, null) }
}

internal fun Device.destroyFramebuffer(framebuffer: VkFramebuffer, deferTicks: Int = 1) {
    DeferredRelease.defer(deferTicks) { vkDestroyFramebuffer(vkDevice, framebuffer.handle, null) }
}

internal fun Device.destroyFence(fence: VkFence) {
    DeferredRelease.defer { vkDestroyFence(vkDevice, fence.handle, null) }
}

internal fun Device.destroyGraphicsPipeline(graphicsPipeline: VkGraphicsPipeline) {
    DeferredRelease.defer { vkDestroyPipeline(vkDevice, graphicsPipeline.handle, null) }
}

internal fun Device.destroyImageView(imageView: VkImageView, deferTicks: Int = 1) {
    DeferredRelease.defer(deferTicks) { vkDestroyImageView(vkDevice, imageView.handle, null) }
}

internal fun Device.destroyPipelineLayout(pipelineLayout: VkPipelineLayout) {
    DeferredRelease.defer { vkDestroyPipelineLayout(vkDevice, pipelineLayout.handle, null) }
}

internal fun Device.destroyQueryPool(queryPool: VkQueryPool) {
    DeferredRelease.defer { vkDestroyQueryPool(vkDevice, queryPool.handle, null) }
}

internal fun Device.destroyRenderPass(renderPass: VkRenderPass) {
    DeferredRelease.defer { vkDestroyRenderPass(vkDevice, renderPass.handle, null) }
}

internal fun Device.destroyShaderModule(shaderModule: VkShaderModule) {
    DeferredRelease.defer { vkDestroyShaderModule(vkDevice, shaderModule.handle, null) }
}

internal fun Device.destroySampler(sampler: VkSampler) {
    DeferredRelease.defer { vkDestroySampler(vkDevice, sampler.handle, null) }
}

internal fun Device.destroySemaphore(semaphore: VkSemaphore) {
    DeferredRelease.defer { vkDestroySemaphore(vkDevice, semaphore.handle, null) }
}

internal fun Device.destroySwapchain(swapchain: VkSwapchain) {
    DeferredRelease.defer { vkDestroySwapchainKHR(vkDevice, swapchain.handle, null) }
}

internal fun Device.resetCommandPool(commandPool: VkCommandPool, flags: Int = 0) {
    vkResetCommandPool(vkDevice, commandPool.handle, flags)
}
