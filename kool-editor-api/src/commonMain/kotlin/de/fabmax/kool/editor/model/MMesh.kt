package de.fabmax.kool.editor.model

import kotlinx.serialization.Serializable

@Serializable
data class MProceduralMesh(
    val commonProps: MCommonNodeProperties,
    val generatorClass: String
)
