package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.KHRSurface.*
import org.lwjgl.vulkan.VK10.*
import kotlin.math.min

class PhysicalDevice(val backend: VkRenderBackend) : VkResource() {

    val vkPhysicalDevice: VkPhysicalDevice
    val queueFamiliyIndices: QueueFamilyIndices
    val swapChainSupport: SwapChainSupportDetails
    val vkDeviceProperties = VkPhysicalDeviceProperties.calloc()
    val vkDeviceFeatures = VkPhysicalDeviceFeatures.calloc()
    val msaaSamples: Int

    val deviceName: String
    val apiVersion: String
    val driverVersion: String

    val depthFormat: Int

    init {
        memStack {
            val (cnt, devPtrs) = getPointers { cnt, ptrs -> vkEnumeratePhysicalDevices(backend.instance.vkInstance, cnt, ptrs) }
            val devices = (0 until cnt).map { PhysicalDeviceWrapper(devPtrs[it], this) }
            val selectedDevice = selectPhysicalDevice(devices)
            check(selectedDevice.queueFamiliyIndices.isComplete) {
                "Failed to find a suitable GPU"
            }

            val stackSwapChain = selectedDevice.querySwapChainSupport(this)
            swapChainSupport = selectedDevice.querySwapChainSupport(this,
                VkSurfaceCapabilitiesKHR.malloc(), VkSurfaceFormatKHR.malloc(stackSwapChain.formats.size)
            )

            vkPhysicalDevice = selectedDevice.device
            queueFamiliyIndices = selectedDevice.queueFamiliyIndices
            vkGetPhysicalDeviceProperties(vkPhysicalDevice, vkDeviceProperties)
            vkGetPhysicalDeviceFeatures(vkPhysicalDevice, vkDeviceFeatures)
            msaaSamples = getMaxUsableSampleCount()

            val api = vkDeviceProperties.apiVersion()
            val drv = vkDeviceProperties.driverVersion()
            apiVersion = "${VK_VERSION_MAJOR(api)}.${VK_VERSION_MINOR(api)}.${VK_VERSION_PATCH(api)}"
            driverVersion = "${VK_VERSION_MAJOR(drv)}.${VK_VERSION_MINOR(drv)}.${VK_VERSION_PATCH(drv)}"
            deviceName = vkDeviceProperties.deviceNameString()

            logI { "Selected GPU: $deviceName [api: $apiVersion, driver: $driverVersion]" }
            logD {
                "Using queue families: present: ${queueFamiliyIndices.presentFamily}, " +
                        "graphics: ${queueFamiliyIndices.graphicsFamily}, " +
                        "compute: ${queueFamiliyIndices.computeFamily}, " +
                        "transfer: ${queueFamiliyIndices.transferFamily}"
            }
        }

        depthFormat = findSupportedFormat(
            candidates = listOf(VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT, VK_FORMAT_D24_UNORM_S8_UINT),
            tiling = VK_IMAGE_TILING_OPTIMAL,
            features = VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT
        )

        backend.instance.addDependingResource(this)
    }

    private fun MemoryStack.selectPhysicalDevice(devices: List<PhysicalDeviceWrapper>): PhysicalDeviceWrapper {
        val suitableDevices = devices.filter {
            it.queueFamiliyIndices.isComplete &&
                    it.isSupportingExtensions(backend.setup.enabledDeviceExtensions) &&
                    it.querySwapChainSupport(this).isValid
        }
        check(suitableDevices.isNotEmpty()) {
            "No suitable Vulkan devices found! Available devices: ${devices.map { it.properties.deviceName() }}, none matched the required extensions and capabilities"
        }

        if (suitableDevices.size > 1) {
            logI { "Multiple Vulkan capable GPUS found:" }
            suitableDevices.forEach { dev -> logI { "  ${dev.properties.deviceNameString()}" } }
        }
        return suitableDevices.find { it.properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU } ?: devices.first()
    }

