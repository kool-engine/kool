package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
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

    override fun enableLimit(yLimitAngle: AngleF, zLimitAngle: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLimitCone(yLimitAngle.rad, zLimitAngle.rad, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLimitCone(limit)
            pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, false)
    }
}