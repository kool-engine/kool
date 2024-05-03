package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.data.PhysicsWorldProperties

class SetPhysicsWorldPropertiesAction(
    component: PhysicsWorldComponent,
    private val oldProps: PhysicsWorldProperties,
    private val newProps: PhysicsWorldProperties
) : ComponentAction<PhysicsWorldComponent>(component) {

    private val component: PhysicsWorldComponent? get() = nodeModel?.getComponent()

    override fun doAction() {
        component?.physicsWorldState?.set(newProps)
    }

    override fun undoAction() {
        component?.physicsWorldState?.set(oldProps)
    }
}