package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import de.fabmax.kool.util.releaseWith
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
                samplerAnisotropy(physicalDevice.vkDeviceFeatures.samplerAnisotropy())
            }

            val enableExtensions = backend.setup.requestedDeviceExtensions.filter { it.name in physicalDevice.availableDeviceExtensions }
            val extNames = mallocPointer(enableExtensions.size)
            enableExtensions.forEachIndexed { i, ext -> extNames.put(i, ASCII(ext.name)) }

            vkDevice = physicalDevice.createDevice {
                pQueueCreateInfos(queueCreateInfo)
                pEnabledFeatures(features)
                ppEnabledExtensionNames(extNames)
            }

            graphicsQueue = getQueue(physicalDevice.queueFamiliyIndices.graphicsFamily!!, 0)
            presentQueue = getQueue(physicalDevice.queueFamiliyIndices.presentFamily!!, 0)
            transferQueue = physicalDevice.queueFamiliyIndices.transferFamily?.let { getQueue(it, 0) } ?: graphicsQueue
            computeQueue = physicalDevice.queueFamiliyIndices.computeFamily?.let { getQueue(it, 0) }
        }

        releaseWith(backend.instance)
        logD { "Created logical device" }
    }

    fun waitForIdle() {
        vkDeviceWaitIdle(vkDevice)
    }

    override fun release() {
        super.release()
        vkDestroyDevice(vkDevice, null)
        logD { "Destroyed logical device" }
    }

    private fun MemoryStack.getQueue(queueFamily: Int, queueIndex: Int): VkQueue {
        val ptr = mallocPointer(1)
        vkGetDeviceQueue(vkDevice, queueFamily, queueIndex, ptr)
        return VkQueue(ptr[0], vkDevice)
    }
}

internal inline fun Device.allocateDescriptorSets(stack: MemoryStack? = null, block: VkDescriptorSetAllocateInfo.() -> Unit): List<VkDescriptorSet> {
    memStack(stack) {
        val allocInfo = callocVkDescriptorSetAllocateInfo(block)
        val handles = mallocLong(allocInfo.descriptorSetCount())
        checkVk(vkAllocateDescriptorSets(vkDevice, allocInfo, handles)) { "Failed allocating descriptor sets: $it" }
        return buildList { repeat(allocInfo.descriptorSetCount()) { add(VkDescriptorSet(handles[it])) } }
    }
}

internal inline fun Device.createCommandPool(stack: MemoryStack? = null, block: VkCommandPoolCreateInfo.() -> Unit): VkCommandPool {
    memStack(stack) {
        val createInfo = callocVkCommandPoolCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateCommandPool(vkDevice, createInfo, null, handle)) { "Failed creating command pool: $it" }
        return VkCommandPool(handle[0])
    }
}

internal inline fun Device.createDescriptorPool(stack: MemoryStack? = null, block: VkDescriptorPoolCreateInfo.() -> Unit): VkDescriptorPool {
    memStack(stack) {
        val createInfo = callocVkDescriptorPoolCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateDescriptorPool(vkDevice, createInfo, null, handle)) { "Failed creating descriptor pool: $it" }
        return VkDescriptorPool(handle[0])
    }
}

internal inline fun Device.createDescriptorSetLayout(stack: MemoryStack? = null, block: VkDescriptorSetLayoutCreateInfo.() -> Unit): VkDescriptorSetLayout {
    memStack(stack) {
        val createInfo = callocVkDescriptorSetLayoutCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateDescriptorSetLayout(vkDevice, createInfo, null, handle)) { "Failed creating descriptor set layout: $it" }
        return VkDescriptorSetLayout(handle[0])
    }
}

internal inline fun Device.createFramebuffer(stack: MemoryStack? = null, block: VkFramebufferCreateInfo.() -> Unit): VkFramebuffer {
    memStack(stack) {
        val createInfo = callocVkFramebufferCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateFramebuffer(vkDevice, createInfo, null, handle)) { "Failed creating framebuffer: $it" }
        return VkFramebuffer(handle[0])
    }
}

internal inline fun Device.createFence(stack: MemoryStack? = null, block: VkFenceCreateInfo.() -> Unit): VkFence {
    memStack(stack) {
        val createInfo = callocVkFenceCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateFence(vkDevice, createInfo, null, handle)) { "Failed creating fence: $it" }
        return VkFence(handle[0])
    }
}

internal fun Device.createGraphicsPipeline(stack: MemoryStack? = null, block: VkGraphicsPipelineCreateInfo.() -> Unit): VkGraphicsPipeline {
    memStack(stack) {
        val createInfo = callocVkGraphicsPipelineCreateInfoN(1) {
            this[0].block()
        }
        val handle = mallocLong(1)
        checkVk(vkCreateGraphicsPipelines(vkDevice, VK_NULL_HANDLE, createInfo, null, handle)) { "Failed creating graphicsPipeline: $it" }
        return VkGraphicsPipeline(handle[0])
    }
}

