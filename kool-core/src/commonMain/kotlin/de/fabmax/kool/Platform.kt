package de.fabmax.kool

/**
 * @author fabmax
 */

expect fun now(): Double

expect fun getMemoryInfo(): String

expect fun Double.toString(precision: Int): String

/**
 * Replacement function for synchronized() which is deprecated on multi-platform. On JVM this falls back to
 * synchronized(), on JS this simply executes block.
 */
expect inline fun <R> lock(lock: Any, block: () -> R): R

fun Float.toString(precision: Int): String = this.toDouble().toString(precision)
