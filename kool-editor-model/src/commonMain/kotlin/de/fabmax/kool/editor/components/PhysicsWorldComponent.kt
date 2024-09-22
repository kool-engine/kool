package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.CharacterControllerComponent.Companion.CHARACTER_CONTACT_OFFSET
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerManager
import de.fabmax.kool.physics.character.CharacterControllerProperties
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logT
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

    var physicsBounds: BoundingBoxF? = null
    val isLimitedRange: Boolean get() = physicsBounds != null

    var characterControllerManager: CharacterControllerManager? = null; private set
    private val charControllers = mutableListOf<CharacterControllerComponent>()

    private val actorStates = mutableListOf<ActorState>()
    private val _actors = mutableMapOf<RigidActor, RigidActorComponent>()
    val actors: Map<RigidActor, RigidActorComponent> get() = _actors

    private val _materials = mutableMapOf<EntityId, PhysicsMaterial>()
    val materials: Map<EntityId, PhysicsMaterial> get() = _materials

    init {
        updateMaterials(data)
        updateBounds(data)
    }

    fun addActor(rigidActorComponent: RigidActorComponent) {
        val world = physicsWorld
        val actor = rigidActorComponent.rigidActor
        if (world == null) {
            logE { "Unable to add rigid actor: parent physics world was not yet created" }
            return
        }
        if (actor == null) {
            logE { "Unable to add rigid actor: actor was not yet created" }
            return
        }
        _actors[actor] = rigidActorComponent

        val actorState = ActorState(actor, rigidActorComponent)
        actorStates += actorState
        actorState.isAttached = !isLimitedRange
        if (actorState.isAttached) {
            world.addActor(actor)
        }
    }

    fun removeActor(rigidActorComponent: RigidActorComponent) {
        val actor = rigidActorComponent.rigidActor ?: return
        _actors -= actor
        val state = actorStates.find { it.component === rigidActorComponent }
        state?.let {
            actorStates -= it
            if (it.isAttached) {
                physicsWorld?.removeActor(actor)
            }
        }
    }

    fun addCharController(charController: CharacterControllerComponent): CharacterController? {
        val world = physicsWorld
        val charMgr = characterControllerManager
        if (world == null) {
            logE { "Unable to add character controller: parent physics world was not yet created" }
            return null
        }
        if (charMgr == null) {
            logE { "Unable to add character controller: controller manager was not yet created" }
            return null
        }
        charControllers += charController
        charController.isAttachedToSimulation = true

        val props = CharacterControllerProperties(
            height = charController.data.shape.length.toFloat(),
            radius = charController.data.shape.radius.toFloat() - CHARACTER_CONTACT_OFFSET,
            slopeLimit = charController.data.slopeLimit.deg,
            contactOffset = CHARACTER_CONTACT_OFFSET
        )
        return charController.charController ?: charMgr.createController(props)
    }

    fun removeCharController(charController: CharacterControllerComponent) {
        val charMgr = characterControllerManager ?: return
        val ctrl = charController.charController ?: return
        charMgr.removeController(ctrl)
        charControllers -= charController
        charController.isAttachedToSimulation = false
    }

    fun updateReferenceFrame() {
        actorStates.forEach { state ->
            state.component.setPhysicsTransformFromDrawNode()
        }
    }

    override fun onDataChanged(oldData: PhysicsWorldComponentData, newData: PhysicsWorldComponentData) {
        physicsWorld?.let { world ->
            world.gravity = newData.gravity.toVec3f()
            if (oldData.isContinuousCollisionDetection != newData.isContinuousCollisionDetection) {
                logW { "Continuous collision detection can not be changed after physics world was created" }
            }
        }
        updateMaterials(newData)
        updateBounds(newData)
    }

    private fun updateBounds(data: PhysicsWorldComponentData) {
        physicsBounds = if (data.physicsRange.x > 1.0) {
            BoundingBoxF(Vec3f(-data.physicsRange.x.toFloat()), Vec3f(data.physicsRange.x.toFloat()))
        } else {
            null
        }
    }

    private fun isInBounds(actor: RigidActor): Boolean {
        val bounds = physicsBounds ?: return true
        return actor.pose.position in bounds
    }

    private fun updateMaterials(data: PhysicsWorldComponentData) {
        _materials.keys.retainAll(data.materials.map { it.id }.toSet())
        data.materials.forEach { matData ->
            val mat = _materials.getOrPut(matData.id) {
                val material = Material(matData.staticFriction, matData.dynamicFriction, matData.restitution)
                PhysicsMaterial(matData.id, matData.name, material)
            }
            mat.name = matData.name
            mat.material.staticFriction = matData.staticFriction
            mat.material.dynamicFriction = matData.dynamicFriction
            mat.material.restitution = matData.restitution
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        Physics.loadAndAwaitPhysics()
        physicsWorld = PhysicsWorld(null, data.isContinuousCollisionDetection).also { world ->
            world.gravity = gravity
            characterControllerManager = CharacterControllerManager(world)

            world.onAdvancePhysics += {
                if (isLimitedRange) {
                    world.applyWorldBounds()
                }
            }
        }
    }

    private fun PhysicsWorld.applyWorldBounds() {
        for (i in actorStates.indices) {
            val state = actorStates[i]

            if (state.isAttached && !isInBounds(state.actor)) {
                logT { "Actor ${state.component.gameEntity.name} left physics bounds" }
                removeActor(state.actor)
                state.isAttached = false
            } else if (!state.isAttached && isInBounds(state.actor)) {
                logT { "Actor ${state.component.gameEntity.name} entered physics bounds" }
                addActor(state.actor)
                state.isAttached = true
            }
        }
    }

    override fun onUpdate(ev: RenderPass.UpdateEvent) {
        physicsWorld?.let { world ->
            world.isPauseSimulation = AppState.appMode == AppMode.PAUSE
        }
    }

    override fun destroyComponent() {
        characterControllerManager?.let { mgr ->
            charControllers.forEach {
                it.charController?.let { ctrl ->
                    mgr.removeController(ctrl)
                    ctrl.release()
                }
                it.charController = null
                it.isAttachedToSimulation = false
            }
            mgr.release()
        }
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
        physicsWorld?.registerHandlers(sceneComponent.sceneNode)
    }

    private class ActorState(val actor: RigidActor, val component: PhysicsActorComponent<*>) {
        var isAttached: Boolean by component::isAttachedToSimulation
    }
}

fun PhysicsMaterial(data: PhysicsMaterialData) =
    PhysicsMaterial(data.id, data.name, Material(data.staticFriction, data.dynamicFriction, data.restitution))

class PhysicsMaterial(val id: EntityId, var name: String, val material: Material)