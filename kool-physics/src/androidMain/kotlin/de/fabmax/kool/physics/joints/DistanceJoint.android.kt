package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.PxDistanceJoint
import physxandroid.extensions.PxDistanceJointFlagEnum

actual fun DistanceJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): DistanceJoint {
    return DistanceJointImpl(bodyA, bodyB, frameA, frameB)
}

class DistanceJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), DistanceJoint {

    override val joint: PxDistanceJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            joint = PxTopLevelFunctions.DistanceJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun setMaxDistance(maxDistance: Float) {
        joint.maxDistance = maxDistance
        joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, true)
    }
    override fun setMinDistance(minDistance: Float) {
        joint.minDistance = minDistance
        joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, true)
    }

    override fun clearMaxDistance() {
        joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, false)
    }

    override fun clearMinDistance() {
        joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, false)
    }
}