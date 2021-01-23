package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.physics.toPxTransform
import de.fabmax.kool.util.BoundingBox
import physx.*
import kotlin.math.sqrt

actual class BoxShape actual constructor(size: Vec3f, private val localPose: Mat4f?) : CommonBoxShape(size), CollisionShape {

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

    override fun attachTo(actor: PxRigidActor, flags: PxShapeFlags, material: PxMaterial, bodyProps: RigidBodyProperties?): PxShape {
        val geometry = PxBoxGeometry(size.x * 0.5f, size.y * 0.5f, size.z * 0.5f)
        val shape = Physics.physics.createShape(geometry, material, true, flags)
        localPose?.let { shape.setLocalPose(it.toPxTransform(shape.getLocalPose())) }
        bodyProps?.let { setFilterDatas(shape, it) }
        actor.attachShape(shape)
        return shape
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        result.x = (mass / 12f) * (size.y * size.y + size.z * size.z)
        result.y = (mass / 12f) * (size.x * size.x + size.z * size.z)
        result.z = (mass / 12f) * (size.x * size.x + size.y * size.y)
        return result
    }
}