package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import kotlin.math.max
import kotlin.math.sqrt

interface PointDistance<T: Any> {
    fun nodeSqrDistanceToPoint(node: SpatialTree<T>.Node, point: Vec3f): Float = node.bounds.pointDistanceSqr(point)

    fun itemSqrDistanceToPoint(tree: SpatialTree<T>, item: T, point: Vec3f): Float {
        val dx = tree.itemAdapter.getCenterX(item) - point.x
        val dy = tree.itemAdapter.getCenterY(item) - point.y
        val dz = tree.itemAdapter.getCenterZ(item) - point.z
        return dx*dx + dy*dy + dz*dz
    }
}

interface BoundingSphereDistance<T: Any> : PointDistance<T> {
    override fun itemSqrDistanceToPoint(tree: SpatialTree<T>, item: T, point: Vec3f): Float {
        val dx = tree.itemAdapter.getCenterX(item) - point.x
        val dy = tree.itemAdapter.getCenterY(item) - point.y
        val dz = tree.itemAdapter.getCenterZ(item) - point.z

        val rx = tree.itemAdapter.getSzX(item) * 0.5f
        val ry = tree.itemAdapter.getSzY(item) * 0.5f
        val rz = tree.itemAdapter.getSzZ(item) * 0.5f

        val d = max(0f, sqrt(dx*dx + dy*dy + dz*dz) - sqrt(rx*rx + ry*ry + rz*rz))
        return d*d
    }
}

interface RayDistance<T: Any> {
    fun nodeSqrDistanceToRay(node: SpatialTree<T>.Node, ray: Ray): Float {
        return node.bounds.hitDistanceSqr(ray)
    }

    fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
        return ray.sqrDistanceToPoint(tree.itemAdapter.getCenterX(item), tree.itemAdapter.getCenterY(item), tree.itemAdapter.getCenterZ(item))
    }
}

abstract class SpatialTreeTraverser<T: Any> {

    open fun traverse(tree: SpatialTree<T>) {
        traverseNode(tree, tree.root)
    }

    protected open fun traverseNode(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        if (node.isLeaf) {
            traverseLeaf(tree, node)
        } else {
            traverseChildren(tree, node)
        }
    }

    protected abstract fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node)

    protected abstract fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node)
}

abstract class CenterPointTraverser<T: Any> : SpatialTreeTraverser<T>() {
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
    protected var radiusSqr = 1f

    open fun setup(center: Vec3f, radius: Float): InRadiusTraverser<T> {
        super.setup(center)
        this.radius = radius
        this.radiusSqr = radius * radius
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        result.clear()
        super.traverse(tree)
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        for (i in node.children.indices) {
            val child = node.children[i]
            val dSqr = pointDistance.nodeSqrDistanceToPoint(child, center)
            if (dSqr < radiusSqr) {
                traverseNode(tree, child)
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            if (pointDistance.itemSqrDistanceToPoint(tree, it, center) < radiusSqr) {
                result += it
            }
        }
    }
}

open class BoundingSphereInRadiusTraverser<T: Any> : InRadiusTraverser<T>() {
    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.items[i]
            val rx = tree.itemAdapter.getSzX(it) / 2
            val ry = tree.itemAdapter.getSzY(it) / 2
            val rz = tree.itemAdapter.getSzZ(it) / 2
            val itRadius = sqrt(rx*rx + ry*ry + rz*rz)
            if (sqrt(pointDistance.itemSqrDistanceToPoint(tree, it, center)) - itRadius < radius) {
                result += it
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
                val maxDist = if (items.size < k) { radiusSqr } else { items.peek().dSqr }
                val child = childList[i]
                if (child.dist < maxDist) {
                    traverseNode(tree, child.node!!)
                } else {
                    break
                }
            }
        }
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
                if (child.dist < distanceSqr) {
                    traverseNode(tree, child.node!!)
                } else {
                    break
                }
            }
        }
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

open class NearestEdgeToRayTraverser<T: Edge<*>> : NearestToRayTraverser<T>() {
    init {
        rayDistance = object : RayDistance<T> {
            override fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: Ray): Float {
                return item.rayDistanceSqr(ray)
            }
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
            hitPoint.set(ray.direction).scale(distance).add(ray.origin)
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
