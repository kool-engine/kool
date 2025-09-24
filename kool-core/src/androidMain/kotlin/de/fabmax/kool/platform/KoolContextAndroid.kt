package de.fabmax.kool.platform

import android.opengl.GLSurfaceView
import de.fabmax.kool.KoolConfigAndroid
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.util.*
import kotlinx.coroutines.runBlocking
import java.util.*

typealias AndroidLog = android.util.Log

class KoolContextAndroid(config: KoolConfigAndroid) : KoolContext() {

    override val backend: RenderBackendGlImpl
    override val window: AndroidWindow
    val surfaceView: GLSurfaceView get() = window.surfaceView

    private val sysInfo = SysInfo()

    init {
        check(!KoolSystem.isContextCreated) { "KoolContext was already created" }

        backend = RenderBackendGlImpl(this)
        window = AndroidWindow(this, config)
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

        // setup draw queues for all scenes / render passes
        val frameData = render()
        frameData.syncData()
        incrementFrameTime()

        // execute draw queues
        KoolDispatchers.Backend.executeDispatchedTasks()
        backend.renderFrame(frameData, this@KoolContextAndroid)
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