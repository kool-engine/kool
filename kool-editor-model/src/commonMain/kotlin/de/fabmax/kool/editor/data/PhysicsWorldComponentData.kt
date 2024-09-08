package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
data class PhysicsWorldComponentData(
    val gravity: Vec3Data = Vec3Data(0.0, -9.81, 0.0),
    val isContinuousCollisionDetection: Boolean = true,
    val physicsRange: Vec3Data = Vec3Data(0.0, 0.0, 0.0),
    val materials: List<PhysicsMaterialData> = emptyList()
) : ComponentData

@Serializable
data class PhysicsMaterialData(
    val id: EntityId,
    val name: String,
    val staticFriction: Float = 0.5f,
    val dynamicFriction: Float = 0.5f,
    val restitution: Float = 0.2f
)
