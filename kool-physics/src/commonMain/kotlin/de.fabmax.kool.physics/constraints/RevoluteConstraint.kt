package de.fabmax.kool.physics.constraints

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidBody

expect class RevoluteConstraint(bodyA: RigidBody, bodyB: RigidBody,
                                pivotA: Vec3f, pivotB: Vec3f,
                                axisA: Vec3f, axisB: Vec3f) : Constraint {

    val bodyA: RigidBody
    val bodyB: RigidBody

    val pivotA: Vec3f
    val pivotB: Vec3f

    val axisA: Vec3f
    val axisB: Vec3f

    fun disableAngularMotor()

    fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float)

    fun setAngleLimit(lowerLimit: Float, upperLimit: Float)

    fun clearAngleLimit()
}