    private fun getMaxUsableSampleCount(): Int {
        return min(
            vkDeviceProperties.limits().framebufferColorSampleCounts(),
            vkDeviceProperties.limits().framebufferDepthSampleCounts()
        ).takeHighestOneBit().coerceAtLeast(VK_SAMPLE_COUNT_1_BIT)
    }

    private fun findSupportedFormat(candidates: List<Int>, tiling: Int, features: Int): Int {
        memStack {
            val props = VkFormatProperties.calloc(this)
            candidates.forEach { format ->
                vkGetPhysicalDeviceFormatProperties(vkPhysicalDevice, format, props)
                if (tiling == VK_IMAGE_TILING_LINEAR && (props.linearTilingFeatures() and features) == features) {
                    return format
                } else if (tiling == VK_IMAGE_TILING_OPTIMAL && (props.optimalTilingFeatures() and features) == features) {
                    return format
                }
            }
        }
        throw RuntimeException("Failed to find supported format")
    }

    fun findMemoryType(typeFiler: Int, properties: Int): Int {
        memStack {
            val memProperties = VkPhysicalDeviceMemoryProperties.malloc(this)
            vkGetPhysicalDeviceMemoryProperties(vkPhysicalDevice, memProperties)
            for (i in 0 until memProperties.memoryTypeCount()) {
                if (typeFiler and (1 shl i) != 0 && memProperties.memoryTypes(i).propertyFlags() and properties == properties) {
                    return i
                }
            }
        }
        throw RuntimeException("Failed to find suitable memory type")
    }

    override fun freeResources() {
        vkDeviceProperties.free()
        vkDeviceFeatures.free()
    }

