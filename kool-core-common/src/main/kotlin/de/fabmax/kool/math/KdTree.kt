package de.fabmax.kool.math

import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.ObjectPool
import kotlin.math.*

/**
 * @author fabmax
 */

fun <T: Vec3f> pointTree(items: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(items, KdTree.VEC3F_HELPER, bucketSz)
}

interface KdTreeTraverser<T: Any> {
    fun onStart(tree: KdTree<T>) { }
    fun onFinish(tree: KdTree<T>) { }

    fun traversalOrder(tree: KdTree<T>, left: KdTree<T>.Node, right: KdTree<T>.Node): Int {
        return KdTree.TRAV_NO_PREFERENCE
    }

    fun traverseLeaf(tree: KdTree<T>, leaf: KdTree<T>.Node)
}

open class InRadiusTraverser<T: Any>() : KdTreeTraverser<T> {
    val result: MutableList<T> = mutableListOf()
    val center = MutableVec3f()
    var radius = 1f
        set(value) {
            field = value
            radiusSqr = value * value
        }
    private var radiusSqr = 1f

    constructor(center: Vec3f, radius: Float) : this() {
        reset(center, radius)
    }

    fun reset(center: Vec3f, radius: Float): InRadiusTraverser<T> {
        this.center.set(center)
        this.radius = radius
        return this
    }

    override fun onStart(tree: KdTree<T>) {
        result.clear()
    }

    override fun traversalOrder(tree: KdTree<T>, left: KdTree<T>.Node, right: KdTree<T>.Node): Int {
        val dLeft = left.bounds.pointDistanceSqr(center)
        val dRight = right.bounds.pointDistanceSqr(center)

        return when {
            dLeft > radiusSqr && dRight > radiusSqr -> KdTree.TRAV_NONE
            dLeft > radiusSqr -> KdTree.TRAV_RIGHT_ONLY
            dRight > radiusSqr -> KdTree.TRAV_LEFT_ONLY
            else -> KdTree.TRAV_NO_PREFERENCE
        }
    }

    override fun traverseLeaf(tree: KdTree<T>, leaf: KdTree<T>.Node) {
        for (i in leaf.indices) {
            val it = tree.items[i]
            val dSqr = sqrDistance(tree, it)
            if (dSqr < radiusSqr) {
                result.add(it)
            }
        }
    }

    protected open fun sqrDistance(tree: KdTree<T>, item: T): Float {
        val dx = tree.helper.getX(item) - center.x
        val dy = tree.helper.getY(item) - center.y
        val dz = tree.helper.getZ(item) - center.z
        return dx*dx + dy*dy + dz*dz
    }
}

open class KNearestTraverser<T: Any>() : KdTreeTraverser<T> {
    val result = mutableListOf<T>()
    val center = MutableVec3f()
    var k = 10
    var maxRadius = 1e9f
        set(value) {
            field = value
            radiusSqr = value * value
        }
    private var radiusSqr = 1e18f
    private var maxDSqr = 0f

    private val itemRecycler = ObjectPool { Item<T>() }
    private val items = mutableListOf<Item<T>>()

    constructor(center: Vec3f, k: Int, maxRadius: Float = 1e9f) : this() {
        reset(center, k, maxRadius)
    }

    fun reset(center: Vec3f, k: Int, maxRadius: Float = 1e9f): KNearestTraverser<T> {
        this.center.set(center)
        this.k = k
        this.maxRadius = maxRadius
        return this
    }

    override fun onStart(tree: KdTree<T>) {
        result.clear()
        maxDSqr = 0f
    }

    override fun onFinish(tree: KdTree<T>) {
        for (i in items.indices) {
            result += items[i].item
        }
        items.clear()
        itemRecycler.recycleAll()
        maxRadius = sqrt(maxDSqr)
    }

