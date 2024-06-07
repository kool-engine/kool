package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ProjectData {
    val scenes: MutableList<SceneData> = mutableListOf()
    val materials: MutableList<GameEntityData> = mutableListOf()
}

@Serializable
class SceneData(
    var name: String,
    val entities: MutableList<GameEntityData> = mutableListOf()
)
