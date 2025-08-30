package de.fabmax.kool.platform

import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.KoolWindow
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VkInstance
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

interface WindowSubsystem {
    val isCloseRequested: Boolean
    val input: PlatformInput

    fun queryRequiredVkExtensions(): List<String>
    fun createWindow(clientApi: ClientApi, ctx: Lwjgl3Context): KoolWindowJvm

    fun onEarlyInit()
    fun onBackendCreated(ctx: Lwjgl3Context)
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
