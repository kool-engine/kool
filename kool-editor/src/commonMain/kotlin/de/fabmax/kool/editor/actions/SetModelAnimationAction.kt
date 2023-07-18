package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.ModelComponent

class SetModelAnimationAction(
    private val editComponent: ModelComponent,
    private val newAnimation: Int,
) : EditorAction {

    private val oldAnimation = editComponent.animationIndexState.value

    override fun doAction() = editComponent.animationIndexState.set(newAnimation)

    override fun undoAction() = editComponent.animationIndexState.set(oldAnimation)
}