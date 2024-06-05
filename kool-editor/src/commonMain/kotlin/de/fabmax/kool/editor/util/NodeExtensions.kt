package de.fabmax.kool.editor.util

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.util.logE

val EntityId.isSceneId: Boolean get() = this in KoolEditor.instance.projectModel.createdScenes.keys

val EntityId.gameEntity: GameEntity? get() {
    val gameEntity = if (isSceneId) {
        KoolEditor.instance.projectModel.createdScenes[this]?.sceneEntity
    } else {
        KoolEditor.instance.projectModel.createdScenes.values.find { this in it.sceneEntities }?.sceneEntities?.get(this)
    }
    if (gameEntity == null && value != -1L) {
        logE { "GameEntity with id $this not found" }
    }
    return gameEntity
}
