package de.fabmax.kool.editor.data

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

    val enableDefaultControls: Boolean = true,
    val runByDefault: Boolean = true,
)
