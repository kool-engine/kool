package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.mesh.HalfEdgeMesh

class ErrorQuadric(val vertex: HalfEdgeMesh.HalfEdgeVertex) {

    val errQuadric = Mat4f().setZero()

    private val tmpVec4 = MutableVec4f()

    init {
        for (i in vertex.edges.indices) {
            addTriPlane(vertex.edges[i])
        }
    }

    fun consume(other: ErrorQuadric) {
        errQuadric.add(other.errQuadric)
    }

    fun getError(v: Vec3f): Float {
        errQuadric.transform(tmpVec4.set(v, 1f))
        return tmpVec4.x * v.x + tmpVec4.y * v.y + tmpVec4.z * v.z + tmpVec4.w
    }

    private fun addTriPlane(edge: HalfEdgeMesh.HalfEdge) {
        edge.computeTriPlane(tmpVec4)

        errQuadric[0, 0] += tmpVec4.x * tmpVec4.x
        errQuadric[1, 0] += tmpVec4.x * tmpVec4.y
        errQuadric[2, 0] += tmpVec4.x * tmpVec4.z
        errQuadric[3, 0] += tmpVec4.x * tmpVec4.w

        errQuadric[0, 1] += tmpVec4.y * tmpVec4.x
        errQuadric[1, 1] += tmpVec4.y * tmpVec4.y
        errQuadric[2, 1] += tmpVec4.y * tmpVec4.z
        errQuadric[3, 1] += tmpVec4.y * tmpVec4.w

        errQuadric[0, 2] += tmpVec4.z * tmpVec4.x
        errQuadric[1, 2] += tmpVec4.z * tmpVec4.y
        errQuadric[2, 2] += tmpVec4.z * tmpVec4.z
        errQuadric[3, 2] += tmpVec4.z * tmpVec4.w

        errQuadric[0, 3] += tmpVec4.w * tmpVec4.x
        errQuadric[1, 3] += tmpVec4.w * tmpVec4.y
        errQuadric[2, 3] += tmpVec4.w * tmpVec4.z
        errQuadric[3, 3] += tmpVec4.w * tmpVec4.w
    }
}