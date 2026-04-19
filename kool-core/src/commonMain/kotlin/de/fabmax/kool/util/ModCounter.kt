package de.fabmax.kool.util

import kotlinx.atomicfu.atomic

class ModCounter(init: Int = 0) {
    private val counter = atomic(init)
    val count: Int get() = counter.value

    fun increment(): ModCounter {
        counter.incrementAndGet()
        return this
    }

    fun incrementIf(value: Boolean) {
        if (value) {
            counter.incrementAndGet()
        }
    }

    fun reset(count: Int) { counter.getAndSet(count) }
    fun isDirty(count: Int): Boolean = count != this.count
    fun isNotDirty(count: Int): Boolean = count == this.count

    fun reset(counter: ModCounter) { this.counter.getAndSet(counter.count) }
    fun isDirty(counter: ModCounter): Boolean = counter.count != this.count
    fun isNotDirty(counter: ModCounter): Boolean = counter.count == this.count

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ModCounter
        return count == other.count
    }

    override fun hashCode(): Int {
        return count
    }
}