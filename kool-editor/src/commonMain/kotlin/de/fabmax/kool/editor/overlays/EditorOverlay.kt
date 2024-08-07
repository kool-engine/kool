package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.api.EditorScene

interface EditorOverlay {

    fun onEditorSceneChanged(scene: EditorScene) { }

}