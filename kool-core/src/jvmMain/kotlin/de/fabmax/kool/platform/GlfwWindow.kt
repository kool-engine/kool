package de.fabmax.kool.platform

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage

open class GlfwWindow(props: Lwjgl3Context.InitProps, val ctx: Lwjgl3Context) {

    val windowPtr: Long

    var windowWidth = props.width
        private set
    var windowHeight = props.height
        private set

    var windowPosX = 0
        private set
    var windowPosY = 0
        private set

    var framebufferWidth = props.width
        private set
    var framebufferHeight = props.height
        private set

    var isFullscreen = false
        set(value) {
            if (value != field) {
                field = value
                setFullscreenMode(value)
            }
        }

    var isVisible = false
        set(value) {
            field = value
            if (value) {
                glfwShowWindow(windowPtr)
            } else {
                glfwHideWindow(windowPtr)
            }
        }

    private val fsMonitor: Long
    private var windowedWidth = props.width
    private var windowedHeight = props.height
    private var windowedPosX = 0
    private var windowedPosY = 0

    init {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        windowPtr = glfwCreateWindow(props.width, props.height, props.title, 0L, 0L)
        if (windowPtr == MemoryUtil.NULL) {
            throw KoolException("Failed to create the GLFW window")
        }

        if (props.icon.isNotEmpty()) {
            setWindowIcon(props.icon)
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

        glfwSetWindowSizeCallback(windowPtr) { _, w, h -> onWindowSizeChanged(w, h) }
        glfwSetFramebufferSizeCallback(windowPtr) { _, w, h -> onFramebufferSizeChanged(w, h) }
        glfwSetWindowPosCallback(windowPtr) { _, x, y -> onWindowPositionChanged(x, y) }
        glfwSetWindowCloseCallback(windowPtr) { onWindowCloseRequest() }
        glfwSetWindowFocusCallback(windowPtr) { _, isFocused -> onWindowFocusChanged(isFocused) }
        glfwSetWindowContentScaleCallback(windowPtr) { _, xScale, yScale -> onWindowContentScaleChanged(xScale, yScale) }

        fsMonitor = if (props.monitor < 0) {
            DesktopImpl.primaryMonitor.monitor
        } else {
            DesktopImpl.monitors[props.monitor].monitor
        }
        windowedWidth = windowWidth
        windowedHeight = windowHeight
        windowedPosX = windowPosX
        windowedPosY = windowPosY
    }

    fun setWindowSize(width: Int, height: Int) {
        glfwSetWindowSize(windowPtr, width, height)
    }

    fun setWindowPos(x: Int, y: Int) {
        glfwSetWindowPos(windowPtr, x, y)
    }

    protected open fun onWindowSizeChanged(width: Int, height: Int) {
        println("on size changed: $width x $height")
        windowWidth = width
        windowHeight = height
    }

    protected open fun onFramebufferSizeChanged(width: Int, height: Int) {
        framebufferWidth = width
        framebufferHeight = height
    }

    protected open fun onWindowContentScaleChanged(xScale: Float, yScale: Float) {
        if (xScale != yScale) {
            logW { "Window scale x != y (x: $xScale, y: $yScale)" }
        }
        ctx.windowScale = (xScale + yScale) / 2f
    }

    protected open fun onWindowPositionChanged(x: Int, y: Int) {
        println("on pos changed: $x, $y")
        windowPosX = x
        windowPosY = y
    }

    protected open fun onWindowFocusChanged(isFocused: Boolean) {
        ctx.isWindowFocused = isFocused
    }

    protected open fun onWindowCloseRequest() {
        if (!ctx.applicationCallbacks.onWindowCloseRequest(ctx)) {
            logD { "Window close request was suppressed by application callback" }
            glfwSetWindowShouldClose(windowPtr, false)
        }
    }

    fun setWindowTitle(windowTitle: String) {
        glfwSetWindowTitle(windowPtr, windowTitle)
    }

    fun setWindowIcon(icon: List<BufferedImage>) {
        MemoryStack.stackPush().use {
            val images = GLFWImage.malloc(icon.size, it)
            icon.forEachIndexed { i, img ->
                val buffer = ImageTextureData(img, TexFormat.RGBA).data as Uint8BufferImpl
                images.get(i).apply {
                    width(img.width)
                    height(img.height)
                    pixels(buffer.buffer)
                }
            }
            glfwSetWindowIcon(windowPtr, images)
        }
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