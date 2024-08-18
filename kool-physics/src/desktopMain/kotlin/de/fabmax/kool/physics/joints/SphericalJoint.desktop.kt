package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.PxJointLimitCone
import physx.extensions.PxSphericalJoint
import physx.extensions.PxSphericalJointFlagEnum

actual fun SphericalJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): SphericalJoint {
    return SphericalJointImpl(bodyA, bodyB, frameA, frameB)
}

class SphericalJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), SphericalJoint {

    override val joint: PxSphericalJoint

    init {
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            joint = PxTopLevelFunctions.SphericalJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun enableLimit(yLimitAngle: AngleF, zLimitAngle: AngleF, limitBehavior: LimitBehavior) {
        memStack {
            val limit = PxJointLimitCone.createAt(this, MemoryStack::nmalloc, yLimitAngle.rad, zLimitAngle.rad)
            limit.stiffness = limitBehavior.stiffness
            limit.damping = limitBehavior.damping
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            joint.setLimitCone(limit)
            joint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        joint.setSphericalJointFlag(PxSphericalJointFlagEnum.eLIMIT_ENABLED, false)
    }
}