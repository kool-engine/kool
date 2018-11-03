package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import kotlin.math.sqrt


interface SpatialTreeTraverser<T: Any> {
    fun onStart(tree: SpatialTree<T>) { }
    fun onFinish(tree: SpatialTree<T>) { }

    fun traversalOrder(tree: SpatialTree<T>, candidates: MutableList<SpatialTree<T>.Node>)

    fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node)
}

interface PointDistance<T: Any> {
    fun nodeDistanceToPoint(node: SpatialTree<T>.Node, point: Vec3f): Float = node.bounds.pointDistanceSqr(point)

    fun itemSqrDistanceToPoint(tree: SpatialTree<T>, item: T, point: Vec3f): Float {
        val dx = tree.itemAdapter.getCenterX(item) - point.x
        val dy = tree.itemAdapter.getCenterY(item) - point.y
        val dz = tree.itemAdapter.getCenterZ(item) - point.z
        return dx*dx + dy*dy + dz*dz
    }
}

interface RayDistance<T: Any> {
    fun nodeDistanceToRay(node: SpatialTree<T>.Node, ray: Ray): Float {
        val halfExtX = node.bounds.size.x * 0.5f
        val halfExtY = node.bounds.size.y * 0.5f
        val halfExtZ = node.bounds.size.z * 0.5f
        val r = sqrt(halfExtX*halfExtX + halfExtY*halfExtY + halfExtZ*halfExtZ)
        val dist = (ray.distanceToPoint(node.bounds.center) - r).clamp(0f, Float.MAX_VALUE)
        return dist * dist
    }

    fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
        return ray.sqrDistanceToPoint(tree.itemAdapter.getCenterX(item), tree.itemAdapter.getCenterY(item), tree.itemAdapter.getCenterZ(item))
    }
}

abstract class CenterPointTraverser<T: Any> : SpatialTreeTraverser<T> {
    val center = MutableVec3f()

    var pointDistance = object : PointDistance<T> { }

    protected fun setup(center: Vec3f) {
        this.center.set(center)
    }
}

open class InRadiusTraverser<T: Any> : CenterPointTraverser<T>() {
    val result: MutableList<T> = mutableListOf()
    var radius = 1f
        protected set
    private var radiusSqr = 1f

    open fun setup(center: Vec3f, radius: Float): InRadiusTraverser<T> {
        super.setup(center)
        this.radius = radius
        this.radiusSqr = radius * radius
        return this
    }

    override fun onStart(tree: SpatialTree<T>) {
        result.clear()
    }

    override fun traversalOrder(tree: SpatialTree<T>, candidates: MutableList<SpatialTree<T>.Node>) {
        candidates.computeTraversalOrder { pointDistance.nodeDistanceToPoint(it, center) }
        candidates.removeCandidatesOutOfDist(radiusSqr)
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            val dSqr = pointDistance.itemSqrDistanceToPoint(tree, it, center)
            if (dSqr < radiusSqr) {
                result.add(it)
            }
        }
    }
}

open class KNearestTraverser<T: Any> : CenterPointTraverser<T>() {
    var k = 10
        protected set
    var radiusSqr = 1e18f
        protected set

    val result = mutableListOf<T>()
    var maxDistance = 0f
        protected set

    // store items in a priority queue, farthest distance first
    private val items = PriorityQueue<Item<T>>(Comparator { a, b -> b.dSqr.compareTo(a.dSqr) })
    private val itemRecycler = ObjectPool { Item<T>() }

    open fun setup(center: Vec3f, k: Int, maxRadius: Float = MAX_RADIUS): KNearestTraverser<T> {
        super.setup(center)
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
        candidates.computeTraversalOrder { pointDistance.nodeDistanceToPoint(it, center) }
        candidates.removeCandidatesOutOfDist(remThresh)
        candidates.sortByTraversalOrder()
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            val dSqr = pointDistance.itemSqrDistanceToPoint(tree, it, center)
            if (dSqr < radiusSqr && (items.size < k || dSqr < items.peek().dSqr)) {
                insert(it, dSqr)
            }
        }
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

open class NearestToRayTraverser<T: Any> : SpatialTreeTraverser<T> {
    val ray = Ray()
    var nearest: T? = null
        protected set
    var distance = 0f
        protected set
    var distanceSqr = Float.MAX_VALUE
        protected set

    var rayDistance = object : RayDistance<T> { }

    open fun setup(ray: Ray): NearestToRayTraverser<T> {
        this.ray.set(ray)
        nearest = null
        distanceSqr = Float.MAX_VALUE
        return this
    }

    override fun onFinish(tree: SpatialTree<T>) {
        distance = if (nearest != null) sqrt(distanceSqr) else Float.MAX_VALUE
    }

    override fun traversalOrder(tree: SpatialTree<T>, candidates: MutableList<SpatialTree<T>.Node>) {
        candidates.computeTraversalOrder { rayDistance.nodeDistanceToRay(it, ray) }
        candidates.removeCandidatesOutOfDist(distanceSqr)
        candidates.sortByTraversalOrder()
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            val dSqr = rayDistance.itemSqrDistanceToRay(tree, it, ray)
            if (dSqr < distanceSqr) {
                nearest = it
                distanceSqr = dSqr
            }
        }
    }
}

open class NearestEdgeToRayTraverser<T: Edge> : NearestToRayTraverser<T>() {
    init {
        rayDistance = object : RayDistance<T> {
            override fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
                return item.rayDistanceSqr(ray)
            }
        }
    }
}

open class TriangleHitTraverser<T: Triangle> : NearestToRayTraverser<T>() {
    init {
        rayDistance = object : RayDistance<T> {
            override fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
                val dist = item.hitDistance(ray)
                return if (dist < Float.MAX_VALUE) {
                    dist * dist
                } else {
                    return Float.MAX_VALUE
                }
            }
        }
    }
}

inline fun <T: Any> MutableList<SpatialTree<T>.Node>.computeTraversalOrder(distFun: (SpatialTree<T>.Node) -> Float) {
    for (i in indices) {
        val nd = get(i)
        nd.traversalOrder = distFun(nd)
    }
}

fun <T: Any> MutableList<SpatialTree<T>.Node>.removeCandidatesOutOfDist(dist: Float) {
    // avoid object allocation by not using an iterator...
    for (i in size-1 downTo 0) {
        // remove all candidate nodes with bounds out of search radius
        if (get(i).traversalOrder > dist) {
            removeAt(i)
        }
    }
}

fun <T: Any> MutableList<SpatialTree<T>.Node>.sortByTraversalOrder() {
    if (size > 1) {
        // sort candidates by distance (traverse nearest first)
        if (size == 2) {
            // optimization in case there are only two candidates (always true for kd-trees)
            if (get(1).traversalOrder < get(0).traversalOrder) {
                // swap candidates
                this[1] = this[0].also { this[0] = this[1] }
            }
        } else {
            // more than two candidate nodes, sort them by distance
            sortBy { it.traversalOrder }
        }
    }
}
