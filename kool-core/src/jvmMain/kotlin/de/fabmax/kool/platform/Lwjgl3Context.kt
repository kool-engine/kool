package de.fabmax.kool.platform

import de.fabmax.kool.InputManager
import de.fabmax.kool.KeyCode
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.min
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.platform.gl.GlRenderBackend
import de.fabmax.kool.platform.vk.VkRenderBackend
import de.fabmax.kool.util.Viewport
import org.lwjgl.glfw.GLFW.*
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

/**
 * @author fabmax
 */
class Lwjgl3Context(props: InitProps) : KoolContext() {
    override val assetMgr = JvmAssetManager(props, this)
    override val inputMgr: JvmInputManager

    val renderBackend: RenderBackend

    override val shaderGenerator
        get() = renderBackend.shaderGenerator

    override val windowWidth: Int
        get() = renderBackend.glfwWindow.framebufferWidth
    override val windowHeight: Int
        get() = renderBackend.glfwWindow.framebufferHeight
    override var isFullscreen: Boolean
        get() = renderBackend.glfwWindow.isFullscreen
        set(value) { renderBackend.glfwWindow.isFullscreen = value }

    var maxFrameRate = props.maxFrameRate
    var windowUnfocusedFrameRate = props.windowUnfocusedFrameRate
    private val mainThreadRunnables = mutableListOf<GpuThreadRunnable>()

    private var prevFrameTime = System.nanoTime()

    private object SysInfo : ArrayList<String>() {
        private var prevHeapSz = 1e9
        private var prevHeapSzTime = 0L
        private var avgHeapGrowth = 0.0

        fun set(api: String, deviceName: String) {
            clear()
            add(System.getProperty("java.vm.name") + " " + System.getProperty("java.version"))
            add(api)
            add(deviceName)
            add("")
            update()
        }

        fun update() {
            val rt = Runtime.getRuntime()
            val freeMem = rt.freeMemory()
            val totalMem = rt.totalMemory()
            val heapSz = (totalMem - freeMem) / 1024.0 / 1024.0
            val t = System.currentTimeMillis()
            if (heapSz > prevHeapSz) {
                val growth = (heapSz - prevHeapSz)
                val dt = (t - prevHeapSzTime) / 1000.0
                if (dt > 0.0) {
                    val w = dt.clamp(0.0, 0.5)
                    avgHeapGrowth = avgHeapGrowth * (1.0 - w) + (growth / dt) * w
                    prevHeapSzTime = t
                }
            }
            prevHeapSz = heapSz
            set(3, "Heap: %.1f MB (+%.1f MB/s)".format(Locale.ENGLISH, heapSz, avgHeapGrowth))
        }
    }

    init {
        renderBackend = if (props.renderBackend == Backend.VULKAN) {
            VkRenderBackend(props, this)
        } else {
            GlRenderBackend(props, this)
        }

        projCorrectionMatrixScreen.set(renderBackend.projCorrectionMatrixScreen)
        projCorrectionMatrixOffscreen.set(renderBackend.projCorrectionMatrixOffscreen)
        depthBiasMatrix.set(renderBackend.depthBiasMatrix)

        SysInfo.set(renderBackend.apiName, renderBackend.deviceName)

        inputMgr = JvmInputManager(renderBackend.glfwWindow.windowPtr, this)
    }

    fun setWindowTitle(windowTitle: String) = renderBackend.glfwWindow.setWindowTitle(windowTitle)

    fun setWindowIcon(icon: List<BufferedImage>) = renderBackend.glfwWindow.setWindowIcon(icon)

