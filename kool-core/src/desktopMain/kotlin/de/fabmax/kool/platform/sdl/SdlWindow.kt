package de.fabmax.kool.platform.sdl

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.ClientApi
import de.fabmax.kool.platform.ImageDecoder
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import org.lwjgl.sdl.SDLError.SDL_GetError
import org.lwjgl.sdl.SDLPixels.SDL_PIXELFORMAT_ABGR8888
import org.lwjgl.sdl.SDLSurface.SDL_AddSurfaceAlternateImage
import org.lwjgl.sdl.SDLSurface.SDL_CreateSurface
import org.lwjgl.sdl.SDLVideo.*
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_CreateSurface
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_DestroySurface
import org.lwjgl.vulkan.VkInstance
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.LinkOption
import kotlin.io.path.*

class SdlWindow(internal val handle: Long, width: Int, height: Int, title: String, private val clientApi: ClientApi, ctx: Lwjgl3Context) : KoolWindowJvm {
    val input = SdlInput(this)

    override var isMouseOverWindow: Boolean = false; private set

    override var parentScreenScale: Float = 1f; private set
    override var positionOnScreen: Vec2i = Vec2i.ZERO; private set
    override var sizeOnScreen: Vec2i = Vec2i(width, height); private set

    override var renderResolutionFactor: Float = 1f
        set(value) {
            if (value != field) {
                field = value
                updateSizesAndScales()
            }
        }
    override var framebufferSize: Vec2i = Vec2i(width, height); private set
    override var size: Vec2i = Vec2i(width, height); private set
    override val renderScale: Float
        get() = parentScreenScale * renderResolutionFactor

    override var title: String = title
        set(value) {
            field = value
            BackendScope.launch {
                SDL_SetWindowTitle(handle, value)
            }
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
        canHideTitleBar = false,
    )

    override val resizeListeners: BufferedList<WindowResizeListener> = BufferedList()
    override val scaleChangeListeners: BufferedList<ScaleChangeListener> = BufferedList()
    override val flagListeners: BufferedList<WindowFlagsListener> = BufferedList()
    override val closeListeners: BufferedList<WindowCloseListener> = BufferedList()
    override val dragAndDropListeners: BufferedList<DragAndDropListener> = BufferedList()

    override var windowTitleHoverHandler: WindowTitleHoverHandler = WindowTitleHoverHandler()

    var isCloseRequested = false; private set

    private var isIconSet = false

    init {
        if (KoolSystem.platform.isMacOs) {
            // workaround for https://github.com/libsdl-org/SDL/issues/13920
            val events1 = drainSdlEventQueue()
            SDL_RaiseWindow(handle)
            val events2 = drainSdlEventQueue()
            handleEvents(events1)
            handleEvents(events2)
        }

        scopedMem {
            val x = callocInt(1)
            val y = callocInt(1)
            check(SDL_GetWindowPosition(handle, x, y))
            positionOnScreen = Vec2i(x.get(0), y.get(0))
            parentScreenScale = SDL_GetWindowDisplayScale(handle)
        }
        updateSizesAndScales()
    }

    override fun setSizeOnScreen(size: Vec2i) {
        BackendScope.launch {
            SDL_SetWindowSize(handle, size.x, size.y)
        }
    }

    override fun setPositionOnScreen(pos: Vec2i) {
        BackendScope.launch {
            SDL_SetWindowPosition(handle, size.x, size.y)
        }
    }

    override fun setFullscreen(flag: Boolean) {
        flags = flags.copy(isFullscreen = flag)
        BackendScope.launch {
            SDL_SetWindowFullscreen(handle, flag)
        }
    }

    override fun setMaximized(flag: Boolean) {
        flags = flags.copy(isMaximized = flag)
        BackendScope.launch {
            if (flag) {
                SDL_MaximizeWindow(handle)
            } else {
                SDL_RestoreWindow(handle)
            }
        }
    }

