package de.fabmax.kool.math

import de.fabmax.kool.util.BoundingBox
import kotlin.math.*

/**
 * @author fabmax
 */

fun <T: Vec3f> pointTree(items: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(items, { x }, { y }, { z }, { 0f }, { 0f }, { 0f }, bucketSz)
}

interface KdTreeTraverser<T> {
    fun onStart(tree: KdTree<T>) { }

    fun traversalOrder(tree: KdTree<T>, left: KdTree<T>.Node, right: KdTree<T>.Node): Int {
        return KdTree.TRAV_NO_PREFERENCE
    }

    fun traverseLeaf(tree: KdTree<T>, leaf: KdTree<T>.Node)
}

class InRadiusTraverser<T>() : KdTreeTraverser<T> {

    val result: MutableList<T> = mutableListOf()
    val center = MutableVec3f()
    var radius = 1f
        set(value) {
            field = value
            radiusSqr = value * value
        }
    private var radiusSqr = 1f

    constructor(center: Vec3f, radius: Float) : this() {
        this.center.set(center)
        this.radius = radius
    }

    override fun onStart(tree: KdTree<T>) {
        result.clear()
    }

    override fun traversalOrder(tree: KdTree<T>, left: KdTree<T>.Node, right: KdTree<T>.Node): Int {
        val dLeft = left.bounds.pointDistanceSqr(center)
        val dRight = right.bounds.pointDistanceSqr(center)

        if (dLeft > radiusSqr && dRight > radiusSqr) {
            return KdTree.TRAV_NONE
        } else if (dLeft > radiusSqr) {
            return KdTree.TRAV_RIGHT_ONLY
        } else if (dRight > radiusSqr) {
            return KdTree.TRAV_LEFT_ONLY
        }
        return KdTree.TRAV_NO_PREFERENCE
    }

    override fun traverseLeaf(tree: KdTree<T>, leaf: KdTree<T>.Node) {
        for (i in leaf.indices) {
            val it = tree.items[i]
            val dx = tree.getX.invoke(it) - center.x
            val dy = tree.getY.invoke(it) - center.y
            val dz = tree.getZ.invoke(it) - center.z
            if (dx*dx + dy*dy + dz*dz < radiusSqr) {
                result.add(it)
            }
        }
    }
}

class KdTree<T>(items: List<T>,
                val getX: T.() -> Float, val getY: T.() -> Float, val getZ: T.() -> Float,
                val getSzX: T.() -> Float, val getSzY: T.() -> Float, val getSzZ: T.() -> Float,
                bucketSz: Int = 20) {

    val items: List<T> get() = mutItems
    val root: Node

    private val mutItems: MutableList<T> = mutableListOf()

    private val cmpX: (T, T) -> Int = { a, b -> a.getX().compareTo(b.getX()) }
    private val cmpY: (T, T) -> Int = { a, b -> a.getY().compareTo(b.getY()) }
    private val cmpZ: (T, T) -> Int = { a, b -> a.getZ().compareTo(b.getZ()) }

    companion object {
        const val TRAV_NO_PREFERENCE = 0
        const val TRAV_LEFT_FIRST = 1
        const val TRAV_LEFT_ONLY = 2
        const val TRAV_RIGHT_FIRST = 3
        const val TRAV_RIGHT_ONLY = 4
        const val TRAV_NONE = 5
    }

    init {
        this.mutItems.addAll(items)
        root = Node(this.mutItems.indices, 0, bucketSz)
    }

    fun traverse(traverser: KdTreeTraverser<T>) {
        traverser.onStart(this)
        root.traverse(traverser)
    }

    fun inRadius(center: Vec3f, radius: Float, result: MutableList<T>) {
        result.clear()
        root.inRadius(center, radius*radius, result)
    }

    inner class Node(val indices: IntRange, val depth: Int, bucketSz: Int) {

        val isLeaf: Boolean
        val left: Node?
        val right: Node?

        val bounds = BoundingBox()

        private val tmpVec = MutableVec3f()

        init {
            bounds.batchUpdate = true
            for (i in indices) {
                val it = mutItems[i]
                bounds.add(tmpVec.set(it.getX(), it.getY(), it.getZ()))
                tmpVec.x += it.getSzX()
                tmpVec.y += it.getSzY()
                tmpVec.z += it.getSzZ()
                bounds.add(tmpVec)
            }
            bounds.batchUpdate = false

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

        fun inRadius(center: Vec3f, sqrRadius: Float, result: MutableList<T>) {
            if (isLeaf) {
                for (i in indices) {
                    val dx = center.x - mutItems[i].getX()
                    val dy = center.y - mutItems[i].getY()
                    val dz = center.z - mutItems[i].getZ()
                    if (dx*dx + dy*dy + dz*dz < sqrRadius) {
                        result.add(mutItems[i])
                    }
                }
            } else {
                if (left!!.bounds.pointDistanceSqr(center) < sqrRadius) {
                    left.inRadius(center, sqrRadius, result)
                }
                if (right!!.bounds.pointDistanceSqr(center) < sqrRadius) {
                    right.inRadius(center, sqrRadius, result)
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
