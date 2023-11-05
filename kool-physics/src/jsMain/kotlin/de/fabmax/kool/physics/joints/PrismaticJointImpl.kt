package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.*
import physx.PxJointLinearLimitPair
import physx.PxPrismaticJoint
import physx.PxPrismaticJointFlagEnum
import physx.PxSpring

class PrismaticJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), PrismaticJoint {

    override val pxJoint: PxPrismaticJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.PrismaticJointCreate(PhysicsImpl.physics, bodyA.holder.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun setLimit( lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float ) {
        pxJoint.setLimit(PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
    }

    override fun removeLimit() {
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }

}