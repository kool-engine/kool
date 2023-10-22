package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import kotlin.math.max
import kotlin.math.min

object Subdivide {

    /**
     * Subdivides the given list of triangles in-place.
     */
    fun subdivideTris(verts: MutableList<Vec3f>,
                      triIndices: MutableList<Int>,
                      computeMid: (Vec3f, Vec3f) -> Vec3f = { a, b -> MutableVec3f(a).add(b).mul(0.5f) }) {

        val newTris = IntArray(triIndices.size * 4)
        val midVerts = mutableMapOf<Double, Int>()

        fun getMidVertex(fromIdx: Int, toIdx: Int): Int {
            // using a Double as key is much faster in javascript, where Long is not a native type...
            val key = min(fromIdx, toIdx).toDouble() * 1048576 + max(fromIdx, toIdx)
            return midVerts.getOrPut(key) {
                val insertIdx = verts.size
                verts += computeMid(verts[fromIdx], verts[toIdx])
                insertIdx
            }
        }

        var i = 0
        for (j in triIndices.indices step 3) {
            val v1 = triIndices[j]
            val v2 = triIndices[j + 1]
            val v3 = triIndices[j + 2]

            // subdivide edges
            val a = getMidVertex(v1, v2)
            val b = getMidVertex(v2, v3)
            val c = getMidVertex(v3, v1)

            newTris[i++] = v1; newTris[i++] = a; newTris[i++] = c
            newTris[i++] = v2; newTris[i++] = b; newTris[i++] = a
            newTris[i++] = v3; newTris[i++] = c; newTris[i++] = b
            newTris[i++] = a;  newTris[i++] = b; newTris[i++] = c
        }
        triIndices.clear()
        newTris.forEach { triIndices.add(it) }
    }
}