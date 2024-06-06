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
import de.fabmax.kool.physics.character.CharacterControllerManager
import de.fabmax.kool.util.logW

class PhysicsWorldComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<PhysicsWorldComponentData> = ComponentInfo(PhysicsWorldComponentData())
) : GameEntityDataComponent<PhysicsWorldComponent, PhysicsWorldComponentData>(gameEntity, componentInfo) {

    var physicsWorld: PhysicsWorld? = null
        private set
    var gravity: Vec3f
        get() = data.gravity.toVec3f()
        set(value) { data = data.copy(gravity = Vec3Data(value)) }

    var characterControllerManager: CharacterControllerManager? = null

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

        onUpdate {
            physicsWorld?.let { world ->
                world.isPauseSimulation = AppState.appMode == AppMode.PAUSE
            }
        }
    }

    override fun destroyComponent() {
        super.destroyComponent()
        physicsWorld?.let {
            it.unregisterHandlers()
            it.release()
        }
        physicsWorld = null
    }

    override fun onStart() {
        super.onStart()
        physicsWorld?.registerHandlers(sceneComponent.drawNode)
    }
}