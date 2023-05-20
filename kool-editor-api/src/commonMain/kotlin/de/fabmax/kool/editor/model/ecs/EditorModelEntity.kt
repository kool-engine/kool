package de.fabmax.kool.editor.model.ecs

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf

abstract class EditorModelEntity(val componentData: MutableList<ComponentData>) {

    val components = mutableStateListOf<EditorModelComponent>()

    init {
        createComponentsFromData(componentData)
    }

    private fun createComponentsFromData(componentData: List<ComponentData>) {
        componentData.forEach { data ->
            when (data) {
                is MeshComponentData -> components += MeshComponent(data)
                is ModelComponentData -> components += ModelComponent(data)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(data)
                is TransformComponentData -> components += TransformComponent(data)
            }
        }
    }

    inline fun <reified T: EditorModelComponent> getOrPutComponent(factory: () -> T): T {
        var c = components.find { it is T }
        if (c == null) {
            c = factory()
            if (c is EditorDataComponent<*>) {
                componentData.add(c.componentData)
            }
            components += c
        }
        return c as T
    }

    inline fun <reified T: EditorModelComponent> getComponent(): T? {
        return components.filterIsInstance<T>().firstOrNull()
    }

    inline fun <reified T: EditorModelComponent> getComponents(): List<T> {
        return components.filterIsInstance<T>()
    }
}