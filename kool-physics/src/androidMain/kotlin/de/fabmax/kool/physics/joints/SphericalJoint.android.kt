package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.PxJointLimitCone
import physxandroid.extensions.PxSphericalJoint
import physxandroid.extensions.PxSphericalJointFlagEnum

actual fun SphericalJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): SphericalJoint {
    return SphericalJointImpl(bodyA, bodyB, frameA, frameB)
}

class SphericalJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), SphericalJoint {

    override val joint: PxSphericalJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            joint = PxTopLevelFunctions.SphericalJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun enableLimit(yLimitAngle: AngleF, zLimitAngle: AngleF, limitBehavior: LimitBehavior) {
        memStack {
            val limit = autoDelete(PxJointLimitCone(yLimitAngle.rad, zLimitAngle.rad), PxJointLimitCone::destroy)
            limit.stiffness = limitBehavior.stiffness
            limit.damping = limitBehavior.damping
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            joint.setLimitCone(limit)
            joint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        joint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, false)
    }
}