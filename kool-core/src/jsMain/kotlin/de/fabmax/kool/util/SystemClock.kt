package de.fabmax.kool.util

internal actual object SystemClock {
    actual fun now(): Double = js("performance.now()") as Double / 1e3
}