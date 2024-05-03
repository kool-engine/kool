package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent

class SetModelAnimationAction(
    component: ModelComponent,
    private val newAnimation: Int,
) : ComponentAction<ModelComponent>(component) {

    private val component: ModelComponent? get() = nodeModel?.getComponent()
    private val oldAnimation = component.animationIndexState.value

    override fun doAction() {
        component?.animationIndexState?.set(newAnimation)
    }

    override fun undoAction() {
        component?.animationIndexState?.set(oldAnimation)
    }
}