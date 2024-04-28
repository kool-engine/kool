package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidBodyComponent

class SetRigidBodyMassAction(
    private val editedRigidBodyComponent: RigidBodyComponent,
    private val oldMass: Float,
    private val newMass: Float
) : EditorAction {

    override fun doAction() {
        editedRigidBodyComponent.massState.set(newMass)
    }

    override fun undoAction() {
        editedRigidBodyComponent.massState.set(oldMass)
    }
}