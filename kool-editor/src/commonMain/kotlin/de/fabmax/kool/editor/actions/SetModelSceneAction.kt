package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent

class SetModelSceneAction(
    component: ModelComponent,
    private val newScene: Int,
) : ComponentAction<ModelComponent>(component) {

    private val component: ModelComponent? get() = nodeModel?.getComponent()
    private val oldScene = component.sceneIndexState.value

    override fun doAction() {
        component?.sceneIndexState?.set(newScene)
    }

    override fun undoAction() {
        component?.sceneIndexState?.set(oldScene)
    }
}