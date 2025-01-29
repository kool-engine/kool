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
            Log.printer = DesktopLogPrinter
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

    fun createContext(): Lwjgl3Context {
        synchronized(this) {
            if (ctx == null) {
                ctx = Lwjgl3Context()
            }
        }
        return ctx!!
    }
}

private object DesktopLogPrinter : LogPrinter {
    val dateFmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    val numberRegex = """[0-9a-fA-F]+""".toRegex()

    var sameMsgCnt = 0
    var prevMsg: String? = null

    override fun print(lvl: Log.Level, tag: String?, message: String) {
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
            val tagStr = coloredText("${lvl.indicator}/$tag", tagColor, bold = bold)

            val txt = message.replace("\n", "\n  ")
            val messageStr = when (lvl) {
                Log.Level.WARN -> coloredText(txt, MdColor.AMBER tone 200)
                Log.Level.ERROR -> coloredText(txt, MdColor.RED tone 200)
                else -> txt
            }

            val printMsg = "$tagStr: $messageStr"
            val checkMsg = printMsg.replace(numberRegex) { "" }
            if (checkMsg == prevMsg) {
                sameMsgCnt++
                val colored = coloredText("[Similar message: $sameMsgCnt times]", MdColor.GREY tone 900, MdColor.GREY tone 500)
                print("\b\r  $colored")
            } else {
                if (sameMsgCnt > 0) {
                    println()
                }
                sameMsgCnt = 0
                prevMsg = checkMsg
                println("$timestamp|$frame  $printMsg")
            }
        }
    }

    private fun coloredText(text: String, fgColor: Color, bgColor: Color? = null, bold: Boolean = false): String {
        val bg = if (bgColor == null) "" else {
            val r = (bgColor.r.clamp() * 255).toInt()
            val g = (bgColor.g.clamp() * 255).toInt()
            val b = (bgColor.b.clamp() * 255).toInt()
            "\u001b[48;2;$r;$g;${b}m"
        }

        val r = (fgColor.r.clamp() * 255).toInt()
        val g = (fgColor.g.clamp() * 255).toInt()
        val b = (fgColor.b.clamp() * 255).toInt()

        val weight = if (bold) "\u001b[1m" else ""
        return "\u001b[38;2;$r;$g;${b}m$weight$bg$text\u001b[0m"
    }
}