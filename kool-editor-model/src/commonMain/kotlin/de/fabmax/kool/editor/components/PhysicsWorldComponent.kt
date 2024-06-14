package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.PhysicsWorldComponentData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.character.CharacterControllerManager
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

class PhysicsWorldComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<PhysicsWorldComponentData> = ComponentInfo(PhysicsWorldComponentData())
) : GameEntityDataComponent<PhysicsWorldComponentData>(gameEntity, componentInfo) {

    var physicsWorld: PhysicsWorld? = null
        private set
    var gravity: Vec3f
        get() = data.gravity.toVec3f()
        set(value) { dataState.set(data.copy(gravity = Vec3Data(value))) }

    var characterControllerManager: CharacterControllerManager? = null

    private val _actors = mutableMapOf<RigidActor, RigidActorComponent>()
    val actors: Map<RigidActor, RigidActorComponent> get() = _actors

    fun addActor(rigidActorComponent: RigidActorComponent) {
        val world = physicsWorld
        val actor = rigidActorComponent.rigidActor
        if (world == null) {
            logE { "Unable to create rigid actor: parent physics world was not yet created" }
            return
        }
        if (actor == null) {
            logE { "Unable to add rigid actor: actor was not yet created" }
            return
        }
        _actors[actor] = rigidActorComponent
        world.addActor(actor)
    }

    fun removeActor(rigidActorComponent: RigidActorComponent) {
        val world = physicsWorld ?: return
        val actor = rigidActorComponent.rigidActor ?: return
        _actors -= actor
        world.removeActor(actor)
    }

    override fun onDataChanged(oldData: PhysicsWorldComponentData, newData: PhysicsWorldComponentData) {
        physicsWorld?.let { world ->
            world.gravity = newData.gravity.toVec3f()
            if (oldData.isContinuousCollisionDetection != newData.isContinuousCollisionDetection) {
                logW { "Continuous collision detection can not be changed after physics world was created" }
            }
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        Physics.loadAndAwaitPhysics()
        physicsWorld = PhysicsWorld(null, data.isContinuousCollisionDetection).also {
            it.gravity = gravity
            characterControllerManager = CharacterControllerManager(it)
        }
    }

    override fun onUpdate(ev: RenderPass.UpdateEvent) {
        physicsWorld?.let { world ->
            world.isPauseSimulation = AppState.appMode == AppMode.PAUSE
        }
    }

    override fun destroyComponent() {
        characterControllerManager?.release()
        characterControllerManager = null
        physicsWorld?.let {
            it.unregisterHandlers()
            it.release()
        }
        physicsWorld = null
        super.destroyComponent()
    }

    override fun onStart() {
        super.onStart()
        physicsWorld?.registerHandlers(sceneComponent.drawNode)
    }
}