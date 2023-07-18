package de.fabmax.kool.editor.model

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.launchOnMainThread

abstract class EditorNodeModel(val nodeData: SceneNodeData) {

    val nodeId: Long
        get() = nodeData.nodeId

    val nameState = mutableStateOf(nodeData.name).onChange {
        nodeData.name = it
        drawNode.name = it
    }

    val name: String
        get() = nodeData.name

    val isVisibleState = mutableStateOf(nodeData.isVisible).onChange {
        if (AppState.isEditMode) {
            nodeData.isVisible = it
        }
        drawNode.isVisible = it
    }

    val components = mutableStateListOf<EditorModelComponent>()
    abstract val drawNode: Node

    var isCreated: Boolean = false
        private set

    val onNodeUpdate: MutableList<(RenderPass.UpdateEvent) -> Unit> = mutableListOf()

    init {
        createComponentsFromData(nodeData.components)
    }

    abstract fun addChild(child: SceneNodeModel)
    abstract fun removeChild(child: SceneNodeModel)

    private val requireSceneNode: SceneNodeModel
        get() = requireNotNull(this as? SceneNodeModel) { "$name is not a SceneNodeModel" }
    private val requireScene: SceneModel
        get() = requireNotNull(this as? SceneModel) { "$name is not a SceneModel" }

    private fun createComponentsFromData(componentData: List<ComponentData>) {
        componentData.forEach { data ->
            when (data) {
                is DiscreteLightComponentData -> components += DiscreteLightComponent(requireSceneNode, data)
                is MaterialComponentData -> components += MaterialComponent(requireSceneNode, data)
                is MeshComponentData -> components += MeshComponent(requireSceneNode, data)
                is ModelComponentData -> components += ModelComponent(requireSceneNode, data)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(requireScene, data)
                is ScriptComponentData -> components += ScriptComponent(this, data)
                is ShadowMapComponentData -> components += ShadowMapComponent(requireSceneNode, data)
                is SsaoComponentData -> components += SsaoComponent(requireScene, data)
                is TransformComponentData -> components += TransformComponent(requireSceneNode, data)
            }
        }
        components.sortByDependencies()
    }

    open suspend fun createComponents() {
        isCreated = true
        isVisibleState.set(nodeData.isVisible)
        components.forEach {
            it.createComponent()
            check(it.isCreated) { "Component not created: $it" }
        }
    }

    open fun destroyComponents() {
        components.forEach {
            it.destroyComponent()
            check(!it.isCreated) { "Component not destroyed: $it" }
        }
        drawNode.dispose(KoolSystem.requireContext())
        drawNode.parent?.removeNode(drawNode)
        isCreated = false
    }

    fun addComponent(component: EditorModelComponent) {
        components += component
        if (component is EditorDataComponent<*>) {
            nodeData.components += component.componentData
        }
        if (isCreated) {
            launchOnMainThread {
                component.createComponent()
            }
        }
    }

    fun removeComponent(component: EditorModelComponent) {
        components -= component
        if (component is EditorDataComponent<*>) {
            nodeData.components -= component.componentData
        }
        if (component.isCreated) {
            component.destroyComponent()
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