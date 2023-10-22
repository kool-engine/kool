package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.ObjectPool
import de.fabmax.kool.util.PriorityQueue
import kotlin.math.max
import kotlin.math.sqrt

open class NearestTraverser<T: Any> : CenterPointTraverser<T>() {
    var sqrDist = MAX_RADIUS * MAX_RADIUS
    var nearest: T? = null

    open fun setup(center: Vec3f, maxRadius: Float = MAX_RADIUS): NearestTraverser<T> {
        super.setup(center)
        sqrDist = maxRadius * maxRadius
        nearest = null
        return this
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        if (node.children.size == 2) {
            // kd-tree optimized traversal
            val dLt = pointDistance.nodeSqrDistanceToPoint(node.children[0], center)
            val dRt = pointDistance.nodeSqrDistanceToPoint(node.children[1], center)

            val d1st: Float
            val d2nd: Float
            val nd1st: SpatialTree<T>.Node
            val nd2nd: SpatialTree<T>.Node

            if (dLt < dRt) {
                d1st = dLt
                d2nd = dRt
                nd1st = node.children[0]
                nd2nd = node.children[1]
            } else {
                d1st = dRt
                d2nd = dLt
                nd1st = node.children[1]
                nd2nd = node.children[0]
            }

            if (d1st < sqrDist) {
                traverseNode(tree, nd1st)
                if (d2nd < sqrDist) {
                    traverseNode(tree, nd2nd)
                }
            }

        } else {
            for (i in node.children.indices) {
                val child = node.children[i]
                if (!child.isEmpty) {
                    val dSqr = pointDistance.nodeSqrDistanceToPoint(child, center)
                    if (dSqr < sqrDist) {
                        traverseNode(tree, child)
                    }
                }
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.itemsUnbounded[i]
            if (filter(it)) {
                val dSqr = pointDistance.itemSqrDistanceToPoint(tree, it, center)
                if (dSqr < sqrDist) {
                    sqrDist = dSqr
                    nearest = it
                }
            }
        }
    }

    companion object {
        const val MAX_RADIUS = 1.8446743E19f     // sqrt(Float.MAX_VALUE)
    }
}

open class KNearestTraverser<T: Any> : CenterPointTraverser<T>() {
    var k = 10
        protected set
    var radiusSqr = MAX_RADIUS * MAX_RADIUS
        protected set

    val result = mutableListOf<T>()
    var maxDistance = 0f
        protected set

    // store items in a priority queue, farthest distance first
    private val items = PriorityQueue<Item<T>>(compareBy { it.dSqr })
    private val itemRecycler = ObjectPool { Item<T>() }

    private val childLists = ChildNodesWithDistance<T> { pointDistance.nodeSqrDistanceToPoint(it, center) }

