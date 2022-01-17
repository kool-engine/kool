package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.VK10.*
import kotlin.math.min

class PhysicalDevice(val sys: VkSystem) : VkResource() {

    val vkPhysicalDevice: VkPhysicalDevice
    val queueFamiliyIndices: QueueFamilyIndices
    val vkDeviceProperties = VkPhysicalDeviceProperties.malloc()
    val vkDeviceFeatures = VkPhysicalDeviceFeatures.malloc()
    val msaaSamples: Int

    val deviceName: String
    val apiVersion: String
    val driverVersion: String

    val depthFormat: Int

    init {
        memStack {
            val ip = mallocInt(1)
            checkVk(vkEnumeratePhysicalDevices(sys.instance.vkInstance, ip, null))
            val devPtrs = mallocPointer(ip[0])
            checkVk(vkEnumeratePhysicalDevices(sys.instance.vkInstance, ip, devPtrs))

            val devices = (0 until ip[0]).map { PhysicalDevice(devPtrs[it], this) }
            val selectedDevice = sys.setup.selectPhysicalDevice(devices)
            check(selectedDevice != null && selectedDevice.queueFamiliyIndices.isComplete) {
                "Failed to find a suitable GPU"
            }

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

            logI {
                "Selected GPU: $deviceName [api: $apiVersion, driver: $driverVersion]"
            }
            logD {
                "Using queue families: present: ${queueFamiliyIndices.presentFamily}, " +
                        "graphics: ${queueFamiliyIndices.graphicsFamily}, " +
                        "transfer: ${queueFamiliyIndices.transferFamily}"
            }
        }

        depthFormat = findSupportedFormat(
                    listOf(VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT, VK_FORMAT_D24_UNORM_S8_UINT),
                    VK_IMAGE_TILING_OPTIMAL, VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT)

        sys.instance.addDependingResource(this)
    }

    private fun getMaxUsableSampleCount(): Int {
        val counts = min(vkDeviceProperties.limits().framebufferColorSampleCounts(),
            vkDeviceProperties.limits().framebufferDepthSampleCounts())
        return when {
            counts and VK_SAMPLE_COUNT_64_BIT != 0 -> VK_SAMPLE_COUNT_64_BIT
            counts and VK_SAMPLE_COUNT_32_BIT != 0 -> VK_SAMPLE_COUNT_32_BIT
            counts and VK_SAMPLE_COUNT_16_BIT != 0 -> VK_SAMPLE_COUNT_16_BIT
            counts and VK_SAMPLE_COUNT_8_BIT != 0 -> VK_SAMPLE_COUNT_8_BIT
            counts and VK_SAMPLE_COUNT_4_BIT != 0 -> VK_SAMPLE_COUNT_4_BIT
            counts and VK_SAMPLE_COUNT_2_BIT != 0 -> VK_SAMPLE_COUNT_2_BIT
            else -> VK_SAMPLE_COUNT_1_BIT
        }
    }

    fun findSupportedFormat(candidates: List<Int>, tiling: Int, features: Int): Int {
        memStack {
            val props = VkFormatProperties.malloc(this)
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

    inner class PhysicalDevice(ptr: Long, stack: MemoryStack) {
        val device = VkPhysicalDevice(ptr, sys.instance.vkInstance)
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
                var transferFamily: Int? = null
                for (i in 0 until nFams[0]) {
                    val props = queueFamilies[i]
                    KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(device, i, sys.window.surface.surfaceHandle, ip)
                    if (presentFamily == null && props.queueCount() > 0 && ip[0] != 0) {
                        presentFamily = i
                    }
                    if (graphicsFamily == null && props.queueCount() > 0 && props.queueFlags() and VK_QUEUE_GRAPHICS_BIT != 0) {
                        graphicsFamily = i
                    }
                    if (transferFamily == null && props.queueCount() > 0 && props.queueFlags() and VK_QUEUE_GRAPHICS_BIT == 0 && props.queueFlags() and VK_QUEUE_TRANSFER_BIT != 0) {
                        transferFamily = i
                    }
                }

                val indices = QueueFamilyIndices(graphicsFamily, presentFamily, transferFamily)
                return indices
            }
        }
    }

    class QueueFamilyIndices(val graphicsFamily: Int?, val presentFamily: Int?, val transferFamily: Int?) {
        val uniqueFamilies: Set<Int>
        val isComplete = graphicsFamily != null && presentFamily != null

        init {
            val fams = mutableSetOf<Int>()
            if (graphicsFamily != null) { fams += graphicsFamily }
            if (presentFamily != null) { fams += presentFamily }
            if (transferFamily != null) { fams += transferFamily }
            uniqueFamilies = fams
        }
    }
}