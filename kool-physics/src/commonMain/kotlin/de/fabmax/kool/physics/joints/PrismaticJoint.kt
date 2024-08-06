package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor

expect fun PrismaticJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): PrismaticJoint

interface PrismaticJoint : Joint {
    fun setLimit(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun removeLimit()
}