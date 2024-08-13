package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.joints.D6JointMotion
import kotlinx.serialization.Serializable

@Serializable
data class JointComponentData(
    val bodyA: EntityId = EntityId.NULL,
    val bodyB: EntityId = EntityId.NULL,
    val isCollisionEnabled: Boolean = false,
    val isBreakable: Boolean = false,
    val breakForce: Float = 0f,
    val breakTorque: Float = 0f,
    val jointData: JointData = JointData.Fixed
) : ComponentData

@Serializable
sealed interface JointData {
    @Serializable
    data object Fixed : JointData

    @Serializable
    data class Distance(
        val minDistance: Float = 0f,
        val maxDistance: Float = 1f,
    ) : JointData

    @Serializable
    data class Revolute(
        val isMotor: Boolean = false,
        val motorSpeed: Float = 0f,
        val motorTorque: Float = 0f,
        val limit: LimitData? = null,
    ) : JointData

    @Serializable
    data class Spherical(
        val limit: LimitData? = null,
    ) : JointData

    @Serializable
    data class Prismatic(
        val limit: LimitData? = null,
    ) : JointData

    @Serializable
    data class D6(
        val linearMotionX: D6JointMotion = D6JointMotion.Locked,
        val linearMotionY: D6JointMotion = D6JointMotion.Locked,
        val linearMotionZ: D6JointMotion = D6JointMotion.Locked,
        val angularMotionX: D6JointMotion = D6JointMotion.Locked,
        val angularMotionY: D6JointMotion = D6JointMotion.Locked,
        val angularMotionZ: D6JointMotion = D6JointMotion.Locked,

        val linearLimitX: LimitData? = null,
        val linearLimitY: LimitData? = null,
        val linearLimitZ: LimitData? = null,
        val angularLimitX: LimitData? = null,
        val angularLimitY: LimitData? = null,
        val angularLimitZ: LimitData? = null,

        val linearMotorX: MotorData? = null,
        val linearMotorY: MotorData? = null,
        val linearMotorZ: MotorData? = null,
        val angularMotorX: MotorData? = null,
        val angularMotorY: MotorData? = null,
        val angularMotorZ: MotorData? = null,
    ) : JointData
}

@Serializable
data class LimitData(
    val limit1: Float,
    val limit2: Float,
    val stiffness: Float = 0f,
    val damping: Float = 0f,
    val restitution: Float = 0f,
    val bounceThreshold: Float = 0f
)

@Serializable
data class MotorData(
    val targetVelocity: Float,
    val forceLimit: Float,
    val stiffness: Float = 0f,
    val damping: Float = 0f,
    val isAcceleration: Boolean = true
)
