package de.fabmax.kool.util

import de.fabmax.kool.toString

class PerfTimer {

    private var tStart = 0.0

    init {
        reset()
    }

    fun reset() {
        tStart = Time.precisionTime
    }

    fun takeSecs(): Double {
        return Time.precisionTime - tStart
    }

    fun takeMs(): Double {
        return takeSecs() * 1000.0
    }
}

inline fun <T> timedMs(message: String, tag: String? = "PerfTimer", level: Log.Level = Log.Level.INFO, block: () -> T): T {
    val t = Time.precisionTime
    val ret = block()
    Log.log(level, tag) { "$message ${((Time.precisionTime - t) * 1000.0).toString(3)} ms" }
    return ret
}

inline fun <T> timedMs(message: () -> String, tag: String? = "PerfTimer", level: Log.Level = Log.Level.INFO, block: () -> T): T {
    val t = Time.precisionTime
    val ret = block()
    Log.log(level, tag) { "${message()} ${((Time.precisionTime - t) * 1000.0).toString(3)} ms" }
    return ret
}

inline fun <T> Any.timedMs(message: String, level: Log.Level = Log.Level.INFO, block: () -> T): T {
    val t = Time.precisionTime
    val ret = block()
    Log.log(level, this::class.simpleName) { "$message ${((Time.precisionTime - t) * 1000.0).toString(3)} ms" }
    return ret
}

inline fun <T> Any.timedMs(message: () -> String, level: Log.Level = Log.Level.INFO, block: () -> T): T {
    val t = Time.precisionTime
    val ret = block()
    Log.log(level, this::class.simpleName) { "${message()} ${((Time.precisionTime - t) * 1000.0).toString(3)} ms" }
    return ret
}
