package de.fabmax.kool.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

object UniqueId : SynchronizedObject() {
    private var nextId = 1L

    private val prefixIds = mutableMapOf<String, Long>()

    fun nextId(): Long = synchronized(this) { nextId++ }

    fun nextId(prefix: String): String {
        return synchronized(this) {
            val index = (prefixIds[prefix] ?: 0) + 1L
            prefixIds[prefix] = index
            "${prefix}-${index}"
        }
    }
}