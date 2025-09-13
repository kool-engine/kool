package de.fabmax.kool.platform

import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.KoolWindow
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.util.*
import kotlinx.coroutines.cancel
import org.lwjgl.vulkan.VkInstance
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

interface WindowSubsystem {
    val isCloseRequested: Boolean
    val input: PlatformInput

    fun queryRequiredVkExtensions(): List<String>
    fun createWindow(clientApi: ClientApi, glCallbacks: GlWindowCallbacks?, ctx: Lwjgl3Context): KoolWindowJvm

    fun onEarlyInit()
    fun onBackendCreated(ctx: Lwjgl3Context)
    fun runRenderLoop()
}

fun WindowSubsystem.createVkWindow(ctx: Lwjgl3Context) = createWindow(ClientApi.UNMANAGED, null, ctx)
fun WindowSubsystem.createGlWindow(glCallbacks: GlWindowCallbacks, ctx: Lwjgl3Context) = createWindow(ClientApi.OPEN_GL, glCallbacks, ctx)


internal fun WindowSubsystem.shutdown() {
    logI { "Exiting..." }
    val ctx = KoolSystem.requireContext()
    ctx.scenes.forEach { it.release() }
    ctx.backgroundScene.release()
    ctx.onShutdown.updated().forEach { it(ctx) }

    // Somewhat hacky: Many releasables release their resources with a delay of a few frames. Increment
    // frame counter and execute dispatched tasks to run their release code before destroying the backend.
    repeat(3) {
        Time.frameCount++
        KoolDispatchers.Frontend.executeDispatchedTasks()
        KoolDispatchers.Synced.executeDispatchedTasks()
        KoolDispatchers.Backend.executeDispatchedTasks()
    }
    ctx.backend.cleanup(ctx)

    ApplicationScope.cancel()
}

enum class ClientApi {
    OPEN_GL,
    UNMANAGED
}

interface KoolWindowJvm : KoolWindow {
    val isMouseOverWindow: Boolean

    fun pollEvents()

    // vulkan specific functions
    fun createVulkanSurface(instance: VkInstance): Long
    fun destroyVulkanSurface(surface: Long, instance: VkInstance)

    // OpenGL specific functions
    fun swapBuffers()

    companion object {
        val defaultWindowIcon: BufferedImage? by lazy {
            try {
                KoolConfigJvm::class.java.classLoader.getResourceAsStream("icon.png").use {
                    ImageIO.read(it)
                }
            } catch (e: Exception) {
                logE { "Failed to load default window icon" }
                null
            }
        }
    }
}

interface GlWindowCallbacks {
    fun initGl()
    fun drawFrame()
}
