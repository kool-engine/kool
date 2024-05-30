package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.character.HitActorBehavior
import kotlinx.serialization.Serializable

@Serializable
class CharacterControllerComponentData(
    var properties: CharacterControllerComponentProperties = CharacterControllerComponentProperties(),
) : ComponentData

@Serializable
data class CharacterControllerComponentProperties(
    val shape: ShapeData.Capsule = ShapeData.Capsule(0.4, 1.0),
    val crouchSpeed: Double = 1.0,
    val walkSpeed: Double = 2.0,
    val runSpeed: Double = 7.0,
    val jumpSpeed: Double = 6.0,
    val slopeLimit: Double = 50.0,
    val contactOffset: Double = 0.1,
    val pushForce: Double = 10.0,
    val hitActorMode: HitActorBehavior = HitActorBehavior.DEFAULT,

    val enableDefaultControls: Boolean = true,
    val runByDefault: Boolean = true,
)
