package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.FLT_EPSILON
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBody
import physx.*

@Suppress("CanBeParameter")
actual class RevoluteJoint actual constructor(actual val bodyA: RigidBody, actual val bodyB: RigidBody,
                                              frameA: Mat4f, frameB: Mat4f) : Joint {


    actual val frameA = Mat4f().set(frameA)
    actual val frameB = Mat4f().set(frameB)

    actual constructor(bodyA: RigidBody, bodyB: RigidBody,
                       pivotA: Vec3f, pivotB: Vec3f,
                       axisA: Vec3f, axisB: Vec3f)
            : this(bodyA, bodyB, computeFrame(pivotA, axisA), computeFrame(pivotB, axisB))

    override val pxJoint: PxRevoluteJoint

    init {
        Physics.checkIsLoaded()

        pxJoint = PhysX.Px.RevoluteJointCreate(PhysX.physics, bodyA.pxActor, frameA.toPxTransform(), bodyB.pxActor, frameB.toPxTransform())
    }

    actual fun setAngleLimit(lowerLimit: Float, upperLimit: Float) {
        //btConstraint.setLimit(lowerLimit.toRad(), upperLimit.toRad(), 0.9f, 0.3f, 1.0f)
    }

    actual fun clearAngleLimit() {
        //btConstraint.setLimit(-1e30f, 1e30f, 0.9f, 0.3f, 1.0f)
    }

    actual fun disableAngularMotor() {
        pxJoint.setDriveVelocity(0f, true)
        pxJoint.setDriveForceLimit(0f)

        val flags = pxJoint.getRevoluteJointFlags()
        flags.clear(PhysX.PxRevoluteJointFlag.eDRIVE_ENABLED)
        pxJoint.setRevoluteJointFlags(flags)
    }

    actual fun enableAngularMotor(targetVelocity: Float, maxImpulse: Float) {
        pxJoint.setDriveVelocity(targetVelocity, true)
        pxJoint.setDriveForceLimit(maxImpulse)

        val flags = pxJoint.getRevoluteJointFlags()
        flags.set(PhysX.PxRevoluteJointFlag.eDRIVE_ENABLED)
        pxJoint.setRevoluteJointFlags(flags)
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