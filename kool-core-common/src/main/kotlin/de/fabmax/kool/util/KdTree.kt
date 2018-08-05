package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.partition

/**
 * @author fabmax
 */

class KdTree<T: Any>(items: List<T>, itemAdapter: ItemAdapter<T>, bucketSz: Int = 20) : SpatialTree<T>(itemAdapter) {

    override val root: KdNode
    override val size: Int
        get() = items.size

    private val items = MutableList(items.size, items::get)

    private val cmpX: (T, T) -> Int = { a, b -> itemAdapter.getX(a).compareTo(itemAdapter.getX(b)) }
    private val cmpY: (T, T) -> Int = { a, b -> itemAdapter.getY(a).compareTo(itemAdapter.getY(b)) }
    private val cmpZ: (T, T) -> Int = { a, b -> itemAdapter.getZ(a).compareTo(itemAdapter.getZ(b)) }

    init {
        root = KdNode(items.indices, 0, bucketSz)
    }

    override fun contains(element: T) = root.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        for (elem in elements) {
            if (!contains(elem)) {
                return false
            }
        }
        return true
    }

    override fun isEmpty() = items.isEmpty()

    override fun iterator() = items.iterator()

    inner class KdNode(override val nodeRange: IntRange, depth: Int, bucketSz: Int) : Node(depth) {
        override val children = mutableListOf<KdNode>()
        override val size: Int
        override val items: List<T>
            get() = this@KdTree.items

        init {
            val tmpVec = MutableVec3f()
            size = nodeRange.last - nodeRange.first + 1
            bounds.batchUpdate {
                for (i in nodeRange) {
                    val it = items[i]
                    add(itemAdapter.getMin(it, tmpVec))
                    add(itemAdapter.getMax(it, tmpVec))
                }
            }

            if (nodeRange.last - nodeRange.first > bucketSz) {
                var cmp = cmpX
                if (bounds.size.y > bounds.size.x && bounds.size.y > bounds.size.z) {
                    cmp = cmpY
                } else if (bounds.size.z > bounds.size.x && bounds.size.z > bounds.size.y) {
                    cmp = cmpZ
                }
                val k = nodeRange.first + (nodeRange.last - nodeRange.first) / 2
                this@KdTree.items.partition(nodeRange, k, cmp)

                children.add(KdNode(nodeRange.first..k, depth + 1, bucketSz))
                children.add(KdNode((k+1)..nodeRange.last, depth + 1, bucketSz))

            } else {
                // this is a leaf node
                for (i in nodeRange) {
                    itemAdapter.setNode(items[i], this)
                }
            }
        }

        fun contains(item: T): Boolean {
            if (isLeaf) {
                for (i in nodeRange) {
                    if (items[i] == item) {
                        return true
                    }
                }
                return false

            } else {
                return when {
                    children[0].bounds.isIncluding(itemAdapter.getX(item), itemAdapter.getY(item), itemAdapter.getZ(item)) -> children[0].contains(item)
                    children[1].bounds.isIncluding(itemAdapter.getX(item), itemAdapter.getY(item), itemAdapter.getZ(item)) -> children[1].contains(item)
                    else -> false
                }
            }
        }
    }
}
