package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ProjectData(
    var meta: ProjectMeta,
    val scenes: MutableList<SceneData> = mutableListOf(),
    val materials: MutableList<GameEntityData> = mutableListOf()
) {
    companion object {
        const val MODEL_VERSION = "1.2.0"
    }
}

@Serializable
class SceneData(
    var meta: SceneMeta,
    val entities: MutableList<GameEntityData> = mutableListOf()
)

@Serializable
data class ProjectMeta(val modelVersion: String, val name: String = "kool-editor-project")

@Serializable
data class SceneMeta(val rootId: EntityId, val name: String = "New Scene")
