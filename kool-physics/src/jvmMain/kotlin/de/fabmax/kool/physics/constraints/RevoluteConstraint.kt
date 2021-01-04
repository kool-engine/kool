package de.fabmax.kool.physics.constraints

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtHingeConstraint
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.physics.toBtVector3f

actual class RevoluteConstraint actual constructor(bodyA: RigidBody, bodyB: RigidBody,
                                                   pivotA: Vec3f, pivotB: Vec3f,
                                                   axisA: Vec3f, axisB: Vec3f) : Constraint {

    override val btConstraint: BtHingeConstraint = BtHingeConstraint(bodyA.btRigidBody, bodyB.btRigidBody,
        pivotA.toBtVector3f(), pivotB.toBtVector3f(), axisA.toBtVector3f(), axisB.toBtVector3f())

    actual fun disableAngularMotor() {
        btConstraint.enableAngularMotor(false, 0f, 0f)
    }

    actual fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float) {
        // jbullet requires drastically smaller impulse values for similar results compared to ammo.js
        val impulseCorrection = 0.1f
        btConstraint.enableAngularMotor(true, targetVelocity, maxImpulse * impulseCorrection)
    }

}