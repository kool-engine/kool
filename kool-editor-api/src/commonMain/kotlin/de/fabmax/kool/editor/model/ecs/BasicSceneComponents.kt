package de.fabmax.kool.editor.model.ecs

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color

class SceneBackgroundComponent(override val componentData: SceneBackgroundComponentData)
    : EditorDataComponent<SceneBackgroundComponentData> {

    constructor(color: Color) : this(SceneBackgroundComponentData(SceneBackgroundData.SingleColor(color)))

    val backgroundState = mutableStateOf(componentData.sceneBackground).onChange { componentData.sceneBackground = it }

    var loadedEnvironmentMaps: EnvironmentMaps? = null
}

class TransformComponent(override val componentData: TransformComponentData) : EditorDataComponent<TransformComponentData> {
    val transformState = mutableStateOf(componentData.transform).onChange { componentData.transform = it }
}

class MeshComponent(override val componentData: MeshComponentData) : EditorDataComponent<MeshComponentData> {
    val shapesState = MutableStateList(componentData.shapes)
}

class ModelComponent(override val componentData: ModelComponentData) : EditorDataComponent<ModelComponentData> {
    val modelPathState = mutableStateOf(componentData.modelPath).onChange { componentData.modelPath = it }
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
