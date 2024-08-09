package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor

expect fun DistanceJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): DistanceJoint

interface DistanceJoint : Joint {
    fun setMaxDistance(maxDistance: Float)
    fun setMinDistance(minDistance: Float)
    fun clearMaxDistance()
    fun clearMinDistance()
}