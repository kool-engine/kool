package de.fabmax.kool.util

/**
 * Derived from Java's TreeMap
 */

open class TreeMap<K: Any, V: Any> : MutableMap<K, V> {
    @Suppress("UNCHECKED_CAST")
    private val cmp: (a: K, b: K) -> Int = { a, b -> (a as Comparable<K>).compareTo(b) }

    private var root: MapEntry? = null
    override var size = 0
        protected set

    override fun clear() {
        root = null
        size = 0
    }

    override fun isEmpty() = size == 0

    fun firstEntry(): MutableMap.MutableEntry<K, V> {
        var p = root
        if (p != null) {
            while (p?.left != null) {
                p = p.left!!
            }
        }
        return p ?: throw NoSuchElementException()
    }
    fun firstValue() = firstEntry().value
    fun firstKey() = firstEntry().key

    fun lastEntry(): MutableMap.MutableEntry<K, V> {
        var p = root
        if (p != null) {
            while (p?.right != null) {
                p = p.right!!
            }
        }
        return p ?: throw NoSuchElementException()
    }
    fun lastValue() = lastEntry().value
    fun lastKey() = lastEntry().key

    override fun put(key: K, value: V): V? {
        if (root == null) {
            root = MapEntry(key, value, null)

        } else {
            var t = root
            var parent: MapEntry
            do {
                parent = t!!
                t = when {
                    cmp(key, t.key) < 0 -> t.left
                    cmp(key, t.key) > 0-> t.right
                    else -> {
                        return t.setValue(value)
                    }
                }
            } while (t != null)

            val e = MapEntry(key, value, parent)
            if (cmp(key, parent.key) < 0) {
                parent.left = e
            } else {
                parent.right = e
            }
            fixAfterInsertion(e)
        }
        size++
        return null
    }

    override fun remove(key: K): V? {
        return getEntry(key)?.let { p ->
            val value = p.value
            deleteEntry(p)
            value
        }
    }

