package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.PxDistanceJoint
import physx.extensions.PxDistanceJointFlagEnum

class DistanceJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), DistanceJoint {

    override val joint: PxDistanceJoint

    init {
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            joint = PxTopLevelFunctions.DistanceJointCreate(PhysicsImpl.physics, bodyA.holder, frmA, bodyB.holder, frmB)
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

    override fun removeMaxDistance() {
        joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMAX_DISTANCE_ENABLED, false)
    }

    override fun removeMinDistance() {
        joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, false)
    }

}