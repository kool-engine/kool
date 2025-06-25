package de.fabmax.kool.util

/**
 * Naive implementation of a sorted map. Look-up has log(n) complexity, insertion and removal have linear complexity.
 */
open class SortedMap<K: Any, V> : MutableMap<K, V> {
    private val elements = mutableListOf<MutableMap.MutableEntry<K, V>>()

    override val keys: MutableSet<K>
        get() = KeysView(LinkedHashSet<K>().apply { addAll(elements.map { it.key }) })
    override val values: MutableCollection<V>
        get() = ValuesView(elements.map { it.value }.toMutableList())
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = EntriesView(LinkedHashSet<MutableMap.MutableEntry<K, V>>().apply { addAll(elements) })

    override fun put(key: K, value: V): V? {
        val insertI = indexOfKeyGreaterEqual(key)
        if (insertI < size && cmp(elements[insertI].key, key) == 0) {
            val existingVal = elements[insertI].value
            elements[insertI] = KeyValue(key, value)
            return existingVal

        } else {
            val newEntry = KeyValue(key, value)
            elements.add(insertI, newEntry)
            return null
        }
    }

    override fun remove(key: K): V? {
        val existingI = indexOfKeyGreaterEqual(key)
        if (existingI < size && cmp(elements[existingI].key, key) == 0) {
            val existingVal = elements[existingI].value
            elements.removeAt(existingI)
            return existingVal
        } else {
            return null
        }
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (k, v) -> put(k, v) }
    }

    override fun clear() {
        elements.clear()
    }

    override val size: Int get() = elements.size

    override fun isEmpty(): Boolean = elements.isEmpty()

    override fun containsKey(key: K): Boolean {
        val i = indexOfKeyGreaterEqual(key)
        return i < size && cmp(elements[i].key, key) == 0
    }

    override fun containsValue(value: V): Boolean = elements.any { it.value == value }

    override fun get(key: K): V? {
        val i = indexOfKeyGreaterEqual(key)
        return if (i < size && cmp(elements[i].key, key) == 0) elements[i].value else null
    }

    fun firstEntry(): MutableMap.MutableEntry<K, V> = elements.first()

    fun lastEntry(): MutableMap.MutableEntry<K, V> = elements.last()

    fun lowerEntry(key: K): MutableMap.MutableEntry<K, V>? {
        return elements.getOrNull(indexOfKeyGreaterEqual(key) - 1)
    }

    fun higherEntry(key: K): MutableMap.MutableEntry<K, V>? {
        val i = indexOfKeyGreaterEqual(key)
        val elem = elements.getOrNull(i)
        return if (elem != null && cmp(elem.key, key) == 0) {
            elements.getOrNull(i + 1)
        } else {
            elem
        }
    }

    fun floorEntry(key: K): MutableMap.MutableEntry<K, V>? {
        val i = indexOfKeyGreaterEqual(key)
        val elem = elements.getOrNull(i)
        return if (elem != null && cmp(elem.key, key) == 0) {
            elem
        } else {
            elements.getOrNull(i - 1)
        }
    }

    fun ceilingEntry(key: K): MutableMap.MutableEntry<K, V>? {
        return elements.getOrNull(indexOfKeyGreaterEqual(key))
    }

    fun firstKey(): K = firstEntry().key
    fun firstValue(): V = firstEntry().value

    fun lastKey(): K = lastEntry().key
    fun lastValue(): V = lastEntry().value

    fun lowerKey(key: K): K? = lowerEntry(key)?.key
    fun lowerValue(key: K): V? = lowerEntry(key)?.value

    fun higherKey(key: K): K? = higherEntry(key)?.key
    fun higherValue(key: K): V? = higherEntry(key)?.value

    fun floorKey(key: K): K? = floorEntry(key)?.key
    fun floorValue(key: K): V? = floorEntry(key)?.value

    fun ceilingKey(key: K): K? = ceilingEntry(key)?.key
    fun ceilingValue(key: K): V? = ceilingEntry(key)?.value

    private fun indexOfKeyGreaterEqual(key: K): Int {
        var l = -1
        var r = elements.size
        while (l + 1 < r) {
            val m = (l + r) ushr 1
            if (cmp(elements[m].key, key) >= 0) {
                r = m
            } else {
                l = m
            }
        }
        return r
    }

    @Suppress("UNCHECKED_CAST")
    private fun cmp(a: K, b: K): Int = (a as Comparable<K>).compareTo(b)

    private data class KeyValue<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            val prev = value
            value = newValue
            return prev
        }
    }

    private inner class KeysView(val keys: MutableSet<K>) : Set<K> by keys, MutableSet<K> {
        override fun add(element: K): Boolean =
            throw UnsupportedOperationException("Adding elements to key set is not allowed")

        override fun addAll(elements: Collection<K>): Boolean =
            throw UnsupportedOperationException("Adding elements to key set is not allowed")

        override fun remove(element: K): Boolean {
            this@SortedMap.remove(element)
            return keys.remove(element)
        }

        override fun removeAll(elements: Collection<K>): Boolean {
            elements.forEach { this@SortedMap.remove(it) }
            return keys.removeAll(elements)
        }

        override fun retainAll(elements: Collection<K>): Boolean {
            return removeAll(keys - elements)
        }

        override fun clear() {
            this@SortedMap.clear()
            keys.clear()
        }

        override fun iterator(): MutableIterator<K> {
            return ObservingIterator(keys.iterator()) { key, _ ->
                key?.let { this@SortedMap.remove(it) }
            }
        }
    }

    private inner class ValuesView(val values: MutableList<V>) : Collection<V> by values, MutableCollection<V> {
        override fun add(element: V): Boolean =
            throw UnsupportedOperationException("Adding elements to value collection is not allowed")

        override fun addAll(elements: Collection<V>): Boolean =
            throw UnsupportedOperationException("Adding elements to value collection is not allowed")

        override fun clear() {
            this@SortedMap.clear()
            values.clear()
        }

        override fun iterator(): MutableIterator<V> {
            return ObservingIterator(values.iterator()) { _, i ->
                elements.removeAt(i)
            }
        }

        override fun remove(element: V): Boolean {
            val removeIdx = values.indexOf(element)
            if (removeIdx >= 0) {
                values.removeAt(removeIdx)
                elements.removeAt(removeIdx)
                return true
            }
            return false
        }

        override fun removeAll(elements: Collection<V>): Boolean {
            val sizeBefore = values.size
            elements.forEach { remove(it) }
            return values.size < sizeBefore
        }

        override fun retainAll(elements: Collection<V>): Boolean {
            return removeAll(values - elements)
        }
    }

    private inner class EntriesView(val entries: MutableSet<MutableMap.MutableEntry<K, V>>) :
        Set<MutableMap.MutableEntry<K, V>> by entries, MutableSet<MutableMap.MutableEntry<K, V>>
    {
        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
            throw UnsupportedOperationException("Adding elements to entry set is not allowed")

        override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            throw UnsupportedOperationException("Adding elements to entry set is not allowed")

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
            return if (entries.remove(element)) {
                this@SortedMap.remove(element.key)
                true
            } else {
                false
            }
        }

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
            val sizeBefore = size
            elements.forEach { remove(it) }
            return size < sizeBefore
        }

        override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
            return removeAll(entries - elements)
        }

        override fun clear() {
            this@SortedMap.clear()
            entries.clear()
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
            return ObservingIterator(entries.iterator()) { entry, _ ->
                entry?.let { this@SortedMap.remove(it.key) }
            }
        }
    }

    private class ObservingIterator<T>(val it: MutableIterator<T>, val removeCallback: (T?, Int) -> Unit) : MutableIterator<T> by it {
        private var elem: T? = null
        private var removeIdx = -1

        override fun next(): T {
            removeIdx++
            return it.next().also { elem = it }
        }

        override fun remove() {
            it.remove()
            removeCallback(elem, removeIdx)
            removeIdx--
        }
    }
}