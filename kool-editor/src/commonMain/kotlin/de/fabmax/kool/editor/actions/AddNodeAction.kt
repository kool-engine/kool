package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchOnMainThread

class AddNodeAction(
    private val addNodeModels: List<SceneNodeModel>
) : EditorAction {

    constructor(addNodeModel: SceneNodeModel): this(listOf(addNodeModel))

    override fun doAction() {
        launchOnMainThread {
            // todo: the naive loop approach does not work if addNodeModels form a hierarchy
            addNodeModels.forEach {
                it.sceneModel.addSceneNode(it)
            }
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }

    override fun undoAction() {
        addNodeModels.forEach { addNodeModel ->
            if (addNodeModel in KoolEditor.instance.selectionOverlay.selection) {
                KoolEditor.instance.selectionOverlay.selection -= addNodeModel
            }
            addNodeModel.sceneModel.removeSceneNode(addNodeModel)
        }
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }
}