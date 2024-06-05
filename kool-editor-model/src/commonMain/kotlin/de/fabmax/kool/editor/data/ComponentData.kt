package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ComponentData

@Serializable
class ModelComponentData(var modelPath: String, var sceneIndex: Int = 0, var animationIndex: Int = -1) : ComponentData

@Serializable
class TransformComponentData(
    var transform: TransformData = TransformData.IDENTITY,
    var isFixedScaleRatio: Boolean = true
) : ComponentData