    private inner class PhysicalDeviceWrapper(ptr: Long, stack: MemoryStack) {
        val device = VkPhysicalDevice(ptr, backend.instance.vkInstance)
        val queueFamiliyIndices: QueueFamilyIndices
        val properties: VkPhysicalDeviceProperties
        val features: VkPhysicalDeviceFeatures

        init {
            queueFamiliyIndices = findQueueFamilies()
            properties = VkPhysicalDeviceProperties.malloc(stack)
            features = VkPhysicalDeviceFeatures.malloc(stack)
            vkGetPhysicalDeviceProperties(device, properties)
            vkGetPhysicalDeviceFeatures(device, features)
        }

        private fun findQueueFamilies(): QueueFamilyIndices {
            memStack {
                val nFams = mallocInt(1)
                vkGetPhysicalDeviceQueueFamilyProperties(device, nFams, null)
                val queueFamilies = VkQueueFamilyProperties.malloc(nFams[0], this)
                vkGetPhysicalDeviceQueueFamilyProperties(device, nFams, queueFamilies)

                val ip = mallocInt(1)
                var presentFamily: Int? = null
                var graphicsFamily: Int? = null
                var computeFamily: Int? = null
                var transferFamily: Int? = null
                for (i in 0 until nFams[0]) {
                    val props = queueFamilies[i]
                    vkGetPhysicalDeviceSurfaceSupportKHR(device, i, backend.glfwWindow.surface.surfaceHandle, ip)
                    if (presentFamily == null && props.queueCount() > 0 && ip[0] != 0) {
                        presentFamily = i
                    }
                    if (graphicsFamily == null && props.queueCount() > 0 && props.queueFlags() and VK_QUEUE_GRAPHICS_BIT != 0) {
                        graphicsFamily = i
                    }
                    if (computeFamily == null && props.queueCount() > 0 && props.queueFlags() and VK_QUEUE_COMPUTE_BIT != 0) {
                        computeFamily = i
                    }
                    if (transferFamily == null && props.queueCount() > 0 && props.queueFlags() and VK_QUEUE_TRANSFER_BIT != 0) {
                        transferFamily = i
                    }
                }
                val indices = QueueFamilyIndices(graphicsFamily, computeFamily, presentFamily, transferFamily)
                return indices
            }
        }

        fun isSupportingExtensions(extensions: Set<String>): Boolean {
            memStack {
                val ip = mallocInt(1)
                checkVk(vkEnumerateDeviceExtensionProperties(device, null as String?, ip, null))
                val availableExtensions = VkExtensionProperties.malloc(ip[0], this)
                checkVk(vkEnumerateDeviceExtensionProperties(device, null as String?, ip, availableExtensions))

                val layerNames = availableExtensions.map { it.extensionNameString() }.toSet()
                val enableExtensions = extensions.toMutableSet().also { it.retainAll(layerNames) }
                val missingExtensions = extensions - enableExtensions
                if (missingExtensions.isNotEmpty()) {
                    logW { "Requested device extensions are not available:" }
                    missingExtensions.forEach { logW { "  $it" } }
                }
                return missingExtensions.isEmpty()
            }
        }

        fun querySwapChainSupport(
            stack: MemoryStack,
            caps: VkSurfaceCapabilitiesKHR? = null,
            fmts: VkSurfaceFormatKHR.Buffer? = null
        ): SwapChainSupportDetails {
            val ip = stack.mallocInt(1)
            val surface = backend.glfwWindow.surface.surfaceHandle

            val capabilities = caps ?: VkSurfaceCapabilitiesKHR.malloc(stack)
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, capabilities)

            val formatList = buildList {
                vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, ip, null)
                if (ip[0] > 0) {
                    val formats = fmts ?: VkSurfaceFormatKHR.malloc(ip[0], stack)
                    vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, ip, formats)
                    addAll(formats)
                }
            }

            val presentModeList = buildList {
                vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, ip, null)
                if (ip[0] > 0) {
                    val presentModes = stack.mallocInt(ip[0])
                    vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, ip, presentModes)
                    for (i in 0 until ip[0]) {
                        add(presentModes[i])
                    }
                }
            }
            return SwapChainSupportDetails(capabilities, formatList, presentModeList)
        }
    }

    class QueueFamilyIndices(
        val graphicsFamily: Int?,
        val computeFamily: Int?,
        val presentFamily: Int?,
        val transferFamily: Int?
    ) {
        val isComplete = graphicsFamily != null && presentFamily != null
        val uniqueFamilies: Set<Int> = buildSet {
            if (graphicsFamily != null) { add(graphicsFamily) }
            if (computeFamily != null) { add(computeFamily) }
            if (presentFamily != null) { add(presentFamily) }
            if (transferFamily != null) { add(transferFamily) }
        }
    }

    class SwapChainSupportDetails(
        val capabilities: VkSurfaceCapabilitiesKHR,
        val formats: List<VkSurfaceFormatKHR>,
        val presentModes: List<Int>
    ) {
        val isValid: Boolean get() = formats.isNotEmpty() && presentModes.isNotEmpty()

        fun chooseSurfaceFormat(): VkSurfaceFormatKHR {
            return formats
                .find {
                    it.format() == VK_FORMAT_B8G8R8A8_UNORM && it.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR
                } ?: formats[0]
        }

        fun choosePresentationMode(): Int {
            // VK_PRESENT_MODE_FIFO_KHR is guaranteed to be supported this also implies enabled V-Sync
            // other options which may not be supported by every device would be VK_PRESENT_MODE_MAILBOX_KHR
            // or VK_PRESENT_MODE_FIFO_RELAXED_KHR
            return VK_PRESENT_MODE_FIFO_KHR
        }

        fun chooseSwapExtent(window: GlfwVkWindow, stack: MemoryStack): VkExtent2D {
            return if (capabilities.currentExtent().width() != -1) {
                capabilities.currentExtent()
            } else {
                val minWidth = capabilities.minImageExtent().width()
                val maxWidth = capabilities.maxImageExtent().width()
                val minHeight = capabilities.minImageExtent().height()
                val maxHeight = capabilities.maxImageExtent().height()
                VkExtent2D.malloc(stack)
                    .width(window.framebufferWidth.clamp(minWidth, maxWidth))
                    .height(window.framebufferHeight.clamp(minHeight, maxHeight))
            }
        }
    }
}