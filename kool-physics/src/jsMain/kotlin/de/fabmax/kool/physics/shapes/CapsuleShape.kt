package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import physx.*

actual class CapsuleShape actual constructor(height: Float, radius: Float) : CommonCapsuleShape(height, radius), CollisionShape {

    override fun getAabb(result: BoundingBox): BoundingBox {
        return result.set(-radius - height / 2f, -radius, -radius, radius + height / 2f, radius, radius)
    }

    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f = result.set(Vec3f.ZERO, height / 2f + radius)

    override fun attachTo(actor: PxRigidActor, material: PxMaterial, flags: PxShapeFlags, collisionFilter: PxFilterData): PxShape {
        val geometry = PhysX.PxCapsuleGeometry(radius, height / 2f)
        val shape = PhysX.physics.createShape(geometry, material, true, flags)
        shape.setSimulationFilterData(collisionFilter)
        actor.attachShape(shape)
        return shape
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        val h = height - radius
        val iy = 0.5f * mass * radius * radius
        val ixz = 1f / 12f * mass * (3 * radius * radius + h * h)
        return result.set(ixz, iy, ixz)
    }
}