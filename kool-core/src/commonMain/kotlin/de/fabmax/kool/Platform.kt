package de.fabmax.kool

expect fun Double.toString(precision: Int): String

fun Float.toString(precision: Int): String = this.toDouble().toString(precision)
