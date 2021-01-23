package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.physics.toPxTransform
import de.fabmax.kool.util.BoundingBox
import physx.PxMaterial
import physx.PxRigidActor
import physx.PxShape
import physx.PxShapeFlags

actual class MultiShape actual constructor() : CommonMultiShape(), CollisionShape {

    private val mutShapes = mutableListOf<ChildShape>()
    override val children: List<ChildShape>
        get() = mutShapes

    override fun getAabb(result: BoundingBox): BoundingBox {
        result.clear()
        val v = MutableVec3f()
        val childAabb = BoundingBox()
        for (i in mutShapes.indices) {
            mutShapes[i].shape.getAabb(childAabb)
            mutShapes[i].transform.transform(v.set(childAabb.min))
            result.add(v)
            mutShapes[i].transform.transform(v.set(childAabb.max))
            result.add(v)
        }
        return result
    }

    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f {
        val aabb = getAabb(BoundingBox())
        val r = aabb.size.length() / 2
        result.set(aabb.center, r)
        return result
    }

    actual constructor(childShapes: List<ChildShape>) : this() {
        childShapes.forEach { addShape(it) }
    }

    override fun addShape(childShape: ChildShape) {
        mutShapes += childShape
    }

    override fun removeShape(shape: CollisionShape) {
        mutShapes.removeAll { it.shape === shape }
    }

    override fun attachTo(actor: PxRigidActor, flags: PxShapeFlags, material: PxMaterial, bodyProps: RigidBodyProperties?): PxShape? {
        children.forEach {
            it.shape.attachTo(actor, flags, material, bodyProps)?.setLocalPose(it.transform.toPxTransform())
        }
        return null
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // rough approximation: use inertia of bounding box
        val aabb = getAabb(BoundingBox())
        result.x = (mass / 12f) * (aabb.size.y * aabb.size.y + aabb.size.z * aabb.size.z)
        result.y = (mass / 12f) * (aabb.size.x * aabb.size.x + aabb.size.z * aabb.size.z)
        result.z = (mass / 12f) * (aabb.size.x * aabb.size.x + aabb.size.y * aabb.size.y)
        return result
    }
}