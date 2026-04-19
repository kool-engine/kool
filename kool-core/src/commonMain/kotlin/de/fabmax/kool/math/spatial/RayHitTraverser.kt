package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.*

class RayHitTraverser<T: Any, HitData>(
    val rayHitTest: RayHitTest<T, HitData>,
) : SpatialTreeTraverser<T>() {
    private val ray = RayD()
    private var maxItems: Int = Int.MAX_VALUE
    private var limitDistSqr: Double = Double.POSITIVE_INFINITY
    private val _result = mutableListOf<Hit<T, HitData>>()
    val result: List<Hit<T, HitData>> get() = _result

    fun setup(ray: RayF, maxItems: Int = Int.MAX_VALUE, maxDistance: Double = Double.POSITIVE_INFINITY): RayHitTraverser<T, HitData> {
        ray.toRayD(this.ray)
        this.maxItems = maxItems
        limitDistSqr = maxDistance * maxDistance
        _result.clear()
        return this
    }

    fun setup(ray: RayD, maxItems: Int = Int.MAX_VALUE, maxDistance: Double = Double.POSITIVE_INFINITY): RayHitTraverser<T, HitData> {
        this.ray.set(ray)
        this.maxItems = maxItems
        limitDistSqr = maxDistance * maxDistance
        _result.clear()
        return this
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        if (node.children.size == 2) {
            val child0 = node.children[0]
            val child1 = node.children[1]
            val dist0 = node.bounds.hitDistanceSqr(ray)
            val dist1 = node.bounds.hitDistanceSqr(ray)
            if (dist0 < dist1) {
                if (dist0 < limitDistSqr) traverseNode(tree, child0)
                if (dist1 < limitDistSqr) traverseNode(tree, child1)
            } else {
                if (dist1 < limitDistSqr) traverseNode(tree, child1)
                if (dist0 < limitDistSqr) traverseNode(tree, child0)
            }
        } else {
            val nodesWithDistance = mutableListOf<Pair<Double, SpatialTree<T>.Node>>()
            for (i in node.children.indices) {
                val child = node.children[i]
                val hitDistSqr = node.bounds.hitDistanceSqr(ray)
                if (hitDistSqr < limitDistSqr) {
                    nodesWithDistance.add(hitDistSqr to child)
                }
            }
            nodesWithDistance.sortBy { it.first }
            for (i in nodesWithDistance.indices) {
                val (hitDistSqr, child) = nodesWithDistance[i]
                if (_result.size < maxItems || hitDistSqr < limitDistSqr) {
                    traverseNode(tree, child)
                }
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.itemsUnbounded[i]
            if (filter(it)) {
                val hitResult = rayHitTest.hitTest(tree, it, ray)
                if (hitResult != null) {
                    val add = _result.size < maxItems || hitResult.distance < _result.last().distance
                    if (add) {
                        val searchIdx = _result.binarySearch { hit -> hit.distance.compareTo(hitResult.distance) }
                        val insertIdx = if (searchIdx < 0) -searchIdx - 1 else searchIdx
                        _result.add(insertIdx, hitResult)
                    }
                    if (_result.size > maxItems) {
                        _result.removeLast()
                    }
                    if (_result.size == maxItems) {
                        val lastDist = _result.last().distance
                        limitDistSqr = lastDist * lastDist
                    }
                }
            }
        }
    }
}

data class Hit<T: Any, HitData>(val item: T, val distance: Double, val hitData: HitData)

fun interface RayHitTest<T: Any, HitData> {
    fun hitTest(tree: SpatialTree<T>, item: T, ray: RayD): Hit<T, HitData>?

    companion object {
        fun <T: Any, HitData> default(defaultHitData: HitData): RayHitTest<T, HitData> = RayHitTest { tree, item, ray ->
            val center = Vec3d(tree.itemAdapter.getCenterX(item), tree.itemAdapter.getCenterY(item), tree.itemAdapter.getCenterZ(item))
            val dCenter = ray.sqrDistanceToPoint(center)
            val rx = tree.itemAdapter.getSzX(item) * 0.5
            val ry = tree.itemAdapter.getSzY(item) * 0.5
            val rz = tree.itemAdapter.getSzZ(item) * 0.5
            val sqrRadius = rx * rx + ry * ry + rz * rz
            return@RayHitTest if (dCenter <= sqrRadius) {
                val dist = center.distanceToRay(ray)
                Hit(item, dist, defaultHitData)
            } else {
                null
            }
        }
    }
}

fun <T: Any, HitData> SpatialTree<T>.rayHit(
    rayF: RayF,
    maxItems: Int = Int.MAX_VALUE,
    maxDistance: Double = Double.POSITIVE_INFINITY,
    traverser: RayHitTraverser<T, HitData>
): List<Hit<T, HitData>> {
    traverser.setup(rayF, maxItems, maxDistance)
    traverser.traverse(this)
    return traverser.result.toList()
}

fun <T: Any, HitData> SpatialTree<T>.rayHit(
    rayD: RayD,
    maxItems: Int = Int.MAX_VALUE,
    maxDistance: Double = Double.POSITIVE_INFINITY,
    traverser: RayHitTraverser<T, HitData>
): List<Hit<T, HitData>> {
    traverser.setup(rayD, maxItems, maxDistance)
    traverser.traverse(this)
    return traverser.result.toList()
}