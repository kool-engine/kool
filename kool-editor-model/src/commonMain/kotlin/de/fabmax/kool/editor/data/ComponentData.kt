package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ComponentData

@Serializable
data class ModelComponentData(val modelPath: String, val sceneIndex: Int = 0, val animationIndex: Int = -1) : ComponentData

@Serializable
data class TransformComponentData(
    var transform: TransformData = TransformData.IDENTITY,
    var isFixedScaleRatio: Boolean = true
) : ComponentData