package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

expect class PrismaticJoint(bodyA: RigidActor, bodyB: RigidActor, posA: Mat4f, posB: Mat4f) : Joint {
    val frameA: Mat4f
    val frameB: Mat4f
    fun setLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun removeLimit()
}