package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.toHierarchy
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.util.launchOnMainThread

class AddSceneNodeAction(
    addEntityDatas: List<GameEntityData>
) : EditorAction {

    private val hierarchy = addEntityDatas.toHierarchy()

    override fun doAction() {
        launchOnMainThread {
            hierarchy.forEach {
                val scene = it.entityData.parentId?.gameEntity?.scene
                scene?.addGameEntities(it)
            }
            KoolEditor.instance.selectionOverlay.setSelection(hierarchy.mapNotNull { it.entityData.id.gameEntity })
            refreshComponentViews()
        }
    }

    override fun undoAction() {
        KoolEditor.instance.selectionOverlay.reduceSelection(hierarchy.mapNotNull { it.entityData.id.gameEntity })
        hierarchy.forEach { root ->
            val entity = root.entityData.id.gameEntity
            val scene = entity?.scene
            entity?.let { scene?.removeGameEntity(it) }
        }
        refreshComponentViews()
    }
}