package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedProjectComponents
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.util.launchOnMainThread

class MaterialComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MaterialComponentData> = ComponentInfo(MaterialComponentData(EntityId(-1L)))
) : GameEntityDataComponent<MaterialComponentData>(gameEntity, componentInfo) {

    val material: MaterialData? get() = project.materialsById[data.materialId]

    private val listeners by cachedProjectComponents<ListenerComponent>()

    init {
        collectRequiredAssets(material)
    }

    override fun onDataChanged(oldData: MaterialComponentData, newData: MaterialComponentData) {
        val material = project.materialsById[newData.materialId]
        collectRequiredAssets(material)

//        listeners.forEach { it.onMaterialChanged(this, material) }

        if (isApplied) {
            launchOnMainThread {
                listeners.forEach { it.onMaterialChanged(this, material) }
//                gameEntity.getComponents<UpdateMaterialComponent>().forEach { it.updateMaterial(material) }
            }
        }
    }

    fun isHoldingMaterial(material: MaterialData?): Boolean {
        return material?.id == data.materialId
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

    fun interface ListenerComponent {
        fun onMaterialChanged(component: MaterialComponent, materialData: MaterialData?)
    }
}
