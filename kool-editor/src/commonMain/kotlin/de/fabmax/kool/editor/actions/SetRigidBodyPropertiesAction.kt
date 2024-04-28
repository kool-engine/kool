package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.editor.data.RigidBodyProperties

class SetRigidBodyPropertiesAction(
    private val editedRigidBodyComponent: RigidBodyComponent,
    private val oldProps: RigidBodyProperties,
    private val newProps: RigidBodyProperties
) : EditorAction {

    override fun doAction() {
        editedRigidBodyComponent.bodyState.set(newProps)
    }

    override fun undoAction() {
        editedRigidBodyComponent.bodyState.set(oldProps)
    }
}