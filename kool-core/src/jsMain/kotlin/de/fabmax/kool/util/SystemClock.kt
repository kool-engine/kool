package de.fabmax.kool.util

internal actual fun SystemClock(): SystemClock = SystemClockImpl

private object SystemClockImpl : SystemClock {
    override fun now(): Double = js("performance.now()") as Double / 1e3
}