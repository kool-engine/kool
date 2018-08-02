package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import kotlin.math.sqrt


interface SpatialTreeTraverser<T: Any> {
    fun onStart(tree: SpatialTree<T>) { }
    fun onFinish(tree: SpatialTree<T>) { }

    fun traversalOrder(tree: SpatialTree<T>, candidates: MutableList<SpatialTree<T>.Node>) { }

    fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node)
}

open class InRadiusTraverser<T: Any>() : SpatialTreeTraverser<T> {
    val result: MutableList<T> = mutableListOf()
    val center = MutableVec3f()
    var radius = 1f
        set(value) {
            field = value
            radiusSqr = value * value
        }
    private var radiusSqr = 1f

    constructor(center: Vec3f, radius: Float) : this() {
        setup(center, radius)
    }

    fun setup(center: Vec3f, radius: Float): InRadiusTraverser<T> {
        this.center.set(center)
        this.radius = radius
        return this
    }

    override fun onStart(tree: SpatialTree<T>) {
        result.clear()
    }

    override fun traversalOrder(tree: SpatialTree<T>, candidates: MutableList<SpatialTree<T>.Node>) {
        candidates.removeCandidatesOutOfSqrDist(center, radiusSqr)
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            val dSqr = sqrDistance(tree, it)
            if (dSqr < radiusSqr) {
                result.add(it)
            }
        }
    }

    protected open fun sqrDistance(tree: SpatialTree<T>, item: T): Float {
        val dx = tree.itemDim.getCenterX(item) - center.x
        val dy = tree.itemDim.getCenterY(item) - center.y
        val dz = tree.itemDim.getCenterZ(item) - center.z
        return dx*dx + dy*dy + dz*dz
    }
}

open class KNearestTraverser<T: Any>() : SpatialTreeTraverser<T> {
    val center = MutableVec3f()
    var k = 10
    var radiusSqr = 1e18f

    val result = mutableListOf<T>()
    var maxDistance = 0f

    private val itemRecycler = ObjectPool { Item<T>() }

    // store items in a priority queue, farthest distance first
    private val items = PriorityQueue<Item<T>>(Comparator { a, b -> b.dSqr.compareTo(a.dSqr) })

    constructor(center: Vec3f, k: Int, maxRadius: Float = MAX_RADIUS) : this() {
        setup(center, k, maxRadius)
    }

    fun setup(center: Vec3f, k: Int, maxRadius: Float = MAX_RADIUS): KNearestTraverser<T> {
        this.center.set(center)
        this.k = k
        this.radiusSqr = maxRadius * maxRadius
        return this
    }

    override fun onFinish(tree: SpatialTree<T>) {
        result.clear()
        maxDistance = 0f

        if (!items.isEmpty()) {
            maxDistance = sqrt(items.peek().dSqr)
            while (!items.isEmpty()) {
                result += items.poll().item
            }
        }
        itemRecycler.recycleAll()
    }

    override fun traversalOrder(tree: SpatialTree<T>, candidates: MutableList<SpatialTree<T>.Node>) {
        val remThresh = if (items.size < k) {
            // result size is less than request number of items, traverse all nodes in max radius
            radiusSqr
        } else {
            // result already contains k items, only traverse nodes nearer than farthest item
            items.peek().dSqr
        }
        candidates.removeCandidatesOutOfSqrDist(center, remThresh)

        if (candidates.size > 1) {
            // sort candidates by distance (traverse nearest first)
            if (candidates.size == 2) {
                // optimization in case there are only two candidates (always true for kd-trees)
                if (candidates[1].bounds.pointDistanceSqr(center) < candidates[0].bounds.pointDistanceSqr(center)) {
                    // swap candidates
                    candidates[1] = candidates[0].also { candidates[0] = candidates[1] }
                }
            } else {
                // more than two candidate nodes, sort them by distance
                candidates.sortBy { it.bounds.pointDistanceSqr(center) }
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            val dSqr = sqrDistance(tree, it)
            if (dSqr < radiusSqr && (items.size < k || dSqr < items.peek().dSqr)) {
                insert(it, dSqr)
            }
        }
    }

    protected open fun sqrDistance(tree: SpatialTree<T>, item: T): Float {
        val dx = tree.itemDim.getCenterX(item) - center.x
        val dy = tree.itemDim.getCenterY(item) - center.y
        val dz = tree.itemDim.getCenterZ(item) - center.z
        return dx*dx + dy*dy + dz*dz
    }

    private fun insert(value: T, dSqr: Float) {
        if (items.size == k) {
            items.poll()
        }
        items += itemRecycler.get().set(value, dSqr)
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

    companion object {
        const val MAX_RADIUS = 1.8446743E19f     // sqrt(Float.MAX_VALUE)
    }
}

fun <T: Any> MutableList<SpatialTree<T>.Node>.removeCandidatesOutOfSqrDist(center: Vec3f, sqrDist: Float) {
    // avoid object allocation by not using an iterator...
    for (i in size-1 downTo 0) {
        // remove all candidate nodes with bounds out of search radius
        if (get(i).bounds.pointDistanceSqr(center) > sqrDist) {
            removeAt(i)
        }
    }
}