    open fun setup(center: Vec3f, k: Int, maxRadius: Float = MAX_RADIUS): KNearestTraverser<T> {
        super.setup(center)
        this.k = k
        this.radiusSqr = maxRadius * maxRadius
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        super.traverse(tree)

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

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        childLists.use(node.children.size) { childList ->
            for (i in node.children.indices) {
                childList[i].setChildNode(node.children[i])
            }
            childLists.sortByDistance(childList)

            for (i in node.children.indices) {
                val child = childList[i]
                if (!child.node!!.isEmpty) {
                    val maxDist = if (items.size < k) { radiusSqr } else { items.peek().dSqr }
                    if (child.dist < maxDist) {
                        traverseNode(tree, child.node!!)
                    } else {
                        break
                    }
                }
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.itemsUnbounded[i]
            if (filter(it)) {
                val dSqr = pointDistance.itemSqrDistanceToPoint(tree, it, center)
                if (dSqr < radiusSqr && (items.size < k || dSqr < items.peek().dSqr)) {
                    insert(it, dSqr)
                }
            }
        }
    }

    private fun insert(value: T, dSqr: Float) {
        val insItem = if (items.size == k) {
            items.poll()
        } else {
            itemRecycler.get()
        }
        items += insItem.set(value, dSqr)
    }

    private class Item<T : Any> {
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

open class NearestToRayTraverser<T: Any> : SpatialTreeTraverser<T>() {
    val ray = Ray()
    var nearest: T? = null
        protected set
    var distance = 0f
        protected set
    var distanceSqr = Float.MAX_VALUE
        protected set

    var rayDistance = object : RayDistance<T> { }

    private val childLists = ChildNodesWithDistance<T> { rayDistance.nodeSqrDistanceToRay(it, ray) }

    open fun setup(ray: Ray): NearestToRayTraverser<T> {
        this.ray.set(ray)
        nearest = null
        distanceSqr = Float.MAX_VALUE
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        super.traverse(tree)
        distance = if (distanceSqr != Float.MAX_VALUE) sqrt(distanceSqr) else Float.MAX_VALUE
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        childLists.use(node.children.size) { childList ->
            for (i in node.children.indices) {
                childList[i].setChildNode(node.children[i])
            }
            childLists.sortByDistance(childList)

            for (i in node.children.indices) {
                val child = childList[i]
                if (!child.node!!.isEmpty) {
                    if (child.dist < distanceSqr) {
                        traverseNode(tree, child.node!!)
                    } else {
                        break
                    }
                }
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.itemsUnbounded[i]
            if (filter(it)) {
                val dSqr = rayDistance.itemSqrDistanceToRay(tree, it, ray)
                if (dSqr < distanceSqr) {
                    nearest = it
                    distanceSqr = dSqr
                }
            }
        }
    }
}

open class NearestEdgeToRayTraverser<T: Edge<*>> : NearestToRayTraverser<T>() {
    init {
        rayDistance = object : RayDistance<T> {
            override fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
                return item.rayDistanceSqr(ray)
            }
        }
    }
}

class ChildNodesWithDistance<T: Any>(val childDist: (SpatialTree<T>.Node) -> Float) {
    val childListRecycler = ObjectPool { mutableListOf<Child>() }
    val childComparator = compareBy<Child> { it.dist }

    inline fun use(n: Int, block: (MutableList<Child>) -> Unit) {
        childListRecycler.use { childList ->
            if (childList.size != n) {
                if (childList.size < n) {
                    for (i in 1..(n - childList.size)) {
                        childList += Child(0f, null)
                    }
                } else {
                    while (childList.size > n) {
                        childList.removeAt(childList.lastIndex)
                    }
                }
            }

            block(childList)
        }
    }

    fun sortByDistance(childList: MutableList<Child>) {
        childList.sortWith(childComparator)
    }

    inner class Child(var dist: Float, var node: SpatialTree<T>.Node?) {
        fun setChildNode(node: SpatialTree<T>.Node) {
            this.node = node
            dist = childDist(node)
        }
    }
}

open class TriangleHitTraverser<T: Triangle> : NearestToRayTraverser<T>() {
    val isHit: Boolean
        get() = nearest != null

    val hitPoint = MutableVec3f()

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

    override fun setup(ray: Ray): TriangleHitTraverser<T> {
        hitPoint.set(Vec3f.ZERO)
        super.setup(ray)
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        super.traverse(tree)
        if (isHit) {
            hitPoint.set(ray.direction).mul(distance).add(ray.origin)
        }
    }
}

interface RayDistance<T: Any> {
    fun nodeSqrDistanceToRay(node: SpatialTree<T>.Node, ray: Ray): Float {
        return if (node.isEmpty) {
            Float.MAX_VALUE
        } else {
            node.bounds.center
            val d = max(0f, ray.distanceToPoint(node.bounds.center) - node.bounds.size.length() * 0.5f)
            d * d
        }
    }

    fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
        return ray.sqrDistanceToPoint(tree.itemAdapter.getCenterX(item), tree.itemAdapter.getCenterY(item), tree.itemAdapter.getCenterZ(item))
    }
}
