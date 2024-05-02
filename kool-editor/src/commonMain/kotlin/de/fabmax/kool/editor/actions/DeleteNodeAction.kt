package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchOnMainThread

class DeleteNodeAction(
    private var removeNodeModels: List<SceneNodeModel>
) : EditorAction {

    constructor(removeNodeModel: SceneNodeModel): this(listOf(removeNodeModel))

    override fun doAction() {
        KoolEditor.instance.selectionOverlay.selection.removeAll(removeNodeModels)
        removeNodeModels.forEach {
            it.sceneModel.removeSceneNode(it)
        }
        KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
    }

    override fun undoAction() {
        // fixme: this will not work in case removed nodes have children, because children will not be present in scene
        //  anymore -> deepcopy child node models before removal and re-add them in correct order on undo
        launchOnMainThread {
            // removed node model was destroyed, crate a new one only using the old data
            removeNodeModels = removeNodeModels.map { SceneNodeModel(it.nodeData, it.parent, it.sceneModel) }
            removeNodeModels.forEach {
                it.sceneModel.addSceneNode(it)
            }
            KoolEditor.instance.ui.sceneBrowser.refreshSceneTree()
        }
    }
}
