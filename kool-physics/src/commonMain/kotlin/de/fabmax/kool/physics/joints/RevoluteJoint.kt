package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.RigidActor

expect fun RevoluteJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): RevoluteJoint

expect fun RevoluteJoint(bodyA: RigidActor?, bodyB: RigidActor, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f): RevoluteJoint

interface RevoluteJoint : Joint {
    fun disableAngularMotor()
    fun enableAngularMotor(angularVelocity: Float, forceLimit: Float)

    fun enableLimit(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun disableLimit()

    companion object {
        fun computeFrame(pivot: Vec3f, axis: Vec3f): PoseF {
            val ax1 = MutableVec3f()
            val ax2 = MutableVec3f()
            val dot = axis.dot(Vec3f.X_AXIS)
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
            val rot = MutableMat3f(axis, ax2.norm(), ax1.norm()).getRotation()
            return PoseF(pivot, rot)
        }
    }
}
