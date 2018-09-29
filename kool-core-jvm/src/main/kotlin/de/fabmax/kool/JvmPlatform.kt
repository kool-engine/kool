package de.fabmax.kool

import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.Log
import de.fabmax.kool.util.SimpleShadowMap
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import java.text.SimpleDateFormat
import java.util.*

/**
 * Desktop LWJGL3 platform implementation
 *
 * @author fabmax
 */

fun createContext(): KoolContext = createContext(Lwjgl3Context.InitProps())

fun createContext(props: Lwjgl3Context.InitProps): KoolContext = DesktopImpl.createContext(props)

actual fun now(): Double = System.nanoTime() / 1e6

actual fun getMemoryInfo(): String {
    val rt = Runtime.getRuntime()
    val freeMem = rt.freeMemory()
    val totalMem = rt.totalMemory()
    return "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB"
}

actual fun Double.toString(precision: Int): String =
        java.lang.String.format(Locale.ENGLISH, "%.${precision.clamp(0, 12)}f", this)

internal object DesktopImpl {
    private var ctx: Lwjgl3Context? = null

    val monitors: MutableList<MonitorSpec> = mutableListOf()
    val primaryMonitor: MonitorSpec

    init {
        val dateFmt = SimpleDateFormat("HH:mm:ss.SSS")
        Log.printer = { lvl, tag, message ->
            synchronized(dateFmt) {
                val txt = "${dateFmt.format(System.currentTimeMillis())} ${lvl.indicator}/$tag: $message"
                if (lvl.level < Log.Level.WARN.level) {
                    println(txt)
                } else {
                    System.err.println(txt)
                }
            }
        }

        SimpleShadowMap.defaultMapSize = 2048

        // setup an error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW
        if (!GLFW.glfwInit()) {
            throw KoolException("Unable to initialize GLFW")
        }

        var primMon: MonitorSpec? = null
        val primMonId = GLFW.glfwGetPrimaryMonitor()
        val mons = GLFW.glfwGetMonitors()!!
        for (i in 0 until mons.limit()) {
            val spec = MonitorSpec(mons[i])
            monitors += spec
            if (mons[i] == primMonId) {
                primMon = spec
            }
        }
        primaryMonitor = primMon!!
    }

    fun createContext(props: Lwjgl3Context.InitProps): KoolContext {
        synchronized(this) {
            if (ctx == null) {
                ctx = Lwjgl3Context(props)
            }
        }
        return ctx!!
    }
}

/**
 * AutoCloseable variant of the standard use extension function (which only works for Closeable).
 * This is mainly needed for lwjgl's MemoryStack.stackPush() to work in a try-with-resources manner.
 */
inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        when {
            this == null -> {}
            exception == null -> close()
            else ->
                try {
                    close()
                } catch (closeException: Throwable) {
                    exception.addSuppressed(closeException)
                }
        }
    }
}
