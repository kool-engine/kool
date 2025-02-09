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

            val enableExtensions = backend.setup.requestedDeviceExtensions.filter { it.name in physicalDevice.availableDeviceExtensions }
            val extNames = mallocPointer(enableExtensions.size)
            logD("Device") { "Enabling device extensions:" }
            enableExtensions.forEachIndexed { i, extension ->
                logD("Device") { "  ${extension.name}" }
                extNames.put(i, ASCII(extension.name))
            }

            val features = callocVkPhysicalDeviceFeatures {
                samplerAnisotropy(physicalDevice.deviceFeatures.samplerAnisotropy())
                imageCubeArray(physicalDevice.cubeMapArrays)
                wideLines(physicalDevice.wideLines)
            }
            val dynamicRenderingFeatures = VkPhysicalDeviceDynamicRenderingFeatures.calloc(this).apply {
                `sType$Default`()
                dynamicRendering(true)
            }
            val synchronization2Features = VkPhysicalDeviceSynchronization2Features.calloc(this).apply {
                `sType$Default`()
                synchronization2(true)
            }

            vkDevice = physicalDevice.createDevice {
                pQueueCreateInfos(queueCreateInfo)
                ppEnabledExtensionNames(extNames)

                pEnabledFeatures(features)
                pNext(dynamicRenderingFeatures)
                pNext(synchronization2Features)
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
        ReleaseQueue.enqueue {
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

internal inline fun Device.allocateDescriptorSets(stack: MemoryStack? = null, block: VkDescriptorSetAllocateInfo.() -> Unit): List<VkDescriptorSet>? {
    logT { "allocate descriptor sets" }
    memStack(stack) {
        val allocInfo = callocVkDescriptorSetAllocateInfo(block)
        val handles = mallocLong(allocInfo.descriptorSetCount())
        val result = vkAllocateDescriptorSets(vkDevice, allocInfo, handles)
        return if (result != VK_SUCCESS) null else {
            buildList { repeat(allocInfo.descriptorSetCount()) { add(VkDescriptorSet(handles[it])) } }
        }
    }
}

internal inline fun Device.createCommandPool(stack: MemoryStack? = null, block: VkCommandPoolCreateInfo.() -> Unit): VkCommandPool {
    logT { "create command pool" }
    memStack(stack) {
        val createInfo = callocVkCommandPoolCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateCommandPool(vkDevice, createInfo, null, handle)) { "Failed creating command pool: $it" }
        return VkCommandPool(handle[0])
    }
}

internal fun Device.createComputePipeline(stack: MemoryStack? = null, block: VkComputePipelineCreateInfo.() -> Unit): VkComputePipeline {
    logT { "create compute pipeline" }
    memStack(stack) {
        val createInfo = callocVkComputePipelineCreateInfoN(1) {
            this[0].block()
        }
        val handle = mallocLong(1)
        vkCheck(vkCreateComputePipelines(vkDevice, VK_NULL_HANDLE, createInfo, null, handle)) { "Failed creating compute pipeline: $it" }
        return VkComputePipeline(handle[0])
    }
}

internal inline fun Device.createDescriptorPool(stack: MemoryStack? = null, block: VkDescriptorPoolCreateInfo.() -> Unit): VkDescriptorPool {
    logT { "create descriptor pool" }
    memStack(stack) {
        val createInfo = callocVkDescriptorPoolCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateDescriptorPool(vkDevice, createInfo, null, handle)) { "Failed creating descriptor pool: $it" }
        return VkDescriptorPool(handle[0])
    }
}

internal inline fun Device.createDescriptorSetLayout(stack: MemoryStack? = null, block: VkDescriptorSetLayoutCreateInfo.() -> Unit): VkDescriptorSetLayout {
    logT { "create descriptor set layout" }
    memStack(stack) {
        val createInfo = callocVkDescriptorSetLayoutCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateDescriptorSetLayout(vkDevice, createInfo, null, handle)) { "Failed creating descriptor set layout: $it" }
        return VkDescriptorSetLayout(handle[0])
    }
}

internal inline fun Device.createFramebuffer(stack: MemoryStack? = null, block: VkFramebufferCreateInfo.() -> Unit): VkFramebuffer {
    logT { "create framebuffer" }
    memStack(stack) {
        val createInfo = callocVkFramebufferCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateFramebuffer(vkDevice, createInfo, null, handle)) { "Failed creating framebuffer: $it" }
        return VkFramebuffer(handle[0])
    }
}

internal inline fun Device.createFence(stack: MemoryStack? = null, block: VkFenceCreateInfo.() -> Unit): VkFence {
    logT { "create fence" }
    memStack(stack) {
        val createInfo = callocVkFenceCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateFence(vkDevice, createInfo, null, handle)) { "Failed creating fence: $it" }
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
        vkCheck(vkCreateGraphicsPipelines(vkDevice, VK_NULL_HANDLE, createInfo, null, handle)) { "Failed creating graphics pipeline: $it" }
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
        vkCheck(vkCreateImageView(vkDevice, createInfo, null, handle)) { "Failed creating image view: $it" }
        return VkImageView(handle[0])
    }
}

internal fun Device.createPipelineLayout(stack: MemoryStack? = null, block: VkPipelineLayoutCreateInfo.() -> Unit): VkPipelineLayout {
    logT { "create pipeline layout" }
    memStack(stack) {
        val createInfo = callocVkPipelineLayoutCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreatePipelineLayout(vkDevice, createInfo, null, handle)) { "Failed creating pipeline layout: $it" }
        return VkPipelineLayout(handle[0])
    }
}

