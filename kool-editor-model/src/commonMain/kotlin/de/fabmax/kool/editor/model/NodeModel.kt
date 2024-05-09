package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.max

sealed class NodeModel(val nodeData: SceneNodeData) {

    val nodeId: NodeId
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
    val requiredAssets: Set<AssetReference> get() = components.flatMap { it.requiredAssets }.toSet()

    abstract val drawNode: Node

    var isCreated: Boolean = false
        private set

    val onNodeUpdate: MutableList<(RenderPass.UpdateEvent) -> Unit> = mutableListOf()

    fun addChild(child: SceneNodeModel, insertionPos: InsertionPos = InsertionPos.End) {
        // addChild() is called during scene creation for all child nodes of this NodeModel, in that case
        // the IDs of the children already exist in nodeData.childNodeIds -> only insert node ID if it isn't
        // already present
        val insertId = child.nodeId !in nodeData.childNodeIds

        when (insertionPos) {
            is InsertionPos.After -> {
                if (insertId) {
                    val insertIdx = nodeData.childNodeIds.indexOf(insertionPos.that) + 1
                    nodeData.childNodeIds.add(insertIdx, child.nodeId)
                }
                val thatNode = getNodeById(insertionPos.that)
                val insertSceneIdx = drawNode.children.indexOf(thatNode?.drawNode) + 1
                drawNode.addNode(child.drawNode, insertSceneIdx)
            }
            is InsertionPos.Before -> {
                if (insertId) {
                    val insertIdx = max(0, nodeData.childNodeIds.indexOf(insertionPos.that))
                    nodeData.childNodeIds.add(insertIdx, child.nodeId)
                }
                val thatNode = getNodeById(insertionPos.that)
                val insertSceneIdx = max(0, drawNode.children.indexOf(thatNode?.drawNode))
                drawNode.addNode(child.drawNode, insertSceneIdx)
            }
            InsertionPos.End -> {
                if (insertId) {
                    nodeData.childNodeIds += child.nodeId
                }
                drawNode.addNode(child.drawNode)
            }
        }

        child.parent = this
    }

    fun removeChild(child: SceneNodeModel) {
        nodeData.childNodeIds -= child.nodeId
        drawNode.removeNode(child.drawNode)
    }

    private fun getNodeById(id: NodeId): SceneNodeModel? {
        return when (this) {
            is SceneModel -> nodeModels[id]
            is SceneNodeModel -> sceneModel.nodeModels[id]
        }
    }

    private val requireSceneNode: SceneNodeModel
        get() = requireNotNull(this as? SceneNodeModel) { "$name is not a SceneNodeModel" }
    private val requireScene: SceneModel
        get() = requireNotNull(this as? SceneModel) { "$name is not a SceneModel" }

    protected fun createComponentsFromData(componentData: List<ComponentData>) {
        componentData.forEach { data ->
            when (data) {
                is CameraComponentData -> components += CameraComponent(requireSceneNode, data)
                is DiscreteLightComponentData -> components += DiscreteLightComponent(requireSceneNode, data)
                is MaterialComponentData -> components += MaterialComponent(requireSceneNode, data)
                is MeshComponentData -> components += MeshComponent(requireSceneNode, data)
                is ModelComponentData -> components += ModelComponent(requireSceneNode, data)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(requireScene, data)
                is ScenePropertiesComponentData -> components += ScenePropertiesComponent(requireScene, data)
                is BehaviorComponentData -> components += BehaviorComponent(this, data)
                is ShadowMapComponentData -> components += ShadowMapComponent(requireSceneNode, data)
                is SsaoComponentData -> components += SsaoComponent(requireScene, data)
                is TransformComponentData -> components += TransformComponent(requireSceneNode, data)

                is PhysicsWorldComponentData -> components += PhysicsWorldComponent(requireScene, data)
                is RigidActorComponentData -> components += RigidActorComponent(requireSceneNode, data)
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
        components.clear()
        onNodeUpdate.clear()
        drawNode.release()
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

    open fun onStart() {
        components.forEach { it.onStart() }
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

    sealed class InsertionPos {
        data object End : InsertionPos()
        data class Before(val that: NodeId) : InsertionPos()
        data class After(val that: NodeId) : InsertionPos()
    }
}