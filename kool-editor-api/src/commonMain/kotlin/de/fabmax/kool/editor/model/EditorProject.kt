package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.data.ProjectData
import de.fabmax.kool.editor.data.SceneNodeData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class EditorProject(val projectData: ProjectData) {

    val entities = mutableListOf<EditorNodeModel>()

    private val _sceneNodeData = projectData.sceneNodes.associateBy { it.nodeId }.toMutableMap()
    val sceneNodeData: Map<Long, SceneNodeData>
        get() = _sceneNodeData

    private val created: MutableMap<Long, SceneModel> = mutableMapOf()

    suspend fun create() {
        created.values.forEach { it.disposeCreatedScene() }
        created.keys.retainAll(projectData.sceneNodeIds.toSet())

        projectData.sceneNodeIds.forEach { sceneNodeId ->
            val sceneData = sceneNodeData[sceneNodeId]
            if (sceneData != null) {
                created.getOrPut(sceneNodeId) { SceneModel(sceneData, this) }.create()
            }
        }
    }

    fun getCreatedScenes(): List<SceneModel> = created.values.toList()

    fun nextId(): Long {
        return projectData.nextId++
    }

    fun addSceneNodeData(data: SceneNodeData) {
        projectData.sceneNodes += data
        _sceneNodeData[data.nodeId] = data
    }

    fun removeSceneNodeData(data: SceneNodeData) {
        projectData.sceneNodes -= data
        _sceneNodeData -= data.nodeId
    }

    inline fun <reified T: EditorModelComponent> getComponentsFromEntities(predicate: (EditorNodeModel) -> Boolean): List<T> {
        return entities.filter(predicate).flatMap { it.components }.filterIsInstance<T>()
    }

    inline fun <reified T: EditorModelComponent> getSceneComponents(sceneModel: SceneModel): List<T> {
        return getComponentsFromEntities { it === sceneModel || (it is SceneNodeModel && it.scene === sceneModel) }
    }

    companion object {
        suspend fun loadFromAssets(): EditorProject? {
            return try {
                val json = Assets.loadBlobAsset("kool-project.json").toArray().decodeToString()
                val projectData: ProjectData = Json.decodeFromString(json)
                EditorProject(projectData)
            } catch (e: Exception) {
                null
            }
        }
    }
}
