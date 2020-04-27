package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
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
            var enabledLayers: PointerBuffer? = null
            if (sys.setup.isValidating) {
                if (!checkValidationLayerSupport()) {
                    logW { "Validation layers requested but not available, VK_LAYER_PATH environment variable must include the validation layer path" }

                } else {
                    enabledLayers = mallocPointer(sys.setup.validationLayers.size)
                    logD { "Enabling layers:" }
                    sys.setup.validationLayers.forEachIndexed { idx, layer ->
                        logD { "  $layer" }
                        enabledLayers.put(idx, ASCII(layer))
                    }
                }
            }

            val appInfo = callocVkApplicationInfo {
                sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                pApplicationName(UTF8(appName))
                applicationVersion(VK_MAKE_VERSION(1, 0, 0))
                pEngineName(UTF8("Kool"))
                engineVersion(VK_MAKE_VERSION(1, 0, 0))
                apiVersion(VK_API_VERSION_1_0)
            }

            val createInfo = callocVkInstanceCreateInfo {
                sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                pApplicationInfo(appInfo)
                ppEnabledExtensionNames(getRequiredExtensions(this@memStack))
                ppEnabledLayerNames(enabledLayers)
            }

            var dbgMessengerInfo: VkDebugUtilsMessengerCreateInfoEXT? = null
            if (sys.setup.isValidating) {
                dbgMessengerInfo = setupDebugMessengerCreateInfo(this@memStack)
                createInfo.pNext(dbgMessengerInfo.address())
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

    private fun checkValidationLayerSupport(): Boolean {
        return memStack {
            val ip = mallocInt(1)
            checkVk(vkEnumerateInstanceLayerProperties(ip, null))
            val availableLayers = VkLayerProperties.mallocStack(ip[0], this)
            checkVk(vkEnumerateInstanceLayerProperties(ip, availableLayers))
            sys.setup.validationLayers.all { layer -> availableLayers.any { it.layerNameString() == layer } }
        }
    }

    private fun getRequiredExtensions(stack: MemoryStack): PointerBuffer {
        val extensionNames = MemoryUtil.memAllocPointer(64)

        // add all extensions required by glfw
        val requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions() ?:
            throw IllegalStateException("glfwGetRequiredInstanceExtensions failed to find the platform surface extensions.")
        extensionNames.put(requiredExtensions)

        // enumerate available extensions. fun fact: not doing this results in a segfault on instance creation...
        val ip = stack.mallocInt(1)
        checkVk(vkEnumerateInstanceExtensionProperties(null as String?, ip, null))
        val instanceExtensions = VkExtensionProperties.mallocStack(ip[0], stack)
        checkVk(vkEnumerateInstanceExtensionProperties(null as String?, ip, instanceExtensions))
        if (sys.setup.isValidating && instanceExtensions.any { it.extensionNameString() == EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME }) {
            extensionNames.put(stack.ASCII(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME))
        }
        extensionNames.flip()

        logD { "Enabling Vulkan instance extensions:" }
        for (i in 0 until extensionNames.limit()) {
            logD { "  ${MemoryUtil.memASCII(extensionNames[i])}" }
        }
        return extensionNames
    }

    private fun setupDebugMessengerCreateInfo(stack: MemoryStack): VkDebugUtilsMessengerCreateInfoEXT {
        return stack.callocVkDebugUtilsMessengerCreateInfoEXT {
            sType(EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
            .messageSeverity(
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
                val msg = "[VkValidation/${getMessageTypeName(messageTypes)}] ${MemoryUtil.memUTF8(arg.pMessage())}"
                when {
                    messageSeverity and EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT != 0 -> {
                        logD { msg }
                    }
                    messageSeverity and EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT != 0 -> {
                        logI { msg }
                    }
                    messageSeverity and EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT != 0 -> {
                        logW { msg }
                    }
                    messageSeverity and EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT != 0 -> {
                        logE { msg }
                    }
                }
                VK_FALSE
            }
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