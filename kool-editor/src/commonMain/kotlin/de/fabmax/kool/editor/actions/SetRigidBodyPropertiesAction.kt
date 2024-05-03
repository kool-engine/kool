package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.editor.data.RigidBodyProperties

class SetRigidBodyPropertiesAction(
    component: RigidBodyComponent,
    private val oldProps: RigidBodyProperties,
    private val newProps: RigidBodyProperties
) : ComponentAction<RigidBodyComponent>(component) {

    private val component: RigidBodyComponent? get() = nodeModel?.getComponent()

    override fun doAction() {
        component?.bodyState?.set(newProps)
    }

    override fun undoAction() {
        component?.bodyState?.set(oldProps)
    }
}