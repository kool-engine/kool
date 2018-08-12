package de.fabmax.kool.modules.mesh

import de.fabmax.kool.KoolException
import de.fabmax.kool.gl.GL_TRIANGLES
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.isFuzzyZero
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * An editable mesh.
 *
 * @author fabmax
 */
class HalfEdgeMesh(meshData: MeshData, val edgeHandler: EdgeHandler = OcTreeEdgeHandler(meshData)): Mesh(meshData) {

    private val vertices: MutableList<HalfEdgeVertex>

    private val positionOffset = meshData.vertexList.attributeOffsets[Attribute.POSITIONS]!!

    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()
    private val tmpVec3 = MutableVec3f()

    val vertCount: Int
        get() = vertices.size
    val faceCount: Int
        get() = edgeHandler.numEdges / 3

    interface EdgeHandler : Iterable<HalfEdge> {
        val numEdges: Int

        operator fun plusAssign(edge: HalfEdge)
        operator fun minusAssign(edge: HalfEdge)

        fun checkedUpdateEdgeTo(edge: HalfEdge, newTo: HalfEdgeVertex)
        fun checkedUpdateEdgeFrom(edge: HalfEdge, newFrom: HalfEdgeVertex)
        fun checkedUpdateVertexPosition(vertex: HalfEdgeVertex, x: Float, y: Float, z: Float)

        fun rebuild()
    }

    init {
        if (meshData.primitiveType != GL_TRIANGLES) {
            throw KoolException("Supplied meshData must be of primitive type GL_TRIANGLES")
        }

        vertices = MutableList(meshData.numVertices) { HalfEdgeVertex(it) }

        for (i in 0 until meshData.numIndices step 3) {
            // create triangle vertices
            val v0 = vertices[meshData.vertexList.indices[i]]
            val v1 = vertices[meshData.vertexList.indices[i+1]]
            val v2 = vertices[meshData.vertexList.indices[i+2]]

            // create inner half-edges and connect them
            val e0 = HalfEdge(v0, v1)
            val e1 = HalfEdge(v1, v2).apply { e0.next = this }
            val e2 = HalfEdge(v2, v0).apply { e1.next = this; next = e0 }

            // link opposite edges if existing
            e0.opp = v1.getEdgeTo(v0)?.apply { opp = e0 }
            e1.opp = v2.getEdgeTo(v1)?.apply { opp = e1 }
            e2.opp = v0.getEdgeTo(v2)?.apply { opp = e2 }

            // insert newly created edges
            edgeHandler += e0
            edgeHandler += e1
            edgeHandler += e2
        }
    }

    fun generateWireframe(lineMesh: LineMesh, lineColor: Color = Color.MD_PINK) {
        val v0 = MutableVec3f()
        val v1 = MutableVec3f()
        edgeHandler.filter { it.opp == null || it.from.index < it.to.index }.forEach { edge ->
            v0.set(edge.from)
            v1.set(edge.to)
            lineMesh.addLine(v0, lineColor, v1, lineColor)
        }
    }

