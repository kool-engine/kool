package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.EXTSwapchainColorspace.VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT
import org.lwjgl.vulkan.EXTSwapchainColorspace.VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT
import org.lwjgl.vulkan.KHRSurface.*
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VK11.vkGetPhysicalDeviceFeatures2
import kotlin.math.min

class PhysicalDevice(val backend: RenderBackendVk) : BaseReleasable() {

    val vkPhysicalDevice: VkPhysicalDevice
    val queueFamiliyIndices: QueueFamilyIndices
    val swapChainSupport: SwapChainSupportDetails
    val deviceProperties = VkPhysicalDeviceProperties.calloc()
    val vkDeviceFeatures2 = VkPhysicalDeviceFeatures2.calloc()
    val deviceFeatures: VkPhysicalDeviceFeatures get() = vkDeviceFeatures2.features()
    val portabilityFeatures = VkPhysicalDevicePortabilitySubsetFeaturesKHR.calloc()
    val availableDeviceExtensions: Set<String>

    val isPortabilityDevice: Boolean
    val maxSamples: Int
    val maxAnisotropy: Float
    val wideLines: Boolean
    val cubeMapArrays: Boolean
    val minLineWidth: Float
    val maxLineWidth: Float

    val deviceName: String
    val apiVersion: String
    val driverVersion: String

    val depthFormat: Int

    private val imgFormatTilingFeatures = mutableMapOf<Int, Int>()

