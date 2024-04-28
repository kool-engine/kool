package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.editor.data.RigidBodyType

class SetRigidBodyTypeAction(
    private val editedRigidBodyComponent: RigidBodyComponent,
    private val oldType: RigidBodyType,
    private val newType: RigidBodyType
) : EditorAction {

    override fun doAction() {
        val props = editedRigidBodyComponent.bodyState.value.copy(bodyType = newType)
        editedRigidBodyComponent.bodyState.set(props)
    }

    override fun undoAction() {
        val props = editedRigidBodyComponent.bodyState.value.copy(bodyType = oldType)
        editedRigidBodyComponent.bodyState.set(props)
    }
}