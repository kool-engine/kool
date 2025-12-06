package de.fabmax.kool.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <R> memStack(block: MemoryStack.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return MemoryStack.stackPush().use(block)
}

class MemoryStack private constructor() {
    val autoDeletables = mutableListOf<AutoDeleteRef<*>>()

    fun <T> autoDelete(obj: T, delete: T.() -> Unit): T {
        autoDeletables += AutoDeleteRef(obj, delete)
        return obj
    }

    inline fun <R> use(block: (MemoryStack) -> R): R {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        try {
            return block(this)
        } finally {
            autoDeletables.forEach { it.delete() }
            autoDeletables.clear()
        }
    }

    class AutoDeleteRef<T>(val obj: T, val delete: T.() -> Unit) {
        fun delete() = obj.delete()
    }

    companion object {
        fun stackPush(): MemoryStack = MemoryStack()
    }
}