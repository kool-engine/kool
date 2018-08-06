package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
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
    fun computeCollapsePosition(q1: ErrorQuadric, q2: ErrorQuadric, resultPos: MutableVec3f): Float
}

fun defaultCollapseStrategy() = object : CollapseStrategy {
    val tmpQ = Mat4f()
    val tmpNrm0 = MutableVec3f()
    val tmpNrm1 = MutableVec3f()
    val tmpPos0 = MutableVec3f()
    val tmpPos1 = MutableVec3f()

    override fun computeCollapsePosition(q1: ErrorQuadric, q2: ErrorQuadric, resultPos: MutableVec3f): Float {
        // count duplicate 'to' vertices in edges of q1 and q2
        // -> for inner plane vertices there must be 2, otherwise 2d manifold is destroyed
        // -> for border vertices it can be less
        var duplCnt = 0
        var q1Border = false
        var q2Border = false
        for (i in q1.vertex.edges.indices) {
            val q1e = q1.vertex.edges[i]
            if (q1e.opp == null) {
                q1Border = true
            }

            for (j in q2.vertex.edges.indices) {
                val q2e = q2.vertex.edges[j]
                if (q2e.opp == null) {
                    q2Border = true
                }

                if (q1e.to === q2e.to) {
                    duplCnt++
                }
            }
        }

        if (duplCnt > 2 || duplCnt < if (q1Border || q2Border) 0 else 2) {
            return Float.MAX_VALUE
        }

        tmpQ.set(q1.errQuadric)
        tmpQ.add(q2.errQuadric)

        tmpQ[3, 0] = 0f
        tmpQ[3, 1] = 0f
        tmpQ[3, 2] = 0f
        tmpQ[3, 3] = 1f

        val err = if (tmpQ.invert()) {
            // optimal position is taken from inverted matrix (magic)
            resultPos.set(tmpQ[0, 3], tmpQ[1, 3], tmpQ[2, 3])
            q1.getError(resultPos) + q2.getError(resultPos)
        } else {
            // error quadric is singular (both vertices lie in a plane), simply join them in the middle
            q2.vertex.subtract(q1.vertex, resultPos).scale(0.5f).add(q1.vertex)
            // error in joining plane vertices is actually 0, but shorter edges should be preferred
            q1.vertex.distance(q2.vertex) / 1e6f
        }

        return if (isRejected(q1.vertex, q2.vertex, resultPos) || isRejected(q2.vertex, q1.vertex, resultPos)) {
            // collapse is rejected
            Float.MAX_VALUE
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
                if (triangleAr(newPos, edge.to, edge.next.to) > 10000) {
                    // degenerated triangle -> rejected
                    return true
                }
            }
        }
        return false
    }

    private fun triangleAr(va: Vec3f, vb: Vec3f, vc: Vec3f): Float {
        val a = va.distance(vb)
        val b = vb.distance(vc)
        val c = vc.distance(va)
        val s = (a + b + c) / 2f
        return a * b * c / (8f * (s - a) * (s - b) * (s - c))
    }

    private fun newNormal(edge: HalfEdgeMesh.HalfEdge, newPos: Vec3f, newNormal: MutableVec3f) {
        edge.to.subtract(newPos, tmpPos0)
        edge.next.to.subtract(newPos, tmpPos1)
        tmpPos0.cross(tmpPos1, newNormal)
        newNormal.norm()
    }

}