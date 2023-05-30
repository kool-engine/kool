package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.MaterialHolderData
import de.fabmax.kool.modules.ui2.mutableStateOf

class MaterialHolderComponent(override val componentData: MaterialHolderData) : EditorDataComponent<MaterialHolderData> {

    val materialModelState = mutableStateOf<MaterialModel?>(null).onChange {
        componentData.materialId = it?.materialState?.value?.id ?: -1
    }
    val materialData: MaterialData?
        get() = materialModelState.value?.materialState?.value

    fun isHoldingMaterial(material: MaterialModel?): Boolean {
        return materialModelState.value === material
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        val sceneNode = requireNotNull(nodeModel as? SceneNodeModel) {
            "MaterialHolderComponent is only allowed in SceneNodeModels (parent node is of type ${nodeModel::class})"
        }
        materialModelState.set(sceneNode.scene.project.materials[componentData.materialId])
    }
}

interface UpdateMaterialComponent : EditorModelComponent {
    fun updateMaterial(material: MaterialModel?)
}

fun EditorProject.updateMaterial(material: MaterialModel) {
    getAllComponents<UpdateMaterialComponent>().forEach {
        it.updateMaterial(material)
    }
}
