package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

// public aliases of PhysX
expect enum class D6JointMotion {
    Free,
    Limited,
    Locked
}

expect class D6Joint(bodyA: RigidActor, bodyB: RigidActor, posA: Mat4f, posB: Mat4f) : Joint {
    val bodyA: RigidActor
    val bodyB: RigidActor

    val frameA: Mat4f
    val frameB: Mat4f

    var projectionLinearTolerance: Float?

    var motionX: D6JointMotion
    var motionY: D6JointMotion
    var motionZ: D6JointMotion
    fun setDistanceLimit(extend: Float, stiffness: Float, damping: Float)
    fun setXLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun setYLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun setZLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
}