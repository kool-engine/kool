package de.fabmax.kool.util

class PriorityQueue<T>(comparator: Comparator<T>? = null) : Collection<T> {

    private val comparator: Comparator<T>
    private val elements = mutableListOf<T>()

    override val size: Int
        get() = elements.size

    init {
        this.comparator = comparator ?: Comparator { a, b ->
            (a as Comparable<T>).compareTo(b)
        }
    }

    fun clear() = elements.clear()

    fun add(element: T) {
        elements += element
        swim(size-1)
    }

    operator fun plusAssign(element: T) = add(element)

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

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val q = PriorityQueue(comparator).also { it.elements.addAll(elements) }

        override fun hasNext() = !q.isEmpty()

        override fun next() = q.poll()
    }

    private fun <T> MutableList<T>.swap(a: Int, b: Int) {
        this[a] = this[b].also { this[b] = this[a] }
    }
}
