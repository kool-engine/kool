package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.components.UpdateSceneBackgroundComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

val EditorScene.sceneComponent: SceneComponent get() = sceneEntity.sceneComponent
val EditorScene.scene: Scene get() = sceneComponent.scene

class EditorScene(val sceneData: GameEntityData, val project: EditorProject) {

    val sceneEntity: GameEntity = GameEntity(sceneData, this)
    val name: String get() = sceneEntity.name

    val nodesToEntities: MutableMap<Node, GameEntity> = mutableMapOf()
    val sceneEntities: MutableMap<EntityId, GameEntity> = mutableMapOf()
    val sceneChildren: List<GameEntity> get() = nodesToEntities.values.filter { it.isSceneChild }

    init {
        nodesToEntities[sceneEntity.drawNode] = sceneEntity
        sceneEntities[sceneEntity.entityId] = sceneEntity
    }


    inline fun <reified T: Any> getAllComponents(): List<T> {
        return sceneEntities.values.flatMap { it.components.filterIsInstance<T>() }
    }

    inline fun <reified T: Any> getComponentsFromEntities(predicate: (GameEntity) -> Boolean): List<T> {
        return sceneEntities.values.filter(predicate).flatMap { it.components.filterIsInstance<T>() }
    }

    suspend fun prepareScene() {
        //disposeCreatedScene()

        fun createEntity(id: EntityId, parent: GameEntity) {
            resolveNode(id, parent)?.let { node ->
                node.entityData.childEntityIds.forEach { childId ->
                    createEntity(childId, node)
                }
            }
        }
        sceneData.childEntityIds.forEach { rootId -> createEntity(rootId, sceneEntity) }

        val requiredAssets = mutableSetOf<AssetReference>()
        sceneEntities.values.forEach { requiredAssets += it.requiredAssets }
        requiredAssets.forEach {
            if (!AppAssets.cacheAsset(it)) {
                logW{ "Failed pre-loading asset: ${it.path}" }
            }
        }
    }

    suspend fun applyComponents() {
        sceneData.childEntityIds.forEach { rootId ->
            resolveNode(rootId, sceneEntity)?.let { addEntity(it) }
        }
    }

    private fun disposeCreatedScene() {
        sceneEntities.values.forEach {
            it.destroyComponents()
        }
        sceneEntities.clear()
        nodesToEntities.clear()

//        drawNode.release()
//        backgroundUpdater.skybox = null
    }

    private fun resolveNode(entityId: EntityId, parent: GameEntity): GameEntity? {
        val nodeModel = sceneEntities[entityId]
        return if (nodeModel != null) nodeModel else {
            val nodeData = project.sceneNodeData[entityId]
            if (nodeData != null) {
                GameEntity(nodeData, this).also {
                    it.parent = parent
                    sceneEntities[entityId] = it
                }
            } else {
                logE { "Failed to resolve node with ID $entityId in scene ${sceneEntity.name}" }
                null
            }
        }
    }

    suspend fun addEntity(gameEntity: GameEntity) {
        if (!gameEntity.isCreated) {
            gameEntity.applyComponents()
        } else {
            logW { "Adding a scene node which is already created" }
        }

        project.addSceneNodeData(gameEntity.entityData)
        sceneEntities[gameEntity.entityId] = gameEntity
        nodesToEntities[gameEntity.drawNode] = gameEntity
        gameEntity.parent?.addChild(gameEntity)

        sceneEntity.getComponent<SceneBackgroundComponent>()?.let { sceneBackground ->
            gameEntity.getComponents<UpdateSceneBackgroundComponent>().forEach { it.updateBackground(sceneBackground) }
        }
        gameEntity.entityData.childEntityIds
            .mapNotNull { resolveNode(it, gameEntity) }
            .forEach { addEntity(it) }
    }

    fun removeEntity(gameEntity: GameEntity) {
        gameEntity.entityData.childEntityIds.mapNotNull { sceneEntities[it] }.forEach { removeEntity(it) }

        project.removeSceneNodeData(gameEntity.entityData)
        sceneEntities -= gameEntity.entityId
        nodesToEntities -= gameEntity.drawNode

        gameEntity.parent?.removeChild(gameEntity)

        launchDelayed(1) {
            gameEntity.destroyComponents()
        }
    }

    fun onStart() {
        sceneEntities.values.forEach { it.onStart() }
    }
}