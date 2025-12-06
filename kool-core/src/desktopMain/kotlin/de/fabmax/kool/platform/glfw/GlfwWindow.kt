package de.fabmax.kool.platform.glfw

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.pipeline.backend.vk.vkCheck
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.KHRSurface
import org.lwjgl.vulkan.VkInstance
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.LinkOption
import kotlin.io.path.*
import kotlin.math.roundToInt

class GlfwWindow(val clientApi: ClientApi, val ctx: Lwjgl3Context) : KoolWindowJvm {

    val windowHandle: Long
    val input: GlfwInput = GlfwInput(this)

    override var isMouseOverWindow: Boolean = false; internal set

    private var _screenPos = Vec2i(0, 0)
    private var _screenSize = Vec2i(KoolSystem.configJvm.windowSize)

    override var parentScreenScale: Float = 1f; private set

    override var positionInScreen: Vec2i
        get() = _screenPos
        set(value) {
            _screenPos = value
            BackendScope.launch {
                platformWindowHelper.setWindowPos(windowHandle, value.x, value.y)
            }
        }

    override var sizeOnScreen: Vec2i
        get() = _screenSize
        set(value) {
            _screenSize = value
            BackendScope.launch {
                platformWindowHelper.setWindowSize(windowHandle, value.x, value.y)
            }
        }

    override var renderResolutionFactor: Float = 1f
        set(value) {
            if (value != field) {
                field = value
                updateSizesAndScales()
            }
        }

    override var framebufferSize: Vec2i = Vec2i(KoolSystem.configJvm.windowSize); private set

    override var size = Vec2i(KoolSystem.configJvm.windowSize); private set

    override val renderScale: Float
        get() = parentScreenScale * renderResolutionFactor

    override var title: String = KoolSystem.configJvm.windowTitle
        set(value) {
            field = value
            glfwSetWindowTitle(windowHandle, value)
        }

    override var flags: WindowFlags = WindowFlags(isHiddenTitleBar = ctx.config.isNoTitleBar)
        private set(value) {
            if (value != field) {
                val oldFlags = field
                field = value
                flagListeners.updated().forEach { it.onFlagsChanged(oldFlags, value) }
            }
        }

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

    override val resizeListeners = BufferedList<WindowResizeListener>()
    override val scaleChangeListeners = BufferedList<ScaleChangeListener>()
    override val flagListeners = BufferedList<WindowFlagsListener>()
    override val closeListeners = BufferedList<WindowCloseListener>()
    override val dragAndDropListeners = BufferedList<DragAndDropListener>()

    override var windowTitleHoverHandler = WindowTitleHoverHandler()

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

        val subsystem = KoolSystem.configJvm.windowSubsystem as GlfwWindowSubsystem
        fsMonitor = if (KoolSystem.configJvm.monitor < 0) {
            subsystem.primaryMonitor!!.monitor
        } else {
            subsystem.monitors[KoolSystem.configJvm.monitor].monitor
        }
        windowHandle = glfwCreateWindow(
            KoolSystem.configJvm.windowSize.x,
            KoolSystem.configJvm.windowSize.y,
            KoolSystem.configJvm.windowTitle,
            0L,
            0L
        )
        check(windowHandle != MemoryUtil.NULL) { "Failed to create the GLFW window" }

        if (flags.isHiddenTitleBar) {
            platformWindowHelper.hideTitleBar(windowHandle)
        }

        val iconList = KoolSystem.configJvm.windowIcon.ifEmpty { KoolWindowJvm.loadDefaultWindowIconSet() }
        if (iconList.isNotEmpty()) {
            setWindowIcon(iconList)
        }

        val outInt1 = IntArray(1)
        val outInt2 = IntArray(1)
        val outFloat1 = FloatArray(1)
        val outFloat2 = FloatArray(1)
        if (GlfwWindowSubsystem.platform != GlfwPlatform.LinuxWayland) {
            glfwGetWindowPos(windowHandle, outInt1, outInt2)
            _screenPos = Vec2i(outInt1[0], outInt2[0])
        }
        windowedPos = _screenPos
        glfwGetFramebufferSize(windowHandle, outInt1, outInt2)
        framebufferSize = Vec2i(outInt1[0], outInt2[0])
        glfwGetWindowContentScale(windowHandle, outFloat1, outFloat2)
        parentScreenScale = outFloat1[0]
        updateSizesAndScales()

        _screenSize = Vec2i((size.x / parentScreenScale).roundToInt(), (size.y / parentScreenScale).roundToInt())
        windowedSize = _screenSize

        updateWindowFlagsFromState()

