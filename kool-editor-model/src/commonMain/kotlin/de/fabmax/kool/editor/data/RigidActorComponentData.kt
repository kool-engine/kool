package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class RigidActorComponentData(var properties: RigidActorProperties = RigidActorProperties()) : ComponentData

@Serializable
data class RigidActorProperties(
    val type: RigidActorType = RigidActorType.DYNAMIC,
    val shapes: List<ShapeData> = emptyList(),
    val mass: Float = 1f,
)

enum class RigidActorType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}
