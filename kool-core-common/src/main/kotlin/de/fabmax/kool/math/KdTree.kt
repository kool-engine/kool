package de.fabmax.kool.math

/**
 * @author fabmax
 */

fun <T: Vec3f> pointKdTree(items: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(items, Vec3fSize, bucketSz)
}

class KdTree<T: Any>(items: List<T>, itemSize: ItemSize<T>, bucketSz: Int = 20) : SpatialTree<T>(itemSize) {

    override val root: KdNode

    private val items = MutableList<T>(items.size, items::get)

    private val cmpX: (T, T) -> Int = { a, b -> itemSize.getX(a).compareTo(itemSize.getX(b)) }
    private val cmpY: (T, T) -> Int = { a, b -> itemSize.getY(a).compareTo(itemSize.getY(b)) }
    private val cmpZ: (T, T) -> Int = { a, b -> itemSize.getZ(a).compareTo(itemSize.getZ(b)) }

    init {
        root = KdNode(items.indices, 0, bucketSz)
    }

    inner class KdNode(override val nodeRange: IntRange, val depth: Int, bucketSz: Int) : SpatialTree<T>.Node() {
        override val children = mutableListOf<KdNode>()
        override val items: List<T>
            get() = this@KdTree.items

        init {
            val tmpVec = MutableVec3f()
            bounds.batchUpdate {
                for (i in nodeRange) {
                    val it = items[i]
                    add(itemSize.getMin(it, tmpVec))
                    add(itemSize.getMax(it, tmpVec))
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
            }
        }
    }
}
