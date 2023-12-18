package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.LinkOption
import kotlin.io.path.*

open class GlfwWindow(val ctx: Lwjgl3Context) {

    val windowPtr: Long

    var windowWidth = KoolSystem.configJvm.windowSize.x
        private set
    var windowHeight = KoolSystem.configJvm.windowSize.y
        private set

    var windowPosX = 0
        private set
    var windowPosY = 0
        private set

    var framebufferWidth = KoolSystem.configJvm.windowSize.x
        private set
    var framebufferHeight = KoolSystem.configJvm.windowSize.y
        private set

    var isFullscreen = false
        set(value) {
            if (value != field) {
                field = value
                setFullscreenMode(value)
            }
        }

    var isMaximized: Boolean
        get() = glfwGetWindowAttrib(windowPtr, GLFW_MAXIMIZED) != 0
        set(value) {
            if (value) {
                glfwMaximizeWindow(windowPtr)
            } else {
                glfwRestoreWindow(windowPtr)
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
    private var windowedWidth = KoolSystem.configJvm.windowSize.x
    private var windowedHeight = KoolSystem.configJvm.windowSize.y
    private var windowedPosX = 0
    private var windowedPosY = 0

    private var renderOnResizeFlag = false

    init {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        windowPtr = glfwCreateWindow(
            KoolSystem.configJvm.windowSize.x,
            KoolSystem.configJvm.windowSize.y,
            KoolSystem.configJvm.windowTitle,
            0L,
            0L
        )
        if (windowPtr == MemoryUtil.NULL) {
            throw KoolException("Failed to create the GLFW window")
        }

        if (KoolSystem.configJvm.windowIcon.isNotEmpty()) {
            setWindowIcon(KoolSystem.configJvm.windowIcon)
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
        glfwSetDropCallback(windowPtr) { _, numFiles, pathPtr -> onFileDrop(numFiles, pathPtr) }

        fsMonitor = if (KoolSystem.configJvm.monitor < 0) {
            DesktopImpl.primaryMonitor.monitor
        } else {
            DesktopImpl.monitors[KoolSystem.configJvm.monitor].monitor
        }
        windowedWidth = windowWidth
        windowedHeight = windowHeight
        windowedPosX = windowPosX
        windowedPosY = windowPosY
    }

    fun pollEvents() {
        renderOnResizeFlag = true
        glfwPollEvents()
        renderOnResizeFlag = false
    }

    fun setWindowSize(width: Int, height: Int) {
        glfwSetWindowSize(windowPtr, width, height)
    }

    fun setWindowPos(x: Int, y: Int) {
        glfwSetWindowPos(windowPtr, x, y)
    }

    protected open fun onWindowSizeChanged(width: Int, height: Int) {
        windowWidth = width
        windowHeight = height

        // with GLFW, window resizing blocks the main-loop, call renderFrame() from here to update window content
        // during window resizing
        if (renderOnResizeFlag) {
            ctx.renderFrame()
        }
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

    @OptIn(ExperimentalPathApi::class)
    protected open fun onFileDrop(numFiles: Int, pathPtr: Long) {
        val files = mutableListOf<LoadableFile>()
        val pathPtrs = PointerBuffer.create(pathPtr, numFiles)
        repeat(numFiles) { i ->
            val file = File(MemoryUtil.memUTF8(pathPtrs[i]))
            if (file.isDirectory) {
                val dirPath = file.toPath()
                dirPath.walk(PathWalkOption.INCLUDE_DIRECTORIES)
                    .filter { it.isRegularFile(LinkOption.NOFOLLOW_LINKS) }
                    .forEach { files += LoadableFileImpl(it.toFile(), it.relativeTo(dirPath.parent).pathString) }
            } else {
                files += LoadableFileImpl(file)
            }
        }
        ctx.applicationCallbacks.onFileDrop(files)
    }

    fun setWindowTitle(windowTitle: String) {
        glfwSetWindowTitle(windowPtr, windowTitle)
    }

    fun setWindowIcon(icon: List<BufferedImage>) {
        MemoryStack.stackPush().use { stack ->
            val images = GLFWImage.malloc(icon.size, stack)
            icon.forEachIndexed { i, img ->
                val buffer = ImageTextureData(img, TexFormat.RGBA).data as Uint8BufferImpl
                images.get(i).apply {
                    width(img.width)
                    height(img.height)
                    buffer.useRaw {
                        pixels(it)
                    }
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