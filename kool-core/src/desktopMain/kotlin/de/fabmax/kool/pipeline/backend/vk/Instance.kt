package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.*
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.VK10.*

class Instance(val backend: RenderBackendVk, appName: String) : BaseReleasable() {

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
                apiVersion(backend.setup.vkApiVersion)
            }

            val createInfo = callocVkInstanceCreateInfo {
                pApplicationInfo(appInfo)
                ppEnabledExtensionNames(enableExtensions)
                ppEnabledLayerNames(enabledLayers)
                flags(KHRPortabilityEnumeration.VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR)
            }

            val dbgMessengerInfo: VkDebugUtilsMessengerCreateInfoEXT? = if (backend.setup.isValidation) {
                setupDebugMessengerCreateInfo().also { createInfo.pNext(it.address()) }
            } else {
                null
            }

            val ptr = checkCreatePointer { vkCreateInstance(createInfo, null, it) }
            vkInstance = VkInstance(ptr, createInfo)

            if (dbgMessengerInfo != null) {
                val lp = mallocLong(1)
                vkCheck(EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(vkInstance, dbgMessengerInfo, null, lp))
                debugMessenger = lp.get()
            }
        }

        logD { "Created Vulkan instance" }
    }

    override fun release() {
        super.release()
        ReleaseQueue.enqueue {
            if (debugMessenger != 0L) {
                EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(vkInstance, debugMessenger, null)
            }
            vkDestroyInstance(vkInstance, null)
            logD { "Destroyed instance" }
        }
    }

    private fun MemoryStack.getRequestedLayers(): PointerBuffer? {
        val availableLayers = enumerateLayerProperties { cnt, buffer ->
            vkEnumerateInstanceLayerProperties(cnt, buffer)
        }.map { it.layerNameString() }.toSet()

        val missingLayers = backend.setup.requestedLayers.filter { it.name !in availableLayers }
        if (missingLayers.isNotEmpty()) {
            logD("Instance") { "Requested optional layers are not available:" }
            missingLayers.filter { !it.isRequired }.forEach { logD("Instance") { "  ${it.name}" } }
            logW("Instance") { "Make sure that the VK_LAYER_PATH environment variable is set and points to the directory with the layer specification json files" }
            if (missingLayers.any { it.isRequired }) {
                error("Missing non-optional layers: ${missingLayers.filter { it.isRequired }.map { it.name }}")
            }
        }

        val enableLayers = backend.setup.requestedLayers - missingLayers
        return if (enableLayers.isNotEmpty()) {
            val ptrs = mallocPointer(enableLayers.size)
            logD("Instance") { "Enabling layers:" }
            enableLayers.forEachIndexed { i, layer ->
                logD("Instance") { "  ${layer.name}" }
                ptrs.put(i, ASCII(layer.name))
            }
            ptrs
        } else {
            null
        }
    }

    private fun MemoryStack.getRequestedExtensions(): PointerBuffer? {
        val requestedExtensions = backend.setup.requestedInstanceExtensions.toMutableSet()

        // add all extensions required by glfw
        val glfwExtensions = checkNotNull(GLFWVulkan.glfwGetRequiredInstanceExtensions()) {
            "glfwGetRequiredInstanceExtensions failed to find the platform surface extensions."
        }
        for (i in 0 until glfwExtensions.limit()) {
            requestedExtensions += VkSetup.RequestedFeature(MemoryUtil.memASCII(glfwExtensions[i]), true)
        }

        val availableExtensions = enumerateExtensionProperties { cnt, buffer ->
            vkEnumerateInstanceExtensionProperties(null as String?, cnt, buffer)
        }.map { it.extensionNameString() }.toSet()

        val missingExtensions = requestedExtensions.filter { it.name !in availableExtensions }
        if (missingExtensions.isNotEmpty()) {
            logD("Instance") { "Requested optional extensions are not available:" }
            missingExtensions.filter { !it.isRequired }.forEach { logD("Instance") { "  ${it.name}" } }
            if (missingExtensions.any { it.isRequired }) {
                error("Missing non-optional extensions: ${missingExtensions.filter { it.isRequired }.map { it.name }}")
            }
        }

        val enableExtensions = requestedExtensions - missingExtensions
        return if (enableExtensions.isNotEmpty()) {
            val ptrs = mallocPointer(enableExtensions.size)
            logD("Instance") { "Enabling extensions:" }
            enableExtensions.forEachIndexed { i, extension ->
                logD("Instance") { "  ${extension.name}" }
                ptrs.put(i, ASCII(extension.name))
            }
            ptrs
        } else {
            null
        }
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
            val tag = "VkValidation/${getMessageTypeName(messageTypes)}"
            val logStr = arg.pMessage()?.let { MemoryUtil.memUTF8(it) } ?: "<null>"
            val msgSplit = logStr.replace(" | ", "\n")

            when {
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> logE(tag) { msgSplit }
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> logW(tag) { msgSplit }
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT -> logI(tag) { msgSplit }
                messageSeverity >= EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> logD(tag) { msgSplit }
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