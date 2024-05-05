package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneNodeModel

class SetVisibilityAction(nodes: List<SceneNodeModel>, val visible: Boolean) : SceneNodeAction(nodes) {

    private val undoVisibilities = nodes.associate { it.nodeId to it.isVisibleState.value }

    constructor(node: SceneNodeModel, visible: Boolean): this(listOf(node), visible)

    override fun doAction() {
        sceneNodes.forEach { it.isVisibleState.set(visible) }
        KoolEditor.instance.sceneObjectsOverlay.updateOverlayObjects()
    }

    override fun undoAction() {
        sceneNodes.forEach {
            undoVisibilities[it.nodeId]?.let { undoState -> it.isVisibleState.set(undoState) }
        }
        KoolEditor.instance.sceneObjectsOverlay.updateOverlayObjects()
    }
}