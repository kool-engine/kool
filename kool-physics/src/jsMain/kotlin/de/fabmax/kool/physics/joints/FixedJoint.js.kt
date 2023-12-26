package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.*
import physx.PxFixedJoint

actual fun FixedJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): FixedJoint {
    return FixedJointImpl(bodyA, bodyB, frameA, frameB)
}

class FixedJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), FixedJoint {

    override val frameA = Mat4f(frameA)
    override val frameB = Mat4f(frameB)

    override val pxJoint: PxFixedJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.FixedJointCreate(PhysicsImpl.physics, bodyA.holder.px, frmA, bodyB.holder.px, frmB)
        }
    }
}