package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.*
import de.fabmax.kool.util.scopedMem
import physx.PxPrismaticJoint
import physx.PxPrismaticJointFlagEnum
import physx.prototypes.PxTopLevelFunctions

// GENERATED CODE BELOW:
// Transformed from desktop source

actual fun PrismaticJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): PrismaticJoint {
    return PrismaticJointImpl(bodyA, bodyB, frameA, frameB)
}

class PrismaticJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), PrismaticJoint {

    override val pxJoint: PxPrismaticJoint

    init {
        scopedMem {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            pxJoint = PxTopLevelFunctions.PrismaticJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun enableLimit(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        scopedMem {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointLinearLimitPair(lowerLimit, upperLimit, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLimit(limit)
            pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }
}