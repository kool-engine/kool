package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.vulkan.AMDDeviceCoherentMemory.VK_AMD_DEVICE_COHERENT_MEMORY_EXTENSION_NAME
import org.lwjgl.vulkan.EXTDebugUtils
import org.lwjgl.vulkan.EXTMemoryBudget.VK_EXT_MEMORY_BUDGET_EXTENSION_NAME
import org.lwjgl.vulkan.EXTMemoryPriority.VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME
import org.lwjgl.vulkan.KHRBindMemory2.VK_KHR_BIND_MEMORY_2_EXTENSION_NAME
import org.lwjgl.vulkan.KHRBufferDeviceAddress.VK_KHR_BUFFER_DEVICE_ADDRESS_EXTENSION_NAME
import org.lwjgl.vulkan.KHRCopyCommands2.VK_KHR_COPY_COMMANDS_2_EXTENSION_NAME
import org.lwjgl.vulkan.KHRDedicatedAllocation.VK_KHR_DEDICATED_ALLOCATION_EXTENSION_NAME
import org.lwjgl.vulkan.KHRDynamicRendering.VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME
import org.lwjgl.vulkan.KHRExternalMemoryWin32.VK_KHR_EXTERNAL_MEMORY_WIN32_EXTENSION_NAME
import org.lwjgl.vulkan.KHRMaintenance4.VK_KHR_MAINTENANCE_4_EXTENSION_NAME
import org.lwjgl.vulkan.KHRMaintenance5.VK_KHR_MAINTENANCE_5_EXTENSION_NAME
import org.lwjgl.vulkan.KHRPortabilityEnumeration.VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME
import org.lwjgl.vulkan.KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSynchronization2.VK_KHR_SYNCHRONIZATION_2_EXTENSION_NAME
import org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1
import org.lwjgl.vulkan.VK12.VK_API_VERSION_1_2
import org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3

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

    val preferredColorSpace: ColorSpace = ColorSpace.sRGB,

    val forceDeviceName: String? = null,
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
        if (vkApiVersion < VK_API_VERSION_1_3) {
            requestedDeviceExtensions.add(dynamicRendering)
            requestedDeviceExtensions.add(synchronization2)
            requestedDeviceExtensions.add(copyCommands2)
        }
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

        val dynamicRendering = RequestedFeature(VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME, true)
        val synchronization2 = RequestedFeature(VK_KHR_SYNCHRONIZATION_2_EXTENSION_NAME, true)
        val copyCommands2 = RequestedFeature(VK_KHR_COPY_COMMANDS_2_EXTENSION_NAME, true)

        val vmaHelperBufferDeviceAddress = RequestedFeature(VK_KHR_BUFFER_DEVICE_ADDRESS_EXTENSION_NAME, false)
        val vmaHelperDedicatedAllocation = RequestedFeature(VK_KHR_DEDICATED_ALLOCATION_EXTENSION_NAME, false)
        val vmaHelperBindMemory2 = RequestedFeature(VK_KHR_BIND_MEMORY_2_EXTENSION_NAME, false)
        val vmaHelperExtensions = listOf(
            RequestedFeature(VK_KHR_MAINTENANCE_4_EXTENSION_NAME, false),
            RequestedFeature(VK_KHR_MAINTENANCE_5_EXTENSION_NAME, false),
            RequestedFeature(VK_EXT_MEMORY_BUDGET_EXTENSION_NAME, false),
            RequestedFeature(VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME, false),
            RequestedFeature(VK_AMD_DEVICE_COHERENT_MEMORY_EXTENSION_NAME, false),
            RequestedFeature(VK_KHR_EXTERNAL_MEMORY_WIN32_EXTENSION_NAME, false),
        )
    }
}

enum class ColorSpace {
    sRGB,
    AdobeRGB,
    DCI_P3,
}