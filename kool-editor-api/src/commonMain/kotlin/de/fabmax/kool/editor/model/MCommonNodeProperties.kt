package de.fabmax.kool.editor.model

import kotlinx.serialization.Serializable

@Serializable
data class MCommonNodeProperties(
    val id: Long,
    var name: String,
    var transform: MTransform,
    val children: MutableSet<Long> = mutableSetOf()
)