package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Camera
import kotlin.math.sqrt

interface PointDistance<T: Any> {
    fun nodeSqrDistanceToPoint(node: SpatialTree<T>.Node, point: Vec3d): Double = node.bounds.pointDistanceSqr(point)

    fun itemSqrDistanceToPoint(tree: SpatialTree<T>, item: T, point: Vec3d): Double {
        val dx = tree.itemAdapter.getCenterX(item) - point.x
        val dy = tree.itemAdapter.getCenterY(item) - point.y
        val dz = tree.itemAdapter.getCenterZ(item) - point.z
        return dx*dx + dy*dy + dz*dz
    }
}

abstract class SpatialTreeTraverser<T: Any> {

    var filter: ((T) -> Boolean) = { true }

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
    val center = MutableVec3d()

    var pointDistance = object : PointDistance<T> { }

    protected open fun setup(center: Vec3f): CenterPointTraverser<T> {
        this.center.set(center)
        return this
    }
}

class InViewFrustumTraverser<T: Any> : SpatialTreeTraverser<T>() {
    private var cam: Camera? = null
    private var fixedRadius = 0.0
    private val centerVec = MutableVec3f()
    private val centerVecD = MutableVec3d()

    val result = mutableListOf<T>()

    fun setup(cam: Camera, fixedRadius: Double = 0.0): InViewFrustumTraverser<T> {
        this.cam = cam
        this.fixedRadius = fixedRadius
        return this
    }

    override fun traverse(tree: SpatialTree<T>) {
        result.clear()
        super.traverse(tree)
    }

    override fun traverseChildren(tree: SpatialTree<T>, node: SpatialTree<T>.Node) {
        val cam = this.cam ?: return
        for (i in node.children.indices) {
            val child = node.children[i]
            val childRadius = child.bounds.size.length() * 0.5 + fixedRadius
            if (cam.isInFrustum(child.bounds.center.toMutableVec3f(centerVec), childRadius.toFloat())) {
                traverseNode(tree, child)
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        val cam = this.cam ?: return
        for (i in leaf.nodeRange) {
            val item = leaf.itemsUnbounded[i]
            if (filter(item)) {
                val elemR = if (fixedRadius > 0.0) {
                    fixedRadius
                } else {
                    val sx = tree.itemAdapter.getSzX(item)
                    val sy = tree.itemAdapter.getSzX(item)
                    val sz = tree.itemAdapter.getSzX(item)
                    sqrt(sx * sx + sy * sy + sz * sz) / 2f
                }
                if (cam.isInFrustum(tree.itemAdapter.getCenter(item, centerVecD).toMutableVec3f(centerVec), elemR.toFloat())) {
                    result += item
                }
            }
        }
    }
}