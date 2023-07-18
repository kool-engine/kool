package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent

class SetModelSceneAction(
    private val editComponent: ModelComponent,
    private val newScene: Int,
) : EditorAction {

    private val oldScene = editComponent.sceneIndexState.value

    override fun doAction() = editComponent.sceneIndexState.set(newScene)

    override fun undoAction() = editComponent.sceneIndexState.set(oldScene)
}