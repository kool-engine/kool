package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.pipeline.RenderPass

abstract class KoolBehavior {

    private var _gameEntity: GameEntity? = null
    protected val gameEntity: GameEntity
        get() = _gameEntity ?: throw IllegalStateException("KoolBehavior is not yet initialized")

    protected lateinit var behaviorComponent: BehaviorComponent
        private set

    private val onUpdateHandler: (RenderPass.UpdateEvent) -> Unit = {
        if (AppState.appMode == AppMode.PLAY) {
            onUpdate()
        }
    }

    fun init(gameEntity: GameEntity, behaviorComponent: BehaviorComponent) {
        _gameEntity?.let { old ->
            old.onNodeUpdate -= onUpdateHandler
        }
        this.behaviorComponent = behaviorComponent

        this._gameEntity = gameEntity
        gameEntity.onNodeUpdate += onUpdateHandler
        onInit()
    }

    open fun onInit() { }

    open fun onStart() { }

    open fun onUpdate() { }

}