package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Transform

abstract class KoolBehavior {

    var isUpdateInEditMode = false

    private var component: BehaviorComponent? = null
    private val componentNonNull: BehaviorComponent
        get() = checkNotNull(component) { "KoolBehavior is not yet initialized" }

    val isInitialized: Boolean get() = component != null

    val scene: EditorScene get() = componentNonNull.gameEntity.scene
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

    val RigidActor.component: RigidActorComponent? get() {
        val world = this@KoolBehavior.gameEntity.sceneEntity.getComponent<PhysicsWorldComponent>() ?: return null
        return world.actors[this]
    }

    val RigidActor.gameEntity: GameEntity? get() = component?.gameEntity
}