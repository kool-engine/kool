package de.fabmax.kool.util

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

    override fun remove(element: T) = elements.remove(element)

    override fun removeAll(elements: Collection<T>) = this.elements.removeAll(elements)

    override fun retainAll(elements: Collection<T>) = this.elements.retainAll(elements)

    operator fun plusAssign(element: T) {
        add(element)
    }

    operator fun minusAssign(element: T) {
        remove(element)
    }

    fun peek(): T {
        if (size == 0) {
            throw NoSuchElementException()
        }
        return elements[0]
    }

    fun poll(): T {
        if (size == 0) {
            throw NoSuchElementException()
        }
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
            elements.remove(current)
        }
    }

    private fun <T> MutableList<T>.swap(a: Int, b: Int) {
        this[a] = this[b].also { this[b] = this[a] }
    }
}
