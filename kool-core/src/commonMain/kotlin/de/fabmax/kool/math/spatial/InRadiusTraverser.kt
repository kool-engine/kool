package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.Vec3f
import kotlin.math.sqrt

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
            if (!child.isEmpty) {
                val dSqr = pointDistance.nodeSqrDistanceToPoint(child, center)
                if (dSqr < radiusSqr) {
                    traverseNode(tree, child)
                }
            }
        }
    }

    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.itemsUnbounded[i]
            if (filter(it) && pointDistance.itemSqrDistanceToPoint(tree, it, center) < radiusSqr) {
                result += it
            }
        }
    }
}

open class BoundingSphereInRadiusTraverser<T: Any> : InRadiusTraverser<T>() {
    override fun traverseLeaf(tree: SpatialTree<T>, leaf: SpatialTree<T>.Node) {
        for (i in leaf.nodeRange) {
            val it = leaf.itemsUnbounded[i]
            if (filter(it)) {
                val rx = tree.itemAdapter.getSzX(it) / 2
                val ry = tree.itemAdapter.getSzY(it) / 2
                val rz = tree.itemAdapter.getSzZ(it) / 2
                val itRadius = sqrt(rx * rx + ry * ry + rz * rz)
                if (sqrt(pointDistance.itemSqrDistanceToPoint(tree, it, center)) - itRadius < radius) {
                    result += it
                }
            }
        }
    }
}
