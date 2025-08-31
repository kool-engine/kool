package de.fabmax.kool.platform.swing

import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.platform.ClientApi
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.WindowSubsystem
import org.lwjgl.awt.AWT
import org.lwjgl.vulkan.KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME
import org.lwjgl.vulkan.awt.AWTVK
import java.awt.Canvas

class SwingWindowSubsystem(
    val providedCanvas: Canvas,
    val screenScale: Float = 1f,
    val onCreated: (() -> Unit)? = null
) : WindowSubsystem {

    var primaryWindow: KoolCanvas? = null
        private set

    override val isCloseRequested: Boolean = false
    override val input: PlatformInput get() = requireNotNull(primaryWindow?.input)

    override fun onEarlyInit() {
        check(AWT.isPlatformSupported()) {
            "AWT window subsystem is not supported on this platform"
        }
    }

    override fun queryRequiredVkExtensions(): List<String> = listOf(
        VK_KHR_SURFACE_EXTENSION_NAME,
        AWTVK.getSurfaceExtensionName()
    )

    override fun createWindow(clientApi: ClientApi, ctx: Lwjgl3Context): KoolWindowJvm {
        require(clientApi == ClientApi.UNMANAGED) {
            "SwingWindowSubsystem currently only supports UNMANAGED client API"
        }
        check(primaryWindow == null)
        primaryWindow = KoolCanvas(providedCanvas, this)
        return primaryWindow!!
    }

    override fun onBackendCreated(ctx: Lwjgl3Context) {
        onCreated?.invoke()
    }
}