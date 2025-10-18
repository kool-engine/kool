package de.fabmax.kool.modules.mesh

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.spatial.BoundingBoxD
import de.fabmax.kool.math.spatial.ItemAdapter
import de.fabmax.kool.math.spatial.OcTree
import de.fabmax.kool.math.spatial.SpatialTree
import de.fabmax.kool.math.toMutableVec3d
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.logW
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class OcTreeEdgeHandler<Layout: Struct>(treeBounds: BoundingBoxD) : HalfEdgeMesh.EdgeHandler<Layout> {
    val edgeTree = OcTree(halfEdgeAdapter<Layout>(), bounds = treeBounds)
    override val numEdges: Int
        get() = edgeTree.size

    constructor(geometry: IndexedVertexList<*>): this(BoundingBoxD().apply {
        batchUpdate {
            val v = geometry.vertexIt
            val posD = MutableVec3d()
            for (i in 0 until geometry.numVertices) {
                v.index = i
                add(v.position.toMutableVec3d(posD))
            }
        }
    })

    override fun plusAssign(edge: HalfEdgeMesh<Layout>.HalfEdge) = edgeTree.plusAssign(edge)

    override fun minusAssign(edge: HalfEdgeMesh<Layout>.HalfEdge) {
        if (edge.isDeleted) {
            logW { "edge is already deleted, probably not in tree anymore..." }
        }
        edgeTree.minusAssign(edge)
    }

    override fun checkedUpdateEdgeTo(edge: HalfEdgeMesh<Layout>.HalfEdge, newTo: HalfEdgeMesh<Layout>.HalfEdgeVertex) {
        edge.apply {
            val newX = (from.x + newTo.x) * 0.5f
            val newY = (from.y + newTo.y) * 0.5f
            val newZ = (from.z + newTo.z) * 0.5f

            if (treeNode?.isCenterInNode(newX, newY, newZ) == true) {
                // edge stays in same tree node, no full update required
                to = newTo
            } else {
                // updated position is in different tree node, full update required
                edgeTree -= this
                to = newTo
                edgeTree += this
            }
        }
    }

    override fun checkedUpdateEdgeFrom(edge: HalfEdgeMesh<Layout>.HalfEdge, newFrom: HalfEdgeMesh<Layout>.HalfEdgeVertex) {
        edge.apply {
            val newX = (newFrom.x + to.x) * 0.5f
            val newY = (newFrom.y + to.y) * 0.5f
            val newZ = (newFrom.z + to.z) * 0.5f

            if (treeNode?.isCenterInNode(newX, newY, newZ) == true) {
                // edge stays in same tree node, no full update required
                from = newFrom
            } else {
                // updated position is in different tree node, full update required
                edgeTree -= this
                from = newFrom
                edgeTree += this
            }
        }
    }

    override fun checkedUpdateVertexPosition(vertex: HalfEdgeMesh<Layout>.HalfEdgeVertex, x: Float, y: Float, z: Float) {
        vertex.apply {
            for (i in edges.indices) {
                // check from this vertex
                var ed = edges[i]
                var newX = (x + ed.to.x) * 0.5f
                var newY = (y + ed.to.y) * 0.5f
                var newZ = (z + ed.to.z) * 0.5f
                if (ed.treeNode?.isCenterInNode(newX, newY, newZ) == false) {
                    // full tree update required
                    edgeTree -= ed
                    ed.treeNode = null
                }

                // check edge to this vertex
                ed = edges[i].next.next
                newX = (x + ed.from.x) * 0.5f
                newY = (y + ed.from.y) * 0.5f
                newZ = (z + ed.from.z) * 0.5f
                if (ed.treeNode?.isCenterInNode(newX, newY, newZ) == false) {
                    // full tree update required
                    edgeTree -= ed
                    ed.treeNode = null
                }
            }

            setPosition(x, y, z)

            for (i in edges.indices) {
                val ed = edges[i]
                if (ed.treeNode == null) {
                    this@OcTreeEdgeHandler += ed
                }
                if (ed.next.next.treeNode == null) {
                    this@OcTreeEdgeHandler += ed.next.next
                }
            }
        }
    }

    override fun rebuild() { }

    override fun iterator(): Iterator<HalfEdgeMesh<Layout>.HalfEdge> = edgeTree.iterator()

    companion object {
        fun <Layout: Struct> halfEdgeAdapter() = object : ItemAdapter<HalfEdgeMesh<Layout>.HalfEdge> {
            override fun getMinX(item: HalfEdgeMesh<Layout>.HalfEdge): Double = min(item.from.x, item.to.x).toDouble()
            override fun getMinY(item: HalfEdgeMesh<Layout>.HalfEdge): Double = min(item.from.y, item.to.y).toDouble()
            override fun getMinZ(item: HalfEdgeMesh<Layout>.HalfEdge): Double = min(item.from.z, item.to.z).toDouble()

            override fun getMaxX(item: HalfEdgeMesh<Layout>.HalfEdge): Double = max(item.from.x, item.to.x).toDouble()
            override fun getMaxY(item: HalfEdgeMesh<Layout>.HalfEdge): Double = max(item.from.y, item.to.y).toDouble()
            override fun getMaxZ(item: HalfEdgeMesh<Layout>.HalfEdge): Double = max(item.from.z, item.to.z).toDouble()

            override fun getSzX(item: HalfEdgeMesh<Layout>.HalfEdge): Double = abs(item.from.x - item.to.x).toDouble()
            override fun getSzY(item: HalfEdgeMesh<Layout>.HalfEdge): Double = abs(item.from.y - item.to.y).toDouble()
            override fun getSzZ(item: HalfEdgeMesh<Layout>.HalfEdge): Double = abs(item.from.z - item.to.z).toDouble()

            override fun getCenterX(item: HalfEdgeMesh<Layout>.HalfEdge): Double = (item.from.x + item.to.x) * 0.5
            override fun getCenterY(item: HalfEdgeMesh<Layout>.HalfEdge): Double = (item.from.y + item.to.y) * 0.5
            override fun getCenterZ(item: HalfEdgeMesh<Layout>.HalfEdge): Double = (item.from.z + item.to.z) * 0.5

            override fun setNode(item: HalfEdgeMesh<Layout>.HalfEdge, node: SpatialTree<HalfEdgeMesh<Layout>.HalfEdge>.Node) {
                item.treeNode = node as OcTree<HalfEdgeMesh<Layout>.HalfEdge>.OcNode
            }
        }
    }
}