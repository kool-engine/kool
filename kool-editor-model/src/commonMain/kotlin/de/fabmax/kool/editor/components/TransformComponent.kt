package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node

class TransformComponent(nodeModel: SceneNodeModel, override val componentData: TransformComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<TransformComponentData>
{

    val onTransformEdited = mutableListOf<(TransformComponent) -> Unit>()

    val transformState = mutableStateOf(componentData.transform).onChange {
        if (AppState.isEditMode) {
            componentData.transform = it
        }
        if (nodeModel.isCreated) {
            it.toTransform(nodeModel.drawNode.transform)
            nodeModel.drawNode.updateModelMat()
        }
        onTransformEdited.forEach { it(this) }
    }

    val isFixedScaleRatio = mutableStateOf(componentData.isFixedScaleRatio).onChange {
        if (AppState.isEditMode) {
            componentData.isFixedScaleRatio = it
        }
    }

    init {
        componentOrder = COMPONENT_ORDER_EARLY
    }

    override suspend fun createComponent() {
        super.createComponent()
        transformState.set(componentData.transform)
    }

    fun applyTransformTo(drawNode: Node) {
        componentData.transform.toTransform(drawNode.transform)
        drawNode.updateModelMat()
    }
}