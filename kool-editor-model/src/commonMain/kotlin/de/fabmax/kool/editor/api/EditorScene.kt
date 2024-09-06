package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

val EditorScene.sceneComponent: SceneComponent get() = sceneEntity.sceneComponent
val EditorScene.scene: Scene get() = sceneComponent.sceneNode
val EditorScene.upAxis: SceneUpAxis get() = sceneComponent.data.upAxis

class EditorScene(val sceneData: SceneData, val project: EditorProject) : BaseReleasable() {

    private val _sceneEntities: MutableMap<EntityId, GameEntity> = mutableMapOf()
    val sceneEntities: Map<EntityId, GameEntity> get() = _sceneEntities

    // list of all entities in this scene, ordered such that any child GameEntity comes after its parent
    private val _orderedEntities = mutableListOf<GameEntity>()
    val orderedEntities: List<GameEntity> get() = _orderedEntities

    var componentModCnt = 0
        internal set

    val sceneNodes = SceneNodes(this)
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

    val hitTest = SceneHitTest(this)
    val sceneOrigin = TrsTransformD()

    val listenerComponents = mutableListOf<Any>(sceneNodes)

    init {
        _orderedEntities += sceneEntity
        _sceneEntities[sceneEntity.id] = sceneEntity

        val parentsToChildren = mutableMapOf<EntityId?, MutableList<GameEntityData>>()
        project.entityData.values.forEach { entityData ->
            parentsToChildren.getOrPut(entityData.parentId) { mutableListOf() } += entityData
        }

        fun createEntity(entityData: GameEntityData) {
            addGameEntity(GameEntity(entityData, this))
            parentsToChildren[entityData.id]?.sortedBy { it.order }?.forEach { createEntity(it) }
        }
        parentsToChildren[sceneEntity.id]?.sortedBy { it.order }?.forEach { createEntity(it) }

        sceneData.entities.filter { it.parentId != EntityId.NULL && it.parentId !in sceneEntities }.forEach {
            logW { "Parent for entity ${it.settings.name} not found: Inserting in scene root" }
            it.parentId = sceneEntity.id
            createEntity(it)
        }

        sceneEntity.sceneComponent.sceneNode.onUpdate { updateEntities(it) }
    }

    private fun SceneData.getOrAddSceneEntityData(): GameEntityData {
        var entityData = entities.find { it.id == meta.rootId } ?: entities.find { it.parentId == EntityId.NULL }
        if (entityData == null) {
            entityData = GameEntityData(meta.rootId, EntityId.NULL, GameEntitySettings(sceneData.meta.name)).also {
                entities += it
            }
        }
        if (entityData.id != meta.rootId) {
            logW { "Fixing scene root id mismatch" }
            entityData = entityData.copy(id = meta.rootId)
        }
        if (entityData.components.none { it.data is SceneComponentData }) {
            entityData.components += ComponentInfo(SceneComponentData())
        }
        return entityData
    }

    private fun addEntityData(data: GameEntityData) {
        if (data.id in project.entityData) {
            logE { "addEntityData: Duplicate ID ${data.id} of entity ${data.settings.name}" }
        }
        sceneData.entities += data
        project.entityData[data.id] = data
    }

    private fun removeEntityData(data: GameEntityData) {
        sceneData.entities -= data
        project.entityData -= data.id
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
                logW{ "Failed pre-loading asset: $it" }
            }
        }
    }

    suspend fun applyComponents() {
        val failedEntities = mutableListOf<GameEntity>()
        var succeededEntities = 0

        suspend fun GameEntity.applyComponentsRecursive() {
            if (isCreated) {
                applyComponents()
                if (isPrepared) {
                    succeededEntities++
                } else {
                    failedEntities += this
                }
            }
            children.forEach { it.applyComponentsRecursive() }
        }

        do {
            failedEntities.clear()
            succeededEntities = 0
            sceneEntity.applyComponentsRecursive()
            if (failedEntities.size > 0 && succeededEntities == 0) {
                logE { "Failed to initialize entities: ${failedEntities.joinToString { it.name }}" }
                break
            }
        } while (failedEntities.size > 0)

        lifecycle = EntityLifecycle.PREPARED
    }

    fun startScene() {
        sceneEntities.values.forEach { it.onStart() }
        getAllComponents<PhysicsWorldComponent>().firstOrNull()?.physicsWorld?.let { physics ->
            physics.onPhysicsUpdate += { timeStep ->
                for (entity in sceneEntities.values) {
                    entity.onPhysicsUpdate(timeStep)
                }
            }
        }
        lifecycle = EntityLifecycle.RUNNING
    }

    private fun updateEntities(ev: RenderPass.UpdateEvent) {
        for (i in _orderedEntities.indices) {
            _orderedEntities[i].onUpdate(ev)
        }
        sceneNodes.updateInstances()
    }

    override fun release() {
        super.release()

        sceneEntities.values.forEach { it.destroyComponents() }
        sceneComponent.sceneNode.release()
        lifecycle = EntityLifecycle.DESTROYED
    }

    suspend fun addGameEntities(
        hierarchy: GameEntityDataHierarchy,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ) {
        addGameEntity(hierarchy.entityData, insertionPos)
        hierarchy.children.forEach { addGameEntities(it) }
    }

    suspend fun addGameEntity(
        data: GameEntityData,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ): GameEntity {
        val entity = GameEntity(data, this)
        addGameEntity(entity, insertionPos)
        addEntityData(data)
        entity.applyLifecycleState()
        return entity
    }

    private fun addGameEntity(
        gameEntity: GameEntity,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ) {
        check(gameEntity.isCreated) {
            "addGameEntity called with entity ${gameEntity.name} which lifecycle state is ${gameEntity.lifecycle} (expected CREATED)"
        }

        val parent = checkNotNull(sceneEntities[gameEntity.entityData.parentId]) {
            "Parent of ${gameEntity.name} is not present in scene $name (parent-id: ${gameEntity.entityData.parentId})"
        }
        parent.addChild(gameEntity, insertionPos)

        _sceneEntities[gameEntity.id] = gameEntity
        _orderedEntities += gameEntity
    }

    private suspend fun GameEntity.applyLifecycleState() {
        if (this@EditorScene.isPreparedOrRunning) {
            applyComponents()
            if (!isPrepared) {
                logE { "Failed to apply components of newly added GameEntity $name" }
            }
            if (this@EditorScene.isRunning && isPrepared) {
                onStart()
            }
        }
    }

    fun removeGameEntity(gameEntity: GameEntity) {
        val removeChildren = gameEntity.children.toList()
        removeChildren.forEach { removeGameEntity(it) }

        removeEntityData(gameEntity.entityData)
        _sceneEntities -= gameEntity.id
        _orderedEntities -= gameEntity
        componentModCnt++

        gameEntity.parent?.removeChild(gameEntity)

        launchDelayed(1) {
            gameEntity.destroyComponents()
        }
    }

    fun interface SceneShaderDataListener {
        fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData)
    }
}