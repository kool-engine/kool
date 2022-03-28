package de.fabmax.kool.physics.character

import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import physx.PxTopLevelFunctions
import physx.character.PxCapsuleClimbingModeEnum
import physx.character.PxCapsuleControllerDesc
import physx.character.PxControllerManager
import physx.character.PxControllerNonWalkableModeEnum
import kotlin.math.cos

actual class CharacterControllerManager actual constructor(world: PhysicsWorld) : CommonCharacterControllerManager(world) {

    private val pxManager: PxControllerManager

    init {
        Physics.checkIsLoaded()
        pxManager = PxTopLevelFunctions.CreateControllerManager(world.pxScene)
    }

    override fun doCreateController(): JvmCharacterController {
        // create controller with default configuration
        val hitCallback = ControllerHitCallback(world)
        val desc = PxCapsuleControllerDesc()
        desc.height = 1f
        desc.radius = 0.3f
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.nonWalkableMode = PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
        desc.slopeLimit = cos(50f.toRad())
        desc.material = Physics.defaultMaterial.pxMaterial
        desc.contactOffset = 0.1f
        desc.reportCallback = hitCallback
        val pxCharacter = pxManager.createController(desc)
        desc.destroy()
        return JvmCharacterController(pxCharacter, hitCallback, this, world)
    }

    override fun release() {
        super.release()
        pxManager.release()
    }
}