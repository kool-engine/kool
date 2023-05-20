package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object EditorState {

    val projectModel: EditorProject = loadProjectModel()
    val loadedApp = MutableStateValue<EditorAwareApp?>(null)

    val selectedScene = MutableStateValue<SceneModel?>(null)
    val selectedNode = mutableStateOf<EditorNodeModel?>(null)

    private val uniqueNameIds = mutableMapOf<String, Int>()

    private fun loadProjectModel(): EditorProject {
        val projFile = File(KoolEditor.APP_PROJECT_MODEL_PATH)
        return try {
            val projData = Json.decodeFromString<ProjectData>(projFile.readText())
            EditorProject(projData)
        } catch (e: Exception) {
            logW { "Project not found at ${projFile.absolutePath}, creating new empty project" }
            newProject()
        }
    }

    private fun newProject() = EditorProject(
        ProjectData(KoolEditor.APP_PROJECT_MAIN_CLASS).apply {
            val sceneId = nextId++
            val boxId = nextId++
            sceneNodeIds += sceneId
            sceneNodes += SceneNodeData("New Scene", sceneId).apply {
                childNodeIds += boxId
                components += SceneBackgroundComponentData(SceneBackgroundData.SingleColor(MdColor.GREY tone 900))
            }
            sceneNodes += SceneNodeData("Default Cube", boxId).apply {
                components += MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0)))
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