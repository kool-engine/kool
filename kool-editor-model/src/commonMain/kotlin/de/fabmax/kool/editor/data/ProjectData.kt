package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ProjectData {
    val entities: MutableSet<GameEntityData> = mutableSetOf()
    val materials: MutableList<MaterialData> = mutableListOf()
}