package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.FLT_EPSILON
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor

expect class RevoluteJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f) : CommonRevoluteJoint, Joint {
    val bodyA: RigidActor
    val bodyB: RigidActor

    val frameA: Mat4f
    val frameB: Mat4f

    constructor(bodyA: RigidActor, bodyB: RigidActor, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f)

    fun disableAngularMotor()

    fun enableAngularMotor(angularVelocity: Float, forceLimit: Float)
}

abstract class CommonRevoluteJoint {
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
