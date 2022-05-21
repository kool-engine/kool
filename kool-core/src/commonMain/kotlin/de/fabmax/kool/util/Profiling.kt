package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.lock
import de.fabmax.kool.now
import de.fabmax.kool.toString
import kotlin.math.abs

inline fun <T> profiled(tag: String, block: () -> T): T {
    Profiling.enter(tag)
    val retVal = block()
    Profiling.exit(tag)
    return retVal
}

object Profiling {

    private val timers = mutableMapOf<String, SectionTimer>()
    private var lastPrint = now()
    private var lastPrintFrameIdx = 0

    private var autoPrinter: ((KoolContext) -> Unit)? = null

    fun enter(tag: String) {
        lock(timers) { timers.getOrPut(tag) { SectionTimer(tag) } }.enter()
    }

    fun exit(tag: String) {
        lock(timers) { timers[tag] }?.exit()
    }

    private class SectionTimer(val tag: String) {
        var enterTime = 0.0
        var spentTime = 0.0

        fun enter() {
            enterTime = now()
        }

        fun exit() {
            spentTime += now() - enterTime
        }

        fun reset() {
            spentTime = 0.0
        }
    }

    fun printStatistics(ctx: KoolContext) {
        val numFrames = ctx.frameIdx - lastPrintFrameIdx
        val deltaT = now() - lastPrint
        lastPrint = now()
        lastPrintFrameIdx = ctx.frameIdx

        lock(timers) {
            println(" Profiling statistics for last ${deltaT.toString(0)} ms / $numFrames frames")
            println(" Tag                            | ms/frame | Relative | Total ms ")
            println("-----------------------------------------------------------------")
            timers.keys.sorted().forEach { tag ->
                val t = timers[tag]!!
                val r = t.spentTime / deltaT * 100
                val msPerFrame = fmtStr((t.spentTime / numFrames).toString(3), -9)
                val rel = fmtStr(r.toString(2), -6)
                val tot = fmtStr(t.spentTime.toString(3), -9)
                println("${fmtStr(tag, 32)}|${msPerFrame} |${rel} % |${tot}")
            }
            timers.clear()
            println("-----------------------------------------------------------------------")

        }
    }

    fun enableAutoPrint(intervalSecs: Double, ctx: KoolContext) {
        autoPrinter?.let { ctx.onRender -= it }
        autoPrinter = {
            if (now() - lastPrint >= intervalSecs * 1000.0) {
                printStatistics(it)
            }
        }
        ctx.onRender += autoPrinter!!
    }

    fun disableAutoPrint(ctx: KoolContext) {
        autoPrinter?.let { ctx.onRender -= it }
    }

    private fun fmtStr(str: String, len: Int): String {
        return if (str.length > abs(len)) {
            str.substring(0 until len)
        } else {
            var s = str
            while (s.length < abs(len)) {
                s = if (len < 0) " $s" else "$s "
            }
            s
        }
    }
}