package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import de.fabmax.kool.util.memStack
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.PxFixedJoint

// GENERATED CODE BELOW:
// Transformed from desktop source

actual fun FixedJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): FixedJoint {
    return FixedJointImpl(bodyA, bodyB, frameA, frameB)
}

class FixedJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), FixedJoint {

    override val pxJoint: PxFixedJoint

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
            pxJoint = PxTopLevelFunctions.FixedJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }
}