    override fun openUrl(url: String, sameWindow: Boolean)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        while (!glfwWindowShouldClose(renderBackend.glfwWindow.windowPtr)) {
            SysInfo.update()
            renderBackend.glfwWindow.pollEvents()
            renderFrame()
        }
        renderBackend.cleanup(this)
    }

    internal fun renderFrame() {
        synchronized(mainThreadRunnables) {
            if (mainThreadRunnables.isNotEmpty()) {
                for (r in mainThreadRunnables) {
                    r.r()
                    r.future.complete(null)
                }
                mainThreadRunnables.clear()
            }
        }

        if (windowUnfocusedFrameRate > 0 || maxFrameRate > 0) {
            checkFrameRateLimits(prevFrameTime)
        }

        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevFrameTime) / 1e9
        prevFrameTime = time

        // setup draw queues for all scenes / render passes
        render(dt)

        // execute draw queues
        engineStats.resetPerFrameCounts()
        renderBackend.drawFrame(this)
    }

    private fun checkFrameRateLimits(prevTime: Long) {
        val t = System.nanoTime()
        val dtFocused = if (maxFrameRate > 0) 1.0 / maxFrameRate else 0.0
        val dtUnfocused = if (windowUnfocusedFrameRate > 0) 1.0 / windowUnfocusedFrameRate else dtFocused
        val dtCurrent = (t - prevTime) / 1e9
        val dtCmp = if (isWindowFocused || inputMgr.isMouseOverWindow) dtFocused else dtUnfocused
        if (dtCmp > dtCurrent) {
            val untilFocused = t + ((dtFocused - dtCurrent) * 1e9).toLong()
            val untilUnfocused = t + ((dtUnfocused - dtCurrent) * 1e9).toLong()
            delayFrameRender(untilFocused, untilUnfocused)
        }
    }

    private fun delayFrameRender(untilFocused: Long, untilUnfocused: Long) {
        while (!glfwWindowShouldClose(renderBackend.glfwWindow.windowPtr)) {
            val t = System.nanoTime()
            val isFocused = isWindowFocused || inputMgr.isMouseOverWindow
            if ((isFocused && t >= untilFocused) || (!isFocused && t >= untilUnfocused)) {
                break
            }

            val until = if (isFocused) untilFocused else untilUnfocused
            val delayMillis = ((until - t) / 1e6).toLong()
            if (delayMillis > 5) {
                val sleep = min(5L, delayMillis)
                Thread.sleep(sleep)
                if (sleep == delayMillis) {
                    break
                }
                glfwPollEvents()
            }
        }
    }

    override fun close() {
        renderBackend.close(this)
    }

    override fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout) = renderBackend.generateKslShader(shader, pipelineLayout)

    override fun getSysInfos(): List<String> = SysInfo

    override fun getWindowViewport(result: Viewport) {
        renderBackend.getWindowViewport(result)
    }

    fun runOnMainThread(action: () -> Unit): CompletableFuture<Void> {
        synchronized(mainThreadRunnables) {
            val r = GpuThreadRunnable(action)
            mainThreadRunnables += r
            return r.future
        }
    }

    private class GpuThreadRunnable(val r: () -> Unit) {
        val future = CompletableFuture<Void>()
    }

    companion object {
        val KEY_CODE_MAP: Map<Int, KeyCode> = mutableMapOf(
                GLFW_KEY_LEFT_CONTROL to InputManager.KEY_CTRL_LEFT,
                GLFW_KEY_RIGHT_CONTROL to InputManager.KEY_CTRL_RIGHT,
                GLFW_KEY_LEFT_SHIFT to InputManager.KEY_SHIFT_LEFT,
                GLFW_KEY_RIGHT_SHIFT to InputManager.KEY_SHIFT_RIGHT,
                GLFW_KEY_LEFT_ALT to InputManager.KEY_ALT_LEFT,
                GLFW_KEY_RIGHT_ALT to InputManager.KEY_ALT_RIGHT,
                GLFW_KEY_LEFT_SUPER to InputManager.KEY_SUPER_LEFT,
                GLFW_KEY_RIGHT_SUPER to InputManager.KEY_SUPER_RIGHT,
                GLFW_KEY_ESCAPE to InputManager.KEY_ESC,
                GLFW_KEY_MENU to InputManager.KEY_MENU,
                GLFW_KEY_ENTER to InputManager.KEY_ENTER,
                GLFW_KEY_KP_ENTER to InputManager.KEY_NP_ENTER,
                GLFW_KEY_KP_DIVIDE to InputManager.KEY_NP_DIV,
                GLFW_KEY_KP_MULTIPLY to InputManager.KEY_NP_MUL,
                GLFW_KEY_KP_ADD to InputManager.KEY_NP_PLUS,
                GLFW_KEY_KP_SUBTRACT to InputManager.KEY_NP_MINUS,
                GLFW_KEY_BACKSPACE to InputManager.KEY_BACKSPACE,
                GLFW_KEY_TAB to InputManager.KEY_TAB,
                GLFW_KEY_DELETE to InputManager.KEY_DEL,
                GLFW_KEY_INSERT to InputManager.KEY_INSERT,
                GLFW_KEY_HOME to InputManager.KEY_HOME,
                GLFW_KEY_END to InputManager.KEY_END,
                GLFW_KEY_PAGE_UP to InputManager.KEY_PAGE_UP,
                GLFW_KEY_PAGE_DOWN to InputManager.KEY_PAGE_DOWN,
                GLFW_KEY_LEFT to InputManager.KEY_CURSOR_LEFT,
                GLFW_KEY_RIGHT to InputManager.KEY_CURSOR_RIGHT,
                GLFW_KEY_UP to InputManager.KEY_CURSOR_UP,
                GLFW_KEY_DOWN to InputManager.KEY_CURSOR_DOWN,
                GLFW_KEY_F1 to InputManager.KEY_F1,
                GLFW_KEY_F2 to InputManager.KEY_F2,
                GLFW_KEY_F3 to InputManager.KEY_F3,
                GLFW_KEY_F4 to InputManager.KEY_F4,
                GLFW_KEY_F5 to InputManager.KEY_F5,
                GLFW_KEY_F6 to InputManager.KEY_F6,
                GLFW_KEY_F7 to InputManager.KEY_F7,
                GLFW_KEY_F8 to InputManager.KEY_F8,
                GLFW_KEY_F9 to InputManager.KEY_F9,
                GLFW_KEY_F10 to InputManager.KEY_F10,
                GLFW_KEY_F11 to InputManager.KEY_F11,
                GLFW_KEY_F12 to InputManager.KEY_F12
        )
    }

    class InitProps(block: InitProps.() -> Unit = {}) {
        var width = 1600
        var height = 900
        var title = "Kool"
        var icon: List<BufferedImage> = DEFAULT_ICON?.let { listOf(it) } ?: emptyList()
        var monitor = -1
        var isFullscreen = false
        var isWithHttpAssets = true
        var showWindowOnStart = true
        var isVsync = true
        var windowUnfocusedFrameRate = 0
        var maxFrameRate = 0

        var renderBackend = Backend.OPEN_GL

        var msaaSamples = 8

        var localAssetPath = "./assets"
        var storageDir = "./.storage"

        val customFonts = mutableMapOf<String, String>()

        init {
            setWindowed(1600, 900)
            this.block()
        }

        fun setWindowed(width: Int, height: Int) {
            this.width = width
            this.height = height
            this.monitor = -1
            this.isFullscreen = false
        }

        fun setFullscreen(monitor: Int = -1) {
            this.width = 1600
            this.height = 900
            this.monitor = monitor
            this.isFullscreen = true
        }

        companion object {
            val DEFAULT_ICON: BufferedImage?

            init {
                DEFAULT_ICON = try {
                    ImageIO.read(ClassLoader.getSystemResourceAsStream("icon.png"))
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    enum class Backend(val displayName: String) {
        VULKAN("Vulkan"),
        OPEN_GL("OpenGL")
    }
}

