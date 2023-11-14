package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.vulkan.KHRMaintenance1.VK_KHR_MAINTENANCE1_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME

class VkSetup {

    var isValidating = false
    var validationLayers = listOf("VK_LAYER_KHRONOS_validation")

    var deviceExtensions = listOf(VK_KHR_SWAPCHAIN_EXTENSION_NAME, VK_KHR_MAINTENANCE1_EXTENSION_NAME)

    fun selectPhysicalDevice(devices: List<PhysicalDevice.PhysicalDevice>): PhysicalDevice.PhysicalDevice {
        return devices[0]
    }

}