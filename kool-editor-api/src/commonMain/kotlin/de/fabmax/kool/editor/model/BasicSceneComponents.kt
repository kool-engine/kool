package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.ScriptLoader
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logD

class SceneBackgroundComponent(override val componentData: SceneBackgroundComponentData)
    : EditorDataComponent<SceneBackgroundComponentData> {

    constructor(color: Color) : this(SceneBackgroundComponentData(SceneBackgroundData.SingleColor(color)))

    val backgroundState = mutableStateOf(componentData.sceneBackground).onChange { componentData.sceneBackground = it }

    var loadedEnvironmentMaps: EnvironmentMaps? = null
}

class TransformComponent(override val componentData: TransformComponentData) :
    EditorDataComponent<TransformComponentData> {
    val transformState = mutableStateOf(componentData.transform).onChange {
        componentData.transform = it
        nodeModel?.let { nodeModel -> it.toTransform(nodeModel.node.transform) }
    }
    private var nodeModel: EditorNodeModel? = null
    private val tmpMat = Mat4d()

    override suspend fun onCreate(nodeModel: EditorNodeModel) {
        this.nodeModel = nodeModel
        componentData.transform.toTransform(nodeModel.node.transform)
    }

    fun getMatrix(): Mat4d = componentData.transform.toMat4d(tmpMat)
    fun setMatrix(mat: Mat4d) = transformState.set(TransformData((mat)))
}

class MeshComponent(override val componentData: MeshComponentData) : EditorDataComponent<MeshComponentData> {
    val shapesState = MutableStateList(componentData.shapes)
}

class ModelComponent(override val componentData: ModelComponentData) : EditorDataComponent<ModelComponentData> {
    val modelPathState = mutableStateOf(componentData.modelPath).onChange { componentData.modelPath = it }
}

class ScriptComponent(override val componentData: ScriptComponentData) : EditorDataComponent<ScriptComponentData> {
    val scriptClassNameState = mutableStateOf(componentData.scriptClassName).onChange { componentData.scriptClassName = it }
    val runInEditMode = mutableStateOf(componentData.runInEditMode).onChange { componentData.runInEditMode = it }

    override suspend fun onCreate(nodeModel: EditorNodeModel) {
        logD { "Attaching script ${componentData.scriptClassName} to node ${nodeModel.name}" }
        val script = ScriptLoader.newScriptInstance(componentData.scriptClassName)
        script.init(nodeModel, this)
    }
}

fun interface UpdateSceneBackgroundComponent : EditorModelComponent {
    fun updateBackground(background: SceneBackgroundComponent)

    companion object {
        fun updateBackground(sceneModel: SceneModel) {
            sceneModel.project.getSceneComponents<UpdateSceneBackgroundComponent>(sceneModel).forEach {
                it.updateBackground(sceneModel.sceneBackground)
            }
        }
    }
}
