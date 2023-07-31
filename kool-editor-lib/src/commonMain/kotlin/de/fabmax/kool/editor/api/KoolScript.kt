package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.ScriptComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.pipeline.RenderPass

abstract class KoolScript {

    private var _node: NodeModel? = null
    protected val node: NodeModel
        get() = _node ?: throw IllegalStateException("KoolScript is not yet initialized")

    protected lateinit var scriptComponent: ScriptComponent
        private set

    private val onUpdateHandler: (RenderPass.UpdateEvent) -> Unit = {
        if (AppState.appMode == AppMode.PLAY || scriptComponent.componentData.runInEditMode) {
            onUpdate()
        }
    }

    fun init(nodeModel: NodeModel, scriptComponent: ScriptComponent) {
        _node?.let { old ->
            old.onNodeUpdate -= onUpdateHandler
        }
        this.scriptComponent = scriptComponent

        this._node = nodeModel
        nodeModel.onNodeUpdate += onUpdateHandler
        onInit()
    }

    open fun onInit() { }

    open fun onUpdate() { }

}