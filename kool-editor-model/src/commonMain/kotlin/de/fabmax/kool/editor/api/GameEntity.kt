package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.max

val GameEntity.project: EditorProject get() = scene.project
val GameEntity.sceneEntity: GameEntity get() = scene.sceneEntity
val GameEntity.sceneComponent: SceneComponent get() = scene.sceneEntity.requireComponent()

class GameEntity(val entityData: GameEntityData, val scene: EditorScene) {

    val entityId: EntityId get() = entityData.id
    val name: String get() = entityData.name
    val components = mutableStateListOf<GameEntityComponent>()

    val isSceneRoot: Boolean = entityData.components.any { it is SceneComponentData }
    val isSceneChild: Boolean = !isSceneRoot

    var parent: GameEntity? = null
        //private set

    private val _children = mutableListOf<GameEntity>()
    val children: List<GameEntity> get() = _children

    var drawNode: Node = if (isSceneRoot) Scene().apply { tryEnableInfiniteDepth() } else Node(name)
        private set

    val transform: TransformComponent




    val nameState = mutableStateOf(entityData.name).onChange {
        entityData.name = it
        drawNode.name = it
    }


    val isVisibleState = mutableStateOf(entityData.isVisible).onChange {
        if (AppState.isEditMode) {
            entityData.isVisible = it
        }
        drawNode.isVisible = it
    }


    val requiredAssets: Set<AssetReference> get() = components.flatMap { it.requiredAssets }.toSet()

    var isCreated: Boolean = false
        private set

    val onNodeUpdate: MutableList<(RenderPass.UpdateEvent) -> Unit> = mutableListOf()




    private val nodeUpdateCb: (RenderPass.UpdateEvent) -> Unit = { ev -> onNodeUpdate.forEach { cb -> cb(ev) } }


    init {
        createComponentsFromData(entityData.components)
        transform = getOrPutComponent { TransformComponent(this, TransformComponentData()) }

        nameState.onChange { drawNode.name = it }
        drawNode.onUpdate += nodeUpdateCb
        transform.applyTransformTo(drawNode)
    }

    fun replaceDrawNode(newDrawNode: Node) {
        val oldDrawNode = drawNode

        oldDrawNode.parent?.let { parent ->
            val ndIdx = parent.children.indexOf(oldDrawNode)
            parent.removeNode(oldDrawNode)
            parent.addNode(newDrawNode, ndIdx)
        }
        val childEntities = entityData.childEntityIds.mapNotNull { scene.sceneEntities[it] }
        childEntities.forEach {
            oldDrawNode.removeNode(it.drawNode)
            newDrawNode.addNode(it.drawNode)
        }

        oldDrawNode.onUpdate -= nodeUpdateCb
        oldDrawNode.release()

        transform.transformState.value.toTransform(newDrawNode.transform)
        newDrawNode.name = name
        drawNode = newDrawNode
        drawNode.onUpdate += nodeUpdateCb

        val wasInScene = scene.nodesToEntities.remove(oldDrawNode) != null
        if (wasInScene) {
            scene.nodesToEntities[newDrawNode] = this
        }
    }

    fun isVisibleWithParents(): Boolean {
        if (!isVisibleState.value) {
            return false
        }
        return parent?.isVisibleWithParents() != false
    }

    fun addChild(child: GameEntity, insertionPos: InsertionPos = InsertionPos.End) {
        // addChild() is called during scene creation for all child nodes of this NodeModel, in that case
        // the IDs of the children already exist in nodeData.childNodeIds -> only insert node ID if it isn't
        // already present
        val insertId = child.entityId !in entityData.childEntityIds

        when (insertionPos) {
            is InsertionPos.After -> {
                if (insertId) {
                    val insertIdx = entityData.childEntityIds.indexOf(insertionPos.that) + 1
                    entityData.childEntityIds.add(insertIdx, child.entityId)
                }
                val thatNode = scene.sceneEntities[insertionPos.that]
                val insertSceneIdx = drawNode.children.indexOf(thatNode?.drawNode) + 1
                drawNode.addNode(child.drawNode, insertSceneIdx)
            }
            is InsertionPos.Before -> {
                if (insertId) {
                    val insertIdx = max(0, entityData.childEntityIds.indexOf(insertionPos.that))
                    entityData.childEntityIds.add(insertIdx, child.entityId)
                }
                val thatNode = scene.sceneEntities[insertionPos.that]
                val insertSceneIdx = max(0, drawNode.children.indexOf(thatNode?.drawNode))
                drawNode.addNode(child.drawNode, insertSceneIdx)
            }
            InsertionPos.End -> {
                if (insertId) {
                    entityData.childEntityIds += child.entityId
                }
                drawNode.addNode(child.drawNode)
            }
        }

        child.parent = this
    }

