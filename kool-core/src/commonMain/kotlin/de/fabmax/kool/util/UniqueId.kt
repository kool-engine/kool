package de.fabmax.kool.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

object UniqueId {
    private val lock = SynchronizedObject()
    private var nextId = 1L

    private val prefixIds = mutableMapOf<String, Long>()

    fun nextId(): Long = synchronized(lock) { nextId++ }

    fun nextId(prefix: String): String {
        return synchronized(lock) {
            val index = (prefixIds[prefix] ?: 0) + 1L
            prefixIds[prefix] = index
            "${prefix}-${index}"
        }
    }
}