package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder

expect class ConvexHullShape(points: List<Vec3f>) : CommonConvexHullShape, CollisionShape

abstract class CommonConvexHullShape(val points: List<Vec3f>) {

    abstract val geometry: IndexedVertexList

    open fun generateGeometry(target: MeshBuilder) {
        val hull = geometry
        target.apply {
            val inds = mutableListOf<Int>()
            hull.forEach {
                inds += vertex(it, it.normal)
            }
            for (i in 0 until hull.numIndices step 3) {
                val i0 = inds[hull.indices[i]]
                val i1 = inds[hull.indices[i + 1]]
                val i2 = inds[hull.indices[i + 2]]
                geometry.addTriIndices(i0, i1, i2)
            }
        }
    }

}
