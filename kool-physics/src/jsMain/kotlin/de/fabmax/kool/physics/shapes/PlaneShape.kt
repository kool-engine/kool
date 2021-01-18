package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.util.BoundingBox
import physx.*

actual class PlaneShape : CommonPlaneShape(), CollisionShape {

    override fun getAabb(result: BoundingBox) = result.set(0f, -1e10f, -1e10f, 0f, 1e10f, 1e10f)
    override fun getBoundingSphere(result: MutableVec4f) = result.set(0f, 0f, 0f, 1e10f)

    override fun attachTo(actor: PxRigidActor, material: PxMaterial, flags: PxShapeFlags, bodyProps: RigidBodyProperties): PxShape {
        val geometry = PhysX.PxPlaneGeometry()
        val shape = PhysX.physics.createShape(geometry, material, true, flags)
        setFilterDatas(shape, bodyProps)
        actor.attachShape(shape)
        return shape
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // plane does not have a meaningful inertia
        return result.set(1f, 1f, 1f)
    }
}