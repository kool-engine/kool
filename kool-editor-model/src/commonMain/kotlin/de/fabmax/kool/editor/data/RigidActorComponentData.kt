package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.character.HitActorBehavior
import kotlinx.serialization.Serializable

@Serializable
class RigidActorComponentData(var properties: RigidActorProperties = RigidActorProperties()) : ComponentData

@Serializable
data class RigidActorProperties(
    val type: RigidActorType = RigidActorType.STATIC,
    val shapes: List<ShapeData> = emptyList(),
    val mass: Double = 1.0,
    val characterControllerHitBehavior: HitActorBehavior = HitActorBehavior.SLIDE,
)

enum class RigidActorType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}
