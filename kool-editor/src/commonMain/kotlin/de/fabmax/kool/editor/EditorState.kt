package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.*
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

    val projectModel: MProject = loadProjectModel()
    val loadedApp = MutableStateValue<EditorAwareApp?>(null)

    val selectedScene = MutableStateValue(projectModel.scenes.getOrNull(0))
    val selectedObject = mutableStateOf<MSceneNode<*>?>(null)

    private val uniqueNameIds = mutableMapOf<String, Int>()

    private fun loadProjectModel(): MProject {
        val projFile = File(KoolEditor.PROJECT_MODEL_PATH)
        return try {
            Json.decodeFromString<MProject>(projFile.readText())
        } catch (e: Exception) {
            logW { "Project not found at ${projFile.absolutePath}, creating new empty project" }
            newProject()
        }
    }

    private fun newProject(): MProject {
        val testScene = MScene(
            nodeProperties = MCommonNodeProperties(
                id = 1L,
                name = "New Scene",
                transform = MTransform.IDENTITY,
                children = mutableSetOf(2L)
            ),
            clearColor = MColor(MdColor.GREY tone 900),
            meshes = mutableMapOf(
                2L to MMesh(
                    MCommonNodeProperties(
                        id = 2L,
                        name = "Default Cube",
                        transform = MTransform.IDENTITY
                    ),
                    meshType = MMeshType.Box(MVec3(1.0, 1.0, 1.0))
                )
            )
        )

        val projectModel = MProject(
            mainClass = KoolEditor.PROJECT_MAIN_CLASS,
            scenes = listOf(testScene)
        )
        return projectModel
    }

    fun saveProject() {
        val modelPath = File(KoolEditor.PROJECT_MODEL_PATH)
        modelPath.parentFile.mkdirs()

        //modelPath.writeText(Json.encodeToString(projectModel))
        val json = Json {
            prettyPrint = true
        }
        modelPath.writeText(json.encodeToString(projectModel))

        logD { "Saved project to ${modelPath.absolutePath}" }
    }
}