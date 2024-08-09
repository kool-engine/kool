package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor

/**
 * This is also known as a ball-socket joint.
 * It doesn't allow linear movement along the joint, but allows the orientation to vary freely.
 * An adjustable mirror connected to a vehicle is a good example of a spherical joint.
 */
expect fun SphericalJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): SphericalJoint

interface SphericalJoint : Joint {
    fun enableLimit(yLimitAngle: AngleF, zLimitAngle: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun disableLimit()
}