package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.LineMesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.ColorGradient

fun <T: Vec3f> pointKdTree(points: List<T>, bucketSz: Int = 16): KdTree<T> {
    return KdTree(points, Vec3fAdapter(), bucketSz)
}

fun <T: Vec3f> pointOcTree(points: List<T> = emptyList(), bounds: BoundingBoxD = BoundingBoxD(), bucketSz: Int = 16): OcTree<T> {
    return OcTree(Vec3fAdapter(), points, bounds, bucketSz = bucketSz)
}

fun triangleKdTree(mesh: IndexedVertexList, bucketSz: Int = 16): KdTree<Triangle> {
    val triangles = mutableListOf<Triangle>()
    val v = mesh[0]
    for (i in 0 until mesh.numIndices step 3) {
        val p0 = Vec3f(v.apply { index = mesh.indices[i] })
        val p1 = Vec3f(v.apply { index = mesh.indices[i+1] })
        val p2 = Vec3f(v.apply { index = mesh.indices[i+2] })
        triangles += Triangle(p0, p1, p2)
    }
    return triangleKdTree(triangles, bucketSz)
}

fun <T: Triangle> triangleKdTree(triangles: List<T>, bucketSz: Int = 16): KdTree<T> {
    return KdTree(triangles, TriangleAdapter(), bucketSz)
}

fun <T: Triangle> triangleOcTree(triangles: List<T> = emptyList(), bounds: BoundingBoxD = BoundingBoxD(), bucketSz: Int = 16): OcTree<T> {
    return OcTree(TriangleAdapter(), triangles, bounds, bucketSz = bucketSz)
}

fun triangleOcTree(mesh: IndexedVertexList, bucketSz: Int = 16): OcTree<Triangle> {
    val triangles = mutableListOf<Triangle>()
    val v = mesh[0]
    for (i in 0 until mesh.numIndices step 3) {
        val p0 = Vec3f(v.apply { index = mesh.indices[i] })
        val p1 = Vec3f(v.apply { index = mesh.indices[i+1] })
        val p2 = Vec3f(v.apply { index = mesh.indices[i+2] })
        triangles += Triangle(p0, p1, p2)
    }
    return triangleOcTree(triangles, mesh.bounds.toBoundingBoxD(), bucketSz)
}

fun <T: Edge<*>> edgeKdTree(edges: List<T>, bucketSz: Int = 16): KdTree<T> {
    return KdTree(edges, EdgeAdapter(), bucketSz)
}

fun <T: Edge<*>> edgeOcTree(triangles: List<T> = emptyList(), bounds: BoundingBoxD = BoundingBoxD(), bucketSz: Int = 16): OcTree<T> {
    return OcTree(EdgeAdapter(), triangles, bounds, bucketSz = bucketSz)
}

abstract class SpatialTree<T: Any>(val itemAdapter: ItemAdapter<T>) : Collection<T> {

    abstract val root: Node

    open fun drawNodeBounds(lineMesh: LineMesh) {
        fun Node.drawBounds(lineMesh: LineMesh, depth: Int) {
            val color = ColorGradient.JET_MD.getColor((depth % 6.7f) / 6.7f)
            lineMesh.addBoundingBox(bounds.toBoundingBoxF(), color)
            for (i in children.indices) {
                children[i].drawBounds(lineMesh, depth + 1)
            }
        }
        root.drawBounds(lineMesh, 0)
    }

    abstract inner class Node {
        abstract val size: Int
        abstract val children: List<Node>
        val bounds = BoundingBoxD()
        val isLeaf
            get() = children.isEmpty()
        val isEmpty
            get() = children.isEmpty() && itemsUnbounded.isEmpty()
        val isNotEmpty
            get() = !isEmpty

        /**
         * traversalOrder can be set to arbitrary values (e.g. temporarily computed distance values) during tree
         * traversal by tree traversers.
         */
        var traversalOrder = 0f

        /**
         * Item list, depending on implementation the list can be shared between multiple nodes, meaning not all
         * element within the list belong to this node. Therefore, when using this list one must consider [nodeRange].
         *
         * Non-leaf nodes can, but don't have to, supply items of sub-nodes.
         */
        abstract val itemsUnbounded: List<T>

        /**
         * Range within [itemsUnbounded] in which elements belong to this node.
         */
        abstract val nodeRange: IntRange
    }
}