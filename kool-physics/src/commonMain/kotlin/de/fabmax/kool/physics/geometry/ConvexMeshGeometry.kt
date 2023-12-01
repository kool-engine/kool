package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.MeshBuilder

expect fun ConvexMeshGeometry(convexMesh: ConvexMesh, scale: Vec3f = Vec3f.ONES): ConvexMeshGeometry
expect fun ConvexMeshGeometry(points: List<Vec3f>, scale: Vec3f = Vec3f.ONES): ConvexMeshGeometry

interface ConvexMeshGeometry : CollisionGeometry {

    val convexMesh: ConvexMesh
    val scale: Vec3f

    override fun generateMesh(target: MeshBuilder) {
        target.apply {
            withTransform {
                scale(scale.x, scale.y, scale.z)
                val hull = convexMesh.convexHull
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

    override fun getBounds(result: BoundingBoxF) = result.set(convexMesh.convexHull.bounds)

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of bounding box
        val bounds = convexMesh.convexHull.bounds
        result.x = (mass / 12f) * (bounds.size.y * bounds.size.y + bounds.size.z * bounds.size.z)
        result.z = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.y * bounds.size.y)
        result.y = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.z * bounds.size.z)
        return result
    }
}
