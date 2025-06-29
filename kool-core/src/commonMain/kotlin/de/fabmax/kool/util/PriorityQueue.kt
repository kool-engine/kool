package de.fabmax.kool.util

/**
 * Simple priority queue implementation based on a min-heap.
 */
class PriorityQueue<T>(comparator: Comparator<T>? = null) : MutableCollection<T> {

    private val comparator: Comparator<T>
    private val elements = mutableListOf<T>()

    override val size: Int
        get() = elements.size

    init {
        @Suppress("UNCHECKED_CAST")
        this.comparator = comparator ?: Comparator { a, b -> (a as Comparable<T>).compareTo(b) }
    }

    override fun clear() = elements.clear()

    override fun add(element: T): Boolean {
        elements += element
        swim(size-1)
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for (e in elements) {
            add(e)
        }
        return true
    }

    override fun remove(element: T): Boolean {
        val index = elements.indexOf(element)
        if (index < 0) {
            return false
        }
        elements.swap(index, elements.lastIndex)
        elements.removeAt(elements.lastIndex)
        sink(index)
        return true
    }

    override fun removeAll(elements: Collection<T>) = this.elements.removeAll(elements)

    override fun retainAll(elements: Collection<T>) = this.elements.retainAll(elements)

    operator fun plusAssign(element: T) {
        add(element)
    }

    operator fun minusAssign(element: T) {
        remove(element)
    }

    fun peek(): T = elements.getOrNull(0) ?: throw NoSuchElementException()

    fun poll(): T {
        val first = peek()
        elements.swap(0, elements.lastIndex)
        elements.removeAt(elements.lastIndex)
        sink(0)
        return first
    }

    private fun swim(n: Int) {
        var k = n + 1
        while (k > 1 && greater(k / 2 - 1, k - 1)) {
            elements.swap(k - 1, k / 2 - 1)
            k /= 2
        }
    }

    private fun sink(n: Int) {
        var k = n + 1
        while (2 * k <= size) {
            var j = 2 * k
            if (j < size && greater(j - 1, j)) {
                j++
            }
            if (!greater(k - 1, j - 1)) {
                break
            }
            elements.swap(k - 1, j - 1)
            k = j
        }
    }

    private fun greater(i: Int, j: Int) = comparator.compare(elements[i], elements[j]) > 0

    override fun isEmpty() = elements.isEmpty()

    override fun contains(element: T) = elements.contains(element)

    override fun containsAll(elements: Collection<T>) = this.elements.containsAll(elements)

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        val q = PriorityQueue(comparator).also { it.elements.addAll(elements) }
        var current: T? = null

        override fun hasNext() = q.isNotEmpty()

        override fun next(): T {
            val e = q.poll()
            current = e
            return e
        }

        override fun remove() {
            val index = elements.indexOf(current)
            elements.swap(index, elements.lastIndex)
            elements.removeAt(elements.lastIndex)
            sink(index)
        }
    }

    private fun <T> MutableList<T>.swap(a: Int, b: Int) {
        this[a] = this[b].also { this[b] = this[a] }
    }
}
