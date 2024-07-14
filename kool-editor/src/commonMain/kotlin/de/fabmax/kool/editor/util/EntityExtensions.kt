package de.fabmax.kool.editor.util

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.util.logE

val EntityId.isSceneId: Boolean get() = this in KoolEditor.instance.projectModel.createdScenes.keys

val EntityId.gameEntity: GameEntity? get() {
    val proj = KoolEditor.instance.projectModel
    val gameEntity = if (isSceneId) {
        proj.createdScenes[this]?.sceneEntity
    } else {
        proj.createdScenes.values
            .find { this in it.sceneEntities }?.sceneEntities?.get(this)
            ?: proj.materialsById[this]?.gameEntity
    }
    if (gameEntity == null && this != EntityId.NULL) {
        logE { "GameEntity with id $this not found" }
    }
    return gameEntity
}
