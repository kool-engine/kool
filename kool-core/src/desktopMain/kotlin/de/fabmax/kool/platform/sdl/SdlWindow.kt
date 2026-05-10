package de.fabmax.kool.platform.sdl

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.ImageDecoder
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import org.lwjgl.sdl.SDLError.SDL_GetError
import org.lwjgl.sdl.SDLEvents.*
import org.lwjgl.sdl.SDLPixels.SDL_PIXELFORMAT_ABGR8888
import org.lwjgl.sdl.SDLSurface.SDL_AddSurfaceAlternateImage
import org.lwjgl.sdl.SDLSurface.SDL_CreateSurface
import org.lwjgl.sdl.SDLVideo.*
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_CreateSurface
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_DestroySurface
import org.lwjgl.sdl.SDL_DropEvent
import org.lwjgl.sdl.SDL_Event
import org.lwjgl.sdl.SDL_WindowEvent
import org.lwjgl.vulkan.VkInstance
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.LinkOption
import kotlin.io.path.*

class SdlWindow(internal val handle: Long, title: String, ctx: Lwjgl3Context) : KoolWindowJvm {
    val input = SdlInput(this)

    override var isMouseOverWindow: Boolean = false

    override var parentScreenScale: Float = 1f; private set
    override var positionOnScreen: Vec2i = Vec2i.ZERO
    override var sizeOnScreen: Vec2i = Vec2i(1600, 900)
    override var renderResolutionFactor: Float = 1f
    override var framebufferSize: Vec2i = Vec2i(1600, 900); private set
    override var size: Vec2i = Vec2i(1600, 900); private set
    override val renderScale: Float
        get() = parentScreenScale * renderResolutionFactor

    override var title: String = title
        set(value) {
            field = value
            BackendScope.launch {
                SDL_SetWindowTitle(handle, value)
            }
        }

    override val capabilities: WindowCapabilities = WindowCapabilities.NONE.copy(canSetTitle = true)

    override val resizeListeners: BufferedList<WindowResizeListener> = BufferedList()
    override val scaleChangeListeners: BufferedList<ScaleChangeListener> = BufferedList()
    override val flagListeners: BufferedList<WindowFlagsListener> = BufferedList()
    override val closeListeners: BufferedList<WindowCloseListener> = BufferedList()
    override val dragAndDropListeners: BufferedList<DragAndDropListener> = BufferedList()

    override var windowTitleHoverHandler: WindowTitleHoverHandler = WindowTitleHoverHandler()

    override var flags: WindowFlags = WindowFlags(isHiddenTitleBar = ctx.config.isNoTitleBar)
        private set(value) {
            if (value != field) {
                val oldFlags = field
                field = value
                flagListeners.updated().forEach { it.onFlagsChanged(oldFlags, value) }
            }
        }

    init {
        scopedMem {
            val x = callocInt(1)
            val y = callocInt(1)
            check(SDL_GetWindowPosition(handle, x, y))
            positionOnScreen = Vec2i(x.get(0), y.get(0))
            parentScreenScale = SDL_GetWindowDisplayScale(handle)
        }

        val iconList = KoolSystem.configJvm.windowIcon.ifEmpty { KoolWindowJvm.loadDefaultWindowIconSet() }
        if (iconList.isNotEmpty()) {
            setWindowIcon(iconList)
        }
    }

    var isCloseRequested = false; private set
    private val sdlEvent = SDL_Event.create()

