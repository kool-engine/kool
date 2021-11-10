package de.fabmax.kool.platform.vk

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVulkan
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

        val monitor = if (sys.props.monitor < 0) DesktopImpl.primaryMonitor else DesktopImpl.monitors[sys.props.monitor]
        val width = if (sys.props.width < 0) monitor.widthPx else sys.props.width
        val height = if (sys.props.height < 0) monitor.heightPx else sys.props.height
        glfwWindow = GLFW.glfwCreateWindow(1920, 1080, sys.props.title, 0L, sys.props.share)
        if (sys.props.isFullscreen) {
            GLFW.glfwSetWindowMonitor(glfwWindow, monitor.monitor, 0, 0, width, height, GLFW.GLFW_DONT_CARE)
        }
        this.width = width
        this.height = height

        GLFW.glfwSetFramebufferSizeCallback(glfwWindow) { _, w, h ->
            this.width = w
            this.height = h
            for (listener in onResize) {
                listener.onResize(this, w, h)
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