        glfwSetWindowSizeCallback(windowHandle) { _, w, h ->
            _screenSize = Vec2i(w, h)
            windowResizeHandler()
        }
        glfwSetFramebufferSizeCallback(windowHandle) { _, w, h ->
            framebufferSize = Vec2i(w, h)
            updateSizesAndScales()
        }
        glfwSetWindowPosCallback(windowHandle) { _, x, y ->
            _screenPos = platformWindowHelper.getWindowPos(windowHandle, x, y)
        }
        glfwSetWindowCloseCallback(windowHandle) {
            onWindowCloseRequest()
        }
        glfwSetWindowFocusCallback(windowHandle) { _, isFocused ->
            flags = flags.copy(isFocused = isFocused)
        }
        glfwSetWindowContentScaleCallback(windowHandle) { _, xScale, _ ->
            parentScreenScale = xScale
            updateSizesAndScales()
        }
        glfwSetDropCallback(windowHandle) { _, numFiles, pathPtr ->
            onFileDrop(numFiles, pathPtr)
        }
    }


    override fun setFullscreen(flag: Boolean) {
        if (flag != flags.isMaximized) {
            BackendScope.launch {
                if (flag) enableFullscreen() else disableFullscreen()
                updateWindowFlagsFromState()
            }
        }
    }

    override fun setMaximized(flag: Boolean) {
        if (flag != flags.isMaximized) {
            BackendScope.launch {
                if (flag) glfwMaximizeWindow(windowHandle) else glfwRestoreWindow(windowHandle)
                updateWindowFlagsFromState()
            }
        }
    }

    override fun setMinimized(flag: Boolean) {
        if (flag != flags.isMinimized) {
            BackendScope.launch {
                if (flag) glfwIconifyWindow(windowHandle) else glfwRestoreWindow(windowHandle)
                updateWindowFlagsFromState()
            }
        }
    }

    override fun setVisible(flag: Boolean) {
        if (flag != flags.isVisible) {
            BackendScope.launch {
                if (flag) glfwShowWindow(windowHandle) else glfwHideWindow(windowHandle)
                updateWindowFlagsFromState()
            }
        }
    }

    override fun setTitleBarVisibility(flag: Boolean) {
        if (flag != flags.isHiddenTitleBar) {
            BackendScope.launch {
                if (!flag) {
                    platformWindowHelper.hideTitleBar(windowHandle)
                } else {
                    logE { "Restoring the title bar from hidden state is not yet supported" }
                }
            }
        }
    }

    private fun updateWindowFlagsFromState() {
        flags = flags.copy(
            isVisible = glfwGetWindowAttrib(windowHandle, GLFW_VISIBLE) == GLFW_TRUE,
            isFocused = glfwGetWindowAttrib(windowHandle, GLFW_FOCUSED) == GLFW_TRUE,
            isMaximized = glfwGetWindowAttrib(windowHandle, GLFW_MAXIMIZED) == GLFW_TRUE,
            isMinimized = glfwGetWindowAttrib(windowHandle, GLFW_ICONIFIED) == GLFW_TRUE,
        )
    }

    private fun enableFullscreen() {
        windowedPos = positionInScreen
        windowedSize = sizeOnScreen
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
    }

    private fun disableFullscreen() {
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

    override fun close() {
        glfwSetWindowShouldClose(windowHandle, true)
    }

    private fun windowResizeHandler() {
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

    private fun updateSizesAndScales() {
        size = Vec2i(
            (framebufferSize.x * renderResolutionFactor).toInt(),
            (framebufferSize.y * renderResolutionFactor).toInt()
        )
        UiScale.updateUiScaleFromWindowScale(renderScale)
        resizeListeners.updated().forEach { it.onResize(size) }
        scaleChangeListeners.updated().forEach { it.onScaleChanged(renderScale) }
    }

    override fun pollEvents() {
        renderOnResizeFlag = KoolSystem.configJvm.updateOnWindowResize
        glfwPollEvents()
        renderOnResizeFlag = false

        val isMaximized = glfwGetWindowAttrib(windowHandle, GLFW_MAXIMIZED) != 0
        val isIconified = glfwGetWindowAttrib(windowHandle, GLFW_ICONIFIED) != 0
        if (isMaximized != flags.isMaximized || isIconified != flags.isMinimized) {
            flags = flags.copy(isMaximized = isMaximized, isMinimized = isIconified)
        }
    }

    override fun createVulkanSurface(instance: VkInstance): Long {
        check(clientApi == ClientApi.UNMANAGED) { "Client api needs to be UNMANAGED for Vulkan to work" }
        return scopedMem {
            val lp = mallocLong(1)
            vkCheck(GLFWVulkan.glfwCreateWindowSurface(instance, windowHandle, null, lp))
            lp[0]
        }
    }

    override fun destroyVulkanSurface(surface: Long, instance: VkInstance) {
        check(clientApi == ClientApi.UNMANAGED) { "Client api needs to be UNMANAGED for Vulkan to work" }

        KHRSurface.vkDestroySurfaceKHR(instance, surface, null)
        logD { "Destroyed surface" }
        glfwDestroyWindow(windowHandle)
        glfwTerminate()
        logD { "Destroyed GLFW window" }
    }

    override fun swapBuffers() {
        check(clientApi == ClientApi.OPEN_GL) { "Client api needs to be OpenGL for swapBuffers()" }
        glfwSwapBuffers(windowHandle)
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
        dragAndDropListeners.forEach { it.onFileDrop(files) }
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
