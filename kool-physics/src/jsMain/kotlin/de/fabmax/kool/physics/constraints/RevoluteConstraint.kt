package de.fabmax.kool.physics.constraints

import ammo.Ammo
import ammo.btHingeConstraint
import ammo.toBtVector3
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBody

@Suppress("CanBeParameter")
actual class RevoluteConstraint actual constructor(actual val bodyA: RigidBody, actual val bodyB: RigidBody,
                                                   pivotA: Vec3f, pivotB: Vec3f,
                                                   axisA: Vec3f, axisB: Vec3f) : Constraint {

    actual val pivotA = Vec3f(pivotA)
    actual val pivotB = Vec3f(pivotB)
    actual val axisA = Vec3f(axisA)
    actual val axisB = Vec3f(axisB)

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