internal fun Device.createQueryPool(stack: MemoryStack? = null, block: VkQueryPoolCreateInfo.() -> Unit): VkQueryPool {
    logT { "create query pool" }
    memStack(stack) {
        val createInfo = callocVkQueryPoolCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateQueryPool(vkDevice, createInfo, null, handle)) { "Failed creating query pool: $it" }
        return VkQueryPool(handle[0])
    }
}

internal fun Device.createRenderPass(stack: MemoryStack? = null, block: VkRenderPassCreateInfo.() -> Unit): VkRenderPass {
    logT { "create render pass" }
    memStack(stack) {
        val createInfo = callocVkRenderPassCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateRenderPass(vkDevice, createInfo, null, handle)) { "Failed creating render pass: $it" }
        return VkRenderPass(handle[0])
    }
}

internal fun Device.createSampler(stack: MemoryStack? = null, block: VkSamplerCreateInfo.() -> Unit): VkSampler {
    logT { "create sampler" }
    memStack(stack) {
        val createInfo = callocVkSamplerCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateSampler(vkDevice, createInfo, null, handle)) { "Failed creating sampler: $it" }
        return VkSampler(handle[0])
    }
}

internal fun Device.createSemaphore(stack: MemoryStack? = null): VkSemaphore {
    logT { "create semaphore" }
    memStack(stack) {
        val createInfo = callocVkSemaphoreCreateInfo { }
        val handle = mallocLong(1)
        vkCheck(vkCreateSemaphore(vkDevice, createInfo, null, handle)) { "Failed creating semaphore: $it" }
        return VkSemaphore(handle[0])
    }
}

internal fun Device.createShaderModule(stack: MemoryStack? = null, block: VkShaderModuleCreateInfo.() -> Unit): VkShaderModule {
    logT { "create shader module" }
    memStack(stack) {
        val createInfo = callocVkShaderModuleCreateInfo(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateShaderModule(vkDevice, createInfo, null, handle)) { "Failed creating shader module: $it" }
        return VkShaderModule(handle[0])
    }
}

internal fun Device.createSwapchain(stack: MemoryStack? = null, block: VkSwapchainCreateInfoKHR.() -> Unit): VkSwapchain {
    logT { "create swapchain" }
    memStack(stack) {
        val createInfo = callocVkSwapchainCreateInfoKHR(block)
        val handle = mallocLong(1)
        vkCheck(vkCreateSwapchainKHR(vkDevice, createInfo, null, handle)) { "Failed creating swapchain: $it" }
        return VkSwapchain(handle[0])
    }
}

internal fun Device.destroyCommandPool(commandPool: VkCommandPool) {
    ReleaseQueue.enqueue { vkDestroyCommandPool(vkDevice, commandPool.handle, null) }
}

internal fun Device.destroyComputePipeline(computePipeline: VkComputePipeline) {
    ReleaseQueue.enqueue { vkDestroyPipeline(vkDevice, computePipeline.handle, null) }
}

internal fun Device.destroyDescriptorPool(descriptorPool: VkDescriptorPool) {
    ReleaseQueue.enqueue { vkDestroyDescriptorPool(vkDevice, descriptorPool.handle, null) }
}

internal fun Device.destroyDescriptorSetLayout(descriptorSetLayout: VkDescriptorSetLayout) {
    ReleaseQueue.enqueue { vkDestroyDescriptorSetLayout(vkDevice, descriptorSetLayout.handle, null) }
}

internal fun Device.destroyFramebuffer(framebuffer: VkFramebuffer) {
    ReleaseQueue.enqueue { vkDestroyFramebuffer(vkDevice, framebuffer.handle, null) }
}

internal fun Device.destroyFence(fence: VkFence) {
    ReleaseQueue.enqueue { vkDestroyFence(vkDevice, fence.handle, null) }
}

internal fun Device.destroyGraphicsPipeline(graphicsPipeline: VkGraphicsPipeline) {
    ReleaseQueue.enqueue { vkDestroyPipeline(vkDevice, graphicsPipeline.handle, null) }
}

internal fun Device.destroyImageView(imageView: VkImageView) {
    ReleaseQueue.enqueue { vkDestroyImageView(vkDevice, imageView.handle, null) }
}

internal fun Device.destroyPipelineLayout(pipelineLayout: VkPipelineLayout) {
    ReleaseQueue.enqueue { vkDestroyPipelineLayout(vkDevice, pipelineLayout.handle, null) }
}

internal fun Device.destroyQueryPool(queryPool: VkQueryPool) {
    ReleaseQueue.enqueue { vkDestroyQueryPool(vkDevice, queryPool.handle, null) }
}

internal fun Device.destroyRenderPass(renderPass: VkRenderPass) {
    ReleaseQueue.enqueue { vkDestroyRenderPass(vkDevice, renderPass.handle, null) }
}

internal fun Device.destroyShaderModule(shaderModule: VkShaderModule) {
    ReleaseQueue.enqueue { vkDestroyShaderModule(vkDevice, shaderModule.handle, null) }
}

internal fun Device.destroySampler(sampler: VkSampler) {
    ReleaseQueue.enqueue { vkDestroySampler(vkDevice, sampler.handle, null) }
}

internal fun Device.destroySemaphore(semaphore: VkSemaphore) {
    ReleaseQueue.enqueue { vkDestroySemaphore(vkDevice, semaphore.handle, null) }
}

internal fun Device.destroySwapchain(swapchain: VkSwapchain) {
    vkDestroySwapchainKHR(vkDevice, swapchain.handle, null)
}

internal fun Device.resetCommandPool(commandPool: VkCommandPool, flags: Int = 0) {
    vkResetCommandPool(vkDevice, commandPool.handle, flags)
}

internal fun Device.resetDescriptorPool(descriptorPool: VkDescriptorPool) {
    vkResetDescriptorPool(vkDevice, descriptorPool.handle, 0)
}
