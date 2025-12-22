package de.fabmax.kool.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <R> scopedMem(block: ScopedMemory.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return ScopedMemory().use(block)
}

class ScopedMemory : AutoCloseable {
    val autoDeletables = mutableListOf<AutoDeleteRef<*>>()

    fun <T> autoDelete(obj: T, delete: T.() -> Unit): T {
        autoDeletables += AutoDeleteRef(obj, delete)
        return obj
    }

    override fun close() {
        autoDeletables.forEach { it.delete() }
        autoDeletables.clear()
    }

    class AutoDeleteRef<T>(val obj: T, val delete: T.() -> Unit) {
        fun delete() = obj.delete()
    }
}