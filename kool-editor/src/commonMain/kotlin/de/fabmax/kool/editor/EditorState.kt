package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.MMesh
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
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

    val selectedScene = MutableStateValue(projectModel.scenes.getOrNull(0))
    val selectedObject = mutableStateOf<MSceneNode?>(null)

    private val uniqueNameIds = mutableMapOf<String, Int>()

    private fun loadProjectModel(): MProject {
        val projFile = File(KoolEditor.APP_PROJECT_MODEL_PATH)
        return try {
            Json.decodeFromString<MProject>(projFile.readText())
        } catch (e: Exception) {
            logW { "Project not found at ${projFile.absolutePath}, creating new empty project" }
            newProject()
        }
    }

    private fun newProject() = MProject().apply {
        mainClass = KoolEditor.APP_PROJECT_MAIN_CLASS
        scenes += MScene(nodeId = 1L).apply {
            name = "New Scene"
            sceneNodes += MMesh(nodeId = 2L).apply {
                parentId = 1L
                name = "Default Cube"
            }
            childIds += 2L
        }
    }

    fun saveProject() {
        val modelPath = File(KoolEditor.APP_PROJECT_MODEL_PATH)
        modelPath.parentFile.mkdirs()

        //modelPath.writeText(Json.encodeToString(projectModel))
        val json = Json {
            prettyPrint = true
        }
        modelPath.writeText(json.encodeToString(projectModel))

        logD { "Saved project to ${modelPath.absolutePath}" }
    }
}