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

// GENERATED CODE BELOW:
// Transformed from desktop source

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

    override val pxJoint: PxRevoluteJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            pxJoint = PxTopLevelFunctions.RevoluteJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun disableAngularMotor() {
        pxJoint.driveVelocity = 0f
        pxJoint.driveForceLimit = 0f
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, false)
    }

    override fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        pxJoint.driveVelocity = angularVelocity
        pxJoint.driveForceLimit = forceLimit
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, true)
    }

    override fun enableLimit(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointAngularLimitPair(lowerLimit, upperLimit, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLimit(limit)
            pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eLIMIT_ENABLED, false)
    }
}