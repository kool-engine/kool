package de.fabmax.kool.physics.geometry

import de.fabmax.kool.physics.Physics
import physx.geomutils.PxConvexMeshGeometry

actual class CylinderGeometry actual constructor(length: Float, radius: Float) : CommonCylinderGeometry(length, radius), CollisionGeometry {

    val convexMesh: ConvexMesh
    override val pxGeometry: PxConvexMeshGeometry

    init {
        Physics.checkIsLoaded()

        // PhysX does not have a cylinder primitive, we need to approximate it with a convex mesh
        convexMesh = ConvexMesh(convexMeshPoints(length, radius))
        pxGeometry = PxConvexMeshGeometry(convexMesh.pxConvexMesh)
    }
}