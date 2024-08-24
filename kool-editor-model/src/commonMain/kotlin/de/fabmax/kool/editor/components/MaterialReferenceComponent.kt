package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedEntityComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.MapAttribute
import de.fabmax.kool.editor.data.MaterialReferenceComponentData
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.launchOnMainThread

class MaterialReferenceComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MaterialReferenceComponentData> = ComponentInfo(MaterialReferenceComponentData(EntityId.NULL))
) : GameEntityDataComponent<MaterialReferenceComponentData>(gameEntity, componentInfo) {

    val materialId: EntityId get() = data.materialId
    val material: MaterialComponent? get() = project.materialsById[materialId]

    private val listeners by cachedEntityComponents<ListenerComponent>()

    init {
        collectRequiredAssets(material)
    }

    override fun onDataChanged(oldData: MaterialReferenceComponentData, newData: MaterialReferenceComponentData) {
        val material = project.materialsById[newData.materialId]
        collectRequiredAssets(material)

        launchOnMainThread {
            listeners.forEach { it.onMaterialReferenceChanged(this, material) }
        }
    }

    fun isHoldingMaterial(material: MaterialComponent?): Boolean {
        return material?.id == data.materialId
    }

    private fun collectRequiredAssets(material: MaterialComponent?) {
        requiredAssets.clear()
        if (material == null) {
            return
        }
        material.shaderData.collectAttributes().filterIsInstance<MapAttribute>().forEach { matMap ->
            requiredAssets += AssetReference.Texture(matMap.mapPath, matMap.format ?: TexFormat.RGBA)
        }
    }

    fun interface ListenerComponent {
        suspend fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?)
    }
}
