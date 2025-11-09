package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.PxFixedJoint

actual fun FixedJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): FixedJoint {
    return FixedJointImpl(bodyA, bodyB, frameA, frameB)
}

class FixedJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), FixedJoint {

    override val joint: PxFixedJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            joint = PxTopLevelFunctions.FixedJointCreate(PhysicsImpl.physics, bodyA?.holder, frmA, bodyB.holder, frmB)
        }
    }
}