package de.fabmax.kool.platform

import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.input.PlatformInputJvm
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGl
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import org.lwjgl.glfw.GLFW.*
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.net.URI
import java.util.*
import kotlin.math.min

suspend fun Lwjgl3Context(): Lwjgl3Context {
    val config = KoolSystem.configJvm
    val ctx = Lwjgl3Context(config)
    ctx.createBackend()
    return ctx
}

class Lwjgl3Context internal constructor (val config: KoolConfigJvm) : KoolContext() {
    override lateinit var backend: RenderBackendJvm
        private set

    override var renderScale: Float = config.renderScale
        set(value) {
            if (field != value) {
                field = value
                windowScale = backend.glfwWindow.windowScale * value
                (backend as? RenderBackendVk)?.recreateSwapchain()
            }
        }

    override val windowWidth: Int get() = (backend.glfwWindow.framebufferWidth * renderScale).toInt()
    override val windowHeight: Int get() = (backend.glfwWindow.framebufferHeight * renderScale).toInt()
    override var isFullscreen: Boolean
        get() = backend.glfwWindow.isFullscreen
        set(value) { backend.glfwWindow.isFullscreen = value }

    var maxFrameRate = config.maxFrameRate
    var windowNotFocusedFrameRate = config.windowNotFocusedFrameRate

    private var prevFrameTime = System.nanoTime()
    private val sysInfo = SysInfo()

    internal suspend fun createBackend() {
        val backendProvider = config.renderBackend
        val backendResult = config.renderBackend.createBackend(this@Lwjgl3Context)

        backend = when {
            backendResult.isSuccess -> backendResult.getOrThrow()
            config.useOpenGlFallback && backendProvider != RenderBackendGl -> {
                logE { "Failed creating render backend ${backendProvider.displayName}: ${backendResult.exceptionOrNull()}\nFalling back to WebGL2" }
                RenderBackendGl.createBackend(this@Lwjgl3Context).getOrThrow()
            }
            else -> error("Failed creating render backend ${backendProvider.displayName}: ${backendResult.exceptionOrNull()}")
        } as RenderBackendJvm

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
        logI { "Exiting..." }
        scenes.forEach { it.release() }
        backgroundScene.release()
        onShutdown.updated().forEach { it(this) }
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
        backend.renderFrame(this@Lwjgl3Context)
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

