package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent

class SetModelPathAction(
    component: ModelComponent,
    private val newPath: String,
) : ComponentAction<ModelComponent>(component) {

    private val component: ModelComponent? get() = nodeModel?.getComponent()
    private val oldPath = component.modelPathState.value

    override fun doAction() {
        component?.modelPathState?.set(newPath)
    }

    override fun undoAction() {
        component?.modelPathState?.set(oldPath)
    }
}