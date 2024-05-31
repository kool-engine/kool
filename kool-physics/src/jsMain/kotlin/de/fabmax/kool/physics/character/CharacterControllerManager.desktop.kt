package de.fabmax.kool.physics.character

import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.*
import physx.*
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

    override fun doCreateController(charProperties: CharacterControllerProperties): CharacterController {
        // create controller with default configuration
        world as PhysicsWorldImpl
        val hitCallback = ControllerHitListener(world)
        val behaviorCallback = ControllerBahaviorCallback(world)
        val desc = PxCapsuleControllerDesc()
        desc.height = charProperties.height
        desc.radius = charProperties.radius
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.nonWalkableMode = PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
        desc.slopeLimit = cos(charProperties.slopeLimit.toRad())
        desc.material = Physics.defaultMaterial.pxMaterial
        desc.contactOffset = charProperties.contactOffset
        desc.reportCallback = hitCallback.callback
        desc.behaviorCallback = behaviorCallback.callback
        val pxCharacter = pxManager.createController(desc)
        desc.destroy()
        return JsCharacterController(pxCharacter, hitCallback, behaviorCallback, this, world)
    }

    override fun release() {
        world.onAdvancePhysics -= onAdvanceListener
        world.onPhysicsUpdate -= onUpdateListener
        pxManager.release()
        super.release()
    }
}