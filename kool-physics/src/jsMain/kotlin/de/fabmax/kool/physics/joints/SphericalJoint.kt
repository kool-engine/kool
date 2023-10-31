package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.*
import physx.PxJointLimitCone
import physx.PxSphericalJoint
import physx.PxSphericalJointFlagEnum
import physx.PxSpring

actual class SphericalJoint actual constructor(
    actual val bodyA: RigidActor,
    actual val bodyB: RigidActor,
    posA: Mat4f,
    posB: Mat4f
) : Joint() {
    actual val frameA = Mat4f(posA)
    actual val frameB = Mat4f(posB)
    override val pxJoint: PxSphericalJoint

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = posA.toPxTransform(mem.createPxTransform())
            val frmB = posB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.SphericalJointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        }
    }

    actual fun setSoftLimitCone(yLimitAngle: Float, zLimitAngle: Float, stiffness: Float, damping: Float) {
        pxJoint.setLimitCone(PxJointLimitCone(yLimitAngle, zLimitAngle, PxSpring(stiffness, damping)))
        pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, true)
    }

    actual fun removeLimitCone() {
        pxJoint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, false)
    }
}