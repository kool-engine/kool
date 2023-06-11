package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.modules.ui2.mutableStateOf

class MaterialComponent(override val componentData: MaterialComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<MaterialComponentData> {

    constructor(): this(MaterialComponentData(-1L))

    val materialState = mutableStateOf<MaterialData?>(null).onChange {
        componentData.materialId = it?.id ?: -1
    }
    val materialData: MaterialData?
        get() = materialState.value

    fun isHoldingMaterial(material: MaterialData?): Boolean {
        return material?.id == materialData?.id
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
        materialState.set(scene.project.materialsById[componentData.materialId])
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
