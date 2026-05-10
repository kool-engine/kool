package de.fabmax.kool.platform.sdl

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.*
import org.lwjgl.sdl.SDLEvents.*
import org.lwjgl.sdl.SDLVideo.SDL_GetWindowDisplayScale
import org.lwjgl.sdl.SDLVideo.SDL_GetWindowPosition
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_CreateSurface
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_DestroySurface
import org.lwjgl.sdl.SDL_Event
import org.lwjgl.sdl.SDL_WindowEvent
import org.lwjgl.vulkan.VkInstance

class SdlWindow(private val handle: Long, ctx: Lwjgl3Context) : KoolWindowJvm {
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

    override var title: String = "Sdl window"

    override val capabilities: WindowCapabilities = WindowCapabilities.NONE

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
                SDL_EVENT_TEXT_EDITING -> {}
                SDL_EVENT_TEXT_INPUT -> {}
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
                SDL_EVENT_WINDOW_CLOSE_REQUESTED -> { println("close requested") }
                SDL_EVENT_WINDOW_DISPLAY_CHANGED -> { println("display changed") }
                SDL_EVENT_WINDOW_DISPLAY_SCALE_CHANGED -> { println("display changed: ${sdlEvent.window().data1()}") }
                SDL_EVENT_WINDOW_HIT_TEST -> { println("hittest") }
                SDL_EVENT_WINDOW_ICCPROF_CHANGED -> {}
                SDL_EVENT_WINDOW_ENTER_FULLSCREEN -> {}
                SDL_EVENT_WINDOW_LEAVE_FULLSCREEN -> {}
                SDL_EVENT_WINDOW_METAL_VIEW_RESIZED -> {}
                SDL_EVENT_WINDOW_SAFE_AREA_CHANGED -> {}
                SDL_EVENT_WINDOW_HDR_STATE_CHANGED -> {}
                SDL_EVENT_WINDOW_DESTROYED -> {}

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
        sizeOnScreen = sz
        size = sz
        framebufferSize = sz

        UiScale.updateUiScaleFromWindowScale(renderScale)
        resizeListeners.updated().forEach { it.onResize(sz) }
        scaleChangeListeners.updated().forEach { it.onScaleChanged(renderScale) }
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
}