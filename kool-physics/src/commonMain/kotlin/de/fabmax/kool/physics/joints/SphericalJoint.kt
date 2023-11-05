package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

/**
 * This is also known as a ball-socket joint.
 * It doesn't allow linear movement along the joint, but allows the orientation to vary freely.
 * An adjustable mirror connected to a vehicle is a good example of a spherical joint.
 */
expect fun SphericalJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): SphericalJoint

interface SphericalJoint : Joint {
    fun setSoftLimitCone(yLimitAngle: Float, zLimitAngle: Float, stiffness: Float, damping: Float)
    fun removeLimitCone()
}