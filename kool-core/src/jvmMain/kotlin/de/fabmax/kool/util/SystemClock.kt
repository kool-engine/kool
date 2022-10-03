package de.fabmax.kool.util

internal actual object SystemClock {
    actual fun now(): Double = System.nanoTime() / 1e9
}