package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class RigidBodyComponentData(var properties: RigidBodyProperties = RigidBodyProperties()) : ComponentData

@Serializable
data class RigidBodyProperties(
    val bodyType: RigidBodyType = RigidBodyType.DYNAMIC,
    val mass: Float = 1f,
)

enum class RigidBodyType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}
