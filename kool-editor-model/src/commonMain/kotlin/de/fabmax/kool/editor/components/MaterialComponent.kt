package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.launchOnMainThread

class MaterialComponent(nodeModel: SceneNodeModel, override val componentData: MaterialComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<MaterialComponentData>
{

    constructor(nodeModel: SceneNodeModel): this(nodeModel, MaterialComponentData(NodeId(-1L)))

    val materialState = mutableStateOf<MaterialData?>(null).onChange { mat ->
        if (AppState.isEditMode) {
            componentData.materialId = mat?.id ?: NodeId( -1)
        }
        if (isCreated) {
            launchOnMainThread {
                nodeModel.getComponents<UpdateMaterialComponent>().forEach { it.updateMaterial(mat) }
            }
        }
    }

    val materialData: MaterialData?
        get() = materialState.value

    fun isHoldingMaterial(material: MaterialData?): Boolean {
        return material?.id == materialData?.id
    }

    override suspend fun createComponent() {
        super.createComponent()
        materialState.set(sceneModel.project.materialsById[componentData.materialId])
    }
}

interface UpdateMaterialComponent {
    fun updateMaterial(material: MaterialData?)
}

fun EditorProject.updateMaterial(material: MaterialData) {
    getAllComponents<UpdateMaterialComponent>().forEach {
        it.updateMaterial(material)
    }
}
