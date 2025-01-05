package de.fabmax.kool.platform

import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.input.PlatformInputJvm
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.pipeline.backend.vk.VkRenderBackend
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import org.lwjgl.glfw.GLFW.*
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

    override val windowWidth: Int
        get() = backend.glfwWindow.framebufferWidth
    override val windowHeight: Int
        get() = backend.glfwWindow.framebufferHeight
    override var isFullscreen: Boolean
        get() = backend.glfwWindow.isFullscreen
        set(value) { backend.glfwWindow.isFullscreen = value }

    var maxFrameRate = KoolSystem.configJvm.maxFrameRate
    var windowNotFocusedFrameRate = KoolSystem.configJvm.windowNotFocusedFrameRate

    private var prevFrameTime = System.nanoTime()
    private val sysInfo = SysInfo()

    init {
//        if (KoolSystem.configJvm.renderBackend == KoolConfigJvm.Backend.VULKAN) {
//            logE { "Vulkan support is currently disabled, falling back to OpenGL" }
//        }
//        backend = RenderBackendGlImpl(this)
        backend = when (KoolSystem.configJvm.renderBackend) {
            KoolConfigJvm.Backend.VULKAN -> VkRenderBackend(this)
            KoolConfigJvm.Backend.OPEN_GL -> RenderBackendGlImpl(this)
        }

        isWindowFocused = backend.glfwWindow.isFocused
        backend.glfwWindow.onFocusChanged += {
            isWindowFocused = it
        }

        PlatformInputJvm.onContextCreated(this)
        KoolSystem.onContextCreated(this)
    }

    fun setWindowTitle(windowTitle: String) = backend.glfwWindow.setWindowTitle(windowTitle)

    fun setWindowIcon(icon: List<BufferedImage>) = backend.glfwWindow.setWindowIcon(icon)

    override fun openUrl(url: String, sameWindow: Boolean)  = Desktop.getDesktop().browse(URI(url))

    fun close() {
        glfwSetWindowShouldClose(backend.glfwWindow.windowPtr, true)
    }

    override fun run() {
        while (!glfwWindowShouldClose(backend.glfwWindow.windowPtr)) {
            sysInfo.update()
            backend.glfwWindow.pollEvents()

            if (!backend.glfwWindow.isMinimized) {
                renderFrame()
            } else {
                Thread.sleep(10)
            }
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
        val dtCmp = if (isWindowFocused || PlatformInputJvm.isMouseOverWindow) dtFocused else dtUnfocused
        if (dtCmp > dtCurrent) {
            val untilFocused = t + ((dtFocused - dtCurrent) * 1e9).toLong()
            val untilUnfocused = t + ((dtUnfocused - dtCurrent) * 1e9).toLong()
            delayFrameRender(untilFocused, untilUnfocused)
        }
    }

    private fun delayFrameRender(untilFocused: Long, untilUnfocused: Long) {
        while (!glfwWindowShouldClose(backend.glfwWindow.windowPtr)) {
            val t = System.nanoTime()
            val isFocused = isWindowFocused || PlatformInputJvm.isMouseOverWindow
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

    override fun getSysInfos(): List<String> = sysInfo.lines

    private inner class SysInfo {
        val lines = ArrayList<String>()

        private var isInitialized = false
        private var prevHeapSz = 1e9
        private var prevHeapSzTime = 0L
        private var avgHeapGrowth = 0.0

        fun init(deviceName: String) {
            isInitialized = true
            lines.clear()
            lines.add(System.getProperty("java.version") + ": " + System.getProperty("java.vm.name"))
            lines.add(deviceName)
            lines.add("")
            update()
        }

        fun update() {
            if (!isInitialized) {
                init(backend.deviceName)
            }

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
            lines[2] = "Heap: %.1f MB (+%.1f MB/s)".format(Locale.ENGLISH, heapSz, avgHeapGrowth)
        }
    }
}

