package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.PxJointLinearLimitPair
import physx.extensions.PxPrismaticJoint
import physx.extensions.PxPrismaticJointFlagEnum
import physx.extensions.PxSpring

actual fun PrismaticJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): PrismaticJoint {
    return PrismaticJointImpl(bodyA, bodyB, frameA, frameB)
}

class PrismaticJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), PrismaticJoint {

    override val joint: PxPrismaticJoint

    init {
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            joint = PxTopLevelFunctions.PrismaticJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun setHardLimit(lowerLimit: Float, upperLimit: Float) {
        memStack {
            val spring = PxSpring.createAt(this, MemoryStack::nmalloc, 0f, 0f)
            val limit = PxJointLinearLimitPair.createAt(this, MemoryStack::nmalloc, lowerLimit, upperLimit, spring)
            joint.setLimit(limit)
            joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun setSoftLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        memStack {
            val spring = PxSpring.createAt(this, MemoryStack::nmalloc, stiffness, damping)
            val limit = PxJointLinearLimitPair.createAt(this, MemoryStack::nmalloc, lowerLimit, upperLimit, spring)
            joint.setLimit(limit)
            joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun removeLimit() {
        joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }
}