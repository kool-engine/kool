package de.fabmax.kool

import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.*
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import java.util.*

actual fun Double.toString(precision: Int): String = "%.${precision.clamp(0, 12)}f".format(Locale.ENGLISH, this)

val KoolSystem.configJvm: KoolConfigJvm get() = config as KoolConfigJvm
val KoolSystem.isMacOs: Boolean get() = (platform as Platform.Desktop).isMacOs

/**
 * Creates a new [KoolContext] with the given [KoolConfigJvm]. Notice that there can only be one [KoolContext], calling
 * this method multiple times is an error.
 */
suspend fun createContext(config: KoolConfigJvm = KoolConfigJvm()): Lwjgl3Context {
    KoolSystem.initialize(config)
    return DesktopImpl.createContext()
}

fun KoolApplication(config: KoolConfigJvm = KoolConfigJvm(), appBlock: suspend KoolApplication.() -> Unit) {
    runBlocking {
        val ctx = createContext(config)
        KoolApplication(ctx, appBlock)
    }
}

suspend fun KoolApplication(ctx: Lwjgl3Context, appBlock: suspend KoolApplication.() -> Unit) {
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

    suspend fun createContext(): Lwjgl3Context {
        synchronized(this) {
            check(ctx == null) { "Context was already created (multi-context is not yet supported)" }
        }
        ctx = Lwjgl3Context()
        return ctx!!
    }
}

private object DesktopLogPrinter : LogPrinter {
    val startTime = System.currentTimeMillis()

    val numberRegex = """[0-9a-fA-F]+""".toRegex()

    var sameMsgCnt = 0
    var prevMsg: String? = null

    override fun print(lvl: Log.Level, tag: String?, message: String) {
        val timestamp = coloredText("%8.3f s".format((System.currentTimeMillis() - startTime) / 1000.0), MdColor.BROWN tone 300)
        val frameFmt = if (Time.frameCount > 999) "…%03d".format(Time.frameCount % 1000) else "%4d".format(Time.frameCount)
        val frame = coloredText("f:$frameFmt", MdColor.PURPLE tone 300)

        val style = styles[lvl.level]
        val tagStr = coloredText(" ${lvl.indicator} ", Color.WHITE, bgColor = style.bgColor, bold = true) +
                (tag?.let { coloredText(" $it:", style.fgColor, bold = style.bold) } ?: " ")

        val txt = message.replace("\n", "\n  ")
        val messageStr = when (lvl) {
            Log.Level.WARN -> coloredText(txt, MdColor.AMBER tone 200)
            Log.Level.ERROR -> coloredText(txt, MdColor.RED tone 200)
            else -> txt
        }

        val isVkMsg = "VkValidation" in (tag ?: "")
        val printMsg = "$tagStr $messageStr"
        val checkMsg = if (isVkMsg) printMsg.replace(numberRegex) { "" } else printMsg
        if (checkMsg == prevMsg) {
            sameMsgCnt++
            val colored = coloredText("<Repeated message: $sameMsgCnt times>", MdColor.GREY tone 400)
            print("\b\r  $colored")
        } else {
            if (sameMsgCnt > 0) {
                println()
            }
            sameMsgCnt = 0
            prevMsg = checkMsg
            println("$timestamp|$frame $printMsg")
        }
    }

    private fun coloredText(text: String, fgColor: Color, bgColor: Color? = null, bold: Boolean = false): String {
        val ansiTokens = mutableListOf<String>()
        if (bgColor != null) {
            val r = (bgColor.r.clamp() * 255).toInt()
            val g = (bgColor.g.clamp() * 255).toInt()
            val b = (bgColor.b.clamp() * 255).toInt()
            ansiTokens += "48;2;$r;$g;$b"
        }

        val r = (fgColor.r.clamp() * 255).toInt()
        val g = (fgColor.g.clamp() * 255).toInt()
        val b = (fgColor.b.clamp() * 255).toInt()
        ansiTokens += "38;2;$r;$g;$b"

        if (bold) {
            ansiTokens += "1"
        }
        val style = ansiTokens.joinToString(";")
        return "\u001b[${style}m$text\u001b[0m"
    }

    private data class LevelStyle(val fgColor: Color, val bgColor: Color, val bold: Boolean)

    private val styles = listOf(
        LevelStyle(MdColor.INDIGO tone 200, MdColor.INDIGO, false),
        LevelStyle(MdColor.CYAN tone 300, MdColor.CYAN tone 700, false),
        LevelStyle(MdColor.LIGHT_GREEN, MdColor.LIGHT_GREEN, true),
        LevelStyle(MdColor.AMBER, MdColor.AMBER, true),
        LevelStyle(MdColor.RED, MdColor.RED, true),
        LevelStyle(MdColor.PURPLE, MdColor.PURPLE, true),
    )
}