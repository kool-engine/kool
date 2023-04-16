package de.fabmax.kool.editor.actions

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.scene.Node

class SetTransformAction(
    private val editedNode: Node,
    private val oldTransform: Mat4d,
    private val newTransform: Mat4d
) : EditorAction {

    override fun apply() {
        editedNode.transform.set(newTransform)
    }

    override fun undo() {
        editedNode.transform.set(oldTransform)
    }
}