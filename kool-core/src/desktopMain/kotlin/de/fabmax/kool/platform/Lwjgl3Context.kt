package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGl
import de.fabmax.kool.util.ApplicationScope
import de.fabmax.kool.util.KoolDispatchers
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.awt.Desktop
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
    val windowSubsystem: WindowSubsystem get() = config.windowSubsystem

    override lateinit var backend: RenderBackendJvm
        private set

    override val window: KoolWindowJvm
        get() = backend.window

    var maxFrameRate = config.maxFrameRate
    var windowNotFocusedFrameRate = config.windowNotFocusedFrameRate

    private var prevFrameTime = System.nanoTime()
    private val sysInfo = SysInfo()

    private var nextFrameData: Deferred<FrameData>? = null

    internal suspend fun createBackend() {
        val backendProvider = config.renderBackend
        val backendResult = config.renderBackend.createBackend(this@Lwjgl3Context)

        backend = when {
            backendResult.isSuccess -> backendResult.getOrThrow()
            config.useOpenGlFallback && backendProvider != RenderBackendGl -> {
                logE { "Failed creating render backend ${backendProvider.displayName}: ${backendResult.exceptionOrNull()}\nFalling back to OpenGL" }
                RenderBackendGl.createBackend(this@Lwjgl3Context).getOrThrow()
            }
            else -> error("Failed creating render backend ${backendProvider.displayName}: ${backendResult.exceptionOrNull()}")
        } as RenderBackendJvm

        windowSubsystem.onBackendCreated(this)
        KoolSystem.onContextCreated(this)
    }

    override fun openUrl(url: String, sameWindow: Boolean)  = Desktop.getDesktop().browse(URI(url))

    fun close() {
        window.close()
    }

    override fun run() {
        Thread.currentThread().name = "kool-main-backend-thread"
        // blocks until window is closed
        KoolSystem.configJvm.windowSubsystem.runRenderLoop()
    }

    internal suspend fun renderFrame() {
        sysInfo.update()
        if (window.size.x <= 0 || window.size.y <= 0) {
            return
        }

        if (windowNotFocusedFrameRate > 0 || maxFrameRate > 0) {
            checkFrameRateLimits(prevFrameTime)
        }

        val frameData = nextFrameData?.await() ?: render(computeDt())
        frameData.syncData()

        if (config.asyncSceneUpdate) {
            nextFrameData = ApplicationScope.async { render(computeDt()) }
        }
        KoolDispatchers.Backend.executeDispatchedTasks()
        backend.renderFrame(frameData, this@Lwjgl3Context)
    }

    private fun computeDt(): Double {
        val time = System.nanoTime()
        val dt = (time - prevFrameTime) / 1e9
        prevFrameTime = time
        return dt
    }

    private fun checkFrameRateLimits(prevTime: Long) {
        val t = System.nanoTime()
        val dtFocused = if (maxFrameRate > 0) 1.0 / maxFrameRate else 0.0
        val dtUnfocused = if (windowNotFocusedFrameRate > 0) 1.0 / windowNotFocusedFrameRate else dtFocused
        val dtCurrent = (t - prevTime) / 1e9
        val dtCmp = if (window.flags.isFocused || window.isMouseOverWindow) dtFocused else dtUnfocused
        if (dtCmp > dtCurrent) {
            val untilFocused = t + ((dtFocused - dtCurrent) * 1e9).toLong()
            val untilUnfocused = t + ((dtUnfocused - dtCurrent) * 1e9).toLong()
            delayFrameRender(untilFocused, untilUnfocused)
        }
    }

    private fun delayFrameRender(untilFocused: Long, untilUnfocused: Long) {
        while (!config.windowSubsystem.isCloseRequested) {
            val t = System.nanoTime()
            val isFocused = window.flags.isFocused || window.isMouseOverWindow
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
                window.pollEvents()
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
