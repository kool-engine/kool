package de.fabmax.kool.platform.sdl

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import kotlinx.coroutines.runBlocking
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

    override fun createWindow(clientApi: ClientApi, glCallbacks: GlWindowCallbacks?, ctx: Lwjgl3Context): KoolWindowJvm {
        val apiFlag = when (clientApi) {
            ClientApi.OPEN_GL -> SDL_WINDOW_OPENGL
            ClientApi.UNMANAGED -> 0
        }
        val windowHandle = SDL_CreateWindow("SDL Window", 1600, 900, SDL_WINDOW_RESIZABLE or SDL_WINDOW_HIGH_PIXEL_DENSITY or apiFlag)
        logD { "Created SDL window: $windowHandle" }

        val window = SdlWindow(windowHandle, ctx)
        if (primaryWindow == null) {
            primaryWindow = window
        }
        return window
    }

    override fun onEarlyInit() {
        // Put AWT in headless mode on macOS, to avoid clashes in the window event thread
        // This has to happen before *any* AWT class (BufferedImage, etc.) is loaded.
        val osName = System.getProperty("os.name", "unknown").lowercase()
        if ("mac os" in osName || "darwin" in osName || "osx" in osName) {
            logD { "Detected macOS. Enabling AWT headless mode to mitigate AWT / GLFW compatibility issues" }
            System.setProperty("java.awt.headless", "true")
        }

        check(SDL_Init(SDL_INIT_VIDEO)) { "Failed to initialize SDL" }
    }

    override fun onBackendCreated(ctx: Lwjgl3Context) {

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
            }
        }
        shutdown()
    }

}