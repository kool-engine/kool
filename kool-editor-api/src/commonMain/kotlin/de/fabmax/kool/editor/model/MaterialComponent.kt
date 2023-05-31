package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.modules.ui2.mutableStateOf

class MaterialComponent(override val componentData: MaterialComponentData) : EditorDataComponent<MaterialComponentData> {

    val materialState = mutableStateOf<MaterialData?>(null).onChange {
        componentData.materialId = it?.id ?: -1
    }
    val materialData: MaterialData?
        get() = materialState.value

    fun isHoldingMaterial(material: MaterialData?): Boolean {
        return material?.id == materialData?.id
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        val sceneNode = requireNotNull(nodeModel as? SceneNodeModel) {
            "MaterialHolderComponent is only allowed in SceneNodeModels (parent node is of type ${nodeModel::class})"
        }
        materialState.set(sceneNode.scene.project.materials[componentData.materialId])
    }
}

interface UpdateMaterialComponent : EditorModelComponent {
    fun updateMaterial(material: MaterialData?)
}

fun EditorProject.updateMaterial(material: MaterialData) {
    getAllComponents<UpdateMaterialComponent>().forEach {
        it.updateMaterial(material)
    }
}