    override fun setMinimized(flag: Boolean) {
        flags = flags.copy(isMinimized = flag)
        BackendScope.launch {
            if (flag) {
                SDL_MinimizeWindow(handle)
            } else {
                SDL_RestoreWindow(handle)
            }
        }
    }

    override fun setVisible(flag: Boolean) {
        flags = flags.copy(isVisible = flag)
        BackendScope.launch {
            if (flag) {
                SDL_ShowWindow(handle)
            } else {
                SDL_HideWindow(handle)
            }
        }
    }

    override fun requestFocus() {
        BackendScope.launch { SDL_RaiseWindow(handle) }
    }

    override fun pollEvents() {
        handleEvents(drainSdlEventQueue())
    }

    private fun handleEvents(events: List<SdlEvent>) {
        for (event in events) {
            when (event) {
                is SdlEvent.Motion -> input.handleMouseMotion(event)
                is SdlEvent.Button -> input.handleMouseButton(event)
                is SdlEvent.Wheel -> input.handleMouseWheel(event)
                is SdlEvent.Drop -> handleDropEvent(event)
                is SdlEvent.Key -> input.handleKey(event)
                is SdlEvent.Text -> input.handleText(event)
                is SdlEvent.Gamepad -> SdlControllers.gamepadEvent(event)
                is SdlEvent.GamepadButton -> SdlControllers.gamepadButtonEvent(event)
                is SdlEvent.GamepadAxis -> SdlControllers.gamepadAxisEvent(event)
                is SdlEvent.JoystickEvent -> {} // ignored
                is SdlEvent.Window -> handleWindowEvent(event)
                SdlEvent.Quit -> isCloseRequested = true
                is SdlEvent.Other -> logD { "Unhandled SDL event: ${event.name}" }
            }
        }
    }

    private fun handleWindowEvent(window: SdlEvent.Window) {
        when (window.type) {
            SdlEventType.WINDOW_MOUSE_ENTER -> isMouseOverWindow = true
            SdlEventType.WINDOW_MOUSE_LEAVE -> isMouseOverWindow = false
            SdlEventType.WINDOW_MINIMIZED -> flags = flags.copy(isMinimized = true)
            SdlEventType.WINDOW_MAXIMIZED -> flags = flags.copy(isMaximized = true)
            SdlEventType.WINDOW_RESTORED -> flags = flags.copy(isMinimized = false, isMaximized = false)
            SdlEventType.WINDOW_FOCUS_GAINED -> flags = flags.copy(isFocused = true)
            SdlEventType.WINDOW_FOCUS_LOST -> flags = flags.copy(isFocused = false)
            SdlEventType.WINDOW_EXPOSED -> flags = flags.copy(isOccluded = false)
            SdlEventType.WINDOW_OCCLUDED -> flags = flags.copy(isOccluded = true)
            SdlEventType.WINDOW_ENTER_FULLSCREEN -> flags = flags.copy(isFullscreen = true)
            SdlEventType.WINDOW_LEAVE_FULLSCREEN -> flags = flags.copy(isFullscreen = false)
            SdlEventType.WINDOW_MOVED -> updateWindowPos(window)
            SdlEventType.WINDOW_RESIZED -> updateWindowSize(window)
            SdlEventType.WINDOW_PIXEL_SIZE_CHANGED -> updateFramebufferSize(window)
            SdlEventType.WINDOW_DISPLAY_SCALE_CHANGED -> updateScreenScale()
            SdlEventType.WINDOW_CLOSE_REQUESTED -> { isCloseRequested = true }
            SdlEventType.WINDOW_HIDDEN -> flags = flags.copy(isVisible = true)
            SdlEventType.WINDOW_SHOWN -> {
                flags = flags.copy(isVisible = true)
                if (!isIconSet) {
                    isIconSet = true
                    initWindowIcon()
                }
            }
            else -> logD { "Unhandled window event: ${window.name}" }
        }
    }

