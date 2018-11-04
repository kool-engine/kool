package de.fabmax.kool.util

import de.fabmax.kool.lock

object UniqueId {
    private var nextId = 1L
    private val idLock = Any()

    fun nextId(): Long = lock(idLock) { ++nextId }
}