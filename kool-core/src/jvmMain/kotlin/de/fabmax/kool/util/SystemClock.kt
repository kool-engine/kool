package de.fabmax.kool.util

internal actual fun SystemClock(): SystemClock = SystemClockImpl

private object SystemClockImpl : SystemClock {
    override fun now(): Double = System.nanoTime() / 1e9
}