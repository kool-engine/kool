package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppMode
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.PhysicsWorldComponentData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.character.CharacterControllerManager

class PhysicsWorldComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<PhysicsWorldComponentData> = ComponentInfo(PhysicsWorldComponentData())
) : GameEntityDataComponent<PhysicsWorldComponent, PhysicsWorldComponentData>(gameEntity, componentInfo) {

    val physicsWorldState = mutableStateOf(data.properties).onChange {
        data.properties = it
    }

    val gravityState = mutableStateOf<Vec3f>(data.properties.gravity.toVec3f()).onChange {
        physicsWorldState.set(physicsWorldState.value.copy(gravity = Vec3Data(it)))
        physicsWorld?.gravity = it
    }

    var physicsWorld: PhysicsWorld? = null

    var characterControllerManager: CharacterControllerManager? = null

    override suspend fun applyComponent() {
        super.applyComponent()

        Physics.loadAndAwaitPhysics()
        physicsWorld = PhysicsWorld(null, data.properties.isContinuousCollisionDetection).also {
            it.gravity = data.properties.gravity.toVec3f()
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