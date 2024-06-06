package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.max

val GameEntity.project: EditorProject get() = scene.project
val GameEntity.sceneEntity: GameEntity get() = scene.sceneEntity
val GameEntity.sceneComponent: SceneComponent get() = scene.sceneEntity.requireComponent()

class GameEntity(val entityData: GameEntityData, val scene: EditorScene) {

    val id: EntityId get() = entityData.id
    val name: String get() = entityData.name

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




    var isCreated: Boolean = false
        private set
    val onNodeUpdate: MutableList<(RenderPass.UpdateEvent) -> Unit> = mutableListOf()
    private val nodeUpdateCb: (RenderPass.UpdateEvent) -> Unit = { ev -> onNodeUpdate.forEach { cb -> cb(ev) } }




    init {
        check(id.value > 0L)

        createComponentsFromData(entityData.components)
        transform = getOrPutComponent { TransformComponent(this) }

        drawNode = getComponent<DrawNodeComponent>()?.drawNode ?: Node(name)
        drawNode.onUpdate += nodeUpdateCb
        transform.applyTransformTo(drawNode)
    }

    val nameState = mutableStateOf(entityData.name).onChange {
        entityData.name = it
        drawNode.name = it
    }

    val isVisibleState = mutableStateOf(entityData.isVisible).onChange {
        drawNode.isVisible = it
        if (AppState.isEditMode) {
            entityData.isVisible = it
        }
    }

    fun replaceDrawNode(newDrawNode: Node) {
        if (drawNode == newDrawNode) {
            return
        }
        val oldDrawNode = drawNode

        oldDrawNode.parent?.let { parent ->
            val ndIdx = parent.children.indexOf(oldDrawNode)
            parent.removeNode(oldDrawNode)
            parent.addNode(newDrawNode, ndIdx)
        }
        children.forEach {
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

    private val requireSceneChild: GameEntity
        get() = this.also { require(isSceneChild) { "$name is not a scene child" } }
    private val requireScene: GameEntity
        get() = this.also { require(isSceneRoot) { "$name is not a scene" } }

    @Suppress("UNCHECKED_CAST")
    private fun createComponentsFromData(componentInfo: List<ComponentInfo<*>>) {
        componentInfo.forEach { info ->
            when (info.data) {
                is BehaviorComponentData -> components += BehaviorComponent(this, info as ComponentInfo<BehaviorComponentData>)
                is TransformComponentData -> components += TransformComponent(this, info as ComponentInfo<TransformComponentData>)

                is CameraComponentData -> components += CameraComponent(requireSceneChild, info as ComponentInfo<CameraComponentData>)
                is CharacterControllerComponentData -> components += CharacterControllerComponent(requireSceneChild, info as ComponentInfo<CharacterControllerComponentData>)
                is DiscreteLightComponentData -> components += DiscreteLightComponent(requireSceneChild, info as ComponentInfo<DiscreteLightComponentData>)
                is MaterialComponentData -> components += MaterialComponent(requireSceneChild, info as ComponentInfo<MaterialComponentData>)
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

    private fun incComponentModCount() {
        componentModCnt++
        scene.componentModCnt++
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
        if (component is GameEntityDataComponent<*,*>) {
            entityData.components += component.componentInfo
        }
        if (isCreated && autoCreateComponent) {
            launchOnMainThread {
                component.applyComponent()
            }
        }
        incComponentModCount()
    }

    fun removeComponent(component: GameEntityComponent) {
        components -= component
        if (component is GameEntityDataComponent<*,*>) {
            entityData.components -= component.componentInfo
        }
        if (component.isApplied) {
            component.destroyComponent()
        }
        incComponentModCount()
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