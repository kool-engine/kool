package de.fabmax.kool.util

class RingBuffer<T: Any?>(val capacity: Int) : Iterable<T> {

    private val buffer: MutableList<T> = mutableListOf()

    private var firstI = 0
    private var insertI = 0

    var size: Int = 0
        private set

    fun add(item: T) {
        if (insertI < buffer.size) {
            buffer[insertI] = item
        } else {
            buffer += item
        }
        insertI = (insertI + 1) % capacity
        size++
        if (size > capacity) {
            size = capacity
            firstI = (insertI + 1) % capacity
        }
        buffer.size
    }

    fun addAll(items: Collection<T>) {
        items.forEach { add(it) }
    }

    operator fun plusAssign(item: T) = add(item)

    operator fun plusAssign(items: Collection<T>) = addAll(items)

    operator fun get(index: Int): T {
        if (index !in 0..size) {
            throw IndexOutOfBoundsException("Index $index not in buffer bounds")
        }
        return buffer[(firstI + index) % buffer.size]
    }

    operator fun contains(item: T): Boolean {
        return item in buffer
    }

    fun clear() {
        size = 0
        firstI = 0
        insertI = 0
        buffer.clear()
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        var index = 0
        override fun hasNext(): Boolean = index < size
        override fun next(): T = get(index++)
    }
}