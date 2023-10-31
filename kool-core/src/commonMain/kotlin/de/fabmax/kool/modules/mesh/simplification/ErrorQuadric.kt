package de.fabmax.kool.modules.mesh.simplification

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.mesh.HalfEdgeMesh

class ErrorQuadric(val vertex: HalfEdgeMesh.HalfEdgeVertex) {

    val errQuadric = MutableMat4d().set(Mat4d.ZERO)
    var isStickyVertex = false
    var isBorder = false
        private set
    var isDeleted = false
        private set

    private val tmpVec4 = MutableVec4f()

    init {
        for (i in vertex.edges.indices) {
            val ed = vertex.edges[i]
            addPlane(ed.computeTriPlane(tmpVec4))

            if (ed.opp == null) {
                // edge belongs to a border triangle, add a virtual orthogonal plane
                val triNrm = tmpVec4.xyz
                val edDir = MutableVec3f(vertex).subtract(ed.to).norm()
                val nrm = triNrm.cross(edDir, MutableVec3f())

                tmpVec4.set(nrm, -nrm.dot(vertex))
                addPlane(tmpVec4)
                isBorder = true
            }
            if (ed.next.next.opp == null) {
                // edge belongs to a border triangle, add a virtual orthogonal plane
                if (ed.opp == null) {
                    ed.computeTriPlane(tmpVec4)
                }
                val triNrm = tmpVec4.xyz
                val edDir = MutableVec3f(ed.next.next.from).subtract(vertex).norm()
                val nrm = triNrm.cross(edDir, MutableVec3f())
                tmpVec4.set(nrm, -nrm.dot(vertex))
                addPlane(tmpVec4)
                isBorder = true
            }
        }
    }

    fun consume(other: ErrorQuadric) {
        isBorder = isBorder || other.isBorder
        errQuadric.add(other.errQuadric)
        other.isDeleted = true
    }

    fun getError(v: Vec3f): Double {
        errQuadric.transform(tmpVec4.set(v, 1f))
        return tmpVec4.x.toDouble() * v.x + tmpVec4.y.toDouble() * v.y + tmpVec4.z.toDouble() * v.z + tmpVec4.w
    }

    private fun addPlane(planeVec: Vec4f) {
        errQuadric[0, 0] += (planeVec.x * planeVec.x).toDouble()
        errQuadric[1, 0] += (planeVec.x * planeVec.y).toDouble()
        errQuadric[2, 0] += (planeVec.x * planeVec.z).toDouble()
        errQuadric[3, 0] += (planeVec.x * planeVec.w).toDouble()

        errQuadric[0, 1] += (planeVec.y * planeVec.x).toDouble()
        errQuadric[1, 1] += (planeVec.y * planeVec.y).toDouble()
        errQuadric[2, 1] += (planeVec.y * planeVec.z).toDouble()
        errQuadric[3, 1] += (planeVec.y * planeVec.w).toDouble()

        errQuadric[0, 2] += (planeVec.z * planeVec.x).toDouble()
        errQuadric[1, 2] += (planeVec.z * planeVec.y).toDouble()
        errQuadric[2, 2] += (planeVec.z * planeVec.z).toDouble()
        errQuadric[3, 2] += (planeVec.z * planeVec.w).toDouble()

        errQuadric[0, 3] += (planeVec.w * planeVec.x).toDouble()
        errQuadric[1, 3] += (planeVec.w * planeVec.y).toDouble()
        errQuadric[2, 3] += (planeVec.w * planeVec.z).toDouble()
        errQuadric[3, 3] += (planeVec.w * planeVec.w).toDouble()
    }
}