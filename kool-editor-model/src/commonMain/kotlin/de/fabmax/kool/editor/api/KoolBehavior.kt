package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Transform

abstract class KoolBehavior {

    var isUpdateInEditMode = false

    private var component: BehaviorComponent? = null
    private val componentNonNull: BehaviorComponent
        get() = checkNotNull(component) { "KoolBehavior is not yet initialized" }

    val gameEntity: GameEntity get() = componentNonNull.gameEntity
    val transform: Transform get() = gameEntity.transform.transform

    fun init(behaviorComponent: BehaviorComponent) {
        component = behaviorComponent
        onInit()
    }

    open fun onInit() { }

    open fun onStart() { }

    open fun onUpdate(ev: RenderPass.UpdateEvent) { }

    open fun onPhysicsUpdate(timeStep: Float) { }

    open fun onDestroy() { }

}