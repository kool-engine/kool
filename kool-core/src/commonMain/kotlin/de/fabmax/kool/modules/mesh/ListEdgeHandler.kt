package de.fabmax.kool.modules.mesh

import de.fabmax.kool.util.logW

class ListEdgeHandler : HalfEdgeMesh.EdgeHandler {
    val edgeList = mutableListOf<HalfEdgeMesh.HalfEdge>()

    override var numEdges = 0
        private set

    override fun plusAssign(edge: HalfEdgeMesh.HalfEdge) {
        if (edge.isDeleted) {
            logW { "edge was deleted before" }
            edge.isDeleted = false
            rebuild()
        }
        edgeList += edge
        numEdges++
    }

    override fun minusAssign(edge: HalfEdgeMesh.HalfEdge) {
        if (!edge.isDeleted) {
            numEdges--
        } else {
            logW { "edge is already deleted" }
            rebuild()
        }
    }

    override fun checkedUpdateEdgeTo(edge: HalfEdgeMesh.HalfEdge, newTo: HalfEdgeMesh.HalfEdgeVertex) {
        edge.to = newTo
    }

    override fun checkedUpdateEdgeFrom(edge: HalfEdgeMesh.HalfEdge, newFrom: HalfEdgeMesh.HalfEdgeVertex) {
        edge.from = newFrom
    }

    override fun checkedUpdateVertexPosition(vertex: HalfEdgeMesh.HalfEdgeVertex, x: Float, y: Float, z: Float) {
        vertex.setPosition(x, y, z)
    }

    override fun rebuild() {
        edgeList.removeAll { it.isDeleted }
        if (numEdges != edgeList.size) {
            logW { "Wrong edge count: $numEdges != ${edgeList.size}" }
            numEdges = edgeList.size
        }
    }

    override fun iterator(): Iterator<HalfEdgeMesh.HalfEdge> = object : Iterator<HalfEdgeMesh.HalfEdge> {
        var i = 0

        override fun hasNext(): Boolean {
            while (i < edgeList.size && edgeList[i].isDeleted) {
                i++
            }
            return i < edgeList.size
        }

        override fun next(): HalfEdgeMesh.HalfEdge {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return edgeList[i++]
        }
    }

}