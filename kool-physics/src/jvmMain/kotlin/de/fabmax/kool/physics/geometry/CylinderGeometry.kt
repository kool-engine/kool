package de.fabmax.kool.physics.geometry

import de.fabmax.kool.math.Vec3f
import physx.geomutils.PxConvexMesh
import physx.geomutils.PxConvexMeshGeometry
import physx.geomutils.PxGeometry
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

actual class CylinderGeometry actual constructor(length: Float, radius: Float) : CommonCylinderGeometry(length, radius), CollisionGeometry {

    val pxMesh: PxConvexMesh
    override val pxGeometry: PxGeometry

    init {
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

        pxMesh = ConvexMeshGeometry.toConvexMesh(points)
        pxGeometry = PxConvexMeshGeometry(pxMesh)
    }
}