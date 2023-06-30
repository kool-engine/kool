package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node

class TransformComponent(override val componentData: TransformComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<TransformComponentData>
{

    val transformState = mutableStateOf(componentData.transform).onChange {
        if (AppState.isEditMode) {
            componentData.transform = it
        }
        if (isCreated) {
            it.toTransform(sceneNode.drawNode.transform)
        }
    }

    private val tmpMat = Mat4d()

    init {
        componentOrder = COMPONENT_ORDER_EARLY
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
        transformState.set(componentData.transform)
    }

    fun applyTransformTo(drawNode: Node) {
        componentData.transform.toTransform(drawNode.transform)
    }

    fun getMatrix(): Mat4d = transformState.value.toMat4d(tmpMat)
    fun setMatrix(mat: Mat4d) = transformState.set(TransformData((mat)))
}