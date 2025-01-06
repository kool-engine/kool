package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkDevice
import org.lwjgl.vulkan.VkQueue

class Device(val sys: VkSystem) : VkResource() {

    val vkDevice: VkDevice
    val graphicsQueue: VkQueue
    val presentQueue: VkQueue
    val transferQueue: VkQueue

    init {
        memStack {
            val uniqueFamilies = sys.physicalDevice.queueFamiliyIndices.uniqueFamilies
            val queueCreateInfo = callocVkDeviceQueueCreateInfoN(uniqueFamilies.size) {
                uniqueFamilies.forEachIndexed { i, famIdx ->
                    this[i].apply {
                        queueFamilyIndex(famIdx)
                        pQueuePriorities(floats(1f))
                    }
                }
            }

            val features = callocVkPhysicalDeviceFeatures {
                samplerAnisotropy(true)
                //sampleRateShading(true)
            }

            val extNames = mallocPointer(sys.setup.enabledDeviceExtensions.size)
            sys.setup.enabledDeviceExtensions.forEachIndexed { i, name -> extNames.put(i, ASCII(name)) }

            val createInfo = callocVkDeviceCreateInfo {
                pQueueCreateInfos(queueCreateInfo)
                pEnabledFeatures(features)
                ppEnabledExtensionNames(extNames)
            }

            val pp = mallocPointer(1)
            checkVk(vkCreateDevice(sys.physicalDevice.vkPhysicalDevice, createInfo, null, pp))
            vkDevice = VkDevice(pp[0], sys.physicalDevice.vkPhysicalDevice, createInfo)

            vkGetDeviceQueue(vkDevice, sys.physicalDevice.queueFamiliyIndices.graphicsFamily!!, 0, pp)
            graphicsQueue = VkQueue(pp[0], vkDevice)
            vkGetDeviceQueue(vkDevice, sys.physicalDevice.queueFamiliyIndices.presentFamily!!, 0, pp)
            presentQueue = VkQueue(pp[0], vkDevice)

            transferQueue = if (sys.physicalDevice.queueFamiliyIndices.transferFamily != null) {
                vkGetDeviceQueue(vkDevice, sys.physicalDevice.queueFamiliyIndices.transferFamily, 0, pp)
                VkQueue(pp[0], vkDevice)
            } else {
                graphicsQueue
            }
        }

        sys.instance.addDependingResource(this)
        logD { "Created logical device" }
    }

    override fun freeResources() {
        vkDestroyDevice(vkDevice, null)
        logD { "Destroyed logical device" }
    }
}