    init {
        memStack {
            val devPtrs = enumeratePointers { cnt, ptrs -> vkEnumeratePhysicalDevices(backend.instance.vkInstance, cnt, ptrs) }
            val devices = (0 until devPtrs.capacity()).map { PhysicalDeviceWrapper(devPtrs[it], this) }
            val selectedDevice = selectPhysicalDevice(devices)

            val stackSwapChain = selectedDevice.querySwapChainSupport(this)
            swapChainSupport = selectedDevice.querySwapChainSupport(this,
                VkSurfaceCapabilitiesKHR.malloc(), VkSurfaceFormatKHR.malloc(stackSwapChain.formats.size)
            )

            availableDeviceExtensions = selectedDevice.availableExtensions
            isPortabilityDevice = "VK_KHR_portability_subset" in availableDeviceExtensions
            vkPhysicalDevice = selectedDevice.physicalDevice
            queueFamiliyIndices = selectedDevice.queueFamiliyIndices
            vkGetPhysicalDeviceProperties(vkPhysicalDevice, deviceProperties)

            vkDeviceFeatures2.`sType$Default`()
            portabilityFeatures.`sType$Default`()
            if (isPortabilityDevice) {
                vkDeviceFeatures2.pNext(portabilityFeatures)
            }
            vkGetPhysicalDeviceFeatures2(vkPhysicalDevice, vkDeviceFeatures2)

            wideLines = deviceFeatures.wideLines()
            if (wideLines) {
                minLineWidth = deviceProperties.limits().lineWidthRange(0)
                maxLineWidth = deviceProperties.limits().lineWidthRange(1)
            } else {
                minLineWidth = 1f
                maxLineWidth = 1f
            }

            cubeMapArrays = deviceFeatures.imageCubeArray()

            maxSamples = getMaxUsableSampleCount()
            maxAnisotropy = if (!deviceFeatures.samplerAnisotropy()) 1f else {
                deviceProperties.limits().maxSamplerAnisotropy()
            }

            val api = deviceProperties.apiVersion()
            val drv = deviceProperties.driverVersion()
            apiVersion = "${VK_VERSION_MAJOR(api)}.${VK_VERSION_MINOR(api)}.${VK_VERSION_PATCH(api)}"
            driverVersion = "${VK_VERSION_MAJOR(drv)}.${VK_VERSION_MINOR(drv)}.${VK_VERSION_PATCH(drv)}"
            deviceName = deviceProperties.deviceNameString()

            logI("PhysicalDevice") { "Selected GPU: $deviceName [api: $apiVersion, driver: $driverVersion]" }
            logD("PhysicalDevice") {
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

        releaseWith(backend.instance)
    }

    private fun MemoryStack.selectPhysicalDevice(devices: List<PhysicalDeviceWrapper>): PhysicalDeviceWrapper {
        val suitableDevices = devices.filter {
            it.queueFamiliyIndices.isComplete &&
                    it.isSupportingExtensions(backend.setup.requestedDeviceExtensions) &&
                    it.querySwapChainSupport(this).isValid
        }
        check(suitableDevices.isNotEmpty()) {
            "No suitable Vulkan devices found! Available devices: " +
                    "${devices.map { it.properties.deviceName() }}, " +
                    "none matched the required extensions and capabilities"
        }

        if (suitableDevices.size > 1) {
            logD { "Multiple Vulkan capable GPUs found:" }
            suitableDevices.forEach { dev -> logI { "  ${dev.properties.deviceNameString()}" } }
        }
        return suitableDevices.find { it.properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU } ?: devices.first()
    }

    private fun getMaxUsableSampleCount(): Int {
        return min(
            deviceProperties.limits().framebufferColorSampleCounts(),
            deviceProperties.limits().framebufferDepthSampleCounts()
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
        error("Failed to find supported format")
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
        error("Failed to find suitable memory type")
    }

    fun isImageFormatSupportingBlitting(format: Int): Boolean {
        val tilingFeatures = imgFormatTilingFeatures.getOrPut(format) {
            memStack {
                val formatProperties = VkFormatProperties.malloc(this)
                vkGetPhysicalDeviceFormatProperties(backend.physicalDevice.vkPhysicalDevice, format, formatProperties)
                formatProperties.optimalTilingFeatures()
            }
        }
        return tilingFeatures and VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT != 0
    }

    override fun release() {
        super.release()
        deviceProperties.free()
        vkDeviceFeatures2.free()
        portabilityFeatures.free()
    }

    private inner class PhysicalDeviceWrapper(ptr: Long, stack: MemoryStack) {
        val physicalDevice = VkPhysicalDevice(ptr, backend.instance.vkInstance)
        val queueFamiliyIndices: QueueFamilyIndices
        val properties: VkPhysicalDeviceProperties
        val features: VkPhysicalDeviceFeatures
        val availableExtensions: Set<String>

        init {
            queueFamiliyIndices = findQueueFamilies()
            properties = VkPhysicalDeviceProperties.malloc(stack)
            features = VkPhysicalDeviceFeatures.malloc(stack)
            vkGetPhysicalDeviceProperties(physicalDevice, properties)
            vkGetPhysicalDeviceFeatures(physicalDevice, features)

            availableExtensions = memStack {
                enumerateExtensionProperties { cnt, buffer ->
                    vkEnumerateDeviceExtensionProperties(physicalDevice, null as String?, cnt, buffer)
                }.map { it.extensionNameString() }.toSet()
            }
        }

        private fun findQueueFamilies(): QueueFamilyIndices {
            memStack {
                val nFams = mallocInt(1)
                vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, nFams, null)
                val queueFamilies = VkQueueFamilyProperties.malloc(nFams[0], this)
                vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, nFams, queueFamilies)

                val ip = mallocInt(1)
                var presentFamily: Int? = null
                var graphicsFamily: Int? = null
                var computeFamily: Int? = null
                var transferFamily: Int? = null
                for (i in 0 until nFams[0]) {
                    val props = queueFamilies[i]
                    vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, backend.glfwWindow.surface.surfaceHandle, ip)
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

        fun isSupportingExtensions(extensions: Set<VkSetup.RequestedFeature>): Boolean {
            val missingExtensions = extensions.filter { it.isRequired && it.name !in availableExtensions }
            if (missingExtensions.isNotEmpty()) {
                logW { "Requested device extensions are not available:" }
                missingExtensions.forEach { logW { "  $it" } }
            }
            return missingExtensions.isEmpty()
        }

        fun querySwapChainSupport(
            stack: MemoryStack,
            caps: VkSurfaceCapabilitiesKHR? = null,
            fmts: VkSurfaceFormatKHR.Buffer? = null
        ): SwapChainSupportDetails {
            val ip = stack.mallocInt(1)
            val surface = backend.glfwWindow.surface.surfaceHandle

            val capabilities = caps ?: VkSurfaceCapabilitiesKHR.malloc(stack)
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, capabilities)

            val formatList = buildList {
                vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, ip, null)
                if (ip[0] > 0) {
                    val formats = fmts ?: VkSurfaceFormatKHR.malloc(ip[0], stack)
                    vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, ip, formats)
                    addAll(formats)
                }
            }

            val presentModeList = buildList {
                vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, ip, null)
                if (ip[0] > 0) {
                    val presentModes = stack.mallocInt(ip[0])
                    vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, ip, presentModes)
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

    inner class SwapChainSupportDetails(
        val capabilities: VkSurfaceCapabilitiesKHR,
        val formats: List<VkSurfaceFormatKHR>,
        val presentModes: List<Int>
    ) {
        val isValid: Boolean get() = formats.isNotEmpty() && presentModes.isNotEmpty()

        private var selectedSurfaceFmt: VkSurfaceFormatKHR? = null

        fun chooseSurfaceFormat(): VkSurfaceFormatKHR {
            if (selectedSurfaceFmt != null) {
                return selectedSurfaceFmt!!
            }

            // other possible color spaces: e.g. VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT
            val preferredColorSpace = when (backend.setup.preferredColorSpace) {
                ColorSpace.sRGB -> VK_COLOR_SPACE_SRGB_NONLINEAR_KHR
                ColorSpace.AdobeRGB -> VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT
                ColorSpace.DCI_P3 -> VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT
            }
            formats.find {
                it.format() == VK_FORMAT_B8G8R8A8_UNORM && it.colorSpace() == preferredColorSpace
            }?.let {
                selectedSurfaceFmt = it
                logI("PhysicalDevice") { "Selected surface format with preferred color space ${backend.setup.preferredColorSpace}" }
                return it
            }

            formats.find {
                it.format() == VK_FORMAT_B8G8R8A8_UNORM && it.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR
            }?.let {
                selectedSurfaceFmt = it
                logW("PhysicalDevice") { "Preferred color space ${backend.setup.preferredColorSpace} not available, using sRGB" }
                return it
            }

            selectedSurfaceFmt = formats[0]
            logW("PhysicalDevice") { "8-bit RGBA sRGB surface format not available, using first one (${selectedSurfaceFmt!!.format()})" }
            return formats[0]
        }

        fun choosePresentationMode(): Int {
            return when {
                KoolSystem.configJvm.isVsync -> VK_PRESENT_MODE_FIFO_KHR
                VK_PRESENT_MODE_MAILBOX_KHR in presentModes -> VK_PRESENT_MODE_MAILBOX_KHR
                VK_PRESENT_MODE_IMMEDIATE_KHR in presentModes -> VK_PRESENT_MODE_IMMEDIATE_KHR
                else -> VK_PRESENT_MODE_FIFO_KHR
            }
        }

        fun chooseSwapExtent(window: GlfwVkWindow, stack: MemoryStack): VkExtent2D {
            val minWidth = capabilities.minImageExtent().width()
            val maxWidth = capabilities.maxImageExtent().width()
            val minHeight = capabilities.minImageExtent().height()
            val maxHeight = capabilities.maxImageExtent().height()

            return VkExtent2D.malloc(stack)
                .width(window.framebufferWidth.clamp(minWidth, maxWidth))
                .height(window.framebufferHeight.clamp(minHeight, maxHeight))
        }
    }
}

internal inline fun PhysicalDevice.createDevice(stack: MemoryStack? = null, block: VkDeviceCreateInfo.() -> Unit): VkDevice {
    memStack(stack) {
        val createInfo = callocVkDeviceCreateInfo(block)
        val handle = pointers(0)
        vkCheck(vkCreateDevice(vkPhysicalDevice, createInfo, null, handle)) { "Failed creating device: $it" }
        return VkDevice(handle[0], vkPhysicalDevice, createInfo)
    }
}
