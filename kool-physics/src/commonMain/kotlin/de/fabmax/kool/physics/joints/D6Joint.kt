package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.RigidActor

expect fun D6Joint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): D6Joint

interface D6Joint : Joint {
    var motionX: D6JointMotion
    var motionY: D6JointMotion
    var motionZ: D6JointMotion

    fun setDistanceLimit(extend: Float, stiffness: Float, damping: Float)
    fun setXLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun setYLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
    fun setZLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float)
}

enum class D6JointMotion {
    Free,
    Limited,
    Locked
}
