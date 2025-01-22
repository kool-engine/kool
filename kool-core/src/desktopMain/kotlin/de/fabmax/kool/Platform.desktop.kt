package de.fabmax.kool

import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import java.text.SimpleDateFormat
import java.util.*

actual fun Double.toString(precision: Int): String = "%.${precision.clamp(0, 12)}f".format(Locale.ENGLISH, this)

val KoolSystem.configJvm: KoolConfigJvm get() = config as KoolConfigJvm
val KoolSystem.isMacOs: Boolean get() = (platform as Platform.Desktop).isMacOs

/**
 * Creates a new [KoolContext] with the given [KoolConfigJvm]. Notice that there can only be one [KoolContext], calling
 * this method multiple times is an error.
 */
fun createContext(config: KoolConfigJvm = KoolConfigJvm()): Lwjgl3Context {
    KoolSystem.initialize(config)
    return DesktopImpl.createContext()
}

fun KoolApplication(config: KoolConfigJvm, appBlock: suspend KoolApplication.() -> Unit) =
    KoolApplication(createContext(config), appBlock)

fun KoolApplication(ctx: Lwjgl3Context = createContext(), appBlock: suspend KoolApplication.() -> Unit) {
    val koolApp = KoolApplication(ctx)
    launchOnMainThread {
        koolApp.appBlock()
    }
    ctx.run()
}

internal object DesktopImpl {
    private var ctx: Lwjgl3Context? = null

    val monitors: MutableList<MonitorSpec> = mutableListOf()
    val primaryMonitor: MonitorSpec

    init {
        if (Log.printer == Log.DEFAULT_PRINTER) {
            val dateFmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
            Log.printer = { lvl, tag, message ->
                synchronized(dateFmt) {
                    val timestamp = coloredText(dateFmt.format(System.currentTimeMillis()), MdColor.BROWN tone 300)
                    val frame = coloredText("f:${Time.frameCount}", MdColor.PURPLE tone 300)

                    val (tagColor, bold) = when (lvl) {
                        Log.Level.TRACE -> MdColor.INDIGO tone 200 to false
                        Log.Level.DEBUG -> MdColor.CYAN tone 300 to false
                        Log.Level.INFO -> MdColor.LIGHT_GREEN to true
                        Log.Level.WARN -> MdColor.AMBER to true
                        Log.Level.ERROR -> MdColor.RED to true
                        Log.Level.OFF -> MdColor.PURPLE to true
                    }
                    val tagStr = coloredText("${lvl.indicator}/$tag", tagColor, bold)

                    val txt = message.replace("\n", "\n  ")
                    val messageStr = when (lvl) {
                        Log.Level.WARN -> coloredText(txt, MdColor.AMBER tone 200)
                        Log.Level.ERROR -> coloredText(txt, MdColor.RED tone 200)
                        else -> txt
                    }

                    println("$timestamp|$frame  $tagStr: $messageStr")
                }
            }
        }

        // setup an error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

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

    private fun coloredText(text: String, color: Color, bold: Boolean = false): String {
        val r = (color.r.clamp() * 255).toInt()
        val g = (color.g.clamp() * 255).toInt()
        val b = (color.b.clamp() * 255).toInt()
        val weight = if (bold) "\u001b[1m" else ""
        return "\u001b[38;2;$r;$g;${b}m$weight$text\u001b[0m"
    }

    fun createContext(): Lwjgl3Context {
        synchronized(this) {
            if (ctx == null) {
                ctx = Lwjgl3Context()
            }
        }
        return ctx!!
    }
}