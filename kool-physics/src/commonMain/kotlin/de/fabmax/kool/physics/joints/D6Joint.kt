package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor

expect fun D6Joint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): D6Joint

interface D6Joint : Joint {
    var motionX: D6JointMotion
    var motionY: D6JointMotion
    var motionZ: D6JointMotion
    var motionTwist: D6JointMotion
    var motionSwing1: D6JointMotion
    var motionSwing2: D6JointMotion

    fun enableDistanceLimit(extend: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableLinearLimitX(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableLinearLimitY(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableLinearLimitZ(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableTwistLimit(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)
    fun enableSwingLimit(yLimit: AngleF, zLimit: AngleF, limitBehavior: LimitBehavior = LimitBehavior.HARD_LIMIT)

    fun disableDistanceLimit()
    fun disableLinearLimitX()
    fun disableLinearLimitY()
    fun disableLinearLimitZ()
    fun disableTwistLimit()
    fun disableSwingLimit()
}

enum class D6JointMotion {
    Free,
    Limited,
    Locked
}
