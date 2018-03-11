package de.fabmax.kool.util

import de.fabmax.kool.formatDouble
import de.fabmax.kool.now

class PerfTimer {

    private var tStart = 0.0

    init {
        reset()
    }

    fun reset() {
        tStart = now()
    }

    fun takeSecs(): Double {
        return takeMs() / 1000.0
    }

    fun takeMs(): Double {
        return now() - tStart
    }

    fun print(message: String) {
        println("$message ${formatDouble(takeSecs(), 3)} secs")
    }

    fun printMs(message: String) {
        println("$message ${formatDouble(takeMs(), 3)} ms")
    }
}

inline fun <T> timedMs(message: String, block: () -> T): T {
    val t = now()
    val ret = block()
    println("$message ${formatDouble(now() - t, 3)} ms")
    return ret
}

inline fun <T> timedMs(message: () -> String, block: () -> T): T {
    val t = now()
    val ret = block()
    println("${message()} ${formatDouble(now() - t, 3)} ms")
    return ret
}
