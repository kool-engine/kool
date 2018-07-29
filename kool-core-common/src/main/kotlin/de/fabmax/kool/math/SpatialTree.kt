package de.fabmax.kool.math

import de.fabmax.kool.util.AutoRecycler
import de.fabmax.kool.util.BoundingBox

interface ItemSize<in T> {
    fun getX(elem: T): Float
    fun getY(elem: T): Float
    fun getZ(elem: T): Float

    fun getSzX(elem: T): Float = 0f
    fun getSzY(elem: T): Float = 0f
    fun getSzZ(elem: T): Float = 0f

    fun getCenterX(elem: T): Float = getX(elem) + getSzX(elem) / 2
    fun getCenterY(elem: T): Float = getY(elem) + getSzY(elem) / 2
    fun getCenterZ(elem: T): Float = getZ(elem) + getSzZ(elem) / 2

    fun getMin(elem: T, result: MutableVec3f): MutableVec3f =
            result.set(getX(elem), getY(elem), getZ(elem))
    fun getCenter(elem: T, result: MutableVec3f): MutableVec3f =
            result.set(getCenterX(elem), getCenterY(elem), getCenterZ(elem))
    fun getMax(elem: T, result: MutableVec3f): MutableVec3f =
            result.set(getX(elem) + getSzX(elem), getY(elem) + getSzY(elem), getZ(elem) + getSzZ(elem))
}

object Vec3fSize : ItemSize<Vec3f> {
    override fun getX(elem: Vec3f): Float = elem.x
    override fun getY(elem: Vec3f): Float = elem.y
    override fun getZ(elem: Vec3f): Float = elem.z

    override fun getCenterX(elem: Vec3f): Float = elem.x
    override fun getCenterY(elem: Vec3f): Float = elem.y
    override fun getCenterZ(elem: Vec3f): Float = elem.z

    override fun getMin(elem: Vec3f, result: MutableVec3f): MutableVec3f = result.set(elem)
    override fun getCenter(elem: Vec3f, result: MutableVec3f): MutableVec3f  = result.set(elem)
    override fun getMax(elem: Vec3f, result: MutableVec3f): MutableVec3f  = result.set(elem)
}

abstract class SpatialTree<T: Any>(val itemSize: ItemSize<T>) {

    protected val candidatesPool = AutoRecycler<MutableList<Node>> { mutableListOf() }

    abstract val root: Node

    open fun traverse(traverser: SpatialTreeTraverser<T>) {
        traverser.onStart(this)
        root.traverse(traverser)
        traverser.onFinish(this)
    }

    abstract inner class Node() {
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
                    candidates.addAll(children)
                    traverser.traversalOrder(this@SpatialTree, candidates)
                    for (i in candidates.indices) {
                        candidates[i].traverse(traverser)
                    }
                }
            }
        }
    }
}