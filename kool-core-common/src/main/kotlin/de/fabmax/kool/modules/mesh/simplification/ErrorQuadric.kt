package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.mesh.HalfEdgeMesh

class ErrorQuadric(val vertex: HalfEdgeMesh.HalfEdgeVertex) {

    val errQuadric = Mat4f().setZero()

    private val tmpVec4 = MutableVec4f()

    init {
        for (i in vertex.edges.indices) {
            val ed = vertex.edges[i]
            addPlane(ed.computeTriPlane(tmpVec4))

            if (ed.opp == null) {
                // border edge, add a virtual orthogonal plane
                val triNrm = tmpVec4.getXyz(MutableVec3f())
                val edDir = MutableVec3f(vertex).subtract(ed.to).norm()
                val nrm = triNrm.cross(edDir, MutableVec3f())
                tmpVec4.set(nrm, -nrm.dot(vertex))
                addPlane(tmpVec4)
            }
            if (ed.next.next.opp == null) {
                // border edge to vertex, add a virtual orthogonal plane
                if (ed.opp == null) {
                    ed.computeTriPlane(tmpVec4)
                }
                val triNrm = tmpVec4.getXyz(MutableVec3f())
                val edDir = MutableVec3f(ed.next.next.from).subtract(vertex).norm()
                val nrm = triNrm.cross(edDir, MutableVec3f())
                tmpVec4.set(nrm, -nrm.dot(vertex))
                addPlane(tmpVec4)
            }
        }
    }

    fun consume(other: ErrorQuadric) {
        errQuadric.add(other.errQuadric)
    }

    fun getError(v: Vec3f): Float {
        errQuadric.transform(tmpVec4.set(v, 1f))
        return tmpVec4.x * v.x + tmpVec4.y * v.y + tmpVec4.z * v.z + tmpVec4.w
    }

    private fun addPlane(planeVec: Vec4f) {
        errQuadric[0, 0] += planeVec.x * planeVec.x
        errQuadric[1, 0] += planeVec.x * planeVec.y
        errQuadric[2, 0] += planeVec.x * planeVec.z
        errQuadric[3, 0] += planeVec.x * planeVec.w

        errQuadric[0, 1] += planeVec.y * planeVec.x
        errQuadric[1, 1] += planeVec.y * planeVec.y
        errQuadric[2, 1] += planeVec.y * planeVec.z
        errQuadric[3, 1] += planeVec.y * planeVec.w

        errQuadric[0, 2] += planeVec.z * planeVec.x
        errQuadric[1, 2] += planeVec.z * planeVec.y
        errQuadric[2, 2] += planeVec.z * planeVec.z
        errQuadric[3, 2] += planeVec.z * planeVec.w

        errQuadric[0, 3] += planeVec.w * planeVec.x
        errQuadric[1, 3] += planeVec.w * planeVec.y
        errQuadric[2, 3] += planeVec.w * planeVec.z
        errQuadric[3, 3] += planeVec.w * planeVec.w
    }
}