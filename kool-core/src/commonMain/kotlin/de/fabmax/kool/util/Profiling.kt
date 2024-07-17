package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.toString
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.math.abs

inline fun <T> profiled(tag: String, block: () -> T): T {
    Profiling.enter(tag)
    val retVal = block()
    Profiling.exit(tag)
    return retVal
}

object Profiling : SynchronizedObject() {

    private val timers = mutableMapOf<String, SectionTimer>()
    private var lastPrint = Time.precisionTime
    private var lastPrintFrameIdx = 0

    private var autoPrinter: ((KoolContext) -> Unit)? = null

    fun enter(tag: String) {
        synchronized(this) { timers.getOrPut(tag) { SectionTimer() } }.enter()
    }

    fun exit(tag: String) {
        synchronized(this) { timers[tag] }?.exit()
    }

    inline fun profiled(tag: String, block: () -> Unit) {
        enter(tag)
        block()
        exit(tag)
    }

    private class SectionTimer {
        var enterTime = 0.0
        var spentTime = 0.0

        fun enter() {
            enterTime = Time.precisionTime
        }

        fun exit() {
            spentTime += (Time.precisionTime - enterTime) * 1000.0
        }
    }

    fun printStatistics() {
        val numFrames = Time.frameCount - lastPrintFrameIdx
        val deltaT = Time.precisionTime - lastPrint
        lastPrint = Time.precisionTime
        lastPrintFrameIdx = Time.frameCount

        synchronized(this) {
            println("------------------------------------------------------------------")
            println(" Profiling statistics for last ${deltaT.toString(3)} secs / $numFrames frames")
            println(" Tag                             | ms/frame | Relative | Total ms ")
            println("------------------------------------------------------------------")
            timers.keys.sorted().forEach { tag ->
                val t = timers[tag]!!
                val r = t.spentTime / deltaT * 0.1
                val msPerFrame = fmtStr((t.spentTime / numFrames).toString(3), -9)
                val rel = fmtStr(r.toString(2), -7)
                val tot = fmtStr(t.spentTime.toString(3), -9)
                println(" ${fmtStr(tag, 32)}|${msPerFrame} |${rel} % |${tot}")
            }
            timers.clear()
            println("------------------------------------------------------------------")

        }
    }

    fun enableAutoPrint(intervalSecs: Double, ctx: KoolContext = KoolSystem.requireContext()) {
        autoPrinter?.let { ctx.onRender -= it }
        autoPrinter = {
            if (Time.precisionTime - lastPrint >= intervalSecs) {
                printStatistics()
            }
        }
        ctx.onRender += autoPrinter!!
    }

    fun disableAutoPrint(ctx: KoolContext = KoolSystem.requireContext()) {
        autoPrinter?.let { ctx.onRender -= it }
    }

    private fun fmtStr(str: String, len: Int): String {
        return if (str.length > abs(len)) {
            str.substring(0 until abs(len))
        } else {
            var s = str
            while (s.length < abs(len)) {
                s = if (len < 0) " $s" else "$s "
            }
            s
        }
    }
}