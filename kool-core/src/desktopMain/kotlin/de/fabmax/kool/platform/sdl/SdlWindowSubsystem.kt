package de.fabmax.kool.platform.sdl

import de.fabmax.kool.Clipboard
import de.fabmax.kool.ClipboardImpl
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.BackendScope
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.lwjgl.sdl.SDLClipboard.SDL_GetClipboardText
import org.lwjgl.sdl.SDLClipboard.SDL_SetClipboardText
import org.lwjgl.sdl.SDLError.SDL_ClearError
import org.lwjgl.sdl.SDLError.SDL_GetError
import org.lwjgl.sdl.SDLInit.SDL_INIT_VIDEO
import org.lwjgl.sdl.SDLInit.SDL_Init
import org.lwjgl.sdl.SDLVideo.*
import org.lwjgl.sdl.SDLVulkan.SDL_Vulkan_GetInstanceExtensions
import org.lwjgl.system.MemoryUtil

object SdlWindowSubsystem : WindowSubsystem {
    var primaryWindow: SdlWindow? = null; private set
    override val isCloseRequested: Boolean get() = primaryWindow?.isCloseRequested ?: false
    override val input: PlatformInput get() = requireNotNull(primaryWindow?.input)

    override fun queryRequiredVkExtensions(): List<String> {
        return buildList {
            val ptrBuffer = checkNotNull(SDL_Vulkan_GetInstanceExtensions())
            for (i in 0 until ptrBuffer.remaining()) {
                val extension = MemoryUtil.memASCII(ptrBuffer.get(i))
                if (extension != "VK_KHR_portability_enumeration") {
                    add(extension)
                }
            }
        }
    }

    override fun createWindow(clientApi: ClientApi, glInitCallback: GlInitCallback?, ctx: Lwjgl3Context): KoolWindowJvm {
        val apiFlag = when (clientApi) {
            ClientApi.OPEN_GL -> SDL_WINDOW_OPENGL
            ClientApi.UNMANAGED -> 0
        }
        val title = KoolSystem.configJvm.windowTitle
        val windowHandle = SDL_CreateWindow(title, 1600, 900, SDL_WINDOW_RESIZABLE or SDL_WINDOW_HIGH_PIXEL_DENSITY or apiFlag)
        logD { "Created SDL window" }

        if (clientApi == ClientApi.OPEN_GL) {
            check(SDL_GL_CreateContext(windowHandle) != 0L)
            SDL_GL_SetSwapInterval(if (KoolSystem.configJvm.isVsync) 1 else 0)
            requireNotNull(glInitCallback).initGl()
        }

        val window = SdlWindow(windowHandle, title, clientApi, ctx)
        if (primaryWindow == null) {
            primaryWindow = window
        }
        logSdlError("createWindow")
        return window
    }

    override fun onEarlyInit() {
        // Put AWT in headless mode on macOS, to avoid clashes in the window event thread
        // This has to happen before *any* AWT class (BufferedImage, etc.) is loaded.
        if (KoolSystem.platform.isMacOs) {
            logD { "Detected macOS. Enabling AWT headless mode to mitigate AWT / GLFW compatibility issues" }
            System.setProperty("java.awt.headless", "true")
        }
        check(SDL_Init(SDL_INIT_VIDEO)) { "Failed to initialize SDL" }
        logSdlError("onEarlyInit")

        Clipboard.impl = SdlClipboard
    }

    override fun onBackendCreated(ctx: Lwjgl3Context) {
        logSdlError("onBackendCreated")
    }

    override fun runRenderLoop() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        runBlocking {
            logI { "Starting SDL render loop" }
            val window = primaryWindow!!
            while (!isCloseRequested) {
                window.pollEvents()

                if (!window.flags.isMinimized) {
                    ctx.renderFrame()
                } else {
                    Thread.sleep(10)
                }
                logSdlError("runRenderLoop")
            }
        }
        shutdown()
    }

    private fun logSdlError(tag: String) {
        val err = SDL_GetError()
        if (!err.isNullOrBlank()) {
            logW(tag) { "SDL error: $err" }
            SDL_ClearError()
        }
    }
}

internal object SdlClipboard : ClipboardImpl {
    override fun copyToClipboard(string: String) {
        logD { "Copy to clipboard: $string" }
        BackendScope.launch { SDL_SetClipboardText(string) }
    }

    override fun getStringFromClipboard(receiver: (String?) -> Unit) {
        BackendScope.launch {
            val clipboardText = SDL_GetClipboardText()
            logD { "Got from clipboard: $clipboardText" }
            receiver(clipboardText)
        }
    }
}