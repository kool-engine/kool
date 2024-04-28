package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.pipeline.RenderPass

abstract class KoolBehavior {

    private var _node: NodeModel? = null
    protected val node: NodeModel
        get() = _node ?: throw IllegalStateException("KoolBehavior is not yet initialized")

    protected lateinit var behaviorComponent: BehaviorComponent
        private set

    private val onUpdateHandler: (RenderPass.UpdateEvent) -> Unit = {
        if (AppState.appMode == AppMode.PLAY || behaviorComponent.componentData.runInEditMode) {
            onUpdate()
        }
    }

    fun init(nodeModel: NodeModel, behaviorComponent: BehaviorComponent) {
        _node?.let { old ->
            old.onNodeUpdate -= onUpdateHandler
        }
        this.behaviorComponent = behaviorComponent

        this._node = nodeModel
        nodeModel.onNodeUpdate += onUpdateHandler
        onInit()
    }

    open fun onInit() { }

    open fun onStart() { }

    open fun onUpdate() { }

}