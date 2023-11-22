package de.fabmax.kool.modules.mesh

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.OcTree
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.LineMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.TriangulatedLineMesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.*

/**
 * An editable mesh.
 *
 * @author fabmax
 */
class HalfEdgeMesh(geometry: IndexedVertexList, val edgeHandler: EdgeHandler = ListEdgeHandler()): Mesh(geometry) {

    private val verts: MutableList<HalfEdgeVertex>
    val vertices: List<HalfEdgeVertex>
        get() = verts

    private val positionOffset = geometry.attributeByteOffsets[Attribute.POSITIONS]!!

    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()
    private val tmpVec3 = MutableVec3f()

    private val vertexIt1 = geometry[0]
    private val vertexIt2 = geometry[0]

    val vertCount: Int
        get() = verts.size
    val faceCount: Int
        get() = edgeHandler.numEdges / 3

    interface EdgeHandler : Iterable<HalfEdge> {
        val numEdges: Int

        operator fun plusAssign(edge: HalfEdge)
        operator fun minusAssign(edge: HalfEdge)

        fun checkedUpdateEdgeTo(edge: HalfEdge, newTo: HalfEdgeVertex)
        fun checkedUpdateEdgeFrom(edge: HalfEdge, newFrom: HalfEdgeVertex)
        fun checkedUpdateVertexPosition(vertex: HalfEdgeVertex, x: Float, y: Float, z: Float)

        fun distinctTriangleEdges(): List<HalfEdge> = filter { it.id < it.next.id && it.id < it.next.next.id }

        fun rebuild()
    }

