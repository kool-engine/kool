package de.fabmax.kool

/**
 * @author fabmax
 */

expect fun now(): Double

expect fun getMemoryInfo(): String

expect fun formatDouble(d: Double, precision: Int): String

fun formatFloat(f: Float, precision: Int): String = formatDouble(f.toDouble(), precision)
