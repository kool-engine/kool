package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkDevice
import org.lwjgl.vulkan.VkQueue

class LogicalDevice(val backend: VkRenderBackend) : VkResource() {

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
                if (physicalDevice.vkDeviceFeatures.samplerAnisotropy()) {
                    samplerAnisotropy(true)
                }
            }

            val createInfo = callocVkDeviceCreateInfo {
                pQueueCreateInfos(queueCreateInfo)
                pEnabledFeatures(features)

                val extNames = mallocPointer(backend.setup.enabledDeviceExtensions.size)
                backend.setup.enabledDeviceExtensions.forEachIndexed { i, name -> extNames.put(i, ASCII(name)) }
                ppEnabledExtensionNames(extNames)
            }

            val pp = mallocPointer(1)
            checkVk(vkCreateDevice(physicalDevice.vkPhysicalDevice, createInfo, null, pp))
            vkDevice = VkDevice(pp[0], physicalDevice.vkPhysicalDevice, createInfo)

            vkGetDeviceQueue(vkDevice, physicalDevice.queueFamiliyIndices.graphicsFamily!!, 0, pp)
            graphicsQueue = VkQueue(pp[0], vkDevice)
            vkGetDeviceQueue(vkDevice, physicalDevice.queueFamiliyIndices.presentFamily!!, 0, pp)
            presentQueue = VkQueue(pp[0], vkDevice)

            transferQueue = physicalDevice.queueFamiliyIndices.transferFamily?.let {
                vkGetDeviceQueue(vkDevice, it, 0, pp)
                VkQueue(pp[0], vkDevice)
            } ?: graphicsQueue

            computeQueue = physicalDevice.queueFamiliyIndices.computeFamily?.let {
                vkGetDeviceQueue(vkDevice, it, 0, pp)
                VkQueue(pp[0], vkDevice)
            }
        }

        backend.instance.addDependingResource(this)
        logD { "Created logical device" }
    }

    override fun freeResources() {
        vkDestroyDevice(vkDevice, null)
        logD { "Destroyed logical device" }
    }
}