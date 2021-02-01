package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder

expect class ConvexMeshGeometry(points: List<Vec3f>) : CommonConvexMeshGeometry, CollisionGeometry

abstract class CommonConvexMeshGeometry(val points: List<Vec3f>) {

    abstract val convexHull: IndexedVertexList

    open fun generateMesh(target: MeshBuilder) {
        target.apply {
            val inds = mutableListOf<Int>()
            convexHull.forEach {
                inds += vertex(it, it.normal)
            }
            for (i in 0 until convexHull.numIndices step 3) {
                val i0 = inds[convexHull.indices[i]]
                val i1 = inds[convexHull.indices[i + 1]]
                val i2 = inds[convexHull.indices[i + 2]]
                geometry.addTriIndices(i0, i1, i2)
            }
        }
    }

    open fun getBounds(result: BoundingBox) = result.set(convexHull.bounds)

    open fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of bounding box
        val bounds = convexHull.bounds
        result.x = (mass / 12f) * (bounds.size.y * bounds.size.y + bounds.size.z * bounds.size.z)
        result.z = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.y * bounds.size.y)
        result.y = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.z * bounds.size.z)
        return result
    }
}
