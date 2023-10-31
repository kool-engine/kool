package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.mesh.HalfEdgeMesh

/**
 * Implements the strategy for collapsing mesh edges.
 *
 * @author fabmax
 */
interface CollapseStrategy {
    /**
     * Computes the optimal merge position when collapsing the edge between [q1] and [q2].
     * Returns the resulting error.
     */
    fun computeCollapsePosition(q1: ErrorQuadric, q2: ErrorQuadric, resultPos: MutableVec3f): Double
}

fun defaultCollapseStrategy() = object : CollapseStrategy {
    val tmpQ = MutableMat4d()
    val tmpNrm0 = MutableVec3f()
    val tmpNrm1 = MutableVec3f()
    val tmpPos0 = MutableVec3f()
    val tmpPos1 = MutableVec3f()

    override fun computeCollapsePosition(q1: ErrorQuadric, q2: ErrorQuadric, resultPos: MutableVec3f): Double {
        // count number of triangles adjacent to edge between q1 and q2
        //  -> for inner edges in a 2d manifold (plane mesh) triCnt must be 2
        //  -> for border edges in a 2d manifold (plane mesh) triCnt can also be 1
        var triCnt = 0
        for (i in q1.vertex.edges.indices) {
            val he = q1.vertex.edges[i]
            if (he.to === q2.vertex || he.next.to === q2.vertex) {
                triCnt++
            }
        }
        if (triCnt > 2 || triCnt < if (q1.isBorder && q2.isBorder) 1 else 2) {
            return Double.MAX_VALUE
        }

        // check vertex stickyness
        when {
            q1.isStickyVertex && q2.isStickyVertex -> return Double.MAX_VALUE
            q1.isStickyVertex -> {
                resultPos.set(q1.vertex)
                return q1.getError(resultPos) + q2.getError(resultPos)
            }
            q2.isStickyVertex -> {
                resultPos.set(q2.vertex)
                return q1.getError(resultPos) + q2.getError(resultPos)
            }
        }

        // don't collapse edges on corner triangles
        q1.vertex.getEdgeTo(q2.vertex)?.let { ed ->
            if (ed.next.opp == null && ed.next.next.opp == null) {
                return Double.MAX_VALUE
            }
        }
        q2.vertex.getEdgeTo(q1.vertex)?.let { ed ->
            if (ed.next.opp == null && ed.next.next.opp == null) {
                return Double.MAX_VALUE
            }
        }

        tmpQ.set(q1.errQuadric)
        tmpQ.add(q2.errQuadric)
        tmpQ.setRow(3, Vec4d.W_AXIS)

        val err = if (tmpQ.invert(FUZZY_EQ_D)) {
            // optimal position is taken from inverted matrix (magic)
            resultPos.set(tmpQ[0, 3].toFloat(), tmpQ[1, 3].toFloat(), tmpQ[2, 3].toFloat())
            q1.getError(resultPos) + q2.getError(resultPos)

        } else {
            // error quadric is singular (both vertices lie in a plane), simply join them in the middle
            q2.vertex.subtract(q1.vertex, resultPos).mul(0.5f).add(q1.vertex)
            // error in joining plane vertices is actually 0, but shorter edges should be preferred
            q1.vertex.distance(q2.vertex) / 1e100
        }

        return if (isRejected(q1.vertex, q2.vertex, resultPos) || isRejected(q2.vertex, q1.vertex, resultPos)) {
            // collapse is rejected
            Double.MAX_VALUE
        } else {
            err
        }
    }

    private fun isRejected(vert: HalfEdgeMesh.HalfEdgeVertex, excludedTo: HalfEdgeMesh.HalfEdgeVertex, newPos: Vec3f): Boolean {
        for (edge in vert.edges) {
            if (edge.to != excludedTo) {
                edge.computeTriNormal(tmpNrm0)
                newNormal(edge, newPos, tmpNrm1)
                if (tmpNrm0.dot(tmpNrm1) < 0) {
                    // normal flip -> results in overlapping triangles, rejected
                    return true
                }
                if (triAspectRatio(newPos, edge.to, edge.next.to) > 10000) {
                    // degenerated triangle -> rejected
                    return true
                }
            }
        }
        return false
    }

    private fun newNormal(edge: HalfEdgeMesh.HalfEdge, newPos: Vec3f, newNormal: MutableVec3f) {
        edge.to.subtract(newPos, tmpPos0)
        edge.next.to.subtract(newPos, tmpPos1)
        tmpPos0.cross(tmpPos1, newNormal)
        newNormal.norm()
    }

}