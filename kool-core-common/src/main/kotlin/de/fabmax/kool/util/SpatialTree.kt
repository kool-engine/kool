package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

fun <T: Vec3f> pointKdTree(items: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(items, Vec3fAdapter, bucketSz)
}

fun <T: Vec3f> pointOcTree(items: List<T> = emptyList(), bucketSz: Int = 20): OcTree<T> {
    return OcTree(Vec3fAdapter, items, bucketSz = bucketSz)
}

interface ItemAdapter<in T: Any> {
    fun getMinX(item: T): Float
    fun getMinY(item: T): Float
    fun getMinZ(item: T): Float

    fun getMaxX(item: T): Float
    fun getMaxY(item: T): Float
    fun getMaxZ(item: T): Float

    fun getCenterX(item: T): Float = (getMinX(item) + getMaxX(item)) * 0.5f
    fun getCenterY(item: T): Float = (getMinY(item) + getMaxY(item)) * 0.5f
    fun getCenterZ(item: T): Float = (getMinZ(item) + getMaxZ(item)) * 0.5f

    fun getSzX(item: T): Float = getMaxX(item) - getMinX(item)
    fun getSzY(item: T): Float = getMaxY(item) - getMinY(item)
    fun getSzZ(item: T): Float = getMaxZ(item) - getMinZ(item)

    fun getMin(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getMinX(item), getMinY(item), getMinZ(item))
    fun getMax(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getMaxX(item), getMaxY(item), getMaxZ(item))
    fun getCenter(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getCenterX(item), getCenterY(item), getCenterZ(item))

    fun setNode(item: T, node: SpatialTree<T>.Node) { }
}

object Vec3fAdapter : ItemAdapter<Vec3f> {
    override fun getMinX(item: Vec3f): Float = item.x
    override fun getMinY(item: Vec3f): Float = item.y
    override fun getMinZ(item: Vec3f): Float = item.z

    override fun getMaxX(item: Vec3f): Float = item.x
    override fun getMaxY(item: Vec3f): Float = item.y
    override fun getMaxZ(item: Vec3f): Float = item.z

    override fun getCenterX(item: Vec3f): Float = item.x
    override fun getCenterY(item: Vec3f): Float = item.y
    override fun getCenterZ(item: Vec3f): Float = item.z

    override fun getMin(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getCenter(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getMax(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
}

abstract class SpatialTree<T: Any>(val itemAdapter: ItemAdapter<T>) : Collection<T> {

    protected val candidatesPool = AutoRecycler<MutableList<Node>> { mutableListOf() }

    abstract val root: Node

    open fun traverse(traverser: SpatialTreeTraverser<T>) {
        traverser.onStart(this)
        root.traverse(traverser)
        traverser.onFinish(this)
    }

    open fun drawNodeBounds(lineMesh: LineMesh) {
        root.drawNodeBounds(lineMesh)
    }

    abstract inner class Node(val depth: Int) {
        abstract val size: Int
        abstract val children: List<Node>
        val bounds = BoundingBox()
        val isLeaf
            get() = children.isEmpty()

        /**
         * traversalOrder can be set to arbitrary values (e.g. temporarily computed distance values) during tree
         * traversal by tree traversers.
         */
        var traversalOrder = 0f

        /**
         * Item list, depending on implementation the list can be shared between multiple nodes, meaning not all
         * element within the list belng to this node. Therefor, when using this list one must consider [nodeRange].
         *
         * Non-leaf nodes can but don't have to supply items of sub-nodes.
         */
        abstract val items: List<T>

        /**
         * Range within [items] in which elements belong to this node.
         */
        abstract val nodeRange: IntRange

        open fun traverse(traverser: SpatialTreeTraverser<T>) {
            if (isLeaf) {
                traverser.traverseLeaf(this@SpatialTree, this)

            } else {
                candidatesPool.use { candidates ->
                    candidates.clear()
                    for (i in children.indices) {
                        if (children[i].size > 0) {
                            candidates += children[i]
                        }
                    }
                    traverser.traversalOrder(this@SpatialTree, candidates)
                    for (i in candidates.indices) {
                        candidates[i].traverse(traverser)
                    }
                }
            }
        }

        open fun drawNodeBounds(lineMesh: LineMesh) {
            val color = ColorGradient.JET_MD.getColor((depth % 6.7f) / 6.7f)
            lineMesh.addBoundingBox(bounds, color)
            for (i in children.indices) {
                children[i].drawNodeBounds(lineMesh)
            }
        }
    }
}