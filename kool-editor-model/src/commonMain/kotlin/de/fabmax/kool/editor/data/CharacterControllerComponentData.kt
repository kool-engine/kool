package de.fabmax.kool.editor.data

import de.fabmax.kool.physics.character.NonWalkableMode
import kotlinx.serialization.Serializable

@Serializable
data class CharacterControllerComponentData(
    val shape: ShapeData.Capsule = ShapeData.Capsule(0.4, 1.0),
    val crouchSpeed: Float = 1f,
    val walkSpeed: Float = 2f,
    val runSpeed: Float = 7f,
    val jumpSpeed: Float = 6f,
    val maxFallSpeed: Float = 30f,
    val slopeLimit: Float = 45f,
    val nonWalkableMode: NonWalkableMode = NonWalkableMode.PREVENT_CLIMBING,
    val pushForce: Float = 10f,
    val downForce: Float = 1f,

    val enableDefaultControls: Boolean = true,
    val runByDefault: Boolean = true,
) : ComponentData
