package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.util.BoundingBox
import physx.*

actual class SphereShape actual constructor(radius: Float) : CommonSphereShape(radius), CollisionShape {

    override fun getAabb(result: BoundingBox): BoundingBox {
        return result.set(-radius, -radius, -radius, radius, radius, radius)
    }

    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f = result.set(Vec3f.ZERO, radius)

    override fun attachTo(actor: PxRigidActor, flags: PxShapeFlags, material: PxMaterial, bodyProps: RigidBodyProperties?): PxShape {
        val geometry = PxSphereGeometry(radius)
        val shape = Physics.physics.createShape(geometry, material, true, flags)
        bodyProps?.let { setFilterDatas(shape, it) }
        actor.attachShape(shape)
        return shape
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        val i = 2f / 5f * mass * radius * radius
        return result.set(i, i, i)
    }
}