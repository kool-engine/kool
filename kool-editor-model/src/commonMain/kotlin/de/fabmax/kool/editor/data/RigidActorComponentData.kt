package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.character.HitActorBehavior
import kotlinx.serialization.Serializable

@Serializable
data class RigidActorComponentData(
    val type: RigidActorType = RigidActorType.STATIC,
    val shapes: List<ShapeData> = emptyList(),
    val mass: Double = 1.0,
    val isTrigger: Boolean = false,
    val characterControllerHitBehavior: HitActorBehavior = HitActorBehavior.SLIDE,
) : ComponentData

enum class RigidActorType {
    DYNAMIC,
    KINEMATIC,
    STATIC
}
