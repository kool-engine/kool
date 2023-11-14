package de.fabmax.kool.util

object Time {
    private val systemClock = SystemClock()
    /**
     * Time since previous frame in seconds.
     */
    var deltaT: Float = 0f
        internal set

    /**
     * Time since the app started in seconds.
     */
    var gameTime: Double = 0.0
        internal set

    /**
     * Number of rendered frames since the app started.
     */
    var frameCount: Int = 0
        internal set

    /**
     * Precision timer value, unit is seconds. Absolute value has no real meaning, but it can be used to precisely
     * measure time intervals. Precision depends on platform, on JVM it should be around nanoseconds, on JS it should
     * be around 0.1 milliseconds.
     */
    val precisionTime: Double
        get() = systemClock.now()
}

internal expect fun SystemClock(): SystemClock

internal interface SystemClock {
    fun now(): Double
}