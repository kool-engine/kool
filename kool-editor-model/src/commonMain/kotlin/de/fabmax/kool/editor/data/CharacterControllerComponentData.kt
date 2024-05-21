package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class CharacterControllerComponentData(var properties: CharacterControllerProperties = CharacterControllerProperties()) : ComponentData

@Serializable
data class CharacterControllerProperties(
    val shape: ShapeData.Capsule = ShapeData.Capsule(0.4, 1.0)
)
