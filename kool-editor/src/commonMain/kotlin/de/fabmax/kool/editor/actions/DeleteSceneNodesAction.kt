package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.launchOnMainThread

class DeleteSceneNodesAction(
    nodeModels: List<GameEntity>
) : GameEntityAction(removeChildNodes(nodeModels)) {

    private val removeNodeInfos = mutableListOf<NodeInfo>()

    init {
        gameEntities.forEach { appendNodeInfo(it) }
    }

    private fun appendNodeInfo(gameEntity: GameEntity) {
        val parent = gameEntity.parent!!
        val entityIdx = parent.children.indexOf(gameEntity)
        val pos = if (entityIdx > 0) {
            GameEntity.InsertionPos.After(parent.children[entityIdx - 1].id)
        } else {
            val before = parent.children.getOrNull(1)
            before?.let { GameEntity.InsertionPos.Before(it.id) } ?: GameEntity.InsertionPos.End
        }
        removeNodeInfos += NodeInfo(gameEntity.entityData, parent.id, pos)

        gameEntity.children.forEach { child ->
            appendNodeInfo(child)
        }
    }

    override fun doAction() {
        KoolEditor.instance.selectionOverlay.reduceSelection(gameEntities)
        gameEntities.forEach {
            it.scene.removeEntity(it)
        }
        refreshComponentViews()
    }

    override fun undoAction() {
        launchOnMainThread {
            // removed node model was destroyed, crate a new one only using the old data
            removeNodeInfos.forEach { (nodeData, parentId, pos) ->
                parentId.gameEntity?.let { parent ->
                    val scene = parent.scene
                    val node = GameEntity(nodeData, scene)
                    scene.addEntity(node)
                    parent.removeChild(node)
                    parent.addChild(node, pos)
                }
            }
            refreshComponentViews()
        }
    }

    private data class NodeInfo(val nodeData: GameEntityData, val parentId: EntityId, val position: GameEntity.InsertionPos)

    companion object {
        fun removeChildNodes(allNodes: List<GameEntity>): List<GameEntity> {
            val asSet = allNodes.toSet()
            return allNodes.filter {
                var p = it.parent
                while (p != null) {
                    if (p in asSet) {
                        return@filter false
                    }
                    p = p.parent
                }
                true
            }
        }
    }
}
