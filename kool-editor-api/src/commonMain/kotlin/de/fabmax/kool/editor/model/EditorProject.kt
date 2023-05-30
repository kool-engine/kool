package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.ProjectData
import de.fabmax.kool.editor.data.SceneNodeData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class EditorProject(val projectData: ProjectData) {

    val entities = mutableListOf<EditorNodeModel>()

    private val _sceneNodeData = projectData.sceneNodes.associateBy { it.nodeId }.toMutableMap()
    val sceneNodeData: Map<Long, SceneNodeData>
        get() = _sceneNodeData

    private val _materials = mutableMapOf<Long, MaterialModel>()
    val materials: Map<Long, MaterialModel>
        get() = _materials

    private val created: MutableMap<Long, SceneModel> = mutableMapOf()

    suspend fun create() {
        projectData.materials.forEach { (id, data) ->
            _materials[id] = MaterialModel(data.copy(id = id), this)
        }
        projectData.sceneNodeIds.forEach { sceneNodeId ->
            val sceneData = sceneNodeData[sceneNodeId]
            if (sceneData != null) {
                val sceneModel = created.getOrPut(sceneNodeId) { SceneModel(sceneData, this) }
                sceneModel.createScene()
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

    fun createNewMaterial(): MaterialModel {
        val id = nextId()
        val newMat = MaterialData(id, "Material-$id")
        val matModel = MaterialModel(newMat, this)
        _materials[id] = matModel
        projectData.materials[id] = newMat
        return matModel
    }

    inline fun <reified T: EditorModelComponent> getAllComponents(): List<T> {
        return entities.flatMap { it.components.filterIsInstance<T>() }
    }

    inline fun <reified T: EditorModelComponent> getComponentsFromEntities(predicate: (EditorNodeModel) -> Boolean): List<T> {
        return entities.filter(predicate).flatMap { it.components.filterIsInstance<T>() }
    }

    inline fun <reified T: EditorModelComponent> getComponentsInScene(sceneModel: SceneModel): List<T> {
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
