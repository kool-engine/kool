package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.ScriptComponent
import de.fabmax.kool.pipeline.RenderPass

abstract class KoolScript {

    private var _nodeModel: EditorNodeModel? = null
    protected val nodeModel: EditorNodeModel
        get() = _nodeModel ?: throw IllegalStateException("KoolScript is not yet initialized")

    protected lateinit var scriptComponent: ScriptComponent
        private set

    private val onUpdateHandler: (RenderPass.UpdateEvent) -> Unit = {
        if (!AppState.isEditMode || scriptComponent.componentData.runInEditMode) {
            onUpdate()
        }
    }

    fun init(nodeModel: EditorNodeModel, scriptComponent: ScriptComponent) {
        _nodeModel?.let { old ->
            old.node.onUpdate -= onUpdateHandler
        }
        this.scriptComponent = scriptComponent

        this._nodeModel = nodeModel
        nodeModel.node.onUpdate += onUpdateHandler
        onInit()
    }

    open fun onInit() { }

    open fun onUpdate() { }

}