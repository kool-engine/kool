package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

val EditorScene.sceneComponent: SceneComponent get() = sceneEntity.sceneComponent
val EditorScene.scene: Scene get() = sceneComponent.scene

class EditorScene(val sceneData: GameEntityData, val project: EditorProject) : BaseReleasable() {

    var sceneEntity: GameEntity = GameEntity(sceneData, this)
        private set
    val name: String get() = sceneEntity.name

    val nodesToEntities: MutableMap<Node, GameEntity> = mutableMapOf()
    val sceneEntities: MutableMap<EntityId, GameEntity> = mutableMapOf()
    val sceneChildren: List<GameEntity> get() = nodesToEntities.values.filter { it.isSceneChild }

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
        parentsToChildren[sceneData.id]?.sortedBy { it.order }?.forEach { createEntity(it) }
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
    }

    fun onStart() {
        sceneEntities.values.forEach { it.onStart() }
    }

    override fun release() {
        super.release()

        sceneEntities.values.forEach {
            it.destroyComponents()
        }
        sceneEntities.clear()
        nodesToEntities.clear()
    }

    private fun resolveEntity(entityId: EntityId): GameEntity? {
        val entity = sceneEntities[entityId]
        return if (entity != null) entity else {
            val entityData = project.entityData[entityId]
            if (entityData != null) {
                GameEntity(entityData, this).also {
                    sceneEntities[entityId] = it
                }
            } else {
                logE { "Failed to resolve node with ID $entityId in scene ${sceneEntity.name}" }
                null
            }
        }
    }

    suspend fun addEntity(gameEntity: GameEntity) {
//        if (!gameEntity.isCreated) {
//            gameEntity.applyComponents()
//        } else {
//            logW { "Adding a scene node which is already created" }
//        }

        project.addEntityData(gameEntity.entityData)
        sceneEntities[gameEntity.id] = gameEntity
        nodesToEntities[gameEntity.drawNode] = gameEntity
        gameEntity.parent?.addChild(gameEntity)

//        sceneEntity.getComponent<SceneBackgroundComponent>()?.let { sceneBackground ->
//            gameEntity.getComponents<UpdateSceneBackgroundComponent>().forEach { it.updateBackground(sceneBackground) }
//        }
//        gameEntity.entityData.childEntityIds
//            .mapNotNull { resolveEntity(it, gameEntity) }
//            .forEach { addEntity(it) }
    }

    fun removeEntity(gameEntity: GameEntity) {
        gameEntity.children.forEach { removeEntity(it) }

        project.removeEntityData(gameEntity.entityData)
        sceneEntities -= gameEntity.id
        nodesToEntities -= gameEntity.drawNode

        gameEntity.parent?.removeChild(gameEntity)

        launchDelayed(1) {
            gameEntity.destroyComponents()
        }
    }
}