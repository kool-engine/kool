package de.fabmax.kool.util

fun <T> List<T>.copy(): List<T> {
    return List(size) { i -> this[i] }
}

fun <T> Set<T>.copy(): Set<T> {
    return mutableSetOf<T>().also { it.addAll(this) }
}