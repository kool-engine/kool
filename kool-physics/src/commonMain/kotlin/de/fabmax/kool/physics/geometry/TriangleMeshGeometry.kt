package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder

expect fun TriangleMeshGeometry(triangleMesh: TriangleMesh, scale: Vec3f = Vec3f.ONES): TriangleMeshGeometry

expect fun TriangleMeshGeometry(geometry: IndexedVertexList, scale: Vec3f = Vec3f.ONES): TriangleMeshGeometry

interface TriangleMeshGeometry : CollisionGeometry {
    val triangleMesh: TriangleMesh
    val scale: Vec3f

    override fun generateMesh(target: MeshBuilder) {
        target.geometry.addGeometry(triangleMesh.geometry)
    }

    override fun getBounds(result: BoundingBoxF) = result.set(triangleMesh.geometry.bounds)

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of bounding box
        val bounds = triangleMesh.geometry.bounds
        result.x = (mass / 12f) * (bounds.size.y * bounds.size.y + bounds.size.z * bounds.size.z)
        result.z = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.y * bounds.size.y)
        result.y = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.z * bounds.size.z)
        return result
    }
}
