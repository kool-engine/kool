package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

fun MeshComponentData(shape: ShapeData): MeshComponentData = MeshComponentData(listOf(shape))

@Serializable
data class MeshComponentData(val shapes: List<ShapeData>) : ComponentData
