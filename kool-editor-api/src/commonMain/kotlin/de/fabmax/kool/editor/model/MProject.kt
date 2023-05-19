package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.data.ProjectData
import de.fabmax.kool.editor.data.SceneData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MProject(val projectData: ProjectData) {

    private val created: MutableMap<SceneData, MScene> = mutableMapOf()

    suspend fun create() {
        created.values.forEach { it.disposeCreatedScene() }
        created.keys.retainAll(projectData.scenes.toSet())

        projectData.scenes.forEach { sceneData ->
            val sceneModel = created.getOrPut(sceneData) { MScene(sceneData, this) }
            sceneModel.sceneData = sceneData
            sceneModel.create()
        }
    }

    fun getCreatedScenes(): List<MScene> = created.values.toList()

    fun nextId(): Long {
        return projectData.nextId++
    }

    companion object {
        suspend fun loadFromAssets(): MProject? {
            return try {
                val json = Assets.loadBlobAsset("kool-project.json").toArray().decodeToString()
                val projectData: ProjectData = Json.decodeFromString(json)
                MProject(projectData)
            } catch (e: Exception) {
                null
            }
        }
    }
}
