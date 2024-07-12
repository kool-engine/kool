package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.modules.ui2.mutableStateOf

abstract class GameEntityDataComponent<D: ComponentData>(
    gameEntity: GameEntity,
    val componentInfo: ComponentInfo<D>
) : GameEntityComponent(gameEntity) {

    val dataState = mutableStateOf(componentInfo.data)
    val data: D get() = dataState.value

    init {
        dataState.onChange { old, new -> onDataChanged(old, new) }
    }

    open fun setPersistent(componentData: D) {
        componentInfo.data = componentData
        dataState.set(componentData)
    }

    protected open fun onDataChanged(oldData: D, newData: D) { }
}
