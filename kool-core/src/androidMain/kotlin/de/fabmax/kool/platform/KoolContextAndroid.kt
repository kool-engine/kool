package de.fabmax.kool.platform

import android.content.Context
import android.hardware.display.DisplayManager
import android.opengl.GLSurfaceView
import android.util.DisplayMetrics
import de.fabmax.kool.KoolConfigAndroid
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.util.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.max

typealias AndroidLog = android.util.Log

class KoolContextAndroid(config: KoolConfigAndroid) : KoolContext() {
    val surfaceView: GLSurfaceView = config.surfaceView ?: KoolSurfaceView(config.appContext)

    override var renderScale: Float = 1f
        set(value) {
            logE { "Changing render scale is not yet implemented on android" }
        }

    override val backend: RenderBackendGlImpl

    override val windowWidth: Int
        get() = backend.viewWidth
    override val windowHeight: Int
        get() = backend.viewHeight

    // todo: not really applicable on android?
    override var isFullscreen: Boolean
        get() = false
        set(_) { }

    private var prevFrameTime = System.nanoTime()
    private val sysInfo = SysInfo()

    init {
        check(!KoolSystem.isContextCreated) { "KoolContext was already created" }

        val metrics = DisplayMetrics()
        val displayManager = config.appContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        @Suppress("DEPRECATION")
        displayManager.displays[0].getMetrics(metrics)

        windowScale = max(1f, metrics.density * config.scaleModifier)
        backend = RenderBackendGlImpl(this)
        surfaceView.setRenderer(backend)
        KoolSystem.onContextCreated(this)
    }

    fun onPause() {
        surfaceView.onPause()
    }

    fun onResume() {
        surfaceView.onResume()
    }

    fun onDestroy() {
        onShutdown.updated().forEach { it(this) }
    }

    override fun openUrl(url: String, sameWindow: Boolean) {
        logE { "Open URL: $url" }
    }

    override fun run() { }

    override fun getSysInfos(): List<String> {
        return sysInfo.lines
    }

    internal fun renderFrame() = runBlocking {
        sysInfo.update()
        RenderLoopCoroutineDispatcher.executeDispatchedTasks()

        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevFrameTime) / 1e9
        prevFrameTime = time

        // setup draw queues for all scenes / render passes
        render(dt)

        // execute draw queues
        backend.renderFrame(this@KoolContextAndroid)
    }

    companion object {
        init {
            if (Log.printer == Log.DEFAULT_PRINTER) {
                Log.printer = LogPrinter { lvl, tag, message ->
                    val ctx = KoolSystem.getContextOrNull()
                    val frmTxt = ctx?.let { "f:${Time.frameCount}  " } ?: ""
                    val txt = "$frmTxt$message"
                    when (lvl) {
                        Log.Level.TRACE -> AndroidLog.d(tag, "TRC: $txt")
                        Log.Level.DEBUG -> AndroidLog.d(tag, txt)
                        Log.Level.INFO -> AndroidLog.i(tag, txt)
                        Log.Level.WARN -> AndroidLog.w(tag, txt)
                        Log.Level.ERROR -> AndroidLog.e(tag, txt)
                        Log.Level.OFF -> { }
                    }
                }
            }
        }
    }

    private inner class SysInfo {
        val lines = ArrayList<String>()

        private var isInitialized = false
        private var prevHeapSz = 1e9
        private var prevHeapSzTime = 0L
        private var avgHeapGrowth = 0.0

        fun init(deviceName: String) {
            isInitialized = true
            lines.clear()
            lines.add("Android ${android.os.Build.VERSION.RELEASE}, API level: ${android.os.Build.VERSION.SDK_INT}")
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