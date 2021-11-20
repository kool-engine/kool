package de.fabmax.kool.platform.gl

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.KoolException
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.logI
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil

open class GlfwGlWindow(width: Int, height: Int, title: String, fullscreenMonitor: MonitorSpec? = null) {

    val windowPtr: Long

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

    private val fsMonitor: Long
    private var windowedWidth = width
    private var windowedHeight = height
    private var windowedPosX = 0
    private var windowedPosY = 0

    init {
        windowPtr = glfwCreateWindow(width, height, title, 0L, 0L)
        if (windowPtr == MemoryUtil.NULL) {
            throw KoolException("Failed to create the GLFW window")
        }

        glfwSetWindowSizeCallback(windowPtr) { _, w, h ->
            windowWidth = w
            windowHeight = h
            logI { "GLFW window resized: $w x $h" }
        }
        glfwSetFramebufferSizeCallback(windowPtr) { _, w, h ->
            framebufferWidth = w
            framebufferHeight = h
            logI { "GLFW framebuffer resized: $w x $h" }
        }
        glfwSetWindowPosCallback(windowPtr) { _, x, y ->
            windowPosX = x
            windowPosY = y
            logI { "GLFW window position changed: $x, $y" }
        }

        val outInt1 = IntArray(1)
        val outInt2 = IntArray(1)

        glfwGetWindowPos(windowPtr, outInt1, outInt2)
        windowPosX = outInt1[0]
        windowPosY = outInt2[0]

        glfwGetFramebufferSize(windowPtr, outInt1, outInt2)
        framebufferWidth = outInt1[0]
        framebufferHeight = outInt2[0]

        fsMonitor = fullscreenMonitor?.monitor ?: DesktopImpl.primaryMonitor.monitor
        windowedWidth = windowWidth
        windowedHeight = windowHeight
        windowedPosX = windowPosX
        windowedPosY = windowPosY
    }

    private fun setFullscreenMode(enabled: Boolean) {
        if (enabled) {
            windowedWidth = windowWidth
            windowedHeight = windowHeight
            windowedPosX = windowPosX
            windowedPosY = windowPosY

            val vidMode = glfwGetVideoMode(fsMonitor)!!
            glfwSetWindowMonitor(windowPtr, fsMonitor, 0, 0, vidMode.width(), vidMode.height(), GLFW_DONT_CARE)
            // re-enable v-sync
            glfwSwapInterval(1)
        } else {
            glfwSetWindowMonitor(windowPtr, 0, windowedPosX, windowedPosY, windowedWidth, windowedHeight, GLFW_DONT_CARE)
        }
    }
}