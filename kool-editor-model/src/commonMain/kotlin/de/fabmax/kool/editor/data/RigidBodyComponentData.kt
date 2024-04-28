package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class RigidBodyComponentData(var settings: RigidBodySettings = RigidBodySettings()) : ComponentData

@Serializable
data class RigidBodySettings(
    val bodyType: RigidBodyType = RigidBodyType.DYNAMIC,
    val mass: Float = 1f,
)

enum class RigidBodyType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}
