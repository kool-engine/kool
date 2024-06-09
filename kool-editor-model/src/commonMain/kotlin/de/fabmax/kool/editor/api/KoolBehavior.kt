package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.pipeline.RenderPass

abstract class KoolBehavior {

    var isUpdateInEditMode = false

    private var _behaviorComponent: BehaviorComponent? = null
    val behaviorComponent: BehaviorComponent
        get() = checkNotNull(_behaviorComponent) { "KoolBehavior is not yet initialized" }

    val gameEntity: GameEntity get() = behaviorComponent.gameEntity

    fun init(behaviorComponent: BehaviorComponent) {
        this._behaviorComponent = behaviorComponent
        onInit()
    }

    open fun onInit() { }

    open fun onStart() { }

    open fun onUpdate(ev: RenderPass.UpdateEvent) { }

    open fun onPhysicsUpdate(timeStep: Float) { }

    open fun onDestroy() { }

}