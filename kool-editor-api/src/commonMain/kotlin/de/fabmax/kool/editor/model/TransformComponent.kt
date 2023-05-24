package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ui2.mutableStateOf

class TransformComponent(override val componentData: TransformComponentData) :
    EditorDataComponent<TransformComponentData> {

    val transformState = mutableStateOf(componentData.transform).onChange {
        componentData.transform = it
        nodeModel?.let { nodeModel -> it.toTransform(nodeModel.node.transform) }
    }

    private var nodeModel: EditorNodeModel? = null
    private val tmpMat = Mat4d()

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        this.nodeModel = nodeModel
    }

    override suspend fun initComponent(nodeModel: EditorNodeModel) {
        componentData.transform.toTransform(nodeModel.node.transform)
    }

    fun getMatrix(): Mat4d = componentData.transform.toMat4d(tmpMat)
    fun setMatrix(mat: Mat4d) = transformState.set(TransformData((mat)))
}