package de.fabmax.kool.physics.constraints

import ammo.Ammo
import ammo.btHingeConstraint
import ammo.toBtVector3
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBody

actual class RevoluteConstraint actual constructor(bodyA: RigidBody, bodyB: RigidBody,
                                                   pivotA: Vec3f, pivotB: Vec3f,
                                                   axisA: Vec3f, axisB: Vec3f) : Constraint {

    override val btConstraint: btHingeConstraint

    init {
        Physics.checkIsLoaded()

        btConstraint = Ammo.btHingeConstraint(bodyA.btRigidBody, bodyB.btRigidBody,
            pivotA.toBtVector3(), pivotB.toBtVector3(), axisA.toBtVector3(), axisB.toBtVector3())
    }

    actual fun disableAngularMotor() {
        btConstraint.enableAngularMotor(false, 0f, 0f)
    }

    actual fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float) {
        btConstraint.enableAngularMotor(true, targetVelocity, maxImpulse)
    }

}