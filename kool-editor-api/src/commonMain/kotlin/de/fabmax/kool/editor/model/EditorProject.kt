package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.PbrShaderData
import de.fabmax.kool.editor.data.ProjectData
import de.fabmax.kool.editor.data.SceneNodeData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class EditorProject(val projectData: ProjectData) {

    val entities = mutableListOf<EditorNodeModel>()

    private val _sceneNodeData = projectData.sceneNodes.associateBy { it.nodeId }.toMutableMap()
    val sceneNodeData: Map<Long, SceneNodeData>
        get() = _sceneNodeData

    private val _materials = projectData.materials.associateBy { it.id }.toMutableMap()
    val materials: Map<Long, MaterialData>
        get() = _materials

    private val created: MutableMap<Long, SceneModel> = mutableMapOf()

    suspend fun create() {
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

    fun createNewMaterial(): MaterialData {
        val id = nextId()
        val newMat = MaterialData(id, "Material-$id", PbrShaderData())
        _materials[id] = newMat
        projectData.materials += newMat
        return newMat
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
