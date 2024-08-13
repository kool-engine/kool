package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor

expect fun D6Joint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): D6Joint

interface D6Joint : Joint {
    var linearMotionX: D6JointMotion
    var linearMotionY: D6JointMotion
    var linearMotionZ: D6JointMotion
    var angularMotionX: D6JointMotion
    var angularMotionY: D6JointMotion
    var angularMotionZ: D6JointMotion

    fun enableDistanceLimit(extend: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)

    fun enableLinearLimitX(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableLinearLimitY(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableLinearLimitZ(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableAngularLimitX(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableAngularLimitY(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableAngularLimitZ(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)

    fun disableDistanceLimit()
    fun disableLinearLimitX()
    fun disableLinearLimitY()
    fun disableLinearLimitZ()
    fun disableAngularLimitX()
    fun disableAngularLimitY()
    fun disableAngularLimitZ()

    fun setDriveTargetPose(target: PoseF)

    fun enableLinearDriveX(drive: D6JointDrive)
    fun enableLinearDriveY(drive: D6JointDrive)
    fun enableLinearDriveZ(drive: D6JointDrive)
    fun enableAngularDriveX(drive: D6JointDrive)
    fun enableAngularDriveY(drive: D6JointDrive)
    fun enableAngularDriveZ(drive: D6JointDrive)

    fun disableLinearDriveX()
    fun disableLinearDriveY()
    fun disableLinearDriveZ()
    fun disableAngularDriveX()
    fun disableAngularDriveY()
    fun disableAngularDriveZ()
}

enum class D6JointMotion {
    Free,
    Limited,
    Locked
}

data class D6JointDrive(
    val targetVelocity: Float,
    val damping: Float,
    val stiffness: Float,
    val forceLimit: Float,
    val isAcceleration: Boolean = true
) {
    companion object {
        fun velocityDrive(targetVelocity: Float, damping: Float = 1f) = D6JointDrive(
            targetVelocity = targetVelocity,
            damping = damping,
            stiffness = 0f,
            forceLimit = Float.MAX_VALUE,
            isAcceleration = true
        )

        fun positionalDrive(targetVelocity: Float, stiffness: Float = 1f, damping: Float = 1f) = D6JointDrive(
            targetVelocity = targetVelocity,
            damping = damping,
            stiffness = stiffness,
            forceLimit = Float.MAX_VALUE,
            isAcceleration = true
        )
    }
}
