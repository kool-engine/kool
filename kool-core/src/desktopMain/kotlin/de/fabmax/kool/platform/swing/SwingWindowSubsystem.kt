package de.fabmax.kool.platform.swing

import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.platform.ClientApi
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.WindowSubsystem
import de.fabmax.kool.util.ApplicationScope
import org.lwjgl.awt.AWT
import org.lwjgl.vulkan.KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME
import org.lwjgl.vulkan.awt.AWTVK
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import kotlin.system.exitProcess


class SwingWindowSubsystem(
    val providedCanvas: Canvas,
    val screenScale: Float = 1f,
    val onCreated: (() -> Unit)? = null
) : WindowSubsystem {

    var primaryWindow: KoolCanvas? = null
        private set

    override var isCloseRequested: Boolean = false
        private set
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

    companion object {
        fun simpleWindow(windowTitle: String, screenScale: Float = 1f): SwingWindowSubsystem {
            val frame = JFrame(windowTitle)
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
            frame.layout = BorderLayout()
            frame.setPreferredSize(Dimension(1600, 900))
            val canvas = Canvas()
            frame.add(canvas, BorderLayout.CENTER)
            frame.pack()

            val subsystem = SwingWindowSubsystem(canvas, screenScale) { frame.isVisible = true }
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    subsystem.isCloseRequested = true
                    ApplicationScope.job.invokeOnCompletion {
                        exitProcess(0)
                    }
                }
            })
            return subsystem
        }
    }
}