    /**
     * Removes all vertices / triangles marked as deleted
     */
    fun rebuild(generateNormals: Boolean = true, generateTangents: Boolean = true) {
        // assign new vertex indices
        val it = vertices.iterator()
        var vi = 0
        for (v in it) {
            if (v.isDeleted) {
                it.remove()
            } else {
                v.oldIndex = v.index
                v.index = vi++
            }
        }
        edgeHandler.rebuild()

        // apply new indices to mesh vertex list
        val strideF = meshData.vertexList.vertexSizeF
        val strideI = meshData.vertexList.vertexSizeI
        val newDataF = createFloat32Buffer(vertices.size * strideF)
        val newDataI = if (strideI > 0) createUint32Buffer(vertices.size * strideI) else meshData.vertexList.dataI

        for (i in vertices.indices) {
            // copy data from previous location
            val oldIdx = vertices[i].oldIndex
            for (j in 0 until strideF) {
                newDataF.put(meshData.vertexList.dataF[oldIdx * strideF + j])
            }
            if (strideI > 0) {
                for (j in 0 until strideI) {
                    newDataI.put(meshData.vertexList.dataI[oldIdx * strideI + j])
                }
            }
        }

        // rebuild triangle index list
        meshData.vertexList.clearIndices()
        for (i in vertices.indices) {
            val v = vertices[i]
            for (j in v.edges.indices) {
                val e = v.edges[j]
                val ei = e.from.index
                if (ei < e.next.from.index && ei < e.next.next.from.index) {
                    meshData.vertexList.addIndex(e.from.index)
                    meshData.vertexList.addIndex(e.next.from.index)
                    meshData.vertexList.addIndex(e.next.next.from.index)
                }
            }
        }

        if (meshData.numIndices / 3 != faceCount) {
            logE { "Mesh tris != OcTree tris! in mesh: ${meshData.numIndices / 3}, in tree: $faceCount" }
        }

        meshData.vertexList.dataF = newDataF
        meshData.vertexList.dataI = newDataI
        meshData.vertexList.size = vertices.size
        if (generateNormals) {
            meshData.generateNormals()
        }
        if (generateTangents) {
            meshData.generateTangents()
        }
        meshData.isSyncRequired = true
    }

    fun splitEdge(edge: HalfEdge, fraction: Float) {
        // spawn new vertex
        val idx = meshData.vertexList.addVertex {
            position.set(edge.to).subtract(edge.from).scale(fraction).add(edge.from)
        }
        val insertV = HalfEdgeVertex(idx)
        vertices += insertV

        // insert new half edges for right triangle and adjust linkage
        val prevToR = edge.to
        edge.updateTo(insertV)
        edge.next.from.edges -= edge.next
        edge.next.updateFrom(insertV)
        insertV.edges += edge.next

        val insertEdR0 = HalfEdge(insertV, prevToR)
        val insertEdR1 = HalfEdge(prevToR, edge.next.to).apply {
            insertEdR0.next = this
            opp = edge.next.opp
            opp?.opp = this
        }
        val insertEdR2 = HalfEdge(edge.next.to, insertV).apply {
            insertEdR1.next = this
            next = insertEdR0
            opp = edge.next
            edge.next.opp = this
        }

        edgeHandler += insertEdR0
        edgeHandler += insertEdR1
        edgeHandler += insertEdR2

        // insert new half edges for left (opposing) triangle and adjust linkage
        val edgeOpp = edge.opp
        if (edgeOpp != null) {
            val prevToL = edgeOpp.to
            edgeOpp.updateTo(insertV)
            edgeOpp.next.from.edges -= edgeOpp.next
            edgeOpp.next.updateFrom(insertV)
            insertV.edges += edgeOpp.next

            val insertEdL0 = HalfEdge(insertV, prevToL)
            val insertEdL1 = HalfEdge(prevToL, edgeOpp.next.to).apply {
                insertEdL0.next = this
                opp = edgeOpp.next.opp
                opp?.opp = this
            }
            val insertEdL2 = HalfEdge(edgeOpp.next.to, insertV).apply {
                insertEdL1.next = this
                next = insertEdL0
                opp = edgeOpp.next
                edgeOpp.next.opp = this
            }

            insertEdL0.opp = edge
            edge.opp = insertEdL0

            insertEdR0.opp = edgeOpp
            edgeOpp.opp = insertEdR0

            edgeHandler += insertEdL0
            edgeHandler += insertEdL1
            edgeHandler += insertEdL2
        }
    }

