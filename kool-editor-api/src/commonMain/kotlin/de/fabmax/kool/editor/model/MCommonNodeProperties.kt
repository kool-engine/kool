package de.fabmax.kool.editor.model

import kotlinx.serialization.Serializable

@Serializable
data class MCommonNodeProperties(
    val id: Long,
    val name: String,
    val transform: MTransform,
    val children: MutableSet<Long> = mutableSetOf()
)