package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR
import org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR
import org.lwjgl.vulkan.VK10.*

class LogicalDevice(val backend: RenderBackendVk) : VkResource() {

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

            vkDevice = physicalDevice.createLogicalDevice {
                pQueueCreateInfos(queueCreateInfo)
                pEnabledFeatures(features)

                val extNames = mallocPointer(backend.setup.enabledDeviceExtensions.size)
                backend.setup.enabledDeviceExtensions.forEachIndexed { i, name -> extNames.put(i, ASCII(name)) }
                ppEnabledExtensionNames(extNames)
            }

            graphicsQueue = getQueue(physicalDevice.queueFamiliyIndices.graphicsFamily!!, 0)
            presentQueue = getQueue(physicalDevice.queueFamiliyIndices.presentFamily!!, 0)
            transferQueue = physicalDevice.queueFamiliyIndices.transferFamily?.let { getQueue(it, 0) } ?: graphicsQueue
            computeQueue = physicalDevice.queueFamiliyIndices.computeFamily?.let { getQueue(it, 0) }
        }

        backend.instance.addDependingResource(this)
        logD { "Created logical device" }
    }

    override fun freeResources() {
        vkDestroyDevice(vkDevice, null)
        logD { "Destroyed logical device" }
    }

    private fun MemoryStack.getQueue(queueFamily: Int, queueIndex: Int): VkQueue {
        val ptr = mallocPointer(1)
        vkGetDeviceQueue(vkDevice, queueFamily, queueIndex, ptr)
        return VkQueue(ptr[0], vkDevice)
    }
}

inline fun LogicalDevice.createFramebuffer(stack: MemoryStack? = null, block: VkFramebufferCreateInfo.() -> Unit): VkFramebuffer {
    memStack(stack) {
        val createInfo = callocVkFramebufferCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateFramebuffer(vkDevice, createInfo, null, handle)) { "Failed creating framebuffer" }
        return VkFramebuffer(handle[0])
    }
}

inline fun LogicalDevice.createFence(stack: MemoryStack? = null, block: VkFenceCreateInfo.() -> Unit): VkFence {
    memStack(stack) {
        val createInfo = callocVkFenceCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateFence(vkDevice, createInfo, null, handle)) { "Failed creating fence" }
        return VkFence(handle[0])
    }
}

inline fun LogicalDevice.createImageView(stack: MemoryStack? = null, block: VkImageViewCreateInfo.() -> Unit): VkImageView {
    memStack(stack) {
        val createInfo = callocVkImageViewCreateInfo(block)
        val handle = mallocLong(1)
        checkVk(vkCreateImageView(vkDevice, createInfo, null, handle)) { "Failed creating image view" }
        return VkImageView(handle[0])
    }
}

fun LogicalDevice.createSemaphore(stack: MemoryStack? = null): VkSemaphore {
    memStack(stack) {
        val createInfo = callocVkSemaphoreCreateInfo { }
        val handle = mallocLong(1)
        checkVk(vkCreateSemaphore(vkDevice, createInfo, null, handle)) { "Failed creating semaphore" }
        return VkSemaphore(handle[0])
    }
}

fun LogicalDevice.createSwapchain(stack: MemoryStack? = null, block: VkSwapchainCreateInfoKHR.() -> Unit): VkSwapchain {
    memStack(stack) {
        val createInfo = callocVkSwapchainCreateInfoKHR(block)
        val handle = mallocLong(1)
        checkVk(vkCreateSwapchainKHR(vkDevice, createInfo, null, handle)) { "Failed creating swapchain" }
        return VkSwapchain(handle[0])
    }
}

fun LogicalDevice.destroyFramebuffer(framebuffer: VkFramebuffer) {
    vkDestroyFramebuffer(vkDevice, framebuffer.handle, null)
}

fun LogicalDevice.destroyFence(fence: VkFence) {
    vkDestroyFence(vkDevice, fence.handle, null)
}

fun LogicalDevice.destroyImageView(imageView: VkImageView) {
    vkDestroyImageView(vkDevice, imageView.handle, null)
}

fun LogicalDevice.destroySemaphore(semaphore: VkSemaphore) {
    vkDestroySemaphore(vkDevice, semaphore.handle, null)
}

fun LogicalDevice.destroySwapchain(swapchain: VkSwapchain) {
    vkDestroySwapchainKHR(vkDevice, swapchain.handle, null)
}
