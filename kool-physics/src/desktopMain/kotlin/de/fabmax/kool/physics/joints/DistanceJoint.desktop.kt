package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import de.fabmax.kool.util.memStack
import physx.PxTopLevelFunctions
import physx.extensions.PxDistanceJoint
import physx.extensions.PxDistanceJointFlagEnum

actual fun DistanceJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): DistanceJoint {
    return DistanceJointImpl(bodyA, bodyB, frameA, frameB)
}

class DistanceJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), DistanceJoint {

    override val pxJoint: PxDistanceJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            pxJoint = PxTopLevelFunctions.DistanceJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun setMaxDistance(maxDistance: Float) {
        pxJoint.maxDistance = maxDistance
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, true)
    }
    override fun setMinDistance(minDistance: Float) {
        pxJoint.minDistance = minDistance
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, true)
    }

    override fun clearMaxDistance() {
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, false)
    }

    override fun clearMinDistance() {
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, false)
    }
}