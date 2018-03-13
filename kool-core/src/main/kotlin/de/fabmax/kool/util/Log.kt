package de.fabmax.kool.util

/**
 * Super primitive debug logging facility.
 */
object Log {

    val DEFAULT_PRINTER: (lvl: Level, tag: String?, message: String) -> Unit = { lvl, tag, message ->
        println("${lvl.indicator}/$tag: $message")
    }

    var level = Level.DEBUG
    var printer: (lvl: Level, tag: String?, message: String) -> Unit = DEFAULT_PRINTER

    inline fun tExt(src: Any, message: () -> String) = logExt(Level.TRACE, src, message)
    inline fun t(tag: String?, message: () -> String) = log(Level.TRACE, tag, message)

    inline fun dExt(src: Any, message: () -> String) = logExt(Level.DEBUG, src, message)
    inline fun d(tag: String?, message: () -> String) = log(Level.DEBUG, tag, message)

    inline fun iExt(src: Any, message: () -> String) = logExt(Level.INFO, src, message)
    inline fun i(tag: String?, message: () -> String) = log(Level.INFO, tag, message)

    inline fun wExt(src: Any, message: () -> String) = logExt(Level.WARN, src, message)
    inline fun w(tag: String?, message: () -> String) = log(Level.WARN, tag, message)

    inline fun eExt(src: Any, message: () -> String) = logExt(Level.ERROR, src, message)
    inline fun e(tag: String?, message: () -> String) = log(Level.ERROR, tag, message)

    inline fun logExt(level: Level, src: Any, message: () -> String) = log(level, src::class.simpleName, message)
    inline fun log(level: Level, tag: String?, message: () -> String) {
        if (level.level >= this.level.level) {
            printer(level, tag, message())
        }
    }

    enum class Level(val level: Int, val indicator: Char) {
        TRACE(0, 'T'),
        DEBUG(1, 'D'),
        INFO(2, 'I'),
        WARN(3, 'W'),
        ERROR(4, 'E'),
        OFF(5, 'x')
    }
}

inline fun Any.logT(message: () -> String) = Log.tExt(this, message)
inline fun Any.logD(message: () -> String) = Log.dExt(this, message)
inline fun Any.logI(message: () -> String) = Log.iExt(this, message)
inline fun Any.logW(message: () -> String) = Log.wExt(this, message)
inline fun Any.logE(message: () -> String) = Log.eExt(this, message)
