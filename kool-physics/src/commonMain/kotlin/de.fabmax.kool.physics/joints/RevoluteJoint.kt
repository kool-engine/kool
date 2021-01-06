package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidBody

expect class RevoluteJoint(bodyA: RigidBody, bodyB: RigidBody, frameA: Mat4f, frameB: Mat4f) : Joint {

    val bodyA: RigidBody
    val bodyB: RigidBody

    val frameA: Mat4f
    val frameB: Mat4f

    constructor(bodyA: RigidBody, bodyB: RigidBody, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f)

//    val pivotA: Vec3f
//    val pivotB: Vec3f
//
//    val axisA: Vec3f
//    val axisB: Vec3f

    fun disableAngularMotor()

    fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float)

    fun setAngleLimit(lowerLimit: Float, upperLimit: Float)

    fun clearAngleLimit()
}