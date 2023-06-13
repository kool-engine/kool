package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.ScriptComponent
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.pipeline.RenderPass

abstract class KoolScript {

    private var _node: EditorNodeModel? = null
    protected val node: EditorNodeModel
        get() = _node ?: throw IllegalStateException("KoolScript is not yet initialized")

    protected lateinit var scriptComponent: ScriptComponent
        private set

    private val onUpdateHandler: (RenderPass.UpdateEvent) -> Unit = {
        if (AppState.appMode == AppMode.PLAY || scriptComponent.componentData.runInEditMode) {
            onUpdate()
        }
    }

    fun init(nodeModel: EditorNodeModel, scriptComponent: ScriptComponent) {
        _node?.let { old ->
            old.node.onUpdate -= onUpdateHandler
        }
        this.scriptComponent = scriptComponent

        this._node = nodeModel
        nodeModel.node.onUpdate += onUpdateHandler
        onInit()
    }

    open fun onInit() { }

    open fun onUpdate() { }

}