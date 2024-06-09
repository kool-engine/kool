package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedEntityComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Transform
import de.fabmax.kool.scene.TrsTransformF

class TransformComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<TransformComponentData> = ComponentInfo(TransformComponentData())
) : GameEntityDataComponent<TransformComponentData>(gameEntity, componentInfo) {

    private val changeListeners by cachedEntityComponents<ListenerComponent>()

    var transform: Transform = TrsTransformF()

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        data.transform.toTransform(transform)
    }

    fun updateDataFromTransform() {
        dataState.set(data.copy(transform = TransformData(transform)))
    }

    override fun onDataChanged(oldData: TransformComponentData, newData: TransformComponentData) {
        super.onDataChanged(oldData, newData)
        newData.transform.toTransform(transform)
        changeListeners.let { listeners ->
            for (i in listeners.indices) {
                listeners[i].onTransformChanged(this, newData)
            }
        }
    }

    override fun onUpdate(ev: RenderPass.UpdateEvent) {
        gameEntity.drawNode.transform = transform
    }

    fun interface ListenerComponent {
        fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData)
    }
}