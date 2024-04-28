package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.data.PhysicsWorldProperties

class SetPhysicsWorldPropertiesAction(
    private val editedPhysicsWorld: PhysicsWorldComponent,
    private val oldProps: PhysicsWorldProperties,
    private val newProps: PhysicsWorldProperties
) : EditorAction {

    override fun doAction() {
        editedPhysicsWorld.physicsWorldState.set(newProps)
    }

    override fun undoAction() {
        editedPhysicsWorld.physicsWorldState.set(oldProps)
    }
}