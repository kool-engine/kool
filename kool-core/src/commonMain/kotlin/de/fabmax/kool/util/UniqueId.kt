package de.fabmax.kool.util

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

object UniqueId : SynchronizedObject() {
    private var nextId = atomic(1L)
    private val idLock = Any()

    private val prefixIds = mutableMapOf<String, Long>()

    fun nextId(): Long = nextId.incrementAndGet()

    fun nextId(prefix: String): String {
        return synchronized(this) {
            val index = (prefixIds[prefix] ?: 0) + 1L
            prefixIds[prefix] = index
            "${prefix}-${index}"
        }
    }
}