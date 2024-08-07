package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.character.HitActorBehavior
import kotlinx.serialization.Serializable

@Serializable
data class RigidActorComponentData(
    val actorType: RigidActorType = RigidActorType.STATIC,
    val shapes: List<ShapeData> = emptyList(),
    val mass: Double = 1.0,
    val isTrigger: Boolean = false,
    val characterControllerHitBehavior: HitActorBehavior = HitActorBehavior.SLIDE,
    val materialId: EntityId = EntityId.NULL
) : ComponentData

enum class RigidActorType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}
