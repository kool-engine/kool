package de.fabmax.kool.editor

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.copy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object EditorState {

    val projectModel = PlatformFunctions.loadProjectModel(KoolEditor.instance.paths.projectFile) ?: newProject()

    val loadedApp = mutableStateOf<LoadedApp?>(null)

    val activeScene = mutableStateOf<SceneModel?>(null)

    val selection = mutableStateListOf<NodeModel>()
    val onSelectionChanged = mutableListOf<(List<NodeModel>) -> Unit>()

    val transformMode = mutableStateOf(TransformOrientation.GLOBAL)

    @OptIn(ExperimentalSerializationApi::class)
    val jsonCodec = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    fun selectSingle(selectModel: NodeModel?, expandIfShiftIsDown: Boolean = true, toggleSelect: Boolean = true) {
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

    fun expandSelection(addModels: List<NodeModel>) = setSelection(selection.toSet() + addModels.toSet())

    fun reduceSelection(removeModels: List<NodeModel>) = setSelection(selection.toSet() - removeModels.toSet())

    fun setSelection(selectModels: Collection<NodeModel>) {
        if (selection != selectModels) {
            selection.atomic {
                clear()
                addAll(selectModels)
            }
            onSelectionChanged.forEach { it(selection) }
        }
    }

    fun getSelectedNodes(filter: (NodeModel) -> Boolean = { true }): List<NodeModel> {
        return selection.copy().filter(filter)
    }

    fun getSelectedSceneNodes(filter: (SceneNodeModel) -> Boolean = { true }): List<SceneNodeModel> {
        return selection.copy().filterIsInstance<SceneNodeModel>().filter(filter)
    }

    fun newProject() = EditorProject(
        ProjectData().apply {
            val sceneId = nextId++
            val camId = nextId++
            val boxId = nextId++
            val lightId = nextId++
            sceneNodeIds += sceneId
            sceneNodes += SceneNodeData("New Scene", sceneId).apply {
                childNodeIds += listOf(camId, boxId, lightId)
                components += ScenePropertiesComponentData(cameraNodeId = camId)
                components += SceneBackgroundComponentData(
                    SceneBackgroundData.SingleColor(ColorData(MdColor.GREY toneLin 900))
                )
            }
            sceneNodes += SceneNodeData("Camera", camId).apply {
                components += CameraComponentData(CameraTypeData.Perspective())
                components += TransformComponentData(TransformData(
                    Mat4d()
                        .translate(0f, 2.5f, 5f)
                        .rotate(-30f, Vec3f.X_AXIS)
                ))
            }
            sceneNodes += SceneNodeData("Default Cube", boxId).apply {
                components += MeshComponentData(MeshShapeData.Box(Vec3Data(1.0, 1.0, 1.0)))
            }
            sceneNodes += SceneNodeData("Directional Light", lightId).apply {
                components += DiscreteLightComponentData(LightTypeData.Directional())
                components += TransformComponentData(TransformData(
                    Mat4d()
                        .translate(3f, 3f, 3f)
                        .mul(Mat4d().setRotate(EditorDefaults.DEFAULT_LIGHT_ROTATION))
                ))
            }
        }
    )

    fun saveProject() = PlatformFunctions.saveProjectModel(KoolEditor.instance.paths.projectFile)

    enum class TransformOrientation(val label: String) {
        LOCAL("Local"),
        PARENT("Parent"),
        GLOBAL("Global")
    }
}