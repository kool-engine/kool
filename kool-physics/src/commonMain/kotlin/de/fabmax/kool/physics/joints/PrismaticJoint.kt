package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

expect fun PrismaticJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): PrismaticJoint

interface PrismaticJoint : Joint {
    fun setLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun removeLimit()
}