internal inline fun Device.createImageView(stack: MemoryStack? = null, block: VkImageViewCreateInfo.() -> Unit): VkImageView {
    memStack(stack) {
        val createInfo = callocVkImageViewCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateImageView(vkDevice, createInfo, null, handle)) { "Failed creating image view: $it" }
        return VkImageView(handle[0])
    }
}

internal fun Device.createPipelineLayout(stack: MemoryStack? = null, block: VkPipelineLayoutCreateInfo.() -> Unit): VkPipelineLayout {
    memStack(stack) {
        val createInfo = callocVkPipelineLayoutCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreatePipelineLayout(vkDevice, createInfo, null, handle)) { "Failed creating pipeline layout: $it" }
        return VkPipelineLayout(handle[0])
    }
}

internal fun Device.createRenderPass(stack: MemoryStack? = null, block: VkRenderPassCreateInfo.() -> Unit): VkRenderPass {
    memStack(stack) {
        val createInfo = callocVkRenderPassCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateRenderPass(vkDevice, createInfo, null, handle)) { "Failed creating render pass: $it" }
        return VkRenderPass(handle[0])
    }
}

internal fun Device.createSampler(stack: MemoryStack? = null, block: VkSamplerCreateInfo.() -> Unit): VkSampler {
    memStack(stack) {
        val createInfo = callocVkSamplerCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateSampler(vkDevice, createInfo, null, handle)) { "Failed creating sampler: $it" }
        return VkSampler(handle[0])
    }
}

internal fun Device.createSemaphore(stack: MemoryStack? = null): VkSemaphore {
    memStack(stack) {
        val createInfo = callocVkSemaphoreCreateInfo { }
        val handle = mallocLong(1)
        checkVk(vkCreateSemaphore(vkDevice, createInfo, null, handle)) { "Failed creating semaphore: $it" }
        return VkSemaphore(handle[0])
    }
}

internal fun Device.createShaderModule(stack: MemoryStack? = null, block: VkShaderModuleCreateInfo.() -> Unit): VkShaderModule {
    memStack(stack) {
        val createInfo = callocVkShaderModuleCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateShaderModule(vkDevice, createInfo, null, handle)) { "Failed creating shader module: $it" }
        return VkShaderModule(handle[0])
    }
}

internal fun Device.createSwapchain(stack: MemoryStack? = null, block: VkSwapchainCreateInfoKHR.() -> Unit): VkSwapchain {
    memStack(stack) {
        val createInfo = callocVkSwapchainCreateInfoKHR(block)
        val handle = mallocLong(1)
        checkVk(vkCreateSwapchainKHR(vkDevice, createInfo, null, handle)) { "Failed creating swapchain: $it" }
        return VkSwapchain(handle[0])
    }
}

internal fun Device.destroyCommandPool(commandPool: VkCommandPool) {
    vkDestroyCommandPool(vkDevice, commandPool.handle, null)
}

internal fun Device.destroyDescriptorPool(descriptorPool: VkDescriptorPool) {
    vkDestroyDescriptorPool(vkDevice, descriptorPool.handle, null)
}

internal fun Device.destroyDescriptorSetLayout(descriptorSetLayout: VkDescriptorSetLayout) {
    vkDestroyDescriptorSetLayout(vkDevice, descriptorSetLayout.handle, null)
}

internal fun Device.destroyFramebuffer(framebuffer: VkFramebuffer) {
    vkDestroyFramebuffer(vkDevice, framebuffer.handle, null)
}

internal fun Device.destroyFence(fence: VkFence) {
    vkDestroyFence(vkDevice, fence.handle, null)
}

internal fun Device.destroyGraphicsPipeline(graphicsPipeline: VkGraphicsPipeline) {
    vkDestroyPipeline(vkDevice, graphicsPipeline.handle, null)
}

internal fun Device.destroyImageView(imageView: VkImageView) {
    vkDestroyImageView(vkDevice, imageView.handle, null)
}

internal fun Device.destroyPipelineLayout(pipelineLayout: VkPipelineLayout) {
    vkDestroyPipelineLayout(vkDevice, pipelineLayout.handle, null)
}

internal fun Device.destroyRenderPass(renderPass: VkRenderPass) {
    vkDestroyRenderPass(vkDevice, renderPass.handle, null)
}

internal fun Device.destroyShaderModule(shaderModule: VkShaderModule) {
    vkDestroyShaderModule(vkDevice, shaderModule.handle, null)
}

internal fun Device.destroySampler(sampler: VkSampler) {
    vkDestroySampler(vkDevice, sampler.handle, null)
}

internal fun Device.destroySemaphore(semaphore: VkSemaphore) {
    vkDestroySemaphore(vkDevice, semaphore.handle, null)
}

internal fun Device.destroySwapchain(swapchain: VkSwapchain) {
    vkDestroySwapchainKHR(vkDevice, swapchain.handle, null)
}

internal fun Device.resetCommandPool(commandPool: VkCommandPool, flags: Int = 0) {
    vkResetCommandPool(vkDevice, commandPool.handle, flags)
}
