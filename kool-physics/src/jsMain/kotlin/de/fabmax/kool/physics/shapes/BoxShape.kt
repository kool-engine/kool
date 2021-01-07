package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import physx.*
import kotlin.math.sqrt

actual class BoxShape actual constructor(size: Vec3f) : CommonBoxShape(size), CollisionShape {

    override fun getAabb(result: BoundingBox): BoundingBox {
        result.set(-size.x * 0.5f, -size.y * 0.5f, -size.z * 0.5f,
            size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
        return result
    }
    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f {
        val hx = size.x * 0.5f
        val hy = size.y * 0.5f
        val hz = size.z * 0.5f
        val r = sqrt(hx * hx + hy * hy + hz * hz)
        return result.set(Vec3f.ZERO, r)
    }

    override fun attachTo(actor: PxRigidActor, material: PxMaterial, flags: PxShapeFlags, collisionFilter: PxFilterData): PxShape {
        val geometry = PhysX.PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
        val shape = PhysX.physics.createShape(geometry, material, true, flags)
        shape.setSimulationFilterData(collisionFilter)
        actor.attachShape(shape)
        return shape
    }
}