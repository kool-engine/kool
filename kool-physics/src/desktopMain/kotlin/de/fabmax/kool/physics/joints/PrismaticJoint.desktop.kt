package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.PxJointLinearLimitPair
import physx.extensions.PxPrismaticJoint
import physx.extensions.PxPrismaticJointFlagEnum
import physx.extensions.PxSpring

actual fun PrismaticJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): PrismaticJoint {
    return PrismaticJointImpl(bodyA, bodyB, frameA, frameB)
}

class PrismaticJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), PrismaticJoint {

    override val joint: PxPrismaticJoint

    init {
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            joint = PxTopLevelFunctions.PrismaticJointCreate(PhysicsImpl.physics, bodyA.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun setLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        joint.setLimit(PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
    }

    override fun removeLimit() {
        joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }
}