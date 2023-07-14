package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.launchOnMainThread

abstract class EditorNodeModel(val nodeData: SceneNodeData) {

    val nodeId: Long
        get() = nodeData.nodeId

    val nameState = mutableStateOf(nodeData.name).onChange { nodeData.name = it }
    val name: String
        get() = nodeData.name

    val isVisibleState = mutableStateOf(nodeData.isVisible).onChange {
        if (AppState.isEditMode) {
            nodeData.isVisible = it
        }
        if (isCreated) {
            drawNode.isVisible = it
        }
    }

    abstract val drawNode: Node
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
                is DiscreteLightComponentData -> components += DiscreteLightComponent(data)
                is MaterialComponentData -> components += MaterialComponent(data)
                is MeshComponentData -> components += MeshComponent(data)
                is ModelComponentData -> components += ModelComponent(data)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(data)
                is ScriptComponentData -> components += ScriptComponent(data)
                is ShadowMapComponentData -> components += ShadowMapComponent(data)
                is SsaoComponentData -> components += SsaoComponent(data)
                is TransformComponentData -> components += TransformComponent(data)
            }
        }
        components.sortByDependencies()
    }

    open suspend fun createComponents() {
        isVisibleState.set(nodeData.isVisible)
        components.forEach { it.createComponent(this) }
    }

    open fun onNodeAdded() {
        components.forEach { it.onNodeAdded(this) }
    }

    open fun onNodeRemoved() {
        components.forEach { it.onNodeRemoved(this) }
    }

    fun addComponent(component: EditorModelComponent) {
        components += component
        if (component is EditorDataComponent<*>) {
            nodeData.components += component.componentData
        }
        if (isCreated) {
            launchOnMainThread {
                component.createComponent(this)
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

    inline fun <reified T: Any> hasComponent(): Boolean = getComponent<T>() != null

    inline fun <reified T: Any> getComponent(): T? {
        return components.filterIsInstance<T>().firstOrNull()
    }

    inline fun <reified T: Any> getComponents(): List<T> {
        return components.filterIsInstance<T>()
    }
}