    override fun traversalOrder(tree: KdTree<T>, left: KdTree<T>.Node, right: KdTree<T>.Node): Int {
        val dLeft = left.bounds.pointDistanceSqr(center)
        val dRight = right.bounds.pointDistanceSqr(center)

        return when {
            dLeft > maxDSqr && dRight > maxDSqr -> KdTree.TRAV_NONE
            dLeft < dRight -> KdTree.TRAV_LEFT_FIRST
            else -> KdTree.TRAV_RIGHT_FIRST
        }
    }

    override fun traverseLeaf(tree: KdTree<T>, leaf: KdTree<T>.Node) {
        for (i in leaf.indices) {
            val it = tree.items[i]
            val dSqr = sqrDistance(tree, it)
            if (dSqr < radiusSqr && (items.size < k || dSqr < maxDSqr)) {
                insert(it, dSqr)
            }
        }
    }

    protected open fun sqrDistance(tree: KdTree<T>, item: T): Float {
        val dx = tree.helper.getX(item) - center.x
        val dy = tree.helper.getY(item) - center.y
        val dz = tree.helper.getZ(item) - center.z
        return dx*dx + dy*dy + dz*dz
    }

    private fun insert(value: T, dSqr: Float) {
        if (dSqr >= maxDSqr) {
            if (items.size < k) {
                items += itemRecycler.get().set(value, dSqr)
                maxDSqr = dSqr
            } else {
                return
            }
        } else {
            // fixme: not very efficient, priority queue would be better but small k should be fine
            items += itemRecycler.get().set(value, dSqr)
            for (i in items.lastIndex downTo 1) {
                if (items[i].dSqr < items[i-1].dSqr) {
                    items[i] = items[i-1].also { items[i-1] = items[i] }
                } else {
                    break
                }
            }
            if (items.size > k) {
                items.removeAt(items.lastIndex)
                maxDSqr = items.last().dSqr
            }
        }
    }

    private class Item<T: Any> {
        lateinit var item: T
        var dSqr: Float = 0f

        fun set(item: T, dSqr: Float): Item<T> {
            this.item = item
            this.dSqr = dSqr
            return this
        }
    }
}

interface TreeHelper<in T> {
    fun getX(elem: T): Float
    fun getY(elem: T): Float
    fun getZ(elem: T): Float
    fun getSzX(elem: T): Float = 0f
    fun getSzY(elem: T): Float = 0f
    fun getSzZ(elem: T): Float = 0f
}

