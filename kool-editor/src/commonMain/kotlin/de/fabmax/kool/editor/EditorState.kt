package de.fabmax.kool.editor

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ui2.mutableStateListOf
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
    val loadedApp = mutableStateOf<LoadedApp?>(null)

    val activeScene = mutableStateOf<SceneModel?>(null)
    val selection = mutableStateListOf<EditorNodeModel>()

    private val prettyJson = Json { prettyPrint = true }

    fun select(nodeModel: EditorNodeModel?, expandIfShiftIsDown: Boolean = true, removeIfSelected: Boolean = true) {
        val nodeModels = mutableListOf<EditorNodeModel>()
        nodeModel?.let { nodeModels += it }
        select(nodeModels, expandIfShiftIsDown, removeIfSelected)
    }

    fun select(nodeModels: List<EditorNodeModel>, expandIfShiftIsDown: Boolean = true, removeIfSelected: Boolean = false) {
        if (!expandIfShiftIsDown || !KeyboardInput.isShiftDown) {
            selection.clear()
        }
        val addToSelection = nodeModels.toMutableSet()
        if (removeIfSelected) {
            val alreadySelected = nodeModels.toMutableSet()
            alreadySelected.retainAll(selection)
            selection -= alreadySelected
            addToSelection -= alreadySelected
        }

        addToSelection -= selection
        selection += addToSelection
    }

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