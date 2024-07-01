package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ComponentData

@Deprecated("Replaced by MeshComponentData with ShapeData.Model")
@Serializable
data class ModelComponentData(
    val modelPath: String,
    val sceneIndex: Int = 0,
    val animationIndex: Int = -1
) : ComponentData

@Serializable
data class TransformComponentData(
    val transform: TransformData = TransformData.IDENTITY,
    val isFixedScaleRatio: Boolean = true
) : ComponentData