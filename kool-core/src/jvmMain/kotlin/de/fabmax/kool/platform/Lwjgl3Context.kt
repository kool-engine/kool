package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.pipeline.backend.vk.VkRenderBackend
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.net.URI
import java.util.*
import kotlin.math.min

/**
 * @author fabmax
 */
class Lwjgl3Context : KoolContext() {
    override val backend: RenderBackendJvm

    override val isJavascript = false
    override val isJvm = true

    override val windowWidth: Int
        get() = backend.glfwWindow.framebufferWidth
    override val windowHeight: Int
        get() = backend.glfwWindow.framebufferHeight
    override var isFullscreen: Boolean
        get() = backend.glfwWindow.isFullscreen
        set(value) { backend.glfwWindow.isFullscreen = value }

    var maxFrameRate = KoolSystem.config.maxFrameRate
    var windowNotFocusedFrameRate = KoolSystem.config.windowNotFocusedFrameRate

    private var prevFrameTime = System.nanoTime()

    private object SysInfo {
        val lines = ArrayList<String>()

        private var prevHeapSz = 1e9
        private var prevHeapSzTime = 0L
        private var avgHeapGrowth = 0.0

        fun set(api: String, deviceName: String) {
            lines.clear()
            lines.add(System.getProperty("java.version") + ": " + System.getProperty("java.vm.name"))
            lines.add(api)
            lines.add(deviceName)
            lines.add("")
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
            lines[3] = "Heap: %.1f MB (+%.1f MB/s)".format(Locale.ENGLISH, heapSz, avgHeapGrowth)
        }
    }

    init {
        backend = if (KoolSystem.config.renderBackend == Backend.VULKAN) {
            VkRenderBackend(this)
        } else {
            RenderBackendGlImpl(this)
        }

        SysInfo.set(backend.apiName, backend.deviceName)

        PlatformInput.onContextCreated(this)
        KoolSystem.onContextCreated(this)
    }

    fun setWindowTitle(windowTitle: String) = backend.glfwWindow.setWindowTitle(windowTitle)

    fun setWindowIcon(icon: List<BufferedImage>) = backend.glfwWindow.setWindowIcon(icon)

    override fun openUrl(url: String, sameWindow: Boolean)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        while (!glfwWindowShouldClose(backend.glfwWindow.windowPtr)) {
            SysInfo.update()
            backend.glfwWindow.pollEvents()
            renderFrame()
        }
        backend.cleanup(this)
    }

    internal fun renderFrame() {
        RenderLoopCoroutineDispatcher.executeDispatchedTasks()

        if (windowNotFocusedFrameRate > 0 || maxFrameRate > 0) {
            checkFrameRateLimits(prevFrameTime)
        }

        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevFrameTime) / 1e9
        prevFrameTime = time

        // setup draw queues for all scenes / render passes
        render(dt)

        // execute draw queues
        backend.renderFrame(this)
    }

    private fun checkFrameRateLimits(prevTime: Long) {
        val t = System.nanoTime()
        val dtFocused = if (maxFrameRate > 0) 1.0 / maxFrameRate else 0.0
        val dtUnfocused = if (windowNotFocusedFrameRate > 0) 1.0 / windowNotFocusedFrameRate else dtFocused
        val dtCurrent = (t - prevTime) / 1e9
        val dtCmp = if (isWindowFocused || PlatformInput.isMouseOverWindow) dtFocused else dtUnfocused
        if (dtCmp > dtCurrent) {
            val untilFocused = t + ((dtFocused - dtCurrent) * 1e9).toLong()
            val untilUnfocused = t + ((dtUnfocused - dtCurrent) * 1e9).toLong()
            delayFrameRender(untilFocused, untilUnfocused)
        }
    }

    private fun delayFrameRender(untilFocused: Long, untilUnfocused: Long) {
        while (!glfwWindowShouldClose(backend.glfwWindow.windowPtr)) {
            val t = System.nanoTime()
            val isFocused = isWindowFocused || PlatformInput.isMouseOverWindow
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

    override fun getSysInfos(): List<String> = SysInfo.lines

    enum class Backend(val displayName: String) {
        VULKAN("Vulkan"),
        OPEN_GL("OpenGL")
    }
}

