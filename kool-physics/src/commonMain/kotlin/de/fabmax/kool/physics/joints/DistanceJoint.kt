package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

expect fun DistanceJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): DistanceJoint

interface DistanceJoint : Joint {
    fun setMaxDistance(maxDistance: Float)
    fun setMinDistance(minDistance: Float)
    fun removeMaxDistance()
    fun removeMinDistance()
}