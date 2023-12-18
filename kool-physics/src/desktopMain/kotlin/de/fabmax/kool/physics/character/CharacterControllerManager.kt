package de.fabmax.kool.physics.character

import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.*
import physx.PxTopLevelFunctions
import physx.character.PxCapsuleClimbingModeEnum
import physx.character.PxCapsuleControllerDesc
import physx.character.PxControllerManager
import physx.character.PxControllerNonWalkableModeEnum
import kotlin.math.cos

actual fun CharacterControllerManager(world: PhysicsWorld): CharacterControllerManager {
    return CharacterControllerManagerImpl(world)
}

class CharacterControllerManagerImpl(private val world: PhysicsWorld) : CharacterControllerManager() {

    private val pxManager: PxControllerManager

    init {
        PhysicsImpl.checkIsLoaded()
        world as PhysicsWorldImpl
        pxManager = PxTopLevelFunctions.CreateControllerManager(world.pxScene)

        world.onAdvancePhysics += onAdvanceListener
        world.onPhysicsUpdate += onUpdateListener
    }

    override fun doCreateController(): CharacterController {
        // create controller with default configuration
        world as PhysicsWorldImpl
        val hitCallback = ControllerHitListener(world)
        val behaviorCallback = ControllerBahaviorCallback(world)
        val desc = PxCapsuleControllerDesc()
        desc.height = 1f
        desc.radius = 0.3f
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.nonWalkableMode = PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
        desc.slopeLimit = cos(50f.toRad())
        desc.material = Physics.defaultMaterial.pxMaterial
        desc.contactOffset = 0.1f
        desc.reportCallback = hitCallback
        desc.behaviorCallback = behaviorCallback
        val pxCharacter = pxManager.createController(desc)
        desc.destroy()
        return JvmCharacterController(pxCharacter, hitCallback, behaviorCallback, this, world)
    }

    override fun release() {
        world.onAdvancePhysics -= onAdvanceListener
        world.onPhysicsUpdate -= onUpdateListener
        pxManager.release()
        super.release()
    }
}