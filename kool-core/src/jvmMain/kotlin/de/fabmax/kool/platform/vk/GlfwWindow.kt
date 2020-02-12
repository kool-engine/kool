package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.KHRSurface

class GlfwWindow(val sys: VkSystem, var width: Int = 800, var height: Int = 600) : VkResource() {

    val glfwWindow: Long
    lateinit var surface: Surface
        private set

    val onResize = mutableListOf<OnWindowResizeListener>()

    init {
        GLFWErrorCallback.createPrint().set()

        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        check(GLFWVulkan.glfwVulkanSupported()) { "Cannot find a compatible Vulkan installable client driver (ICD)" }

        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API)

        glfwWindow = GLFW.glfwCreateWindow(width, height, "Vulkan", MemoryUtil.NULL, MemoryUtil.NULL)
        GLFW.glfwSetFramebufferSizeCallback(glfwWindow) { _, width, height ->
            this.width = width
            this.height = height
            for (listener in onResize) {
                listener.onResize(this, width, height)
            }
        }

        sys.addDependingResource(this)
        logD { "Created GLFW window" }
    }

    fun createSurface() {
        surface = Surface()
    }

    override fun freeResources() {
        GLFW.glfwDestroyWindow(glfwWindow)
        GLFW.glfwTerminate()
        logD { "Destroyed GLFW window" }
    }

    interface OnWindowResizeListener {
        fun onResize(window: GlfwWindow, newWidth: Int, newHeight: Int)
    }

    inner class Surface : VkResource() {
        var surfaceHandle = 0L
            private set

        init {
            memStack {
                val lp = mallocLong(1)
                checkVk(GLFWVulkan.glfwCreateWindowSurface(sys.instance.vkInstance, glfwWindow, null, lp))
                surfaceHandle = lp[0]
            }
            sys.instance.addDependingResource(this)
            logD { "Created surface" }
        }

        override fun freeResources() {
            KHRSurface.vkDestroySurfaceKHR(sys.instance.vkInstance, surfaceHandle, null)
            logD { "Destroyed surface" }
        }
    }
}