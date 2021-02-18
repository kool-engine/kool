package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder

expect class TriangleMeshGeometry(triangleMesh: TriangleMesh, scale: Vec3f = Vec3f.ONES) : CommonTriangleMeshGeometry, CollisionGeometry {
    constructor(geometry: IndexedVertexList)
}

abstract class CommonTriangleMeshGeometry(val triangleMesh: TriangleMesh) {

    open fun generateMesh(target: MeshBuilder) {
        target.geometry.addGeometry(triangleMesh.geometry)
    }

    open fun getBounds(result: BoundingBox) = result.set(triangleMesh.geometry.bounds)

    open fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of bounding box
        val bounds = triangleMesh.geometry.bounds
        result.x = (mass / 12f) * (bounds.size.y * bounds.size.y + bounds.size.z * bounds.size.z)
        result.z = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.y * bounds.size.y)
        result.y = (mass / 12f) * (bounds.size.x * bounds.size.x + bounds.size.z * bounds.size.z)
        return result
    }
}
