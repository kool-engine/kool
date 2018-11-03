package de.fabmax.kool

/**
 * @author fabmax
 */

expect fun now(): Double

expect fun getMemoryInfo(): String

expect fun Double.toString(precision: Int): String

fun Float.toString(precision: Int): String = this.toDouble().toString(precision)
