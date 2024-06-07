package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*

val EditorScene.sceneComponent: SceneComponent get() = sceneEntity.sceneComponent
val EditorScene.scene: Scene get() = sceneComponent.drawNode

class EditorScene(sceneData: GameEntityData, val project: EditorProject) : BaseReleasable() {

    val nodesToEntities: MutableMap<Node, GameEntity> = mutableMapOf()
    val sceneEntities: MutableMap<EntityId, GameEntity> = mutableMapOf()
    val sceneChildren: List<GameEntity> get() = nodesToEntities.values.filter { it.isSceneChild }

    var componentModCnt = 0
        internal set

    val shaderData = SceneShaderData()
    var sceneEntity: GameEntity = GameEntity(sceneData, this)
        private set
    val name: String get() = sceneEntity.name

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

    suspend fun addEntityDataHierarchy(
        hierarchy: GameEntityDataHierarchy,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ) {
        val gameEntity = GameEntity(hierarchy.entityData, this)
        addGameEntity(gameEntity, insertionPos)
        hierarchy.children.forEach { addEntityDataHierarchy(it) }
    }

    suspend fun addGameEntity(
        gameEntity: GameEntity,
        insertionPos: GameEntity.InsertionPos = GameEntity.InsertionPos.End
    ) {
        val parent = gameEntity.parent ?: sceneEntities[gameEntity.entityData.parentId]?.also {
            it.addChild(gameEntity, insertionPos)
        }
        if (parent == null) {
            logE { "Parent of ${gameEntity.name} is not part of this scene (parent-id: ${gameEntity.entityData.parentId})" }
            return
        }

        // fixme: do some proper lifecycle stuff
        if (!gameEntity.isCreated) {
            gameEntity.applyComponents()
        } else {
            logW { "Adding a scene node which is already created" }
        }

        project.addEntityData(gameEntity.entityData)
        sceneEntities[gameEntity.id] = gameEntity
        nodesToEntities[gameEntity.drawNode] = gameEntity
    }

    fun removeGameEntity(gameEntity: GameEntity) {
        val removeChildren = gameEntity.children.toList()
        removeChildren.forEach { removeGameEntity(it) }

        project.removeEntityData(gameEntity.entityData)
        sceneEntities -= gameEntity.id
        nodesToEntities -= gameEntity.drawNode

        gameEntity.parent?.removeChild(gameEntity)

        launchDelayed(1) {
            gameEntity.destroyComponents()
        }
    }

    inner class SceneShaderData {
        private val listeners by CachedSceneComponents(this@EditorScene, SceneShaderDataListener::class)

        var maxNumberOfLights: Int = 4
            set(value) {
                if (value != field) {
                    field = value
                    listeners.forEach { it.onSceneShaderDataChanged(this) }
                }
            }

        var environmentMaps: EnvironmentMaps? = null
            set(value) {
                if (value != field) {
                    field = value
                    listeners.forEach { it.onSceneShaderDataChanged(this) }
                }
            }

        var ambientColorLinear: Color = Color.BLACK
            set(value) {
                if (value != field) {
                    field = value
                    listeners.forEach { it.onSceneShaderDataChanged(this) }
                }
            }

        var shadowMaps = emptyList<ShadowMap>()
            set(value) {
                if (value != field) {
                    field = value
                    listeners.forEach { it.onSceneShaderDataChanged(this) }
                }
            }

        var ssaoMap: Texture2d? = null
            set(value) {
                if (value != field) {
                    field = value
                    listeners.forEach { it.onSceneShaderDataChanged(this) }
                }
            }

        fun addShadowMap(shadowMap: ShadowMap) {
            shadowMaps = shadowMaps + shadowMap
        }

        fun removeShadowMap(shadowMap: ShadowMap) {
            shadowMaps = shadowMaps - shadowMap
        }
    }

    fun interface SceneShaderDataListener {
        fun onSceneShaderDataChanged(sceneShaderData: SceneShaderData)
    }
}