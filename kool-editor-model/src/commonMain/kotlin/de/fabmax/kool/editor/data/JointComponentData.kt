package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.joints.D6JointMotion
import kotlinx.serialization.Serializable

@Serializable
data class JointComponentData(
    val bodyA: EntityId = EntityId.NULL,
    val bodyB: EntityId = EntityId.NULL,
    val jointData: JointData = JointData.Fixed()
) : ComponentData

@Serializable
sealed interface JointData {
    @Serializable
    data class Fixed(
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class Distance(
        val minDistance: Float = 0f,
        val maxDistance: Float = 1f,
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class Revolute(
        val isMotor: Boolean = false,
        val motorSpeed: Float = 0f,
        val motorTorque: Float = 0f,
        val limit: LimitData? = null,
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class Spherical(
        val limit: LimitData? = null,
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class Prismatic(
        val limit: LimitData? = null,
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class D6(
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f,

        val motionX: D6JointMotion = D6JointMotion.Locked,
        val motionY: D6JointMotion = D6JointMotion.Locked,
        val motionZ: D6JointMotion = D6JointMotion.Locked,
        val motionTwist: D6JointMotion = D6JointMotion.Locked,
        val motionSwingY: D6JointMotion = D6JointMotion.Locked,
        val motionSwingZ: D6JointMotion = D6JointMotion.Locked,

        val limitX: LimitData? = null,
        val limitY: LimitData? = null,
        val limitZ: LimitData? = null,
        val limitTwist: LimitData? = null,
        val limitSwingY: LimitData? = null,
        val limitSwingZ: LimitData? = null,

        val motorX: MotorData? = null,
        val motorY: MotorData? = null,
        val motorZ: MotorData? = null,
        val motorTwist: MotorData? = null,
        val motorSwingY: MotorData? = null,
        val motorSwingZ: MotorData? = null,
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
    val isAcceleration: Boolean = false
)
