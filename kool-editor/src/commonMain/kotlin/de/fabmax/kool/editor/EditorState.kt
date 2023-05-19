package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.MNode
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object EditorState {

    val projectModel: MProject = loadProjectModel()
    val loadedApp = MutableStateValue<EditorAwareApp?>(null)

    val selectedScene = MutableStateValue<MScene?>(null)
    val selectedNode = mutableStateOf<MNode?>(null)

    private val uniqueNameIds = mutableMapOf<String, Int>()

    private fun loadProjectModel(): MProject {
        val projFile = File(KoolEditor.APP_PROJECT_MODEL_PATH)
        return try {
            val projData = Json.decodeFromString<ProjectData>(projFile.readText())
            MProject(projData)
        } catch (e: Exception) {
            logW { "Project not found at ${projFile.absolutePath}, creating new empty project" }
            newProject()
        }
    }

    private fun newProject() = MProject(
        ProjectData(KoolEditor.APP_PROJECT_MAIN_CLASS).apply {
            val sceneId = nextId++
            val boxId = nextId++
            scenes += SceneData(sceneId).apply {
                name = "New Scene"
                sceneNodes += SceneNodeData(nodeId = boxId).apply {
                    parentId = sceneId
                    name = "Default Cube"
                    components += MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0)))
                }
                rootNodeIds += boxId
            }
        }
    )

    fun saveProject() {
        val modelPath = File(KoolEditor.APP_PROJECT_MODEL_PATH)
        modelPath.parentFile.mkdirs()

        //modelPath.writeText(Json.encodeToString(projectModel))
        val json = Json {
            prettyPrint = true
        }
        modelPath.writeText(json.encodeToString(projectModel.projectData))

        logD { "Saved project to ${modelPath.absolutePath}" }
    }
}