    fun collapseEdge(edge: HalfEdge, fraction: Float) {
        val srcVert = edge.from
        val delVert = edge.to

        val oppR1 = edge.next.opp
        if (oppR1 != null) {
            // colOppR1 points to delVert
            edge.next.opp = null
            oppR1.opp = null
            oppR1.updateTo(srcVert)
        }
        val oppR2 = edge.next.next.opp
        if (oppR2 != null) {
            // colOppR2 points from srcVert to colOppR1.from
            edge.next.next.opp = null
            oppR2.opp = oppR1
            if (oppR1 != null) {
                oppR1.opp = oppR2
            }
        }

        val edgeOpp = edge.opp
        if (edgeOpp != null) {
            val oppL1 = edgeOpp.next.opp
            if (oppL1 != null) {
                // colOppL1 points to srcVert
                edgeOpp.next.opp = null
                oppL1.opp = null
            }
            val oppL2 = edgeOpp.next.next.opp
            if (oppL2 != null) {
                // colOppL2 points from delVert to colOppL1.from
                delVert.edges.remove(oppL2)
                srcVert.edges.add(oppL2)
                oppL2.updateFrom(srcVert)
                oppL2.next.next.updateTo(srcVert)
                edgeOpp.next.next.opp = null
                oppL2.opp = oppL1
                if (oppL1 != null) {
                    oppL1.opp = oppL2
                }
            }
        }

        // delete triangle defined by edge
        edge.deleteTriangle()
        // delete opposite triangle of edge (if it exists)
        edgeOpp?.deleteTriangle()

        for (i in delVert.edges.indices) {
            val e = delVert.edges[i]
            e.updateFrom(srcVert)
            e.next.next.updateTo(srcVert)
            val eOpp = e.opp
            if (eOpp != null) {
                eOpp.updateTo(srcVert)
                eOpp.next.updateFrom(srcVert)
            }
            srcVert.edges += e
        }
        delVert.edges.clear()
        delVert.delete()

        if (!fraction.isFuzzyZero()) {
            val newX = srcVert.x + (delVert.x - srcVert.x) * fraction
            val newY = srcVert.y + (delVert.y - srcVert.y) * fraction
            val newZ = srcVert.z + (delVert.z - srcVert.z) * fraction
            srcVert.updatePosition(newX, newY, newZ)
        }
    }

    companion object {
        object HalfEdgeAdapter : ItemAdapter<HalfEdge> {
            override fun getMinX(item: HalfEdge): Float = min(item.from.x, item.to.x)
            override fun getMinY(item: HalfEdge): Float = min(item.from.y, item.to.y)
            override fun getMinZ(item: HalfEdge): Float = min(item.from.z, item.to.z)

            override fun getMaxX(item: HalfEdge): Float = max(item.from.x, item.to.x)
            override fun getMaxY(item: HalfEdge): Float = max(item.from.y, item.to.y)
            override fun getMaxZ(item: HalfEdge): Float = max(item.from.z, item.to.z)

            override fun getSzX(item: HalfEdge): Float = abs(item.from.x - item.to.x)
            override fun getSzY(item: HalfEdge): Float = abs(item.from.y - item.to.y)
            override fun getSzZ(item: HalfEdge): Float = abs(item.from.z - item.to.z)

            override fun getCenterX(item: HalfEdge): Float = (item.from.x + item.to.x) * 0.5f
            override fun getCenterY(item: HalfEdge): Float = (item.from.y + item.to.y) * 0.5f
            override fun getCenterZ(item: HalfEdge): Float = (item.from.z + item.to.z) * 0.5f

            override fun setNode(item: HalfEdge, node: SpatialTree<HalfEdge>.Node) {
                item.treeNode = node as OcTree<HalfEdge>.OcNode
            }
        }
    }

