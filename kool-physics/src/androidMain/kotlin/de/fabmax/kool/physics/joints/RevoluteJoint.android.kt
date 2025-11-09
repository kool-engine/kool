package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.joints.RevoluteJoint.Companion.computeFrame
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.PxJointAngularLimitPair
import physxandroid.extensions.PxRevoluteJoint
import physxandroid.extensions.PxRevoluteJointFlagEnum
import physxandroid.extensions.PxSpring

actual fun RevoluteJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): RevoluteJoint {
    return RevoluteJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun RevoluteJoint(bodyA: RigidActor?, bodyB: RigidActor, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f): RevoluteJoint {
    val frameA = computeFrame(pivotA, axisA)
    val frameB = computeFrame(pivotB, axisB)
    return RevoluteJointImpl(bodyA, bodyB, frameA, frameB)
}

class RevoluteJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), RevoluteJoint {

    override val joint: PxRevoluteJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            joint = PxTopLevelFunctions.RevoluteJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun disableAngularMotor() {
        joint.driveVelocity = 0f
        joint.driveForceLimit = 0f
        joint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, false)
    }

    override fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        joint.driveVelocity = angularVelocity
        joint.driveForceLimit = forceLimit
        joint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, true)
    }

    override fun enableLimit(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        memStack {
            val spring = autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping), PxSpring::destroy)
            val limit = autoDelete(PxJointAngularLimitPair(lowerLimit.rad, upperLimit.rad, spring), PxJointAngularLimitPair::destroy)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            joint.setLimit(limit)
            joint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        joint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eLIMIT_ENABLED, false)
    }
}