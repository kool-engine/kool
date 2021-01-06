package de.fabmax.kool.physics.joints

import ammo.Ammo
import ammo.btHingeConstraint
import ammo.toBtTransform
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBody

@Suppress("CanBeParameter")
actual class RevoluteJoint actual constructor(actual val bodyA: RigidBody, actual val bodyB: RigidBody,
                                              frameA: Mat4f, frameB: Mat4f) : Joint {


    actual val frameA: Mat4f
    actual val frameB: Mat4f

    actual constructor(bodyA: RigidBody, bodyB: RigidBody,
                       pivotA: Vec3f, pivotB: Vec3f,
                       axisA: Vec3f, axisB: Vec3f)
            : this(bodyA, bodyB, computeFrame(pivotA, axisA), computeFrame(pivotB, axisB))

    override val btConstraint: btHingeConstraint

    init {
        Physics.checkIsLoaded()

        // Bullet hinge constraint rotates around z-Axis, given frames assume x-axis
        val fA = Mat4f().set(frameA).rotate(90f, Vec3f.Y_AXIS)
        val fB = Mat4f().set(frameB).rotate(90f, Vec3f.Y_AXIS)
        btConstraint = Ammo.btHingeConstraint(bodyA.btRigidBody, bodyB.btRigidBody,
            fA.toBtTransform(), fB.toBtTransform())

        this.frameA = fA.set(frameA)
        this.frameB = fB.set(frameB)
    }

    actual fun setAngleLimit(lowerLimit: Float, upperLimit: Float) {
        btConstraint.setLimit(lowerLimit.toRad(), upperLimit.toRad(), 0.9f, 0.3f, 1.0f)
    }

    actual fun clearAngleLimit() {
        btConstraint.setLimit(-1e30f, 1e30f, 0.9f, 0.3f, 1.0f)
    }

    actual fun disableAngularMotor() {
        btConstraint.enableAngularMotor(false, 0f, 0f)
    }

    actual fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float) {
        btConstraint.enableAngularMotor(true, targetVelocity, maxImpulse)
    }

    companion object {
        fun computeFrame(pivot: Vec3f, axis: Vec3f): Mat4f {
            val ax1 = MutableVec3f()
            val ax2 = MutableVec3f()

            val dot = axis * Vec3f.X_AXIS
            when {
                dot >= 1.0f - FLT_EPSILON -> {
                    ax1.set(Vec3f.Z_AXIS)
                    ax2.set(Vec3f.Y_AXIS)
                }
                dot <= -1.0f + FLT_EPSILON -> {
                    ax1.set(Vec3f.NEG_Z_AXIS)
                    ax2.set(Vec3f.NEG_Y_AXIS)
                }
                else -> {
                    axis.cross(Vec3f.X_AXIS, ax2)
                    axis.cross(ax2, ax1)
                }
            }

            val frame = Mat4f()
            frame.translate(pivot)
            frame.setCol(0, axis, 0f)
            frame.setCol(1, ax2, 0f)
            frame.setCol(2, ax1, 0f)
            return frame
        }
    }
}