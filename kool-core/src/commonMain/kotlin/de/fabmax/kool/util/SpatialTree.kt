package de.fabmax.kool.util

import de.fabmax.kool.gl.GL_LINES
import de.fabmax.kool.gl.GL_TRIANGLES
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.isFuzzyZero
import de.fabmax.kool.scene.MeshData
import kotlin.math.min

fun <T: Vec3f> pointKdTree(points: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(points, Vec3fAdapter, bucketSz)
}

fun <T: Vec3f> pointOcTree(points: List<T> = emptyList(), bounds: BoundingBox = BoundingBox(), bucketSz: Int = 20): OcTree<T> {
    return OcTree(Vec3fAdapter, points, bounds, bucketSz = bucketSz)
}

fun <T: Triangle> triangleKdTree(triangles: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(triangles, TriangleAdapter, bucketSz)
}

fun <T: Triangle> triangleOcTree(triangles: List<T> = emptyList(), bounds: BoundingBox = BoundingBox(), bucketSz: Int = 20): OcTree<T> {
    return OcTree(TriangleAdapter, triangles, bounds, bucketSz = bucketSz)
}

fun <T: Edge> edgeKdTree(edges: List<T>, bucketSz: Int = 20): KdTree<T> {
    return KdTree(edges, EdgeAdapter, bucketSz)
}

