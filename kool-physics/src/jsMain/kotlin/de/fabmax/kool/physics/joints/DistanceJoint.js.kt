package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.*
import physx.PxDistanceJoint
import physx.PxDistanceJointFlagEnum
import physx.maxDistance
import physx.minDistance

actual fun DistanceJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): DistanceJoint {
    return DistanceJointImpl(bodyA, bodyB, frameA, frameB)
}

class DistanceJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), DistanceJoint {

    override val pxJoint: PxDistanceJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.DistanceJointCreate(PhysicsImpl.physics, bodyA.holder.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun setMaxDistance(maxDistance: Float) {
        pxJoint.maxDistance = maxDistance
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, maxDistance >= 0f)
    }
    override fun setMinDistance(minDistance: Float) {
        pxJoint.minDistance = minDistance
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, minDistance >= 0f)
    }

    override fun removeMaxDistance() {
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, false)
    }

    override fun removeMinDistance() {
        pxJoint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, false)
    }
}