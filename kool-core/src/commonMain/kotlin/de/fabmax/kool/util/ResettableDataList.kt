package de.fabmax.kool.util

class ResettableDataList<T: ResettableData<I>, I>(val factory: () -> T) : List<T> {
    @PublishedApi
    internal val buffer = mutableListOf<T>()
    @PublishedApi
    internal var pointer = 0

    override val size: Int get() = pointer

    fun reset() {
        pointer = 0
    }

    fun shrink() {
        while (buffer.size > pointer) {
            buffer.removeLast()
        }
    }

    fun acquire(init: I): T {
        val data = buffer.getOrNull(pointer++) ?: factory().also { buffer.add(it) }
        data.reset(init)
        return data
    }

    override fun isEmpty(): Boolean = pointer == 0

    override fun contains(element: T): Boolean {
        for (i in 0 until pointer) {
            if (buffer[i] == element) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int): T = buffer[index]

    override fun indexOf(element: T): Int {
        for (i in 0 until pointer) {
            if (buffer[i] == element) {
                return i
            }
        }
        return -1
    }

    override fun lastIndexOf(element: T): Int {
        for (i in (pointer - 1) downTo 0) {
            if (buffer[i] == element) {
                return i
            }
        }
        return -1
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = buildList {
        for (i in fromIndex until toIndex) {
            add(buffer[i])
        }
    }

    override fun iterator(): Iterator<T> = It()
    override fun listIterator(): ListIterator<T> = It()
    override fun listIterator(index: Int): ListIterator<T> = It()

    private inner class It : ListIterator<T> {
        var pos = 0

        override fun next(): T {
            if (pos !in 0 until pointer) {
                throw NoSuchElementException()
            }
            return buffer[pos++]
        }

        override fun hasNext(): Boolean {
            return pos < pointer
        }

        override fun hasPrevious(): Boolean {
            return pos > 0
        }

        override fun previous(): T {
            if (pos !in 1 .. pointer) {
                throw NoSuchElementException()
            }
            return buffer[--pos]
        }

        override fun nextIndex(): Int {
            return pos
        }

        override fun previousIndex(): Int {
            return pos - 1
        }

    }
}

interface ResettableData<I> {
    fun reset(init: I)
}