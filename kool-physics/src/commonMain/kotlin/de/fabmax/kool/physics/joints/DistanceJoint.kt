package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

expect class DistanceJoint(bodyA: RigidActor, bodyB: RigidActor, posA: Mat4f, posB: Mat4f) : Joint {
    val bodyA: RigidActor
    val bodyB: RigidActor

    val frameA: Mat4f
    val frameB: Mat4f
    fun setMaxDistance(maxDistance: Float)
    fun setMinDistance(minDistance: Float)
    fun removeMaxDistance()
    fun removeMinDistance()
}