fun <T: Edge> edgeOcTree(triangles: List<T> = emptyList(), bounds: BoundingBox = BoundingBox(), bucketSz: Int = 20): OcTree<T> {
    return OcTree(EdgeAdapter, triangles, bounds, bucketSz = bucketSz)
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

    override fun getSzX(item: Vec3f): Float = 0f
    override fun getSzY(item: Vec3f): Float = 0f
    override fun getSzZ(item: Vec3f): Float = 0f

    override fun getMin(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getCenter(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
    override fun getMax(item: Vec3f, result: MutableVec3f): MutableVec3f = result.set(item)
}

object EdgeAdapter : ItemAdapter<Edge> {
    override fun getMinX(item: Edge): Float = item.minX
    override fun getMinY(item: Edge): Float = item.minY
    override fun getMinZ(item: Edge): Float = item.minZ

    override fun getMaxX(item: Edge): Float = item.maxX
    override fun getMaxY(item: Edge): Float = item.maxY
    override fun getMaxZ(item: Edge): Float = item.maxZ

    override fun getMin(item: Edge, result: MutableVec3f): MutableVec3f =
            result.set(item.minX, item.minY, item.minZ)
    override fun getMax(item: Edge, result: MutableVec3f): MutableVec3f =
            result.set(item.maxX, item.maxY, item.maxZ)
}

object TriangleAdapter : ItemAdapter<Triangle> {
    override fun getMinX(item: Triangle): Float = item.minX
    override fun getMinY(item: Triangle): Float = item.minY
    override fun getMinZ(item: Triangle): Float = item.minZ

    override fun getMaxX(item: Triangle): Float = item.maxX
    override fun getMaxY(item: Triangle): Float = item.maxY
    override fun getMaxZ(item: Triangle): Float = item.maxZ

    override fun getMin(item: Triangle, result: MutableVec3f): MutableVec3f =
            result.set(item.minX, item.minY, item.minZ)
    override fun getMax(item: Triangle, result: MutableVec3f): MutableVec3f =
            result.set(item.maxX, item.maxY, item.maxZ)
}

open class Edge(val pt0: Vec3f, val pt1: Vec3f) {
    val e: Vec3f
    val length: Float

    val minX: Float
    val minY: Float
    val minZ: Float
    val maxX: Float
    val maxY: Float
    val maxZ: Float

    private val tmpVec = MutableVec3f()
    private val tmpResult = MutableVec3f()

    constructor(data: MeshData, idx0: Int) : this(
            MutableVec3f().apply { data.vertexList.vertexIt.index = data.vertexList.indices[idx0]; set(data.vertexList.vertexIt.position) },
            MutableVec3f().apply { data.vertexList.vertexIt.index = data.vertexList.indices[idx0+1]; set(data.vertexList.vertexIt.position) }
    ) {
        if (data.primitiveType != GL_LINES) {
            throw IllegalArgumentException("Supplied meshData must have primitiveType GL_LINES")
        }
    }

    init {
        e = pt1.subtract(pt0, MutableVec3f()).norm()
        length = pt0.distance(pt1)

        minX = minOf(pt0.x, pt1.x)
        minY = minOf(pt0.y, pt1.y)
        minZ = minOf(pt0.z, pt1.z)
        maxX = maxOf(pt0.x, pt1.x)
        maxY = maxOf(pt0.y, pt1.y)
        maxZ = maxOf(pt0.z, pt1.z)
    }

    open fun rayDistanceSqr(ray: Ray): Float {
        return ray.sqrDistanceToPoint(nearestPointOnEdge(ray, tmpResult))
    }

    open fun nearestPointOnEdge(ray: Ray, result: MutableVec3f): MutableVec3f {
        val dot = e * ray.direction
        val n = 1f - dot * dot
        if (n.isFuzzyZero()) {
            // edge and ray are parallel
            return result.set(if (pt0.sqrDistance(ray.origin) < pt1.sqrDistance(ray.origin)) pt0 else pt1)
        }

        ray.origin.subtract(pt0, tmpVec)
        val a = tmpVec * e
        val b = tmpVec * ray.direction
        val l = (a - b * dot) / n
        return if (l > 0) e.scale(min(l, length), result).add(pt0) else result.set(pt0)
    }

    companion object {
        fun getEdges(meshData: MeshData): List<Edge> {
            val edges = mutableListOf<Edge>()
            for (i in 0 until meshData.numIndices step 2) {
                edges += Edge(meshData, i)
            }
            return edges
        }
    }
}

open class Triangle(val pt0: Vec3f, val pt1: Vec3f, val pt2: Vec3f) {
    val e1: Vec3f
    val e2: Vec3f

    val minX: Float
    val minY: Float
    val minZ: Float
    val maxX: Float
    val maxY: Float
    val maxZ: Float

    private val tmpS = MutableVec3f()
    private val tmpP = MutableVec3f()
    private val tmpQ = MutableVec3f()

    constructor(data: MeshData, idx0: Int) : this(
            MutableVec3f().apply { data.vertexList.vertexIt.index = data.vertexList.indices[idx0]; set(data.vertexList.vertexIt.position) },
            MutableVec3f().apply { data.vertexList.vertexIt.index = data.vertexList.indices[idx0+1]; set(data.vertexList.vertexIt.position) },
            MutableVec3f().apply { data.vertexList.vertexIt.index = data.vertexList.indices[idx0+2]; set(data.vertexList.vertexIt.position) }
    ) {
        if (data.primitiveType != GL_TRIANGLES) {
            throw IllegalArgumentException("Supplied meshData must have primitiveType GL_TRIANGLES")
        }
    }

    init {
        e1 = pt1.subtract(pt0, MutableVec3f())
        e2 = pt2.subtract(pt0, MutableVec3f())

        minX = minOf(pt0.x, pt1.x, pt2.x)
        minY = minOf(pt0.y, pt1.y, pt2.y)
        minZ = minOf(pt0.z, pt1.z, pt2.z)
        maxX = maxOf(pt0.x, pt1.x, pt2.x)
        maxY = maxOf(pt0.y, pt1.y, pt2.y)
        maxZ = maxOf(pt0.z, pt1.z, pt2.z)
    }

    open fun hitDistance(ray: Ray): Float {
        ray.origin.subtract(pt0, tmpS)
        ray.direction.cross(e2, tmpP)
        tmpS.cross(e1, tmpQ)

        val f = 1f / (tmpP * e1)
        val t = f * (tmpQ * e2)
        val u = f * (tmpP * tmpS)
        val v = f * (tmpQ * ray.direction)

        return if (u >= 0f && v >= 0f && u + v <= 1f && t >= 0f) t else Float.MAX_VALUE
    }

    companion object {
        fun getTriangles(meshData: MeshData): List<Triangle> {
            val triangles = mutableListOf<Triangle>()
            for (i in 0 until meshData.numIndices step 3) {
                triangles += Triangle(meshData, i)
            }
            return triangles
        }
    }
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