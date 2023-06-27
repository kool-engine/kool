package de.fabmax.kool.editor

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.math.Mat4d
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
    val loadedApp = MutableStateValue<LoadedApp?>(null)

    val selectedScene = MutableStateValue<SceneModel?>(null)
    val selectedNode = mutableStateOf<EditorNodeModel?>(null)

    private val uniqueNameIds = mutableMapOf<String, Int>()
    private val prettyJson = Json { prettyPrint = true }

    private fun loadProjectModel(): EditorProject {
        val projFile = File(KoolEditor.instance.paths.projectFile)
        return try {
            val projData = Json.decodeFromString<ProjectData>(projFile.readText())
            EditorProject(projData)
        } catch (e: Exception) {
            logW { "Project not found at ${projFile.absolutePath}, creating new empty project" }
            newProject()
        }
    }

    private fun newProject() = EditorProject(
        ProjectData(KoolEditor.instance.paths.appMainClass).apply {
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
            sceneNodes += SceneNodeData("Directional Light", boxId).apply {
                components += DiscreteLightComponentData(LightTypeData.Directional())
                components += TransformComponentData(TransformData(Mat4d().setRotate(EditorDefaults.DEFAULT_LIGHT_ROTATION)))
            }
        }
    )

    fun saveProject() {
        val modelPath = File(KoolEditor.instance.paths.projectFile)
        modelPath.parentFile.mkdirs()
        modelPath.writeText(prettyJson.encodeToString(projectModel.projectData))

        logD { "Saved project to ${modelPath.absolutePath}" }
    }
}