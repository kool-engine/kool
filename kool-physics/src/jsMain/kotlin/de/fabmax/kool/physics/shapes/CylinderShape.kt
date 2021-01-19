package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.util.BoundingBox
import physx.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

actual class CylinderShape actual constructor(length: Float, radius: Float) : CommonCylinderShape(length, radius), CollisionShape {

    val pxMesh: PxConvexMesh

    init {
        Physics.checkIsLoaded()

        // PhysX does not have a cylinder primitive, we need to approximate it with a convex mesh
        val n = 30
        val points = mutableListOf<Vec3f>()
        for (i in 0 until n) {
            val a = i * 2f * PI.toFloat() / n
            val y = cos(a) * radius
            val z = sin(a) * radius
            points.add(Vec3f(length * -0.5f, y, z))
            points.add(Vec3f(length * 0.5f, y, z))
        }

        pxMesh = ConvexHullShape.toConvexMesh(points)
    }

    override fun getAabb(result: BoundingBox): BoundingBox {
        result.set(-radius, -length * 0.5f, -radius,
            radius, length * 0.5f, radius * 0.5f)
        return result
    }
    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f {
        return result.set(Vec3f.ZERO, length * 0.5f + radius)
    }

    override fun attachTo(actor: PxRigidActor, flags: PxShapeFlags, material: PxMaterial, bodyProps: RigidBodyProperties?): PxShape {
        //val scaling = PhysX.PxMeshScale(Vec3f(1f).toPxVec3(), Vec4f(0f, 0f, 0f, 1f).toPxQuat())
        //val meshFlags = PhysX.PxConvexMeshGeometryFlags(PxConvexMeshGeometryFlag.eTIGHT_BOUNDS)
        //val geometry = PhysX.PxConvexMeshGeometry(pxMesh, scaling, meshFlags)
        val geometry = PhysX.PxConvexMeshGeometry(pxMesh)
        val shape = PhysX.physics.createShape(geometry, material, true, flags)
        bodyProps?.let { setFilterDatas(shape, it) }
        actor.attachShape(shape)
        return shape
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        val iy = 0.5f * mass * radius * radius
        val ixz = 1f / 12f * mass * (3 * radius * radius + length * length)
        return result.set(ixz, iy, ixz)
    }
}