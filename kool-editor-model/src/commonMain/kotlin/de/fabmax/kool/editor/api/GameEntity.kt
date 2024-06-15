package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import kotlin.math.max

val GameEntity.project: EditorProject get() = scene.project
val GameEntity.sceneEntity: GameEntity get() = scene.sceneEntity
val GameEntity.sceneComponent: SceneComponent get() = scene.sceneEntity.requireComponent()

class GameEntity(val entityData: GameEntityData, val scene: EditorScene) {

    val id: EntityId get() = entityData.id

    var name: String
        get() = drawNode.name
        set(value) { drawNode.name = value }

    var drawGroupId: Int
        get() = drawNode.drawGroupId
        set(value) { drawNode.drawGroupId = value }

    var isVisible: Boolean
        get() = drawNode.isVisible
        set(value) { drawNode.isVisible = value }

    val isVisibleInScene: Boolean
        get() = if (!isVisible) false else parent?.isVisibleInScene != false

    val components = mutableStateListOf<GameEntityComponent>()
    var componentModCnt = 0
        private set
    val transform: TransformComponent
    val requiredAssets: Set<AssetReference> get() = components.flatMap { it.requiredAssets }.toSet()

    val isSceneRoot: Boolean = entityData.components.any { it.data is SceneComponentData }
    val isSceneChild: Boolean = !isSceneRoot

    var parent: GameEntity? = null
        private set(value) {
            field = value
            entityData.parentId = value?.id
        }

    private val _children = mutableListOf<GameEntity>()
    val children: List<GameEntity> get() = _children

    var drawNode: Node
        private set

    private val nodeUpdateCb: (RenderPass.UpdateEvent) -> Unit = { ev ->
        for (i in components.indices) {
            components[i].onUpdate(ev)
        }
    }

    var lifecycle = EntityLifecycle.CREATED
        private set(value) {
            check(field.isAllowedAsNext(value)) {
                "GameEntity $name: Transitioning from lifecycle state $field to $value is not allowed"
            }
            field = value
        }

    init {
        check(id.value > 0L)

        createComponentsFromData(entityData.components)
        transform = getOrPutComponent { TransformComponent(this).apply { componentInfo.displayOrder = 0 } }

        drawNode = getComponent<DrawNodeComponent>()?.drawNode ?: Node(entityData.name)
        drawNode.applyEntityData()
        drawNode.transform = transform.transform
    }

    private val requireSceneChild: GameEntity
        get() = this.also { check(isSceneChild) { "$name is not a scene child" } }
    private val requireScene: GameEntity
        get() = this.also { check(isSceneRoot) { "$name is not a scene" } }

    @Suppress("UNCHECKED_CAST")
    private fun createComponentsFromData(componentInfo: List<ComponentInfo<*>>) {
        componentInfo.forEach { info ->
            when (info.data) {
                is BehaviorComponentData -> components += BehaviorComponent(this, info as ComponentInfo<BehaviorComponentData>)
                is TransformComponentData -> components += TransformComponent(this, info as ComponentInfo<TransformComponentData>)
                is MaterialComponentData -> components += MaterialComponent(this, info as ComponentInfo<MaterialComponentData>)

                is CameraComponentData -> components += CameraComponent(requireSceneChild, info as ComponentInfo<CameraComponentData>)
                is CharacterControllerComponentData -> components += CharacterControllerComponent(requireSceneChild, info as ComponentInfo<CharacterControllerComponentData>)
                is DiscreteLightComponentData -> components += DiscreteLightComponent(requireSceneChild, info as ComponentInfo<DiscreteLightComponentData>)
                is MaterialReferenceComponentData -> components += MaterialReferenceComponent(requireSceneChild, info as ComponentInfo<MaterialReferenceComponentData>)
                is MeshComponentData -> components += MeshComponent(requireSceneChild, info as ComponentInfo<MeshComponentData>)
                is ModelComponentData -> components += ModelComponent(requireSceneChild, info as ComponentInfo<ModelComponentData>)
                is RigidActorComponentData -> components += RigidActorComponent(requireSceneChild, info as ComponentInfo<RigidActorComponentData>)
                is ShadowMapComponentData -> components += ShadowMapComponent(requireSceneChild, info as ComponentInfo<ShadowMapComponentData>)

                is PhysicsWorldComponentData -> components += PhysicsWorldComponent(requireScene, info as ComponentInfo<PhysicsWorldComponentData>)
                is SceneBackgroundComponentData -> components += SceneBackgroundComponent(requireScene, info as ComponentInfo<SceneBackgroundComponentData>)
                is SceneComponentData -> components += SceneComponent(requireScene, info as ComponentInfo<SceneComponentData>)
                is SsaoComponentData -> components += SsaoComponent(requireScene, info as ComponentInfo<SsaoComponentData>)
            }
        }
        components.sortByDependencies()
        incComponentModCount()
    }

