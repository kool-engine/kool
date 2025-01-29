package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.vulkan.EXTDebugUtils
import org.lwjgl.vulkan.KHRPortabilityEnumeration.VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME
import org.lwjgl.vulkan.KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME
import org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1
import org.lwjgl.vulkan.VK12.VK_API_VERSION_1_2

class VkSetup(
    /**
     * Required Vulkan version. Defaults to Vulkan 1.2
     */
    val vkApiVersion: Int = VK_API_VERSION_1_2,

    /**
     * Enables portability feature, which is required for running Vulkan with MoltenVK
     * on Metal / macOS.
     */
    val isPortability: Boolean = true,

    /**
     * Enables the Vulkan validation layer.
     */
    val isValidation: Boolean = false,

    val preferredColorSpace: ColorSpace = ColorSpace.sRGB
) {
    val requestedLayers = mutableSetOf<RequestedFeature>()

    val requestedInstanceExtensions = mutableSetOf<RequestedFeature>()
    val requestedDeviceExtensions = mutableSetOf(deviceExtensionSwapchain)

    init {
        requestedLayers.addOrRemove(RequestedFeature("VK_LAYER_KHRONOS_validation", false), isValidation)
        requestedInstanceExtensions.addOrRemove(
            RequestedFeature(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME, false),
            isValidation
        )

        requestedInstanceExtensions.addOrRemove(instanceExtensionPortability, isPortability)
        requestedDeviceExtensions.addOrRemove(deviceExtensionPortability, isPortability)

        requestedDeviceExtensions += vmaHelperExtensions
        if (vkApiVersion < VK_API_VERSION_1_2) {
            requestedDeviceExtensions.add(vmaHelperBufferDeviceAddress)
        }
        if (vkApiVersion < VK_API_VERSION_1_1) {
            requestedDeviceExtensions.add(vmaHelperDedicatedAllocation)
            requestedDeviceExtensions.add(vmaHelperBindMemory2)
        }
        if (preferredColorSpace != ColorSpace.sRGB) {
            requestedInstanceExtensions.add(RequestedFeature("VK_EXT_swapchain_colorspace", false))
        }
    }

    private fun MutableSet<RequestedFeature>.addOrRemove(feature: RequestedFeature, flag: Boolean) {
        if (flag) {
            add(feature)
        } else {
            remove(feature)
        }
    }

    data class RequestedFeature(val name: String, val isRequired: Boolean)

    companion object {
        val instanceExtensionPortability = RequestedFeature(VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME, false)
        val deviceExtensionPortability = RequestedFeature(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME, false)
        val deviceExtensionSwapchain = RequestedFeature(VK_KHR_SWAPCHAIN_EXTENSION_NAME, true)

        val vmaHelperBufferDeviceAddress = RequestedFeature("VK_KHR_buffer_device_address", false)
        val vmaHelperDedicatedAllocation = RequestedFeature("VK_KHR_dedicated_allocation", false)
        val vmaHelperBindMemory2 = RequestedFeature("VK_KHR_bind_memory2", false)
        val vmaHelperExtensions = listOf(
            RequestedFeature("VK_KHR_maintenance4", false),
            RequestedFeature("VK_KHR_maintenance5", false),
            RequestedFeature("VK_EXT_memory_budget", false),
            RequestedFeature("VK_EXT_memory_priority", false),
            RequestedFeature("VK_AMD_device_coherent_memory", false),
            RequestedFeature("VK_KHR_external_memory_win32", false),
        )
    }
}

enum class ColorSpace {
    sRGB,
    AdobeRGB,
    DCI_P3,
}