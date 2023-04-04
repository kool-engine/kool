package de.fabmax.kool.util

import de.fabmax.kool.lock

object UniqueId {
    private var nextId = 1L
    private val idLock = Any()

    private val prefixIds = mutableMapOf<String, Long>()

    fun nextId(): Long = lock(idLock) { ++nextId }

    fun nextId(prefix: String): String {
        return lock(idLock) {
            val index = (prefixIds[prefix] ?: 0) + 1L
            prefixIds[prefix] = index
            "${prefix}-${index}"
        }
    }
}