package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

/**
 * This is also known as a ball-socket joint.
 * It doesn't allow linear movement along the joint, but allows the orientation to vary freely.
 * An adjustable mirror connected to a vehicle is a good example of a spherical joint.
 */
expect class SphericalJoint(bodyA: RigidActor, bodyB: RigidActor, posA: Mat4f, posB: Mat4f): Joint {
    val bodyA: RigidActor
    val bodyB: RigidActor

    val frameA: Mat4f
    val frameB: Mat4f
//    fun setHardLimitCone(yLimitAngle: Float, zLimitAngle: Float, contactDist: Float = -1.0f)

    fun setSoftLimitCone(yLimitAngle: Float, zLimitAngle: Float, stiffness: Float, damping: Float)
    fun removeLimitCone()
}