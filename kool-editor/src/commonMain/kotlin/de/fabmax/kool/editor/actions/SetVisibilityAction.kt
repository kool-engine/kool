package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.SceneNodeModel

class SetVisibilityAction(
    val nodes: List<SceneNodeModel>,
    val visible: Boolean
) : EditorAction {

    private val undoVisibilities = nodes.associateWith { it.isVisibleState.value }

    constructor(node: SceneNodeModel, visible: Boolean): this(listOf(node), visible)

    override fun doAction() {
        nodes.forEach { it.isVisibleState.set(visible) }
    }

    override fun undoAction() {
        nodes.forEach {
            undoVisibilities[it]?.let { undoState -> it.isVisibleState.set(undoState) }
        }
    }
}