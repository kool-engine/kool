package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.*
import de.fabmax.kool.util.ObjectPool
import de.fabmax.kool.util.PriorityQueue
import kotlin.math.max
import kotlin.math.sqrt

open class NearestTraverser<T: Any> : CenterPointTraverser<T>() {
    var sqrDist = Double.POSITIVE_INFINITY
    var nearest: T? = null

    open fun setup(center: Vec3f, maxRadius: Double = Double.POSITIVE_INFINITY): NearestTraverser<T> {
        super.setup(center)
        sqrDist = maxRadius * maxRadius
        nearest = null
        return this
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        if (node.children.size == 2) {
            // kd-tree optimized traversal
            var dA = pointDistance.nodeSqrDistanceToPoint(node.children[0], center)
            var dB = pointDistance.nodeSqrDistanceToPoint(node.children[1], center)
            var ndA = node.children[0]
            var ndB = node.children[1]
            if (dB < dA) {
                dA = dB.also { dB = dA }
                ndA = ndB.also { ndB = ndA }
            }

            if (dA < sqrDist) {
                traverseNode(tree, ndA)
                if (dB < sqrDist) {
                    traverseNode(tree, ndB)
                }
            }

        } else {
            // general version for arbitrary number of child nodes
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
}

open class KNearestTraverser<T: Any> : CenterPointTraverser<T>() {
    var k = 10
        protected set
    var radiusSqr = Double.POSITIVE_INFINITY
        protected set

    val result = mutableListOf<T>()
    var maxDistance = 0.0
        protected set

    // store items in a priority queue, farthest distance first
    private val items = PriorityQueue<Item<T>>(compareBy { -it.dSqr })
    private val itemRecycler = ObjectPool { Item<T>() }

    private val childLists = ChildNodesWithDistance { pointDistance.nodeSqrDistanceToPoint(it, center) }

    open fun setup(center: Vec3f, k: Int, maxRadius: Double = Double.POSITIVE_INFINITY): KNearestTraverser<T> {
        super.setup(center)
        this.k = k
        this.radiusSqr = maxRadius * maxRadius
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        super.traverse(tree)

        result.clear()
        maxDistance = 0.0
        if (!items.isEmpty()) {
            maxDistance = sqrt(items.peek().dSqr)
            while (!items.isEmpty()) {
                result += items.poll().item
            }
        }
        itemRecycler.recycleAll()
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        if (node.children.size == 2) {
            // kd-tree optimized traversal
            var dA = pointDistance.nodeSqrDistanceToPoint(node.children[0], center)
            var dB = pointDistance.nodeSqrDistanceToPoint(node.children[1], center)
            var ndA = node.children[0]
            var ndB = node.children[1]
            if (dB < dA) {
                dA = dB.also { dB = dA }
                ndA = ndB.also { ndB = ndA }
            }

            if (dA < radiusSqr && (items.size < k || dA < items.peek().dSqr)) {
                traverseNode(tree, ndA)
                if (dB < radiusSqr && (items.size < k || dB < items.peek().dSqr)) {
                    traverseNode(tree, ndB)
                }
            }

        } else {
            // general version for arbitrary number of child nodes
            childLists.use(node.children.size) { childList ->
                for (i in node.children.indices) {
                    childList[i].setChildNode(node.children[i])
                }
                childLists.sortByDistance(childList)

                for (i in node.children.indices) {
                    val child = childList[i]
                    if (child.node!!.isNotEmpty) {
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

    private fun insert(value: T, dSqr: Double) {
        val insItem = if (items.size == k) {
            items.poll()
        } else {
            itemRecycler.get()
        }
        items += insItem.set(value, dSqr)
    }

    private class Item<T : Any> {
        lateinit var item: T
        var dSqr: Double = 0.0

        fun set(item: T, dSqr: Double): Item<T> {
            this.item = item
            this.dSqr = dSqr
            return this
        }
    }
}

open class NearestToRayTraverser<T: Any> : SpatialTreeTraverser<T>() {
    val ray = RayD()
    var nearest: T? = null
        protected set
    var distance = Double.POSITIVE_INFINITY
        protected set
    var distanceSqr = Double.POSITIVE_INFINITY
        protected set

    var rayDistance = object : RayDistance<T> { }

    private val childLists = ChildNodesWithDistance { rayDistance.nodeSqrDistanceToRay(it, ray) }

    open fun setup(ray: RayD): NearestToRayTraverser<T> {
        this.ray.set(ray)
        nearest = null
        distanceSqr = Double.POSITIVE_INFINITY
        distance = Double.POSITIVE_INFINITY
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        super.traverse(tree)
        distance = if (distanceSqr < Double.POSITIVE_INFINITY) sqrt(distanceSqr) else Double.POSITIVE_INFINITY
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
            override fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: RayD): Double {
                return item.rayDistanceSqr(ray.toRayF()).toDouble()
            }
        }
    }
}

class ChildNodesWithDistance<T: Any>(val childDist: (SpatialTree<T>.Node) -> Double) {
    val childListRecycler = ObjectPool { mutableListOf<Child>() }
    val childComparator = compareBy<Child> { it.dist }

    inline fun use(n: Int, block: (MutableList<Child>) -> Unit) {
        childListRecycler.use { childList ->
            if (childList.size != n) {
                if (childList.size < n) {
                    for (i in 1..(n - childList.size)) {
                        childList += Child(0.0, null)
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

    inner class Child(var dist: Double, var node: SpatialTree<T>.Node?) {
        fun setChildNode(node: SpatialTree<T>.Node) {
            this.node = node
            dist = childDist(node)
        }
    }
}

open class TriangleHitTraverser<T: Triangle> : NearestToRayTraverser<T>() {
    val isHit: Boolean
        get() = nearest != null

    val hitPoint = MutableVec3d()

    init {
        rayDistance = object : RayDistance<T> {
            override fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: RayD): Double {
                val dist = item.hitDistance(ray.toRayF())
                return if (dist < Float.POSITIVE_INFINITY) {
                    (dist * dist).toDouble()
                } else {
                    return Double.POSITIVE_INFINITY
                }
            }
        }
    }

    override fun setup(ray: RayD): TriangleHitTraverser<T> {
        hitPoint.set(Vec3d.ZERO)
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
    fun nodeSqrDistanceToRay(node: SpatialTree<T>.Node, ray: RayD): Double {
        return if (node.isEmpty) {
            Double.POSITIVE_INFINITY
        } else {
            node.bounds.center
            val d = max(0.0, ray.distanceToPoint(node.bounds.center) - node.bounds.size.length() * 0.5)
            d * d
        }
    }

    fun itemSqrDistanceToRay(tree: SpatialTree<T>, item: T, ray: RayD): Double {
        return ray.sqrDistanceToPoint(tree.itemAdapter.getCenterX(item), tree.itemAdapter.getCenterY(item), tree.itemAdapter.getCenterZ(item))
    }
}
