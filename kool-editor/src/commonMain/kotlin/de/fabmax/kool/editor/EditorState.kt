package de.fabmax.kool.editor

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.copy
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

    fun selectSingle(selectModel: EditorNodeModel?, expandIfShiftIsDown: Boolean = true, toggleSelect: Boolean = true) {
        val selectList = selectModel?.let { listOf(it) } ?: emptyList()

        if (toggleSelect && selectModel in selection) {
            if (expandIfShiftIsDown && KeyboardInput.isShiftDown) {
                reduceSelection(selectList)
            } else {
                clearSelection()
            }
        } else if (expandIfShiftIsDown && KeyboardInput.isShiftDown) {
            expandSelection(selectList)
        } else {
            setSelection(selectList)
        }
    }

    fun clearSelection() = setSelection(emptyList())

    fun expandSelection(addModels: List<EditorNodeModel>) = setSelection(selection.toSet() + addModels.toSet())

    fun reduceSelection(removeModels: List<EditorNodeModel>) = setSelection(selection.toSet() - removeModels.toSet())

    fun setSelection(selectModels: Collection<EditorNodeModel>) {
        selection.atomic {
            clear()
            addAll(selectModels)
        }
    }

    fun getSelectedNodes(filter: (EditorNodeModel) -> Boolean = { true }): List<EditorNodeModel> {
        return selection.copy().filter(filter)
    }

    fun getSelectedSceneNodes(filter: (SceneNodeModel) -> Boolean = { true }): List<SceneNodeModel> {
        return selection.copy().filterIsInstance<SceneNodeModel>().filter(filter)
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
                components += SceneBackgroundComponentData(
                    SceneBackgroundData.SingleColor(ColorData(MdColor.GREY toneLin 900))
                )
            }
            sceneNodes += SceneNodeData("Default Cube", boxId).apply {
                components += MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0)))
            }
            sceneNodes += SceneNodeData("Directional Light", boxId).apply {
                components += DiscreteLightComponentData(LightTypeData.Directional())
                components += TransformComponentData(TransformData(
                    Mat4d()
                        .translate(10f, 10f, 10f)
                        .mul(Mat4d().setRotate(EditorDefaults.DEFAULT_LIGHT_ROTATION))
                ))
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