    private fun initWindowIcon() {
        val iconList = KoolSystem.configJvm.windowIcon.ifEmpty { KoolWindowJvm.loadDefaultWindowIconSet() }
        if (iconList.isNotEmpty()) {
            setWindowIcon(iconList)
        }
    }

    private fun updateWindowPos(event: SdlEvent.Window) {
        positionOnScreen = Vec2i(event.data1, event.data2)
    }

    private fun updateFramebufferSize(event: SdlEvent.Window) {
        val sz = Vec2i(event.data1, event.data2)
        logD { "Framebuffer size changed to: ${sz.x}x${sz.y}" }
        framebufferSize = sz
        updateSizesAndScales()
    }

    private fun updateWindowSize(event: SdlEvent.Window) {
        val sz = Vec2i(event.data1, event.data2)
        logD { "Window size changed to: ${sz.x}x${sz.y}" }
        sizeOnScreen = sz
        updateSizesAndScales()
    }

    private fun updateScreenScale() {
        parentScreenScale = SDL_GetWindowDisplayScale(handle)
        logD { "Screen scale changed to: $parentScreenScale" }
        updateSizesAndScales()
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

    private fun handleDropEvent(drop: SdlEvent.Drop) {
        when (drop.type) {
            SdlEventType.DROP_FILE -> {
                val files = mutableListOf<LoadableFile>()
                drop.data?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
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
                }
                if (files.isNotEmpty()) {
                    logD { "Files dropped: ${files.map { it.name }}" }
                    dragAndDropListeners.forEachUpdated {
                        it.onFileDrop(files, Vec2f(drop.x, drop.y))
                    }
                }
            }
            SdlEventType.DROP_TEXT -> {
                drop.data?.let { text ->
                    dragAndDropListeners.forEachUpdated {
                        it.onTextDrop(text, Vec2f(drop.x, drop.y))
                    }
                }
            }
            SdlEventType.DROP_POSITION -> {
                dragAndDropListeners.forEachUpdated {
                    it.onDropCursorPos(Vec2f(drop.x, drop.y))
                }
            }
        }
    }

    override fun createVulkanSurface(instance: VkInstance): Long {
        scopedMem {
            val surface = callocLong(1)
            check(SDL_Vulkan_CreateSurface(handle, instance, null, surface)) {
                "Failed to create Vulkan surface for SDL window $handle"
            }
            return surface[0]
        }
    }

    override fun destroyVulkanSurface(surface: Long, instance: VkInstance) {
        SDL_Vulkan_DestroySurface(instance, surface, null)
    }

    override fun swapBuffers() {
        check(clientApi == ClientApi.OPEN_GL) { "Client api needs to be OpenGL for swapBuffers()" }
        SDL_GL_SwapWindow(handle)
    }

    override fun close() {
        isCloseRequested = true
    }

    fun setWindowIcon(icon: BufferedImage) = setWindowIcon(listOf(icon))

    fun setWindowIcon(icons: List<BufferedImage>) {
        val image = icons.firstOrNull() ?: return

        val surface = checkNotNull(SDL_CreateSurface(image.width, image.height, SDL_PIXELFORMAT_ABGR8888))
        val buffer = ImageDecoder.loadBufferedImage(image, TexFormat.RGBA).data as Uint8BufferImpl
        buffer.useRaw {
            checkNotNull(surface.pixels()).put(it)
        }

        for (i in 1 until icons.size) {
            val altIcon = icons[i]
            val altSurface = checkNotNull(SDL_CreateSurface(altIcon.width, altIcon.height, SDL_PIXELFORMAT_ABGR8888))
            val altBuffer = ImageDecoder.loadBufferedImage(altIcon, TexFormat.RGBA).data as Uint8BufferImpl
            altBuffer.useRaw {
                checkNotNull(altSurface.pixels()).put(it)
            }
            SDL_AddSurfaceAlternateImage(surface, altSurface)
        }

        if (!SDL_SetWindowIcon(handle, surface)) {
            logE { "Failed to set window icon for SDL window $handle: $${SDL_GetError()}" }
        }
    }
}