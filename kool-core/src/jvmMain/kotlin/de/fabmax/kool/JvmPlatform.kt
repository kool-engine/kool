package de.fabmax.kool

import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.Log
import de.fabmax.kool.util.Time
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import java.text.SimpleDateFormat
import java.util.*

/**
 * Desktop LWJGL3 platform implementation
 *
 * @author fabmax
 */

actual fun createDefaultContext(): KoolContext {
    return createContext {
        renderBackend = Lwjgl3Context.Backend.OPEN_GL
        title = "Kool App"
    }
}

fun createContext(block: Lwjgl3Context.InitProps.() -> Unit): KoolContext {
    val props = Lwjgl3Context.InitProps()
    props.block()
    return DesktopImpl.createContext(props)
}

actual fun Double.toString(precision: Int): String =
        java.lang.String.format(Locale.ENGLISH, "%.${precision.clamp(0, 12)}f", this)

actual inline fun <R> lock(lock: Any, block: () -> R): R = synchronized(lock, block)

internal object DesktopImpl {
    private var ctx: Lwjgl3Context? = null

    val monitors: MutableList<MonitorSpec> = mutableListOf()
    val primaryMonitor: MonitorSpec

    init {
        val dateFmt = SimpleDateFormat("HH:mm:ss.SSS")
        Log.printer = { lvl, tag, message ->
            synchronized(dateFmt) {
                val frmTxt = ctx?.let { "|f:${Time.frameCount}" } ?: ""
                val txt = "${dateFmt.format(System.currentTimeMillis())}$frmTxt ${lvl.indicator}/$tag: $message"
                if (lvl.level < Log.Level.WARN.level) {
                    println(txt)
                } else {
                    System.err.println(txt)
                }
            }
        }

        // setup an error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW
        if (!GLFW.glfwInit()) {
            throw KoolException("Unable to initialize GLFW")
        }

        val primMonId = GLFW.glfwGetPrimaryMonitor()
        val mons = GLFW.glfwGetMonitors()!!
        var primMon = MonitorSpec(mons[0])
        for (i in 0 until mons.limit()) {
            val spec = MonitorSpec(mons[i])
            monitors += spec
            if (mons[i] == primMonId) {
                primMon = spec
            }
        }
        primaryMonitor = primMon
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
