package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedEntityComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Transform
import de.fabmax.kool.scene.TrsTransformF

class TransformComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<TransformComponentData> = ComponentInfo(TransformComponentData())
) : GameEntityDataComponent<TransformComponentData>(gameEntity, componentInfo) {

    private val changeListeners by cachedEntityComponents<ListenerComponent>()

    val globalTransform = MutableMat4f()
    var transform: Transform = TrsTransformF()
        set(value) {
            field = value
            updateTransform()
            fireTransformChanged(data)
        }

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        data.transform.toTransform(transform)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateTransform()
    }

    fun updateDataFromTransform() {
        dataState.set(data.copy(transform = TransformData(transform)))
    }

    override fun onDataChanged(oldData: TransformComponentData, newData: TransformComponentData) {
        super.onDataChanged(oldData, newData)
        newData.transform.toTransform(transform)
        updateTransform()
        fireTransformChanged(newData)
    }

    override fun onUpdate(ev: RenderPass.UpdateEvent) {
        updateTransform()
    }

    private fun updateTransform() {
        val parentModelMat = gameEntity.parent?.transform?.globalTransform ?: Mat4f.IDENTITY
        globalTransform.set(parentModelMat).mul(transform.matrixF)
    }

    private fun fireTransformChanged(data: TransformComponentData) {
        changeListeners.let { listeners ->
            for (i in listeners.indices) {
                listeners[i].onTransformChanged(this, data)
            }
        }
    }

    fun interface ListenerComponent {
        fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData)
    }
}