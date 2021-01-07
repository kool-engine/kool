package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import physx.*

actual class MultiShape actual constructor() : CommonMultiShape(), CollisionShape {

    private val mutShapes = mutableListOf<ChildShape>()
    override val children: List<ChildShape>
        get() = mutShapes

    override fun getAabb(result: BoundingBox): BoundingBox {
        // todo
        return result.set(Vec3f(-1f), Vec3f(1f))
    }

    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f {
        // todo
        return result.set(0f, 0f, 0f, 1f)
    }

    actual constructor(childShapes: List<ChildShape>) : this() {
        childShapes.forEach { addShape(it) }
    }

    override fun addShape(childShape: ChildShape) {
        mutShapes += childShape
        //childShape.shape.localPose.set(childShape.transform)
    }

    override fun removeShape(shape: CollisionShape) {
        mutShapes.removeAll { it.shape === shape }
    }

    override fun attachTo(actor: PxRigidActor, material: PxMaterial, flags: PxShapeFlags, collisionFilter: PxFilterData): PxShape? {
        children.forEach {
            it.shape.attachTo(actor, material, flags, collisionFilter)?.setLocalPose(it.transform.toPxTransform())
        }
        return null
    }
}