    inner class HalfEdgeVertex(var index: Int): Vec3f(0f) {
        /**
         * List of edges that start with this vertex.
         */
        val edges = mutableListOf<HalfEdge>()

        var isDeleted = false
            private set
        internal var oldIndex = -1

        override val x: Float
            get() = meshData.vertexList.dataF[index * meshData.vertexList.vertexSizeF + positionOffset]
        override val y: Float
            get() = meshData.vertexList.dataF[index * meshData.vertexList.vertexSizeF + positionOffset + 1]
        override val z: Float
            get() = meshData.vertexList.dataF[index * meshData.vertexList.vertexSizeF + positionOffset + 2]

        internal fun setPosition(x: Float, y: Float, z: Float) {
            meshData.vertexList.dataF[index * meshData.vertexList.vertexSizeF + positionOffset] = x
            meshData.vertexList.dataF[index * meshData.vertexList.vertexSizeF + positionOffset + 1] = y
            meshData.vertexList.dataF[index * meshData.vertexList.vertexSizeF + positionOffset + 2] = z
        }

        fun getEdgeTo(v: HalfEdgeVertex): HalfEdge? {
            for (i in edges.indices) {
                if (edges[i].to === v) {
                    return edges[i]
                }
            }
            return null
        }

        fun delete() {
            while (edges.size > 0) {
                edges[0].deleteTriangle()
            }
            // mark vertex as deleted, cleanup is done sometime later
            isDeleted = true
        }

        fun updatePosition(newPos: Vec3f) = updatePosition(newPos.x, newPos.y, newPos.z)

        fun updatePosition(x: Float, y: Float, z: Float) = edgeHandler.checkedUpdateVertexPosition(this, x, y, z)
    }

    inner class HalfEdge(from: HalfEdgeVertex, to: HalfEdgeVertex) {
        var from = from
            internal set
        var to = to
            internal set
        var isDeleted = false
            internal set
        var treeNode: OcTree<HalfEdge>.OcNode? = null
            internal set

        val id: Long
            get() = (from.index.toLong() shl 32) or to.index.toLong()
        val triId: Long
            get() = minOf(id, next.id, next.next.id)

        lateinit var next: HalfEdge
            internal set
        var opp: HalfEdge? = null
            internal set

        init {
            from.edges += this
        }

        fun computeLength(): Float = from.distance(to)

        fun computeTriArea(): Float {
		    val xAB = to.x - from.x
		    val yAB = to.y - from.y
		    val zAB = to.z - from.z
		    val xAC = next.to.x - from.x
		    val yAC = next.to.y - from.y
		    val zAC = next.to.z - from.z
		    val abSqr = xAB * xAB + yAB * yAB + zAB * zAB
		    val acSqr = xAC * xAC + yAC * yAC + zAC * zAC
		    val abcSqr = xAB * xAC + yAB * yAC + zAB * zAC
		    return 0.5f * sqrt(abSqr * acSqr - abcSqr * abcSqr)
        }

        fun computeTriAspectRatio(): Float {
            val a = from.distance(to)
            val b = to.distance(next.to)
            val c = next.to.distance(from)
            val s = (a + b + c) / 2f
            return a * b * c / (8 * (s-a) * (s-b) * (s-c))
        }

        fun computeTriNormal(result: MutableVec3f): MutableVec3f {
            to.subtract(from, tmpVec1)
            next.to.subtract(from, tmpVec2)
            tmpVec1.cross(tmpVec2, result)
            return result.norm()
        }

        fun computeTriPlane(result: MutableVec4f): MutableVec4f {
            computeTriNormal(tmpVec3)
            result.set(tmpVec3, -tmpVec3.dot(from))
            return result
        }

        private fun deleteEdge() {
            isDeleted = true
            from.edges -= this
            opp?.apply { opp = null }
            edgeHandler -= this
            treeNode = null
        }

        fun deleteTriangle() {
            deleteEdge()
            next.deleteEdge()
            next.next.deleteEdge()
        }

        fun updateFrom(newFrom: HalfEdgeVertex) = edgeHandler.checkedUpdateEdgeFrom(this, newFrom)

        fun updateTo(newTo: HalfEdgeVertex) = edgeHandler.checkedUpdateEdgeTo(this, newTo)

        override fun toString(): String {
            return "$from -> $to"
        }
    }

