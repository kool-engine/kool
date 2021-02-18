package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import physx.PxConvexMeshGeometry
import physx.PxGeometry
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

actual class CylinderGeometry actual constructor(length: Float, radius: Float) : CommonCylinderGeometry(length, radius), CollisionGeometry {

    val convexMesh: ConvexMesh
    override val pxGeometry: PxGeometry

    init {
        Physics.checkIsLoaded()

        // PhysX does not have a cylinder primitive, we need to approximate it with a convex mesh
        val n = 32
        val points = mutableListOf<Vec3f>()
        for (i in 0 until n) {
            val a = i * 2f * PI.toFloat() / n
            val y = cos(a) * radius
            val z = sin(a) * radius
            points.add(Vec3f(length * -0.5f, y, z))
            points.add(Vec3f(length * 0.5f, y, z))
        }

        convexMesh = ConvexMesh(points)
        pxGeometry = PxConvexMeshGeometry(convexMesh.pxConvexMesh)
    }
}