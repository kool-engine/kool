package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.launchOnMainThread

class AddSceneNodeAction(
    val addEntityDatas: List<GameEntityData>,
    val parentId: EntityId
) : EditorAction {

    override fun doAction() {
        val parent = parentId.gameEntity ?: return
        val scene = parent.scene

        launchOnMainThread {
            val topLevelEntities = addEntityDatas.associateBy { it.id }.toMutableMap()
            addEntityDatas.forEach {
                if (it.parentId in topLevelEntities) {
                    topLevelEntities -= it.id
                }
            }
            topLevelEntities.values.forEach {
                scene.addEntity(GameEntity(it, scene))
            }
            KoolEditor.instance.selectionOverlay.setSelection(topLevelEntities.keys.mapNotNull { it.gameEntity })
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        val scene = parentId.gameEntity?.scene ?: return
        val nodes = addEntityDatas.mapNotNull { it.id.gameEntity }
        val nonChildNodes = DeleteSceneNodesAction.removeChildNodes(nodes)

        KoolEditor.instance.selectionOverlay.reduceSelection(nodes)
        nonChildNodes.forEach { scene.removeEntity(it) }

        refreshComponentViews()
    }
}