package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.*
import physx.PxJointLinearLimitPair
import physx.PxPrismaticJoint
import physx.PxPrismaticJointFlagEnum
import physx.PxSpring

actual class PrismaticJoint actual constructor(
    val bodyA: RigidActor,
    val bodyB: RigidActor,
    posA: Mat4f,
    posB: Mat4f
) : Joint() {

    actual val frameA = Mat4f().set(posA)
    actual val frameB = Mat4f().set(posB)
    override val pxJoint: PxPrismaticJoint
    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = posA.toPxTransform(mem.createPxTransform())
            val frmB = posB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.PrismaticJointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        }
    }

    actual fun setLimit( lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float ) {
        pxJoint.setLimit(PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
    }

    actual fun removeLimit() {
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }

}