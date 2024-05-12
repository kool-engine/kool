package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.data.MapAttribute
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.launchOnMainThread

class MaterialComponent(
    nodeModel: SceneNodeModel,
    override val componentData: MaterialComponentData = MaterialComponentData(NodeId(-1L))
) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<MaterialComponentData>
{

    val materialState = mutableStateOf<MaterialData?>(null).onChange { mat ->
        collectRequiredAssets(mat)
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

    init {
        materialState.set(sceneModel.project.materialsById[componentData.materialId])
    }

    fun isHoldingMaterial(material: MaterialData?): Boolean {
        return material?.id == materialData?.id
    }

    private fun collectRequiredAssets(material: MaterialData?) {
        requiredAssets.clear()
        if (material == null) {
            return
        }

        material.shaderData.collectAttributes().filterIsInstance<MapAttribute>().forEach { matMap ->
            requiredAssets += AssetReference.Texture(matMap.mapPath)
        }
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