    class OcTreeEdgeHandler(treeBounds: BoundingBox) : EdgeHandler {
        val edgeTree = OcTree(HalfEdgeAdapter, bounds = treeBounds)
        override val numEdges: Int
            get() = edgeTree.size

        constructor(meshData: MeshData): this(BoundingBox().apply {
            batchUpdate {
                val v = meshData.vertexList.vertexIt
                for (i in 0 until meshData.numVertices) {
                    v.index = i
                    add(v.position)
                }
            }
        })

        override fun plusAssign(edge: HalfEdge) = edgeTree.plusAssign(edge)

        override fun minusAssign(edge: HalfEdge) = edgeTree.minusAssign(edge)

        override fun checkedUpdateEdgeTo(edge: HalfEdge, newTo: HalfEdgeVertex) {
            edge.apply {
                val newX = (from.x + newTo.x) * 0.5f
                val newY = (from.y + newTo.y) * 0.5f
                val newZ = (from.z + newTo.z) * 0.5f

                if (treeNode?.isInNode(newX, newY, newZ) == true) {
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

        override fun checkedUpdateEdgeFrom(edge: HalfEdge, newFrom: HalfEdgeVertex) {
            edge.apply {
                val newX = (newFrom.x + to.x) * 0.5f
                val newY = (newFrom.y + to.y) * 0.5f
                val newZ = (newFrom.z + to.z) * 0.5f

                if (treeNode?.isInNode(newX, newY, newZ) == true) {
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

        override fun checkedUpdateVertexPosition(vertex: HalfEdgeVertex, x: Float, y: Float, z: Float) {
            vertex.apply {
                for (i in edges.indices) {
                    // check from this vertex
                    var ed = edges[i]
                    var newX = (x + ed.to.x) * 0.5f
                    var newY = (y + ed.to.y) * 0.5f
                    var newZ = (z + ed.to.z) * 0.5f
                    if (ed.treeNode?.isInNode(newX, newY, newZ) != true) {
                        // full tree update required
                        this@OcTreeEdgeHandler -= ed
                        ed.treeNode = null
                    }

                    // check edge to this vertex
                    ed = edges[i].next.next
                    newX = (x + ed.from.x) * 0.5f
                    newY = (y + ed.from.y) * 0.5f
                    newZ = (z + ed.from.z) * 0.5f
                    if (ed.treeNode?.isInNode(newX, newY, newZ) != true) {
                        // full tree update required
                        this@OcTreeEdgeHandler -= ed
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

        override fun iterator(): Iterator<HalfEdge> = edgeTree.iterator()
    }

    class ListEdgeHandler : EdgeHandler {
        val edgeList = mutableListOf<HalfEdge>()

        override var numEdges = 0
            private set

        override fun plusAssign(edge: HalfEdge) {
            if (edge.isDeleted) {
                logW { "edge was deleted before" }
                edge.isDeleted = false
                rebuild()
            }
            edgeList += edge
            numEdges++
        }

        override fun minusAssign(edge: HalfEdge) {
            edge.isDeleted = true
            numEdges--
        }

        override fun checkedUpdateEdgeTo(edge: HalfEdge, newTo: HalfEdgeVertex) {
            edge.to = newTo
        }

        override fun checkedUpdateEdgeFrom(edge: HalfEdge, newFrom: HalfEdgeVertex) {
            edge.from = newFrom
        }

        override fun checkedUpdateVertexPosition(vertex: HalfEdgeVertex, x: Float, y: Float, z: Float) {
            vertex.setPosition(x, y, z)
        }

        override fun rebuild() {
            edgeList.removeAll { it.isDeleted }
        }

        override fun iterator(): Iterator<HalfEdge> = object : Iterator<HalfEdge> {
            var i = 0

            override fun hasNext(): Boolean {
                while (i < edgeList.size && edgeList[i].isDeleted) {
                    i++
                }
                return i < edgeList.size
            }

            override fun next(): HalfEdge {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                return edgeList[i++]
            }
        }

    }
}