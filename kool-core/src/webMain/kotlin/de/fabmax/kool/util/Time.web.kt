package de.fabmax.kool.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal actual fun SystemClock(): SystemClock = SystemClockImpl

private object SystemClockImpl : SystemClock {
    @OptIn(ExperimentalTime::class)
    override fun now(): Double {
        val now = Clock.System.now()
        return now.toEpochMilliseconds() / 1000 + now.nanosecondsOfSecond / 1e9
    }
}