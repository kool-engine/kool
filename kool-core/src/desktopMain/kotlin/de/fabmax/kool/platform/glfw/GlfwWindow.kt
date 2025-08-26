package de.fabmax.kool.platform.glfw

import de.fabmax.kool.*
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlinx.coroutines.runBlocking
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.LinkOption
import kotlin.io.path.*
import kotlin.math.roundToInt

class GlfwWindow(val ctx: Lwjgl3Context) : KoolWindow {

    val windowHandle: Long

    private val _windowPos = MutableVec2i()
    private val _physicalWindowSize = MutableVec2i()
    private val _scaledWindowSize = MutableVec2i()

    override var position = Vec2i(0, 0); private set
    override var title: String = KoolSystem.configJvm.windowTitle; private set

    override var physicalSize = Vec2i(KoolSystem.configJvm.windowSize)
        private set(value) {
            if (value != field) {
                field = value
                physicalResizeListeners.updated().forEach { it.onResize(value) }
            }
        }

    override var scaledSize = Vec2i(KoolSystem.configJvm.windowSize); private set

    override var scale: Float = 1f
        private set(value) {
            if (value != field) {
                field = value
                updateRenderScale(windowScale = value)
            }
        }

    override val renderScale: Float
        get() = scale * ctx.renderScaleMultiplier

    override var flags = WindowFlags(
        isFullscreen = false,
        isMaximized = false,
        isMinimized = false,
        isVisible = false,
        isFocused = false,
        isHiddenTitleBar = false,
    ); private set(value) {
        if (value != field) {
            val oldFlags = field
            field = value
            flagListeners.updated().forEach { it.onFlagsChanged(oldFlags, value) }
        }
    }

    override val scaledResizeListeners = BufferedList<WindowResizeListener>()
    override val physicalResizeListeners = BufferedList<WindowResizeListener>()
    override val scaleChangeListeners = BufferedList<ScaleChangeListener>()
    override val flagListeners = BufferedList<WindowFlagsListener>()
    override val closeListeners = BufferedList<WindowCloseListener>()

    override val capabilities: WindowCapabilities = WindowCapabilities(
        canSetSize = true,
        canSetPosition = true,
        canSetFullscreen = true,
        canMaximize = true,
        canMinimize = true,
        canSetVisibility = true,
        canSetTitle = true,
        canHideTitleBar = KoolSystem.platform.isWindows
    )

    private val fsMonitor: Long
    private var windowedSize = Vec2i.ZERO
    private var windowedPos = Vec2i.ZERO

    private var renderOnResizeFlag = false

    private val platformWindowHelper = when (OsInfo.os) {
        OsInfo.OS.WINDOWS -> PlatformWindowHelperWindows(this)
        else -> PlatformWindowHelperCommon()
    }

    init {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        fsMonitor = if (KoolSystem.configJvm.monitor < 0) {
            DesktopImpl.primaryMonitor.monitor
        } else {
            DesktopImpl.monitors[KoolSystem.configJvm.monitor].monitor
        }
        windowHandle = glfwCreateWindow(
            KoolSystem.configJvm.windowSize.x,
            KoolSystem.configJvm.windowSize.y,
            KoolSystem.configJvm.windowTitle,
            0L,
            0L
        )
        check(windowHandle != MemoryUtil.NULL) { "Failed to create the GLFW window" }

        if (KoolSystem.configJvm.windowIcon.isNotEmpty()) {
            setWindowIcon(KoolSystem.configJvm.windowIcon)
        }

        val outInt1 = IntArray(1)
        val outInt2 = IntArray(1)
        val outFloat1 = FloatArray(1)
        val outFloat2 = FloatArray(1)
        glfwGetWindowPos(windowHandle, outInt1, outInt2)
        position = Vec2i(outInt1[0], outInt2[0])
        windowedPos = position
        glfwGetFramebufferSize(windowHandle, outInt1, outInt2)
        physicalSize = Vec2i(outInt1[0], outInt2[0])
        glfwGetWindowContentScale(windowHandle, outFloat1, outFloat2)
        scale = outFloat1[0]

        scaledSize = Vec2i((physicalSize.x / scale).roundToInt(), (physicalSize.y / scale).roundToInt())
        windowedSize = scaledSize

        flags = flags.copy(
            isFocused = glfwGetWindowAttrib(windowHandle, GLFW_FOCUSED) == GLFW_TRUE
        )

        glfwSetWindowSizeCallback(windowHandle) { _, w, h ->
            windowSizeChanged(w, h)
        }
        glfwSetFramebufferSizeCallback(windowHandle) { _, w, h ->
            physicalSize = Vec2i(w, h)
        }
        glfwSetWindowPosCallback(windowHandle) { _, x, y ->
            position = platformWindowHelper.getWindowPos(windowHandle, x, y)
        }
        glfwSetWindowCloseCallback(windowHandle) {
            onWindowCloseRequest()
        }
        glfwSetWindowFocusCallback(windowHandle) { _, isFocused ->
            flags = flags.copy(isFocused = isFocused)
        }
        glfwSetWindowContentScaleCallback(windowHandle) { _, xScale, _ ->
            scale = xScale
        }
        glfwSetDropCallback(windowHandle) { _, numFiles, pathPtr ->
            onFileDrop(numFiles, pathPtr)
        }
    }

    override fun setPosition(position: Vec2i) {
        platformWindowHelper.setWindowPos(windowHandle, position.x, position.y)
    }

    override fun setScaledSize(size: Vec2i) {
        //platformWindowHelper.setWindowSize(windowHandle, size.x, size.y)
    }

    override fun setTitle(newTitle: String) {
        glfwSetWindowTitle(windowHandle, newTitle)
        title = newTitle
    }

