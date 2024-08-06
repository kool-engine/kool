package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.*
import physx.PxJointLimitCone
import physx.PxSphericalJoint
import physx.PxSphericalJointFlagEnum
import physx.PxSpring

actual fun SphericalJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): SphericalJoint {
    return SphericalJointImpl(bodyA, bodyB, frameA, frameB)
}

class SphericalJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), SphericalJoint {

    override val pxJoint: PxSphericalJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.SphericalJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun setHardLimitCone(yLimitAngle: Float, zLimitAngle: Float) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = mem.autoDelete(PxJointLimitCone(yLimitAngle, zLimitAngle, spring))
            pxJoint.setLimitCone(limit)
            pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun setSoftLimitCone(yLimitAngle: Float, zLimitAngle: Float, stiffness: Float, damping: Float) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(stiffness, damping))
            val limit = mem.autoDelete(PxJointLimitCone(yLimitAngle, zLimitAngle, spring))
            pxJoint.setLimitCone(limit)
            pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun removeLimitCone() {
        pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, false)
    }
}