package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.*
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.VK10.*

class Instance(val sys: VkSystem, appName: String) : VkResource() {

    val vkInstance: VkInstance
    private var debugMessenger = 0L

    init {
        memStack {
            val enabledLayers: PointerBuffer? = getRequestedLayers()
            val enableExtensions = getRequestedExtensions()

            val appInfo = callocVkApplicationInfo {
                pApplicationName(UTF8(appName))
                applicationVersion(VK_MAKE_VERSION(1, 0, 0))
                pEngineName(UTF8("Kool ${KoolContext.KOOL_VERSION}"))
                engineVersion(VK_MAKE_VERSION(1, 0, 0))
                apiVersion(sys.setup.vkApiVersion)
            }

            val createInfo = callocVkInstanceCreateInfo {
                pApplicationInfo(appInfo)
                ppEnabledExtensionNames(enableExtensions)
                ppEnabledLayerNames(enabledLayers)
                flags(KHRPortabilityEnumeration.VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR)
            }

            val dbgMessengerInfo: VkDebugUtilsMessengerCreateInfoEXT? = if (sys.setup.isValidation) {
                setupDebugMessengerCreateInfo().also { createInfo.pNext(it.address()) }
            } else {
                null
            }

            val pp = mallocPointer(1)
            checkVk(vkCreateInstance(createInfo, null, pp))
            vkInstance = VkInstance(pp[0], createInfo)

            if (dbgMessengerInfo != null) {
                val lp = mallocLong(1)
                checkVk(EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(vkInstance, dbgMessengerInfo, null, lp))
                debugMessenger = lp.get()
            }
        }

        sys.addDependingResource(this)
        logD { "Created instance" }
    }

    override fun freeResources() {
        if (debugMessenger != 0L) {
            EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(vkInstance, debugMessenger, null)
        }
        vkDestroyInstance(vkInstance, null)
        logD { "Destroyed instance" }
    }

    private fun MemoryStack.getRequestedLayers(): PointerBuffer? {
        val ip = mallocInt(1)
        checkVk(vkEnumerateInstanceLayerProperties(ip, null))
        val availableLayers = VkLayerProperties.malloc(ip[0], this)
        checkVk(vkEnumerateInstanceLayerProperties(ip, availableLayers))

        val layerNames = availableLayers.map { it.layerNameString() }.toSet()
        val enableLayers = sys.setup.enabledLayers.toMutableSet().also { it.retainAll(layerNames) }
        val missingLayers = sys.setup.enabledLayers - enableLayers
        if (missingLayers.isNotEmpty()) {
            logW { "Requested layers are not available:" }
            missingLayers.forEach { logW { "  $it" } }
            logW { "Make sure that the VK_LAYER_PATH environment variable is set and points to the directory with the layer specification json files" }
        }

        return if (enableLayers.isNotEmpty()) {
            val ptrs = mallocPointer(enableLayers.size)
            logD { "Enabling layers:" }
            enableLayers.forEachIndexed { i, layer ->
                logD { "  $layer" }
                ptrs.put(i, ASCII(layer))
            }
            ptrs
        } else {
            null
        }
    }

    private fun MemoryStack.getRequestedExtensions(): PointerBuffer {
        val enableExtensions = MemoryUtil.memAllocPointer(64)

        // add all extensions required by glfw
        val requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions() ?:
            throw IllegalStateException("glfwGetRequiredInstanceExtensions failed to find the platform surface extensions.")
        enableExtensions.put(requiredExtensions)

        val ip = mallocInt(1)
        checkVk(vkEnumerateInstanceExtensionProperties(null as String?, ip, null))
        val availableExtensions = VkExtensionProperties.malloc(ip[0], this)
        checkVk(vkEnumerateInstanceExtensionProperties(null as String?, ip, availableExtensions))

        val extensionNames = availableExtensions.map { it.extensionNameString() }.toSet()
        val addExtensions = sys.setup.enabledInstanceExtensions.toMutableSet().also { it.retainAll(extensionNames) }
        val missingExtensions = sys.setup.enabledInstanceExtensions - addExtensions
        if (missingExtensions.isNotEmpty()) {
            logW { "Requested extensions are not available:" }
            missingExtensions.forEach { logW { "  $it" } }
        }
        addExtensions.forEach { enableExtensions.put(ASCII(it)) }
        enableExtensions.flip()

        logD { "Enabling Vulkan instance extensions:" }
        for (i in 0 until enableExtensions.limit()) {
            logD { "  ${MemoryUtil.memASCII(enableExtensions[i])}" }
        }
        return enableExtensions
    }

    private fun MemoryStack.setupDebugMessengerCreateInfo() = callocVkDebugUtilsMessengerCreateInfoEXT {
        messageSeverity(
            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT or
                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT or
                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT or
                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT
        )
        messageType(
            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT or
                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT or
                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT
        )
        pfnUserCallback { messageSeverity, messageTypes, pCallbackData, _ ->
            val arg = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData)
            val logStr = arg.pMessage()?.let { MemoryUtil.memUTF8(it) } ?: "<null>"
            val tag = "[VkValidation/${getMessageTypeName(messageTypes)}]"
            when {
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> logE(tag) { logStr }
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> logW(tag) { logStr }
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT -> logI(tag) { logStr }
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> logD(tag) { logStr }
            }
            VK_FALSE
        }
    }

    private fun getMessageTypeName(messageType: Int): String {
        return when (messageType) {
            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT -> "General"
            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT -> "Validation"
            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT -> "Performance"
            else -> "VkUnknownMsgType[$messageType]"
        }
    }
}