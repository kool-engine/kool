package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox
import physx.*

actual class CylinderShape actual constructor(height: Float, radius: Float) : CommonCylinderShape(height, radius), CollisionShape {

    init {
        Physics.checkIsLoaded()
    }

    override fun getAabb(result: BoundingBox) = result.set(-radius, -height / 2f, -radius, radius, height / 2, radius)
    // todo
    override fun getBoundingSphere(result: MutableVec4f) = result.set(0f, 0f, 0f, height / 2 + radius)
    override fun attachTo(
        actor: PxRigidActor,
        material: PxMaterial,
        flags: PxShapeFlags,
        collisionFilter: PxFilterData
    ): PxShape? {
        return null
    }
}