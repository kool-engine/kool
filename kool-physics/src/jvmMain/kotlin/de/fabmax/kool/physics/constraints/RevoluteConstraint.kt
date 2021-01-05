package de.fabmax.kool.physics.constraints

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.BtHingeConstraint
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.physics.toBtVector3f

@Suppress("CanBeParameter")
actual class RevoluteConstraint actual constructor(actual val bodyA: RigidBody, actual val bodyB: RigidBody,
                                                   pivotA: Vec3f, pivotB: Vec3f,
                                                   axisA: Vec3f, axisB: Vec3f) : Constraint {

    actual val pivotA = Vec3f(pivotA)
    actual val pivotB = Vec3f(pivotB)
    actual val axisA = Vec3f(axisA)
    actual val axisB = Vec3f(axisB)

    override val btConstraint: BtHingeConstraint = BtHingeConstraint(bodyA.btRigidBody, bodyB.btRigidBody,
        pivotA.toBtVector3f(), pivotB.toBtVector3f(), axisA.toBtVector3f(), axisB.toBtVector3f())

    actual fun setAngleLimit(lowerLimit: Float, upperLimit: Float) {
        btConstraint.setLimit(lowerLimit.toRad(), upperLimit.toRad())
    }

    actual fun clearAngleLimit() {
        btConstraint.setLimit(-1e30f, 1e30f)
    }

    actual fun disableAngularMotor() {
        btConstraint.enableAngularMotor(false, 0f, 0f)
    }

    actual fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float) {
        // jbullet requires drastically smaller impulse values for similar results compared to ammo.js
        val impulseCorrection = 0.1f
        btConstraint.enableAngularMotor(true, targetVelocity, maxImpulse * impulseCorrection)
    }

}