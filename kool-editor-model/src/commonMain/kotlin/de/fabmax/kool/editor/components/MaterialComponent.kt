package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedProjectComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MaterialShaderData

class MaterialComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MaterialComponentData>
) : GameEntityDataComponent<MaterialComponentData>(gameEntity, componentInfo) {

    val id: EntityId get() = gameEntity.id
    val name: String get() = gameEntity.name

    val shaderData: MaterialShaderData get() = data.shaderData

    private val listeners by cachedProjectComponents<ListenerComponent>()

    override fun onDataChanged(oldData: MaterialComponentData, newData: MaterialComponentData) {
        gameEntity.nameState.set(newData.name)
        listeners.forEach { it.onMaterialChanged(this, newData) }
    }

    fun interface ListenerComponent {
        fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData)
    }
}