    fun replaceDrawNode(newDrawNode: Node) {
        if (drawNode == newDrawNode) {
            return
        }

        newDrawNode.name = name
        newDrawNode.isVisible = isVisible
        newDrawNode.drawGroupId = drawGroupId
        newDrawNode.transform = transform.transform

        val oldDrawNode = drawNode
        oldDrawNode.onUpdate -= nodeUpdateCb
        newDrawNode.onUpdate += nodeUpdateCb

        oldDrawNode.parent?.let { parent ->
            val ndIdx = parent.children.indexOf(oldDrawNode)
            parent.removeNode(oldDrawNode)
            parent.addNode(newDrawNode, ndIdx)
        }
        children.forEach {
            oldDrawNode.removeNode(it.drawNode)
            newDrawNode.addNode(it.drawNode)
        }

        oldDrawNode.release()
        drawNode = newDrawNode
        newDrawNode.updateModelMat()

        val wasInScene = scene.nodesToEntities.remove(oldDrawNode) != null
        if (wasInScene) {
            scene.nodesToEntities[newDrawNode] = this
        }
    }

    fun addChild(child: GameEntity, insertionPos: InsertionPos = InsertionPos.End) {
        when (insertionPos) {
            is InsertionPos.After -> {
                val thatEntity = scene.sceneEntities[insertionPos.that]
                val insertEntityIdx = children.indexOf(thatEntity) + 1
                val insertNodeIdx = drawNode.children.indexOf(thatEntity?.drawNode) + 1
                _children.add(insertEntityIdx, child)
                drawNode.addNode(child.drawNode, insertNodeIdx)
            }
            is InsertionPos.Before -> {
                val thatEntity = scene.sceneEntities[insertionPos.that]
                val insertEntityIdx = max(0, children.indexOf(thatEntity) )
                val insertNodeIdx = max(0, drawNode.children.indexOf(thatEntity?.drawNode))
                _children.add(insertEntityIdx, child)
                drawNode.addNode(child.drawNode, insertNodeIdx)
            }
            InsertionPos.End -> {
                _children.add(child)
                drawNode.addNode(child.drawNode)
            }
        }

        child.parent = this
        children.forEachIndexed { i, it -> it.entityData.order = i }
    }

    fun removeChild(child: GameEntity) {
        check(child.parent == this)

        drawNode.removeNode(child.drawNode)
        _children -= child
        // do not clear the parent of the child: doing so also clears the entity data parentId, making it difficult
        // to undo the remove op
    }

    suspend fun applyComponents() {
        var allOk = true
        components.filter { it.isCreated }.forEach {
            it.applyComponent()
            if (!it.isPrepared) {
                allOk = false
            }
        }
        if (allOk) {
            lifecycle = EntityLifecycle.PREPARED
        }
    }

    fun onStart() {
        components.forEach {
            it.onStart()
            check(it.isRunning) { "Component not started (missed calling super?): $it" }
        }
        lifecycle = EntityLifecycle.RUNNING
    }

    fun onPhysicsUpdate(timeStep: Float) {
        for (i in components.indices) {
            components[i].onPhysicsUpdate(timeStep)
        }
    }

    fun destroyComponents() {
        components.forEach {
            it.destroyComponent()
            check(it.isDestroyed) { "Component not destroyed (missed calling super?): $it" }
        }
        drawNode.release()
        lifecycle = EntityLifecycle.DESTROYED
    }

    fun addComponent(component: GameEntityComponent) {
        check(component.isCreated) {
            "addComponent called on GameEntity $name with component ${component.componentType} which lifecycle " +
                    "state is ${component.lifecycle} (expected CREATED)"
        }
        if (component is GameEntityDataComponent<*>) {
            if (component.componentInfo.displayOrder < 0) {
                component.componentInfo.displayOrder = components
                    .filterIsInstance<GameEntityDataComponent<*>>()
                    .maxOf { it.componentInfo.displayOrder } + 1
            }
            entityData.components += component.componentInfo
        }
        components += component
        incComponentModCount()
    }

    suspend fun addComponentLifecycleAware(component: GameEntityComponent) {
        addComponent(component)
        if (isPreparedOrRunning) {
            component.applyComponent()
            if (isRunning) {
                component.onStart()
            }
        }
    }

    fun removeComponent(component: GameEntityComponent) {
        components -= component
        if (component is GameEntityDataComponent<*>) {
            entityData.components -= component.componentInfo
        }
        component.destroyComponent()
        incComponentModCount()
    }

    inline fun <reified T: Any> getComponents(): List<T> = components.filterIsInstance<T>()

    inline fun <reified T: Any> getComponent(): T? = getComponents<T>().firstOrNull()

    inline fun <reified T: Any> requireComponent(): T = checkNotNull(getComponent())

    inline fun <reified T: Any> hasComponent(): Boolean = getComponent<T>() != null

    inline fun <reified T: GameEntityComponent> getOrPutComponent(factory: () -> T): T =
        getComponent<T>() ?: factory().also { addComponent(it) }

    suspend inline fun <reified T: GameEntityComponent> getOrPutComponentLifecycleAware(factory: () -> T): T =
        getComponent<T>() ?: factory().also { addComponentLifecycleAware(it) }

    private fun incComponentModCount() {
        componentModCnt++
        scene.componentModCnt++
    }

    private fun Node.applyEntityData() {
        isVisible = entityData.isVisible
        drawGroupId = entityData.drawGroupId
    }

    sealed class InsertionPos {
        data object End : InsertionPos()
        data class Before(val that: EntityId) : InsertionPos()
        data class After(val that: EntityId) : InsertionPos()
    }
}