class KdTree<T: Any>(items: List<T>,
                val helper: TreeHelper<T>,
                bucketSz: Int = 20) {

    val items: List<T> get() = mutItems
    val root: Node

    private val mutItems: MutableList<T> = mutableListOf()

    private val cmpX: (T, T) -> Int = { a, b -> helper.getX(a).compareTo(helper.getX(b)) }
    private val cmpY: (T, T) -> Int = { a, b -> helper.getY(a).compareTo(helper.getY(b)) }
    private val cmpZ: (T, T) -> Int = { a, b -> helper.getZ(a).compareTo(helper.getZ(b)) }

    companion object {
        const val TRAV_NO_PREFERENCE = 0
        const val TRAV_LEFT_FIRST = 1
        const val TRAV_LEFT_ONLY = 2
        const val TRAV_RIGHT_FIRST = 3
        const val TRAV_RIGHT_ONLY = 4
        const val TRAV_NONE = 5

        val VEC3F_HELPER = object : TreeHelper<Vec3f> {
            override fun getX(elem: Vec3f): Float = elem.x
            override fun getY(elem: Vec3f): Float = elem.y
            override fun getZ(elem: Vec3f): Float = elem.z
        }
    }

    init {
        this.mutItems.addAll(items)
        root = Node(this.mutItems.indices, 0, bucketSz)
    }

    fun traverse(traverser: KdTreeTraverser<T>) {
        traverser.onStart(this)
        root.traverse(traverser)
        traverser.onFinish(this)
    }

    inner class Node(val indices: IntRange, val depth: Int, bucketSz: Int) {

        val isLeaf: Boolean
        val left: Node?
        val right: Node?

        val bounds = BoundingBox()

        init {
            val tmpVec = MutableVec3f()
            bounds.batchUpdate {
                for (i in indices) {
                    val it = mutItems[i]
                    add(tmpVec.set(helper.getX(it), helper.getY(it), helper.getZ(it)))
                    tmpVec.x += helper.getSzX(it)
                    tmpVec.y += helper.getSzY(it)
                    tmpVec.z += helper.getSzZ(it)
                    add(tmpVec)
                }
            }

            if (indices.last - indices.first < bucketSz) {
                isLeaf = true
                left = null
                right = null

            } else {
                isLeaf = false

                var cmp = cmpX
                if (bounds.size.y > bounds.size.x && bounds.size.y > bounds.size.z) {
                    cmp = cmpY
                } else if (bounds.size.z > bounds.size.x && bounds.size.z > bounds.size.y) {
                    cmp = cmpZ
                }
                val k = indices.first + (indices.last - indices.first) / 2
                partition(indices.first, indices.last, k, cmp)
                left = Node(indices.first..k, depth + 1, bucketSz)
                right = Node((k+1)..indices.last, depth + 1, bucketSz)
            }
        }

        fun traverse(traverser: KdTreeTraverser<T>) {
            if (isLeaf) {
                traverser.traverseLeaf(this@KdTree, this)

            } else {
                val pref = traverser.traversalOrder(this@KdTree, left!!, right!!)
                when (pref) {
                    TRAV_NONE -> return
                    TRAV_LEFT_ONLY -> left.traverse(traverser)
                    TRAV_RIGHT_ONLY -> right.traverse(traverser)
                    TRAV_RIGHT_FIRST -> {
                        right.traverse(traverser)
                        left.traverse(traverser)
                    }
                    else -> {   // TRAV_LEFT_FIRST, TRAV_NO_PREFERENCE
                        left.traverse(traverser)
                        right.traverse(traverser)
                    }
                }
            }
        }

        /**
         * Partitions tree items with the given comparator. After partitioning, all elements left of k are smaller
         * than all elements right of k with respect to the given comparator function.
         *
         * This method implements the Floyd-Rivest selection algorithm:
         * https://en.wikipedia.org/wiki/Floyd%E2%80%93Rivest_algorithm
         */
        private fun partition(lt: Int, rt: Int, k: Int, cmp: (T, T) -> Int) {
            var left = lt
            var right = rt
            while (right > left) {
                if (right - left > 600) {
                    val n = right - left + 1
                    val i = k - left + 1
                    val z = ln(n.toDouble())
                    val s = 0.5 * exp(2.0 * z / 3.0)
                    val sd = 0.5 * sqrt(z * s * (n - s) / n) * sign(i - n / 2.0)
                    val newLeft = max(left, (k - i * s / n + sd).toInt())
                    val newRight = min(right, (k + (n - i) * s / n + sd).toInt())
                    partition(newLeft, newRight, k, cmp)
                }
                val t = mutItems[k]
                var i = left
                var j = right
                swapPts(left, k)
                if (cmp(mutItems[right], t) > 0) {
                    swapPts(right, left)
                }
                while (i < j) {
                    swapPts( i, j)
                    i++
                    j--
                    while (cmp(mutItems[i], t) < 0) {
                        i++
                    }
                    while (cmp(mutItems[j], t) > 0) {
                        j--
                    }
                }
                if (cmp(mutItems[left], t) == 0) {
                    swapPts(left, j)
                } else {
                    j++
                    swapPts(j, right)
                }
                if (j <= k) {
                    left = j + 1
                }
                if (k <= j) {
                    right = j - 1
                }
            }
        }

        private fun swapPts(a: Int, b: Int) {
            val tmp = mutItems[a]
            mutItems[a] = mutItems[b]
            mutItems[b] = tmp
        }
    }
}
