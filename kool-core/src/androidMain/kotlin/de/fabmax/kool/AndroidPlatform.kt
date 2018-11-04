package de.fabmax.kool

import de.fabmax.kool.math.clamp
import java.lang.RuntimeException
import java.util.*

/**
 * Android specific platform call implementations
 */

actual fun createDefaultContext(): KoolContext {
    throw RuntimeException("Default context not available on android, use KoolActivity")
}

actual fun now(): Double = System.nanoTime() / 1e6

actual fun Double.toString(precision: Int): String =
        java.lang.String.format(Locale.ENGLISH, "%.${precision.clamp(0, 12)}f", this)

actual fun getMemoryInfo(): String {
    val rt = Runtime.getRuntime()
    val freeMem = rt.freeMemory()
    val totalMem = rt.totalMemory()
    return "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB"
}

actual inline fun <R> lock(lock: Any, block: () -> R): R = synchronized(lock, block)
