package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

fun <T: Vec3f> pointKdTree(items: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(items, Vec3FDim, bucketSz)
}

fun <T: Vec3f> pointOcTree(items: List<T> = emptyList(), bucketSz: Int = 20): OcTree<T> {
    return OcTree(Vec3FDim, items, bucketSz = bucketSz)
}

interface ItemDim<in T> {
    fun getX(item: T): Float
    fun getY(item: T): Float
    fun getZ(item: T): Float

    fun getSzX(item: T): Float = 0f
    fun getSzY(item: T): Float = 0f
    fun getSzZ(item: T): Float = 0f

    fun getCenterX(item: T): Float = getX(item) + getSzX(item) / 2
    fun getCenterY(item: T): Float = getY(item) + getSzY(item) / 2
    fun getCenterZ(item: T): Float = getZ(item) + getSzZ(item) / 2

    fun getMin(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getX(item), getY(item), getZ(item))
    fun getCenter(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getCenterX(item), getCenterY(item), getCenterZ(item))
    fun getMax(item: T, result: MutableVec3f): MutableVec3f =
            result.set(getX(item) + getSzX(item), getY(item) + getSzY(item), getZ(item) + getSzZ(item))
}

object Vec3FDim : ItemDim<Vec3f> {
    override fun getX(item: Vec3f): Float = item.x
    override fun getY(item: Vec3f): Float = item.y
    override fun getZ(item: Vec3f): Float = item.z

    override fun getCenterX(item: Vec3f): Float = item.x
    override fun getCenterY(item: Vec3f): Float = item.y
    override fun getCenterZ(item: Vec3f): Float = item.z

    override fun getMin(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getCenter(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getMax(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
}

abstract class SpatialTree<T: Any>(val itemDim: ItemDim<T>) : Collection<T> {

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