    override fun get(key: K) = getEntry(key)?.value

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val entries = mutableSetOf<MutableMap.MutableEntry<K, V>>()
            if (!isEmpty()) {
                var e = firstEntry() as MapEntry?
                while (e != null) {
                    entries += e
                    e = e.successor()
                }
            }
            return entries
        }

    override val keys: MutableSet<K>
        get() {
            val keys = mutableSetOf<K>()
            if (!isEmpty()) {
                var e = firstEntry() as MapEntry?
                while (e != null) {
                    keys += e.key
                    e = e.successor()
                }
            }
            return keys
        }

    override val values: MutableCollection<V>
        get() {
            val values = mutableSetOf<V>()
            if (!isEmpty()) {
                var e = firstEntry() as MapEntry?
                while (e != null) {
                    values += e.value
                    e = e.successor()
                }
            }
            return values
        }

    override fun containsKey(key: K) = getEntry(key) != null

    override fun containsValue(value: V) = values.contains(value)

    override fun putAll(from: Map<out K, V>) = from.forEach { put(it.key, it.value) }

    private fun getEntry(key: K): MapEntry? {
        var p = root
        while (p != null) {
            p = when {
                cmp(key, p.key) < 0 -> p.left
                cmp(key, p.key) > 0 -> p.right
                else -> return p
            }
        }
        return null
    }

    private fun deleteEntry(entry: MapEntry) {
        var p = entry
        size--

        // If strictly internal, copy successor's element to p and then make p point to successor.
        if (p.left != null && p.right != null) {
            val s = p.successor()!!
            p.key = s.key
            p.value = s.value
            p = s
        } // p has 2 children

        // Start fixup at replacement node, if it exists.
        val replacement = if (p.left != null) p.left else p.right
        if (replacement != null) {
            // Link replacement to parent
            replacement.parent = p.parent
            when {
                p.parent == null -> root = replacement
                p === p.parent!!.left -> p.parent!!.left = replacement
                else -> p.parent!!.right = replacement
            }

            // Null out links so they are OK to use by fixAfterDeletion.
            p.left = null
            p.right = null
            p.parent = null

            // Fix replacement
            if (p.color == BLACK) {
                fixAfterDeletion(replacement)
            }

        } else if (p.parent == null) {
            // return if we are the only node.
            root = null

        } else {
            //  No children. Use self as phantom replacement and unlink.
            if (p.color == BLACK) {
                fixAfterDeletion(p)
            }

            if (p.parent != null) {
                if (p === p.parent!!.left) {
                    p.parent!!.left = null
                } else if (p === p.parent!!.right) {
                    p.parent!!.right = null
                }
                p.parent = null
            }
        }
    }

    private fun fixAfterInsertion(entry: MapEntry) {
        var x: MapEntry? = entry
        x?.color = RED

        while (x != null && x != root && x.parent.color() == RED) {
            if (x.parent === x.parent?.parent?.left) {
                val y = x.parent?.parent?.right
                if (y.color() == RED) {
                    x.parent?.color = BLACK
                    y?.color = BLACK
                    x.parent?.parent?.color = RED
                    x = x.parent?.parent
                } else {
                    if (x === x.parent?.right) {
                        x = x.parent
                        rotateLeft(x)
                    }
                    x?.parent?.color = BLACK
                    x?.parent?.parent?.color = RED
                    rotateRight(x?.parent?.parent)
                }
            } else {
                val y = x.parent?.parent?.left
                if (y.color() == RED) {
                    x.parent?.color = BLACK
                    y?.color = BLACK
                    x.parent?.parent?.color = RED
                    x = x.parent?.parent
                } else {
                    if (x === x.parent?.left) {
                        x = x.parent
                        rotateRight(x)
                    }
                    x?.parent?.color = BLACK
                    x?.parent?.parent?.color = RED
                    rotateLeft(x?.parent?.parent)
                }
            }
        }
        root?.color = BLACK
    }

    private fun fixAfterDeletion(entry: MapEntry) {
        var x = entry
        while (x != root && x.color == BLACK) {
            if (x == x.parent?.left) {
                var sib = x.parent?.right

                if (sib.color() == RED) {
                    sib?.color = BLACK
                    x.parent?.color = RED
                    rotateLeft(x.parent)
                    sib = x.parent?.right
                }

                if (sib?.left.color() == BLACK && sib?.right.color() == BLACK) {
                    sib?.color = RED
                    x = x.parent!!
                } else {
                    if (sib?.right.color() == BLACK) {
                        sib?.left?.color = BLACK
                        sib?.color = RED
                        rotateRight(sib)
                        sib = x.parent?.right
                    }
                    sib?.color = x.parent.color()
                    x.parent?.color = BLACK
                    sib?.right?.color = BLACK
                    rotateLeft(x.parent)
                    x = root!!
                }

            } else { // symmetric
                var sib = x.parent?.left

                if (sib.color() == RED) {
                    sib?.color = BLACK
                    sib?.parent?.color = RED
                    rotateRight(x.parent)
                    sib = x.parent?.left
                }

                if (sib?.right.color() == BLACK && sib?.left.color() == BLACK) {
                    sib?.color = RED
                    x = x.parent!!
                } else {
                    if (sib?.left.color() == BLACK) {
                        sib?.right?.color = BLACK
                        sib?.color = RED
                        rotateLeft(sib)
                        sib = x.parent?.left
                    }
                    sib?.color = x.parent.color()
                    x.parent?.color = BLACK
                    sib?.left?.color = BLACK
                    rotateRight(x.parent)
                    x = root!!
                }
            }
        }
        x.color = BLACK
    }

    private fun rotateLeft(p: MapEntry?) {
        if (p != null) {
            val r = p.right
            p.right = r?.left
            if (r?.left != null) {
                r.left?.parent = p
            }
            r?.parent = p.parent
            when {
                p.parent == null -> root = r
                p.parent?.left === p -> p.parent?.left = r
                else -> p.parent?.right = r
            }
            r?.left = p
            p.parent = r
        }
    }

    private fun rotateRight(p: MapEntry?) {
        if (p != null) {
            val l = p.left
            p.left = l?.right
            if (l?.right != null) {
                l.right?.parent = p
            }
            l?.parent = p.parent
            when {
                p.parent == null -> root = l
                p.parent?.right === p -> p.parent?.right = l
                else -> p.parent?.left = l
            }
            l?.right = p
            p.parent = l
        }
    }

    private fun MapEntry?.color(): Boolean = this?.color ?: de.fabmax.kool.util.TreeMap.Companion.BLACK

    private fun MapEntry?.successor(): MapEntry? {
        return when {
            this == null -> null
            right != null -> {
                var p = right!!
                while (p.left != null) {
                    p = p.left!!
                }
                p
            }
            else -> {
                var p = parent
                var ch = this
                while (p != null && ch == p.right) {
                    ch = p
                    p = p.parent
                }
                p
            }
        }
    }

    private fun MapEntry?.predecessor(): MapEntry? {
        return when {
            this == null -> null
            left != null -> {
                var p = left!!
                while (p.right != null) {
                    p = p.right!!
                }
                p
            }
            else -> {
                var p = parent
                var ch = this
                while (p != null && ch == p.left) {
                    ch = p
                    p = p.parent
                }
                p
            }
        }
    }

    private inner class MapEntry(key: K, value: V, var parent: MapEntry?) : MutableMap.MutableEntry<K, V> {
        override var key = key
            internal set
        override var value = value
            internal set

        var left: MapEntry? = null
        var right: MapEntry? = null
        var color = BLACK

        override fun setValue(newValue: V): V {
            val old = this.value
            this.value = newValue
            return old
        }
    }

    companion object {
        private const val BLACK = false
        private const val RED = true
    }
}