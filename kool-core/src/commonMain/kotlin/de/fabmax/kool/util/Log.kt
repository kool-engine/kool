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

inline fun Any.logT(message: () -> String) = logT(this::class.simpleName, message)
inline fun Any.logD(message: () -> String) = logD(this::class.simpleName, message)
inline fun Any.logI(message: () -> String) = logI(this::class.simpleName, message)
inline fun Any.logW(message: () -> String) = logW(this::class.simpleName, message)
inline fun Any.logE(message: () -> String) = logE(this::class.simpleName, message)

inline fun logT(tag: String?, message: () -> String) = Log.log(Log.Level.TRACE, tag, message)
inline fun logD(tag: String?, message: () -> String) = Log.log(Log.Level.DEBUG, tag, message)
inline fun logI(tag: String?, message: () -> String) = Log.log(Log.Level.INFO, tag, message)
inline fun logW(tag: String?, message: () -> String) = Log.log(Log.Level.WARN, tag, message)
inline fun logE(tag: String?, message: () -> String) = Log.log(Log.Level.ERROR, tag, message)