    init {
//        if (meshData.primitiveType != GL_TRIANGLES) {
//            throw KoolException("Supplied meshData must be of primitive type GL_TRIANGLES")
//        }

        verts = MutableList(geometry.numVertices) { HalfEdgeVertex(it) }

        for (i in 0 until geometry.numIndices step 3) {
            // create triangle vertices
            val v0 = verts[geometry.indices[i]]
            val v1 = verts[geometry.indices[i+1]]
            val v2 = verts[geometry.indices[i+2]]

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

    fun generateWireframe(lineMesh: LineMesh, lineColor: Color = MdColor.PINK) {
        val v0 = MutableVec3f()
        val v1 = MutableVec3f()
        edgeHandler.filter { it.opp == null || it.from.index < it.to.index }.forEach { edge ->
            v0.set(edge.from)
            v1.set(edge.to)
            lineMesh.addLine(v0, lineColor, v1, lineColor)
        }
    }

    fun generateWireframe(lineMesh: TriangulatedLineMesh, lineColor: Color = MdColor.PINK, lineWidth: Float = 2f) {
        val v0 = MutableVec3f()
        val v1 = MutableVec3f()
        edgeHandler.filter { it.opp == null || it.from.index < it.to.index }.forEach { edge ->
            v0.set(edge.from)
            v1.set(edge.to)
            lineMesh.addLine(v0, v1, lineColor, lineWidth)
        }
    }

    fun sanitize() {
        // assign new vertex indices
        val vIt = verts.iterator()
        var vi = 0
        for (v in vIt) {
            if (v.isDeleted) {
                vIt.remove()
            } else {
                v.index = vi++
            }
        }
        val vertCnt = verts.size

        edgeHandler.rebuild()
        if (edgeHandler.numEdges % 3 != 0) {
            logW { "Uneven edge count: ${edgeHandler.numEdges % 3}" }
        }

        // check for invalid edges
        // fixme: check why deleted edges sometimes remain linked to their from vertex
        //  test with cow model: 7 such edges on 1% simplification
        val removeEdges = mutableListOf<HalfEdge>()
        for (v in verts) {
            v.edges.removeAll {
                if (it.isDeleted) {
                    logW { "Deleted edge in v, edge.from == v: ${it.from === v}" }
                }
                it.isDeleted
            }
            for (he in v.edges) {
                val i0 = he.from.index
                val i1 = he.next.from.index
                val i2 = he.next.next.from.index
                if (i0 >= vertCnt || i1 >= vertCnt || i2 >= vertCnt) {
                    logW { "Inconsistent triangle indices: i0=$i0, i1=$i1, i2=$i2, mesh has only $vertCnt vertices" }
                    removeEdges += he
                }
            }
        }
        edgeHandler.forEach { he ->
            if (he.from.isDeleted || he.from.index >= vertCnt || he.to.isDeleted || he.to.index >= vertCnt) {
                logW { "Inconsistent edge: ${he.from.index} (del=${he.from.isDeleted}) -> ${he.to.index} (del=${he.to.isDeleted}), mesh has only $vertCnt vertices" }
                removeEdges += he
            }
            if (he === he.next || he === he.next.next) {
                logW { "Invalid edge linkage: he == he.next || he == he.next.next" }
                removeEdges += he
            }
        }
        removeEdges.forEach { it.deleteTriangle() }
    }

    /**
     * Removes all vertices / triangles marked as deleted
     */
    fun rebuild(generateNormals: Boolean = true, generateTangents: Boolean = true) {
        sanitize()

        geometry.batchUpdate(true) {
            // apply new indices to mesh vertex list
            val strideF = geometry.vertexSizeF
            val strideI = geometry.vertexSizeI
            val vertCnt = verts.size
            val newDataF = Float32Buffer(vertCnt * strideF)
            val newDataI = if (strideI > 0) Int32Buffer(vertCnt * strideI) else null

            for (i in verts.indices) {
                // copy data from previous location
                val oldIdx = verts[i].meshDataIndex
                verts[i].meshDataIndex = verts[i].index

                for (j in 0 until strideF) {
                    newDataF.put(geometry.dataF[oldIdx * strideF + j])
                }
                newDataI?.let {
                    for (j in 0 until strideI) {
                        it.put(geometry.dataI[oldIdx * strideI + j])
                    }
                }
            }

            // rebuild triangle index list
            geometry.clearIndices()
            for (e in edgeHandler.distinctTriangleEdges()) {
                geometry.addIndex(e.from.index)
                geometry.addIndex(e.next.from.index)
                geometry.addIndex(e.next.next.from.index)
            }

            if (geometry.numIndices != faceCount * 3) {
                logW { "Inconsistent triangle count! MeshData: ${geometry.numIndices / 3}, HalfEdgeMesh: $faceCount" }
            }

            geometry.dataF.clear()
            geometry.dataF.put(newDataF)
            newDataI?.let {
                geometry.dataI.clear()
                geometry.dataI.put(it)
            }

            geometry.numVertices = vertCnt
            if (generateNormals) {
                geometry.generateNormals()
            }
            if (generateTangents) {
                geometry.generateTangents()
            }
        }
    }

    fun selectBorders(): MutableList<MutableList<HalfEdge>> {
        val borders = mutableListOf<MutableList<HalfEdge>>()
        val collected = mutableSetOf<HalfEdge>()

        var border = mutableListOf<HalfEdge>()
        for (edge in edgeHandler) {
            if (edge.opp == null) {
                var borderEdge = edge
                while (borderEdge !in collected) {
                    border.add(borderEdge)
                    collected.add(borderEdge)
                    borderEdge = borderEdge.to.edges.find { it.opp == null && it !in collected } ?: break
                }
                if (border.isNotEmpty()) {
                    borders.add(border)
                    border = mutableListOf()
                }
            }
        }
        return borders
    }

    fun subSelect(start: HalfEdge, maxTris: Int = 0): Pair<List<HalfEdge>, List<HalfEdge>> {
        val selection = mutableListOf<HalfEdge>()

        val borderEdges = mutableSetOf<Long>()
        val innerEdges = mutableSetOf<Long>()
        val borderQueue = mutableListOf(start)

        while (borderQueue.isNotEmpty() && (maxTris == 0 || selection.size / 3 < maxTris)) {
            val he = borderQueue.removeAt(0)
            if (he.id in innerEdges) {
                continue
            }

            selection += he.also { innerEdges += it.id }
            selection += he.next.also { innerEdges += it.id }
            selection += he.next.next.also { innerEdges += it.id }

            if (he.opp != null && he.opp!!.id !in innerEdges && he.opp!!.id !in borderEdges) {
                borderQueue += he.opp!!
            }
            if (he.next.opp != null && he.next.opp!!.id !in innerEdges && he.next.opp!!.id !in borderEdges) {
                borderQueue += he.next.opp!!
            }
            if (he.next.next.opp != null && he.next.next.opp!!.id !in innerEdges && he.next.next.opp!!.id !in borderEdges) {
                borderQueue += he.next.next.opp!!
            }
        }
        return Pair(borderQueue, selection)
    }

    fun splitEdge(edge: HalfEdge, fraction: Float): HalfEdgeVertex {
        // spawn new vertex
        val idx = geometry.addVertex {
            position.set(edge.to).subtract(edge.from).mul(fraction).add(edge.from)

            // interpolate texture coordinates and normals
            edge.from.getMeshVertex(vertexIt1)
            edge.to.getMeshVertex(vertexIt2)
            texCoord.set(vertexIt2.texCoord).subtract(vertexIt1.texCoord).mul(fraction).add(vertexIt1.texCoord)
            normal.set(vertexIt2.normal).subtract(vertexIt1.normal).mul(fraction).add(vertexIt1.normal)
        }
        val insertV = HalfEdgeVertex(idx)
        verts += insertV

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
        return insertV
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

        if (fraction != 0f) {
            val newX = srcVert.x + (delVert.x - srcVert.x) * fraction
            val newY = srcVert.y + (delVert.y - srcVert.y) * fraction
            val newZ = srcVert.z + (delVert.z - srcVert.z) * fraction
            srcVert.updatePosition(newX, newY, newZ)
        }
    }

    fun subMeshOf(edges: List<HalfEdge>): IndexedVertexList {
        val subData = IndexedVertexList(geometry.vertexAttributes)
        val indexMap = mutableMapOf<Int, Int>()

        val v = geometry.vertexIt
        edges.forEach { he ->
            if (he.from.index !in indexMap.keys) {
                indexMap[he.from.index] = subData.addVertex {
                    set(v.apply { index = he.from.index })
                }
            }
            if (he.to.index !in indexMap.keys) {
                indexMap[he.to.index] = subData.addVertex {
                    set(v.apply { index = he.to.index })
                }
            }
        }

        val addedHes = mutableSetOf<Long>()
        edges.forEach { he ->
            if (he.id !in addedHes) {
                subData.addTriIndices(indexMap[he.from.index]!!, indexMap[he.next.from.index]!!, indexMap[he.next.next.from.index]!!)
                addedHes += he.id
                addedHes += he.next.id
                addedHes += he.next.next.id
            }
        }
        return subData
    }

    inner class HalfEdgeVertex(var index: Int): Vec3f(0f) {
        /**
         * List of edges that start with this vertex.
         */
        val edges = mutableListOf<HalfEdge>()

        var isDeleted = false
            private set
        internal var meshDataIndex = index

        override val x: Float
            get() = geometry.dataF[index * geometry.vertexSizeF + positionOffset]
        override val y: Float
            get() = geometry.dataF[index * geometry.vertexSizeF + positionOffset + 1]
        override val z: Float
            get() = geometry.dataF[index * geometry.vertexSizeF + positionOffset + 2]

        internal fun setPosition(x: Float, y: Float, z: Float) {
            geometry.dataF[index * geometry.vertexSizeF + positionOffset] = x
            geometry.dataF[index * geometry.vertexSizeF + positionOffset + 1] = y
            geometry.dataF[index * geometry.vertexSizeF + positionOffset + 2] = z
        }

        fun getMeshVertex(result: VertexView): VertexView {
            result.index = this.index
            return result
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
            while (edges.isNotEmpty()) {
                edges.last().deleteTriangle()
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

        fun computeTriArea(): Float = triArea(from, to, next.to)

        fun computeTriAspectRatio() = triAspectRatio(from, to, next.to)

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

        fun collapse(fraction: Float) {
            collapseEdge(this, fraction)
        }

        fun split(fraction: Float): HalfEdgeVertex {
            return splitEdge(this, fraction)
        }

        private fun deleteEdge() {
            edgeHandler -= this
            isDeleted = true
            from.edges -= this
            opp?.apply { opp = null }
            treeNode = null
            opp = null
        }

        fun deleteTriangle() {
            deleteEdge()
            next.deleteEdge()
            next.next.deleteEdge()
        }

        fun updateFrom(newFrom: HalfEdgeVertex) = edgeHandler.checkedUpdateEdgeFrom(this, newFrom)

        fun updateTo(newTo: HalfEdgeVertex) = edgeHandler.checkedUpdateEdgeTo(this, newTo)

        override fun toString(): String {
            return "${from.index} -> ${to.index}"
        }
    }

}
