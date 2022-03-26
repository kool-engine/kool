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

    override fun doCreateController(props: CharacterProperties): CharacterController {
        val desc = PxCapsuleControllerDesc()
        desc.height = props.height
        desc.radius = props.radius
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.nonWalkableMode = PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
        desc.slopeLimit = cos(50f.toRad())
        desc.material = props.material.pxMaterial
        desc.contactOffset = props.contactOffset
        val pxCharacter = pxManager.createController(desc)
        desc.destroy()
        return JvmCharacterController(pxCharacter, this, world)
    }

    override fun release() {
        super.release()
        pxManager.release()
    }
}