    override fun setFullscreen(enabled: Boolean) {
        if (enabled == flags.isFullscreen) return
        if (enabled) {
            windowedPos = position
            windowedSize = scaledSize

            val vidMode = glfwGetVideoMode(fsMonitor)!!
            glfwSetWindowMonitor(
                windowHandle,
                fsMonitor,
                0,
                0,
                vidMode.width(),
                vidMode.height(),
                GLFW_DONT_CARE
            )
            if (KoolSystem.configJvm.isVsync) {
                glfwSwapInterval(1)
            }
        } else {
            glfwSetWindowMonitor(
                windowHandle,
                0,
                windowedPos.x,
                windowedPos.y,
                windowedSize.x,
                windowedSize.y,
                GLFW_DONT_CARE
            )
        }
        flags = flags.copy(isFocused = enabled)
    }

    override fun setMaximized(enabled: Boolean) {
        val wasMaximized = glfwGetWindowAttrib(windowHandle, GLFW_MAXIMIZED) != 0
        if (enabled != wasMaximized) {
            if (enabled) {
                glfwMaximizeWindow(windowHandle)
            } else {
                glfwRestoreWindow(windowHandle)
            }
        }
        flags = flags.copy(isMaximized = enabled)
    }

    override fun setMinimized(enabled: Boolean) {
        val wasMinimized = glfwGetWindowAttrib(windowHandle, GLFW_ICONIFIED) != 0
        if (enabled != wasMinimized) {
            if (enabled) {
                glfwIconifyWindow(windowHandle)
            } else {
                glfwRestoreWindow(windowHandle)
            }
        }
        flags = flags.copy(isMinimized = enabled)
    }

    override fun setVisible(isVisible: Boolean) {
        if (isVisible) {
            glfwShowWindow(windowHandle)
        } else {
            glfwHideWindow(windowHandle)
        }
    }

    override fun close() {
        glfwSetWindowShouldClose(windowHandle, true)
    }

    override fun setTitleBarVisibility(visible: Boolean) {
        if (visible == flags.isHiddenTitleBar) {
            return
        }
        if (!capabilities.canHideTitleBar) {
            logE { "Hiding the title bar is not supported on this platform" }
            return
        }
        if (!visible) {
            platformWindowHelper.hideTitleBar(windowHandle)
            flags = flags.copy(isHiddenTitleBar = true)
        } else {
            logE { "Unhiding the title bar is not yet supported" }
        }
    }

    internal fun updateRenderScale(renderScaleMultiplier: Float = ctx.renderScaleMultiplier, windowScale: Float = scale) {
        UiScale.updateUiScaleFromWindowScale(windowScale * renderScaleMultiplier)
        scaleChangeListeners.updated().forEach { it.onScaleChanged(windowScale) }
    }

    fun pollEvents() {
        renderOnResizeFlag = KoolSystem.configJvm.updateOnWindowResize
        glfwPollEvents()
        renderOnResizeFlag = false
    }

    private fun windowSizeChanged(width: Int, height: Int) {
        val newSize = Vec2i(width, height)
        if (newSize != scaledSize) {
            scaledSize = newSize
            scaledResizeListeners.updated().forEach { it.onResize(newSize) }
        }

        // with GLFW, window resizing blocks the main-loop, call renderFrame() from here to update window content
        // during window resizing
        if (renderOnResizeFlag) {
            runBlocking {
                ctx.renderFrame()
                if (ctx.backend is RenderBackendVk) {
                    // Vulkan needs two renders for swapchain update
                    ctx.renderFrame()
                }
            }
        }
    }

    private fun onWindowCloseRequest() {
        if (closeListeners.updated().any { !it.onCloseRequest() }) {
            logD { "Window close request was suppressed by application callback" }
            glfwSetWindowShouldClose(windowHandle, false)
        }
    }

    private fun onFileDrop(numFiles: Int, pathPtr: Long) {
        val files = mutableListOf<LoadableFile>()
        val pathPtrs = PointerBuffer.create(pathPtr, numFiles)
        repeat(numFiles) { i ->
            val file = File(MemoryUtil.memUTF8(pathPtrs[i]))
            if (file.isDirectory) {
                val dirPath = file.toPath()
                dirPath.walk(PathWalkOption.INCLUDE_DIRECTORIES)
                    .filter { it.isRegularFile(LinkOption.NOFOLLOW_LINKS) }
                    .forEach {
                        files += LoadableFileImpl(it.toFile(), it.relativeTo(dirPath.parent).pathString)
                    }
            } else {
                files += LoadableFileImpl(file)
            }
        }
        TODO()
        //ctx.applicationCallbacks.onFileDrop(files)
    }

    fun setWindowTitle(windowTitle: String) {
        glfwSetWindowTitle(windowHandle, windowTitle)
    }

    fun setWindowIcon(icon: List<BufferedImage>) {
        val platform = glfwGetPlatform()
        if (platform == GLFW_PLATFORM_COCOA || platform == GLFW_PLATFORM_WAYLAND) {
            // bail out on platforms which do not support setting a window icon (either because there is no window
            // icon or because glfw doesn't support that)
            return
        }

        MemoryStack.stackPush().use { stack ->
            val images = GLFWImage.malloc(icon.size, stack)
            icon.forEachIndexed { i, img ->
                val buffer = ImageDecoder.loadBufferedImage(img, TexFormat.RGBA).data as Uint8BufferImpl
                images.get(i).apply {
                    width(img.width)
                    height(img.height)
                    buffer.useRaw { pixels(it) }
                }
            }
            glfwSetWindowIcon(windowHandle, images)
        }
    }
}