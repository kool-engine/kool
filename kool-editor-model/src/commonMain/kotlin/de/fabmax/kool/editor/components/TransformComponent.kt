package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedEntityComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node

class TransformComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<TransformComponentData> = ComponentInfo(TransformComponentData())
) : GameEntityDataComponent<TransformComponentData>(gameEntity, componentInfo) {

    private val changeListeners by cachedEntityComponents<ListenerComponent>()

    val onTransformEdited = mutableListOf<(TransformComponent) -> Unit>()

    val transformState = mutableStateOf(data.transform).onChange {
        if (AppState.isEditMode) {
            data.transform = it
        }
        if (gameEntity.isCreated) {
            it.toTransform(gameEntity.drawNode.transform)
            gameEntity.drawNode.updateModelMat()
        }
        onTransformEdited.forEach { it(this) }
    }

    val isFixedScaleRatio = mutableStateOf(data.isFixedScaleRatio).onChange {
        if (AppState.isEditMode) {
            data.isFixedScaleRatio = it
        }
    }

    init {
        componentOrder = COMPONENT_ORDER_EARLY
    }

    override fun onDataChanged(oldData: TransformComponentData, newData: TransformComponentData) {
        super.onDataChanged(oldData, newData)
        changeListeners.let { listeners ->
            for (i in listeners.indices) {
                listeners[i].onTransformChanged(this, newData)
            }
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        transformState.set(data.transform)
    }

    fun applyTransformTo(drawNode: Node) {
        data.transform.toTransform(drawNode.transform)
        drawNode.updateModelMat()
    }

    fun interface ListenerComponent {
        fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData)
    }
}