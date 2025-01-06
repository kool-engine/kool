package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.vulkan.EXTDebugUtils
import org.lwjgl.vulkan.KHRMaintenance1.VK_KHR_MAINTENANCE1_EXTENSION_NAME
import org.lwjgl.vulkan.KHRPortabilityEnumeration
import org.lwjgl.vulkan.KHRPortabilitySubset
import org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME
import org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1

class VkSetup {

    var vkApiVersion = VK_API_VERSION_1_1

    val enabledLayers = mutableSetOf<String>()
    val enabledInstanceExtensions = mutableSetOf<String>()
    val enabledDeviceExtensions = mutableSetOf(
        VK_KHR_SWAPCHAIN_EXTENSION_NAME,
        VK_KHR_MAINTENANCE1_EXTENSION_NAME
    )

    var isValidation = false
        set(value) {
            field = value
            enabledLayers.enableOrDisable("VK_LAYER_KHRONOS_validation", value)
            enabledInstanceExtensions.enableOrDisable(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME, value)
        }

    var isPortability = false
        set(value) {
            field = value
            enabledInstanceExtensions.enableOrDisable(KHRPortabilityEnumeration.VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME, value)
            enabledDeviceExtensions.enableOrDisable(KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME, value)
        }

    fun selectPhysicalDevice(devices: List<PhysicalDevice.PhysicalDevice>): PhysicalDevice.PhysicalDevice {
        return devices[0]
    }

    private fun MutableSet<String>.enableOrDisable(name: String, flag: Boolean) {
        if (flag) {
            add(name)
        } else {
            remove(name)
        }
    }
}