package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.GameEntityDataComponent
import de.fabmax.kool.editor.data.ComponentData
import kotlin.reflect.KClass

class SetComponentDataAction<T: GameEntityDataComponent<D>, D: ComponentData>(
    component: T,
    componentType: KClass<T>,
    private val undoData: D,
    private val applyData: D
) : ComponentAction<T>(component.gameEntity.id, componentType) {

    override fun doAction() { component?.setPersistent(applyData) }
    override fun undoAction() { component?.setPersistent(undoData) }
}

inline fun <D: ComponentData, reified T: GameEntityDataComponent<D>> SetComponentDataAction(component: T, undoData: D, applyData: D) =
    SetComponentDataAction(component, T::class, undoData, applyData)
