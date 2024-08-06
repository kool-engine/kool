package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.*
import physx.PxJointLinearLimitPair
import physx.PxPrismaticJoint
import physx.PxPrismaticJointFlagEnum
import physx.PxSpring

actual fun PrismaticJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): PrismaticJoint {
    return PrismaticJointImpl(bodyA, bodyB, frameA, frameB)
}

class PrismaticJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), PrismaticJoint {

    override val pxJoint: PxPrismaticJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.PrismaticJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun setHardLimit(lowerLimit: Float, upperLimit: Float) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = PxJointLinearLimitPair(lowerLimit, upperLimit, spring)
            pxJoint.setLimit(limit)
            pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun setSoftLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float ) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(stiffness, damping))
            val limit = PxJointLinearLimitPair(lowerLimit, upperLimit, spring)
            pxJoint.setLimit(limit)
            pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun removeLimit() {
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }

}