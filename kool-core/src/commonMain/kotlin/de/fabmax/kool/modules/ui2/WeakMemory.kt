package de.fabmax.kool.modules.ui2

import kotlin.reflect.KClass

class WeakMemory {

    val memory = mutableListOf<MemEntries<*>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> weakMemory(provider: () -> T): T {
        val entries = memory.find { it.type == T::class } ?: MemEntries(T::class).also { memory += it }
        return (entries as MemEntries<T>).getOrPutNextEntry(provider)
    }

    fun rewind() {
        if (memory.isNotEmpty()) {
            memory.forEach { it.rewind() }
        }
    }

    fun clear() {
        memory.clear()
    }

    class MemEntries<T: Any>(val type: KClass<T>) {
        val entries = mutableListOf<T>()
        var nextEntry = 0

        fun rewind() {
            nextEntry = 0
        }

        inline fun getOrPutNextEntry(provider: () -> T): T {
            return if (nextEntry < entries.size) {
                entries[nextEntry++]
            } else {
                nextEntry++
                val newEntry = provider()
                entries += newEntry
                return newEntry
            }
        }
    }
}