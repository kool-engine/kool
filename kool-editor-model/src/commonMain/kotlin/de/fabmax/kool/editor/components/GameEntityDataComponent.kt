package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.CachedComponents
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.modules.ui2.mutableStateOf

abstract class GameEntityDataComponent<C: GameEntityDataComponent<C, D>, D: ComponentData>(
    gameEntity: GameEntity,
    val componentInfo: ComponentInfo<D>
) : GameEntityComponent(gameEntity) {

    protected var thisRef: C? = null
    protected open val changeListenerComponents: List<DataChangeListenerComponent<C, D>>? = null

    val dataState = mutableStateOf(componentInfo.data)
    var data: D by dataState::value

    init {
        dataState.onChange { newData ->
            onDataChanged(data, newData)
            val thisRef = this.thisRef
            if (thisRef != null) {
                changeListenerComponents?.let { listeners ->
                    for (i in listeners.indices) {
                        listeners[i].onComponentDataChanged(thisRef, newData)
                    }
                }
            }
        }
    }

    fun setPersistent(componentData: D) {
        componentInfo.data = componentData
        dataState.set(componentData)
    }

    protected open fun onDataChanged(oldData: D, newData: D) { }
}

inline fun <reified T: Any> GameEntityDataComponent<*,*>.cachedEntityComponents(): CachedComponents<T> {
    return CachedComponents(gameEntity, T::class, CachedComponents.Scope.ENTITY)
}

inline fun <reified T: Any> GameEntityDataComponent<*,*>.cachedSceneComponents(): CachedComponents<T> {
    return CachedComponents(gameEntity, T::class, CachedComponents.Scope.SCENE)
}

fun interface DataChangeListenerComponent<C: GameEntityDataComponent<C, D>, D: ComponentData> {
    fun onComponentDataChanged(component: C, newData: D)
}
