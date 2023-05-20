package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ComponentData

@Serializable
class ModelComponentData(var modelPath: String) : ComponentData

@Serializable
class TransformComponentData(var transform: TransformData) : ComponentData