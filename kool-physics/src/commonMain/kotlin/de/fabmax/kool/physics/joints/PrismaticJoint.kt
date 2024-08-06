package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor

expect fun PrismaticJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): PrismaticJoint

interface PrismaticJoint : Joint {
    fun setHardLimit(lowerLimit: Float, upperLimit: Float)
    fun setSoftLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun removeLimit()
}