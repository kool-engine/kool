package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.PxJointLinearLimitPair
import physxandroid.extensions.PxPrismaticJoint
import physxandroid.extensions.PxPrismaticJointFlagEnum
import physxandroid.extensions.PxSpring

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
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            joint = PxTopLevelFunctions.PrismaticJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun enableLimit(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        memStack {
            val spring = autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping), PxSpring::destroy)
            val limit = autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring), PxJointLinearLimitPair::destroy)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            joint.setLimit(limit)
            joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        joint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }
}