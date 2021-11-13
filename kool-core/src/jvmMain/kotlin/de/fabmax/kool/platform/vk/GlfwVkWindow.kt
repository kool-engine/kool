package de.fabmax.kool.platform.vk

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.KoolException
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.KHRSurface

class GlfwVkWindow(val sys: VkSystem, width: Int, height: Int, title: String, fullscreenMonitor: MonitorSpec? = null) :
    VkResource()
{

    val glfwWindow: Long

    var windowWidth = width
        private set
    var windowHeight = height
        private set

    var windowPosX = 0
        private set
    var windowPosY = 0
        private set

    var framebufferWidth = width
        private set
    var framebufferHeight = height
        private set

    var isFullscreen = false
        set(value) {
            if (value != field) {
                field = value
                setFullscreenMode(value)
            }
        }

    val onResize = mutableListOf<OnWindowResizeListener>()

    lateinit var surface: Surface
        private set

    private val fsMonitor: Long
    private var windowedWidth = width
    private var windowedHeight = height
    private var windowedPosX = 0
    private var windowedPosY = 0

    init {
        GLFWErrorCallback.createPrint().set()

        check(glfwInit()) { "Unable to initialize GLFW" }
        check(GLFWVulkan.glfwVulkanSupported()) { "Cannot find a compatible Vulkan installable client driver (ICD)" }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)

        glfwWindow = glfwCreateWindow(width, height, title, 0L, 0L)
        if (glfwWindow == MemoryUtil.NULL) {
            throw KoolException("Failed to create the GLFW window")
        }

        glfwSetWindowSizeCallback(glfwWindow) { _, w, h ->
            windowWidth = w
            windowHeight = h
            logI { "GLFW window resized: $w x $h" }
        }
        glfwSetFramebufferSizeCallback(glfwWindow) { _, w, h ->
            framebufferWidth = w
            framebufferHeight = h
            logI { "GLFW framebuffer resized: $w x $h" }
            for (listener in onResize) {
                listener.onResize(this, w, h)
            }
        }
        glfwSetWindowPosCallback(glfwWindow) { _, x, y ->
            windowPosX = x
            windowPosY = y
            logI { "GLFW window position changed: $x, $y" }
        }

        val outInt1 = IntArray(1)
        val outInt2 = IntArray(1)

        glfwGetWindowPos(glfwWindow, outInt1, outInt2)
        windowPosX = outInt1[0]
        windowPosY = outInt2[0]

        glfwGetFramebufferSize(glfwWindow, outInt1, outInt2)
        framebufferWidth = outInt1[0]
        framebufferHeight = outInt2[0]

        fsMonitor = fullscreenMonitor?.monitor ?: DesktopImpl.primaryMonitor.monitor
        windowedWidth = windowWidth
        windowedHeight = windowHeight
        windowedPosX = windowPosX
        windowedPosY = windowPosY

        sys.addDependingResource(this)
        logD { "Created GLFW window" }
    }

    private fun setFullscreenMode(enabled: Boolean) {
        if (enabled) {
            windowedWidth = windowWidth
            windowedHeight = windowHeight
            windowedPosX = windowPosX
            windowedPosY = windowPosY

            val vidMode = glfwGetVideoMode(fsMonitor)!!
            glfwSetWindowMonitor(glfwWindow, fsMonitor, 0, 0, vidMode.width(), vidMode.height(), GLFW_DONT_CARE)
        } else {
            glfwSetWindowMonitor(glfwWindow, 0, windowedPosX, windowedPosY, windowedWidth, windowedHeight, GLFW_DONT_CARE)
        }
    }

    fun createSurface() {
        surface = Surface()
    }

    override fun freeResources() {
        glfwDestroyWindow(glfwWindow)
        glfwTerminate()
        logD { "Destroyed GLFW window" }
    }

    interface OnWindowResizeListener {
        fun onResize(window: GlfwVkWindow, newWidth: Int, newHeight: Int)
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