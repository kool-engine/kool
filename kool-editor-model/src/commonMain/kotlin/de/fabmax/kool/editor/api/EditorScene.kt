package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

val EditorScene.sceneComponent: SceneComponent get() = sceneEntity.sceneComponent
val EditorScene.scene: Scene get() = sceneComponent.drawNode

class EditorScene(val sceneData: SceneData, val project: EditorProject) : BaseReleasable() {

    val nodesToEntities: MutableMap<Node, GameEntity> = mutableMapOf()
    val sceneEntities: MutableMap<EntityId, GameEntity> = mutableMapOf()

    var componentModCnt = 0
        internal set

    val shaderData = SceneShaderData(this)
    val sceneEntity: GameEntity = GameEntity(sceneData.getOrAddSceneEntityData(), this)
    val name: String get() = sceneEntity.name

    var lifecycle = EntityLifecycle.CREATED
        private set(value) {
            check(field.isAllowedAsNext(value)) {
                "EditorScene $name: Transitioning from lifecycle state $field to $value is not allowed"
            }
            field = value
        }

    init {
        nodesToEntities[sceneEntity.drawNode] = sceneEntity
        sceneEntities[sceneEntity.id] = sceneEntity

        val parentsToChildren = mutableMapOf<EntityId?, MutableList<GameEntityData>>()
        project.entityData.values.forEach { entityData ->
            parentsToChildren.getOrPut(entityData.parentId) { mutableListOf() } += entityData
        }

        fun createEntity(entityData: GameEntityData) {
            sceneEntities[entityData.parentId]?.let { parent ->
                val entity = GameEntity(entityData, this)
                sceneEntities[entity.id] = entity
                nodesToEntities[entity.drawNode] = entity
                parent.addChild(entity)
            }
            parentsToChildren[entityData.id]?.sortedBy { it.order }?.forEach { createEntity(it) }
        }
        parentsToChildren[sceneEntity.id]?.sortedBy { it.order }?.forEach { createEntity(it) }

        // fix entities with missing parents by adding them to the scene root entity
        sceneData.entities.filter { it.parentId != null && it.parentId !in sceneEntities }.forEach {
            it.parentId = sceneEntity.id
            createEntity(it)
        }
    }

    private fun SceneData.getOrAddSceneEntityData(): GameEntityData {
        var entityData = entities.find { it.parentId == null }
        if (entityData == null) {
            entityData = GameEntityData(project.nextId(), sceneData.name, null).also {
                entities += it
            }
        }
        if (entityData.components.none { it.data is SceneComponentData }) {
            entityData.components += ComponentInfo(SceneComponentData())
        }
        return entityData
    }

    private fun addEntityData(data: GameEntityData) {
        if (data.id in project.entityData) {
            logE { "addEntityData: Duplicate ID ${data.id} of entity ${data.name}" }
        }
        sceneData.entities += data
        project._entityData[data.id] = data
    }

    private fun removeEntityData(data: GameEntityData) {
        sceneData.entities -= data
        project._entityData -= data.id
    }

    inline fun <reified T: Any> getAllComponents(): List<T> {
        return sceneEntities.values.flatMap { it.components.filterIsInstance<T>() }
    }

    inline fun <reified T: Any> getComponentsFromEntities(predicate: (GameEntity) -> Boolean): List<T> {
        return sceneEntities.values.filter(predicate).flatMap { it.components.filterIsInstance<T>() }
    }

    suspend fun prepareScene() {
        val requiredAssets = mutableSetOf<AssetReference>()
        sceneEntities.values.forEach { requiredAssets += it.requiredAssets }
        requiredAssets.forEach {
            if (!AppAssets.cacheAsset(it)) {
                logW{ "Failed pre-loading asset: ${it.path}" }
            }
        }
    }

    suspend fun applyComponents() {
        suspend fun GameEntity.applyComponentsRecursive() {
            applyComponents()
            children.forEach { it.applyComponentsRecursive() }
        }
        sceneEntity.applyComponentsRecursive()
        lifecycle = EntityLifecycle.PREPARED
    }

    fun startScene() {
        sceneEntities.values.forEach { it.onStart() }
        sceneEntity.getComponent<PhysicsWorldComponent>()?.physicsWorld?.let { physics ->
            physics.onPhysicsUpdate += { timeStep ->
                for (entity in sceneEntities.values) {
                    entity.onPhysicsUpdate(timeStep)
                }
            }
        }
        lifecycle = EntityLifecycle.RUNNING
    }

    override fun release() {
        super.release()

        sceneEntities.values.forEach {
            if (it != sceneEntity) {
                it.destroyComponents()
            }
        }
        sceneEntity.destroyComponents()
        lifecycle = EntityLifecycle.DESTROYED
    }

    suspend fun addGameEntities(
        hierarchy: GameEntityDataHierarchy,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ) {
        val gameEntity = GameEntity(hierarchy.entityData, this)
        addGameEntity(gameEntity, insertionPos)
        hierarchy.children.forEach { addGameEntities(it) }
    }

    suspend fun addGameEntity(
        data: GameEntityData,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ): GameEntity {
        val entity = GameEntity(data, this)
        addGameEntity(entity, insertionPos)
        return entity
    }

    private suspend fun addGameEntity(
        gameEntity: GameEntity,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ) {
        check(gameEntity.isCreated) {
            "addGameEntity called with entity ${gameEntity.name} which lifecycle state is ${gameEntity.lifecycle} (expected CREATED)"
        }

        val parent = gameEntity.parent ?: sceneEntities[gameEntity.entityData.parentId]?.also {
            it.addChild(gameEntity, insertionPos)
        }
        if (parent == null) {
            logE { "Parent of ${gameEntity.name} is not part of this scene (parent-id: ${gameEntity.entityData.parentId})" }
            return
        }

        if (isPreparedOrRunning) {
            gameEntity.applyComponents()
            if (isRunning) {
                gameEntity.onStart()
            }
        }

        addEntityData(gameEntity.entityData)
        sceneEntities[gameEntity.id] = gameEntity
        nodesToEntities[gameEntity.drawNode] = gameEntity
    }

    fun removeGameEntity(gameEntity: GameEntity) {
        val removeChildren = gameEntity.children.toList()
        removeChildren.forEach { removeGameEntity(it) }

        removeEntityData(gameEntity.entityData)
        sceneEntities -= gameEntity.id
        nodesToEntities -= gameEntity.drawNode

        gameEntity.parent?.removeChild(gameEntity)

        launchDelayed(1) {
            gameEntity.destroyComponents()
        }
    }

    fun interface SceneShaderDataListener {
        fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData)
    }
}