    override fun pollEvents() {
        while (true) {
            SDL_PollEvent(sdlEvent)
            when (sdlEvent.type()) {
                SDL_EVENT_POLL_SENTINEL -> break

                SDL_EVENT_MOUSE_MOTION -> input.handleMouseMotion(sdlEvent.motion())
                SDL_EVENT_MOUSE_WHEEL -> input.handleMouseWheel(sdlEvent.wheel())
                SDL_EVENT_MOUSE_BUTTON_DOWN -> input.handleMouseButton(sdlEvent.button())
                SDL_EVENT_MOUSE_BUTTON_UP -> input.handleMouseButton(sdlEvent.button())
                SDL_EVENT_MOUSE_ADDED -> { logI { "Mouse added" } }

                SDL_EVENT_KEY_DOWN -> input.handleKey(sdlEvent.key())
                SDL_EVENT_KEY_UP -> input.handleKey(sdlEvent.key())
                SDL_EVENT_TEXT_INPUT -> input.handleText(sdlEvent.text())
                SDL_EVENT_TEXT_EDITING -> {}
                SDL_EVENT_KEYMAP_CHANGED -> {}
                SDL_EVENT_KEYBOARD_ADDED -> { logI { "Keyboard added" } }

                SDL_EVENT_WINDOW_MOUSE_ENTER -> isMouseOverWindow = true
                SDL_EVENT_WINDOW_MOUSE_LEAVE -> isMouseOverWindow = false
                SDL_EVENT_WINDOW_SHOWN -> flags = flags.copy(isVisible = true)
                SDL_EVENT_WINDOW_HIDDEN -> flags = flags.copy(isVisible = true)
                SDL_EVENT_WINDOW_MINIMIZED -> flags = flags.copy(isMinimized = true)
                SDL_EVENT_WINDOW_MAXIMIZED -> flags = flags.copy(isMaximized = true)
                SDL_EVENT_WINDOW_RESTORED -> flags = flags.copy(isMinimized = false, isMaximized = false)
                SDL_EVENT_WINDOW_FOCUS_GAINED -> flags = flags.copy(isFocused = true)
                SDL_EVENT_WINDOW_FOCUS_LOST -> flags = flags.copy(isFocused = false)
                SDL_EVENT_WINDOW_EXPOSED -> flags = flags.copy(isOccluded = false)
                SDL_EVENT_WINDOW_OCCLUDED -> flags = flags.copy(isOccluded = true)
                SDL_EVENT_WINDOW_MOVED -> updateWindowPos(sdlEvent.window())
                SDL_EVENT_WINDOW_RESIZED -> updateWindowSize(sdlEvent.window())
                SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> updateWindowSize(sdlEvent.window())
                SDL_EVENT_WINDOW_DISPLAY_SCALE_CHANGED -> updateScreenScale(sdlEvent.window())
                SDL_EVENT_WINDOW_DISPLAY_CHANGED -> logD { "Display changed" }
                SDL_EVENT_WINDOW_CLOSE_REQUESTED -> { isCloseRequested = true }
                SDL_EVENT_WINDOW_HIT_TEST -> {}
                SDL_EVENT_WINDOW_ICCPROF_CHANGED -> {}
                SDL_EVENT_WINDOW_ENTER_FULLSCREEN -> {}
                SDL_EVENT_WINDOW_LEAVE_FULLSCREEN -> {}
                SDL_EVENT_WINDOW_METAL_VIEW_RESIZED -> {}
                SDL_EVENT_WINDOW_SAFE_AREA_CHANGED -> {}
                SDL_EVENT_WINDOW_HDR_STATE_CHANGED -> {}
                SDL_EVENT_WINDOW_DESTROYED -> {}

                SDL_EVENT_DROP_FILE -> handleFileDrop(sdlEvent.drop())
                SDL_EVENT_DROP_TEXT -> handleTextDrop(sdlEvent.drop())
                SDL_EVENT_DROP_BEGIN -> logD { "Drop begin" }
                SDL_EVENT_DROP_COMPLETE -> logD { "Drop complete" }
                SDL_EVENT_DROP_POSITION -> handleDropPosition(sdlEvent.drop())

                SDL_EVENT_CLIPBOARD_UPDATE -> {}

                SDL_EVENT_QUIT -> isCloseRequested = true

                else -> logD { "Unhandled SDL event type: ${sdlEvent.type().toHexString()}" }
            }
        }
    }

    private fun updateWindowPos(event: SDL_WindowEvent) {
        positionOnScreen = Vec2i(event.data1(), event.data2())
    }

    private fun updateWindowSize(event: SDL_WindowEvent) {
        val sz = Vec2i(event.data1(), event.data2())
        logD { "Window size changed to: ${sz.x}x${sz.y}" }

        sizeOnScreen = sz
        size = sz
        framebufferSize = sz

        UiScale.updateUiScaleFromWindowScale(renderScale)
        resizeListeners.updated().forEach { it.onResize(sz) }
        scaleChangeListeners.updated().forEach { it.onScaleChanged(renderScale) }
    }

    private fun updateScreenScale(event: SDL_WindowEvent) {
        parentScreenScale = SDL_GetWindowDisplayScale(handle)
        UiScale.updateUiScaleFromWindowScale(renderScale)
        logD { "Screen scale changed to: $parentScreenScale" }
    }

    private fun handleFileDrop(drop: SDL_DropEvent) {
        val files = mutableListOf<LoadableFile>()
        drop.dataString()?.let { path ->
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
                it.onFileDrop(files, Vec2f(drop.x(), drop.y()))
            }
        }
    }

    private fun handleTextDrop(drop: SDL_DropEvent) {
        drop.dataString()?.let { text ->
            dragAndDropListeners.forEachUpdated {
                it.onTextDrop(text, Vec2f(drop.x(), drop.y()))
            }
        }
    }

    private fun handleDropPosition(drop: SDL_DropEvent) {
        dragAndDropListeners.forEachUpdated {
            it.onDropCursorPos(Vec2f(drop.x(), drop.y()))
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

    override fun swapBuffers() { }

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