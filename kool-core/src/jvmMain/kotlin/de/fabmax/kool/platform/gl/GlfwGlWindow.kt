package de.fabmax.kool.platform.gl

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.KoolException
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil

class GlfwGlWindow(width: Int, height: Int, title: String, fullscreenMonitor: MonitorSpec?, ctx: Lwjgl3Context) {

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

        val outInt1 = IntArray(1)
        val outInt2 = IntArray(1)
        val outFloat1 = FloatArray(1)
        val outFloat2 = FloatArray(1)
        glfwGetWindowPos(windowPtr, outInt1, outInt2)
        windowPosX = outInt1[0]
        windowPosY = outInt2[0]
        glfwGetFramebufferSize(windowPtr, outInt1, outInt2)
        framebufferWidth = outInt1[0]
        framebufferHeight = outInt2[0]
        glfwGetWindowContentScale(windowPtr, outFloat1, outFloat2)
        ctx.windowScale = outFloat1[0]
        ctx.isWindowFocused = glfwGetWindowAttrib(windowPtr, GLFW_FOCUSED) == GLFW_TRUE

        glfwSetWindowSizeCallback(windowPtr) { _, w, h ->
            windowWidth = w
            windowHeight = h
        }
        glfwSetFramebufferSizeCallback(windowPtr) { _, w, h ->
            framebufferWidth = w
            framebufferHeight = h
        }
        glfwSetWindowPosCallback(windowPtr) { _, x, y ->
            windowPosX = x
            windowPosY = y
        }
        glfwSetWindowCloseCallback(windowPtr) {
            if (!ctx.applicationCallbacks.onWindowCloseRequest(ctx)) {
                logD { "Window close request was suppressed by application callback" }
                glfwSetWindowShouldClose(windowPtr, false)
            }
        }
        glfwSetWindowFocusCallback(windowPtr) { _, isFocused ->
            ctx.isWindowFocused = isFocused
        }
        glfwSetWindowContentScaleCallback(windowPtr) { _, scale, _ ->
            ctx.windowScale = scale
        }

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