    fun removeChild(child: GameEntity) {
        entityData.childEntityIds -= child.entityId
        drawNode.removeNode(child.drawNode)
    }

    private val requireSceneChild: GameEntity
        get() = this.also { require(isSceneChild) { "$name is not a scene child" } }
    private val requireScene: GameEntity
        get() = this.also { require(isSceneRoot) { "$name is not a scene" } }

    private fun createComponentsFromData(componentData: List<ComponentData>) {
        componentData.forEach { data ->
            when (data) {
                is CameraComponentData -> components += CameraComponent(requireSceneChild, data)
                is DiscreteLightComponentData -> components += DiscreteLightComponent(requireSceneChild, data)
                is MaterialComponentData -> components += MaterialComponent(requireSceneChild, data)
                is MeshComponentData -> components += MeshComponent(requireSceneChild, data)
                is ModelComponentData -> components += ModelComponent(requireSceneChild, data)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(requireScene, data)
                is SceneComponentData -> components += SceneComponent(requireScene, data)
                is BehaviorComponentData -> components += BehaviorComponent(this, data)
                is ShadowMapComponentData -> components += ShadowMapComponent(requireSceneChild, data)
                is SsaoComponentData -> components += SsaoComponent(requireScene, data)
                is TransformComponentData -> components += TransformComponent(this, data)

                is PhysicsWorldComponentData -> components += PhysicsWorldComponent(requireScene, data)
                is RigidActorComponentData -> components += RigidActorComponent(requireSceneChild, data)
                is CharacterControllerComponentData -> components += CharacterControllerComponent(requireSceneChild, data)
            }
        }
        components.sortByDependencies()
    }

    suspend fun applyComponents() {
        isCreated = true
        isVisibleState.set(entityData.isVisible)
        components.forEach {
            it.applyComponent()
            check(it.isApplied) { "Component not created: $it" }
        }
    }

    fun destroyComponents() {
        components.forEach {
            it.destroyComponent()
            check(!it.isApplied) { "Component not destroyed: $it" }
        }
        components.clear()
        onNodeUpdate.clear()
        drawNode.release()
        drawNode.parent?.removeNode(drawNode)
        isCreated = false
    }

    fun addComponent(component: GameEntityComponent, autoCreateComponent: Boolean = true) {
        components += component
        if (component is GameEntityDataComponent<*>) {
            entityData.components += component.componentData
        }
        if (isCreated && autoCreateComponent) {
            launchOnMainThread {
                component.applyComponent()
            }
        }
    }

    fun removeComponent(component: GameEntityComponent) {
        components -= component
        if (component is GameEntityDataComponent<*>) {
            entityData.components -= component.componentData
        }
        if (component.isApplied) {
            component.destroyComponent()
        }
    }

    fun onStart() {
        components.forEach { it.onStart() }
    }

    inline fun <reified T: GameEntityComponent> getOrPutComponent(createComponent: Boolean = true, factory: () -> T): T {
        var c = components.find { it is T }
        if (c == null) {
            c = factory()
            addComponent(c, createComponent)
        }
        return c as T
    }

    inline fun <reified T: Any> getComponents(): List<T> = components.filterIsInstance<T>()

    inline fun <reified T: Any> getComponent(): T? = getComponents<T>().firstOrNull()

    inline fun <reified T: Any> requireComponent(): T = checkNotNull(getComponent())

    inline fun <reified T: Any> hasComponent(): Boolean = getComponent<T>() != null

    sealed class InsertionPos {
        data object End : InsertionPos()
        data class Before(val that: EntityId) : InsertionPos()
        data class After(val that: EntityId) : InsertionPos()
    }
}