package de.fabmax.kool.editor.data

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
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class Spherical(
        val isLimited: Boolean = false,
        val limitAngleY: Float = 0f,
        val limitAngleZ: Float = 0f,
        val stiffness: Float = 1f,
        val damping: Float = 1f,
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData

    @Serializable
    data class Prismatic(
        val isLimited: Boolean = false,
        val lowerLimit: Float = 0f,
        val upperLimit: Float = 1f,
        val stiffness: Float = 1f,
        val damping: Float = 1f,
        val isBreakable: Boolean = false,
        val breakForce: Float = 0f,
        val breakTorque: Float = 0f
    ) : JointData
}
