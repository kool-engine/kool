package de.fabmax.kool.modules.mesh

import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.logW

class ListEdgeHandler<Layout: Struct> : HalfEdgeMesh.EdgeHandler<Layout> {
    val edgeList = mutableListOf<HalfEdgeMesh<Layout>.HalfEdge>()

    override var numEdges = 0
        private set

    override fun plusAssign(edge: HalfEdgeMesh<Layout>.HalfEdge) {
        if (edge.isDeleted) {
            logW { "edge was deleted before" }
            edge.isDeleted = false
            rebuild()
        }
        edgeList += edge
        numEdges++
    }

    override fun minusAssign(edge: HalfEdgeMesh<Layout>.HalfEdge) {
        if (!edge.isDeleted) {
            numEdges--
        } else {
            logW { "edge is already deleted" }
            rebuild()
        }
    }

    override fun checkedUpdateEdgeTo(edge: HalfEdgeMesh<Layout>.HalfEdge, newTo: HalfEdgeMesh<Layout>.HalfEdgeVertex) {
        edge.to = newTo
    }

    override fun checkedUpdateEdgeFrom(edge: HalfEdgeMesh<Layout>.HalfEdge, newFrom: HalfEdgeMesh<Layout>.HalfEdgeVertex) {
        edge.from = newFrom
    }

    override fun checkedUpdateVertexPosition(vertex: HalfEdgeMesh<Layout>.HalfEdgeVertex, x: Float, y: Float, z: Float) {
        vertex.setPosition(x, y, z)
    }

    override fun rebuild() {
        edgeList.removeAll { it.isDeleted }
        if (numEdges != edgeList.size) {
            logW { "Wrong edge count: $numEdges != ${edgeList.size}" }
            numEdges = edgeList.size
        }
    }

    override fun iterator(): Iterator<HalfEdgeMesh<Layout>.HalfEdge> = object : Iterator<HalfEdgeMesh<Layout>.HalfEdge> {
        var i = 0

        override fun hasNext(): Boolean {
            while (i < edgeList.size && edgeList[i].isDeleted) {
                i++
            }
            return i < edgeList.size
        }

        override fun next(): HalfEdgeMesh<Layout>.HalfEdge {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return edgeList[i++]
        }
    }

}