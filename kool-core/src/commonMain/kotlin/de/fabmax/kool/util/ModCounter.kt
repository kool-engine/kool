package de.fabmax.kool.util

class ModCounter(init: Int = 0) {
    var count: Int = init
        private set

    fun increment(): ModCounter {
        count++
        return this
    }

    fun incrementIf(value: Boolean) {
        if (value) {
            count++
        }
    }

    fun reset(value: Int) {
        count = value
    }

    fun isDirty(count: Int): Boolean = count != this.count

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