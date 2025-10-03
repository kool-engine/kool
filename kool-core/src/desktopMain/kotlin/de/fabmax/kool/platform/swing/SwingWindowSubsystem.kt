package de.fabmax.kool.platform.swing

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.lwjgl.awt.AWT
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glEnd
import org.lwjgl.opengl.awt.AWTGLCanvas
import org.lwjgl.opengl.awt.GLData
import org.lwjgl.vulkan.KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME
import org.lwjgl.vulkan.awt.AWTVK
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Taskbar
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.system.exitProcess


class SwingWindowSubsystem(
    val providedCanvas: Canvas? = null,
    val makeFocusable: Boolean = true,
    val onCreated: (() -> Unit)? = null,
    val onClosed: (() -> Unit)? = null,
) : WindowSubsystem {

    private var canvasWrapper: CanvasWrapper? = null

    override val input: PlatformInput get() = requireNotNull(canvasWrapper?.input)
    override var isCloseRequested: Boolean = false
        private set
    private val closeSignal = CompletableDeferred<Unit>()

    override fun onEarlyInit() {
        check(AWT.isPlatformSupported()) {
            "AWT window subsystem is not supported on this platform"
        }
    }

    override fun queryRequiredVkExtensions(): List<String> = listOf(
        VK_KHR_SURFACE_EXTENSION_NAME,
        AWTVK.getSurfaceExtensionName()
    )

    override fun createWindow(clientApi: ClientApi, glCallbacks: GlWindowCallbacks?, ctx: Lwjgl3Context): KoolWindowJvm {
        check(canvasWrapper == null)

        val canvas = if (providedCanvas != null) {
            when (clientApi) {
                ClientApi.OPEN_GL -> check(providedCanvas is AWTGLCanvas) {
                    "For OpenGL, the provided canvas needs to be a AWTGLCanvas"
                }
                ClientApi.UNMANAGED -> check(providedCanvas !is AWTGLCanvas) {
                    "For unmanaged (i.e. Vulkan) clients, the provided canvas needs to be a regular canvas"
                }
            }
            providedCanvas
        } else {
            when (clientApi) {
                ClientApi.UNMANAGED -> Canvas()
                ClientApi.OPEN_GL -> {
                    val data = GLData()
                    data.samples = ctx.config.numSamples
                    if (ctx.config.isVsync) {
                        data.swapInterval = 1
                    }
                    KoolGlCanvas(data)
                }
            }
        }

        if (providedCanvas == null) {
            val icons = KoolSystem.configJvm.windowIcon.ifEmpty { KoolWindowJvm.loadDefaultWindowIconSet() }
            val frame = JFrame(ctx.config.windowTitle)
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
            frame.iconImages = icons
            frame.layout = BorderLayout()
            frame.setPreferredSize(Dimension(ctx.config.windowSize.x, ctx.config.windowSize.y))
            frame.add(canvas, BorderLayout.CENTER)
            frame.pack()
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    close {
                        frame.dispose()
                        onClosed?.invoke() ?: exitProcess(0)
                    }
                }
            })
            frame.isVisible = true

            icons.maxByOrNull { it.width }?.let { taskbarIcon ->
                SwingUtilities.invokeLater {
                    try {
                        Taskbar.getTaskbar().iconImage = taskbarIcon
                    } catch (_: Exception) {
                        // silently ignored: Platform doesn't support setting a taskbar icon
                    }
                }
            }
        }

        if (canvas is KoolGlCanvas) {
            canvas.glCallbacks = glCallbacks
            SwingUtilities.invokeAndWait {
                require(canvas.isValid)
                canvas.initCanvas()
            }
            logI { "KoolGlCanvas initialized" }
        }

        if (makeFocusable) {
            canvas.isFocusable = true
        }

        canvasWrapper = CanvasWrapper(canvas)
        return canvasWrapper!!
    }

    override fun onBackendCreated(ctx: Lwjgl3Context) {
        onCreated?.invoke()
        if (makeFocusable) {
            SwingUtilities.invokeLater {
                canvasWrapper?.canvas?.requestFocus()
            }
        }
    }

    override fun runRenderLoop() {
        if (canvasWrapper?.canvas is KoolGlCanvas) {
            glRenderLoop()
        } else {
            unmanagedRenderLoop()
        }
        runBlocking { closeSignal.await() }
    }

    fun close(onClosed: () -> Unit) {
        isCloseRequested = true
        closeSignal.invokeOnCompletion { onClosed() }
    }

    fun glRenderLoop() {
        val canvas = requireNotNull(canvasWrapper?.canvas as? KoolGlCanvas)
        val renderLoop = object : Runnable {
            override fun run() {
                when {
                    !canvas.isValid -> GL.setCapabilities(null)
                    isCloseRequested -> {
                        shutdown()
                        closeSignal.complete(Unit)
                    }
                    else -> {
                        canvas.render()
                        SwingUtilities.invokeLater(this)
                    }
                }
            }
        }
        logI { "Starting Swing event loop based render loop" }
        SwingUtilities.invokeLater(renderLoop)
    }

    private fun unmanagedRenderLoop() {
        val ctx = KoolSystem.requireContext() as Lwjgl3Context
        runBlocking {
            logI { "Starting unmanaged render loop" }
            val window = canvasWrapper!!
            while (!isCloseRequested) {
                window.pollEvents()
                if (!window.flags.isMinimized) {
                    ctx.renderFrame()
                } else {
                    Thread.sleep(10)
                }
            }
            shutdown()
        }
    }
}

class KoolGlCanvas(data: GLData = DEFAULT_GL_DATA) : AWTGLCanvas(data) {
    internal var glCallbacks: GlWindowCallbacks? = null

    fun initCanvas() {
        beforeRender()
        if (!initCalled) {
            initGL()
            initCalled = true
        }
        afterRender()
    }

    override fun initGL() {
        glCallbacks?.initGl()
    }

    override fun paintGL() {
        glCallbacks?.drawFrame()
        glEnd()
        swapBuffers()
    }

    companion object {
        val DEFAULT_GL_DATA = GLData().apply {
            samples = 4
            swapInterval = 1
        }
    }
}