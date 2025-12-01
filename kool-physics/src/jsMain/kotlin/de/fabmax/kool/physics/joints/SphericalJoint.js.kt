package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physx.PxSphericalJoint
import physx.PxSphericalJointFlagEnum
import physx.prototypes.PxTopLevelFunctions

// GENERATED CODE BELOW:
// Transformed from desktop source

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
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            pxJoint = PxTopLevelFunctions.SphericalJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun enableLimit(yLimitAngle: AngleF, zLimitAngle: AngleF, limitBehavior: LimitBehavior) {
        memStack {
            val limit = createPxJointLimitCone(yLimitAngle, zLimitAngle)
            limit.stiffness = limitBehavior.stiffness
            limit.damping = limitBehavior.damping
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