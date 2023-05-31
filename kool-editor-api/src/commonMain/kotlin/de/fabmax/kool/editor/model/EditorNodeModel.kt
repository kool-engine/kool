package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.launchOnMainThread

abstract class EditorNodeModel(val nodeData: SceneNodeData) {

    val nodeId: Long
        get() = nodeData.nodeId
    val name: String
        get() = nodeData.name

    abstract val node: Node
    abstract val isCreated: Boolean

    val components = mutableStateListOf<EditorModelComponent>()

    init {
        createComponentsFromData(nodeData.components)
    }

    abstract fun addChild(child: SceneNodeModel)
    abstract fun removeChild(child: SceneNodeModel)

    private fun createComponentsFromData(componentData: List<ComponentData>) {
        componentData.forEach { data ->
            when (data) {
                is MaterialComponentData -> components += MaterialComponent(data)
                is MeshComponentData -> components += MeshComponent(data)
                is ModelComponentData -> components += ModelComponent(data)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(data)
                is ScriptComponentData -> components += ScriptComponent(data)
                is TransformComponentData -> components += TransformComponent(data)
            }
        }
    }

    open suspend fun createComponents() {
        components.forEach { it.createComponent(this) }
    }

    open suspend fun initComponents() {
        components.forEach { it.initComponent(this) }
    }

    fun addComponent(component: EditorModelComponent) {
        components += component
        if (component is EditorDataComponent<*>) {
            nodeData.components += component.componentData
        }
        if (isCreated) {
            launchOnMainThread {
                component.createComponent(this)
                component.initComponent(this)
            }
        }
    }

    fun removeComponent(component: EditorModelComponent) {
        components -= component
        if (component is EditorDataComponent<*>) {
            nodeData.components -= component.componentData
        }
    }

    inline fun <reified T: EditorModelComponent> getOrPutComponent(factory: () -> T): T {
        var c = components.find { it is T }
        if (c == null) {
            c = factory()
            if (c is EditorDataComponent<*>) {
                nodeData.components.add(c.componentData)
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