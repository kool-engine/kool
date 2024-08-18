package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.math.RayTest

interface EditorOverlay {

    fun onEditorSceneChanged(scene: EditorScene) { }

    fun pick(rayTest: RayTest): GameEntity? { return null }

}