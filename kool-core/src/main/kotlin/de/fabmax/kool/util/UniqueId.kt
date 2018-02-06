package de.fabmax.kool.util

object UniqueId {
    private var nextId = 1L
    private val idLock = Any()

    fun nextId(): Long {
        var id = 0L
        synchronized(idLock) {
            id = ++nextId
        }
        return id
    }
}