package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import physx.PxCapsuleClimbingModeEnum
import physx.PxCapsuleControllerDesc
import physx.PxControllerManager
import physx.destroy

actual class CharacterControllerManager actual constructor(world: PhysicsWorld) : CommonCharacterControllerManager(world) {

    private val pxManager: PxControllerManager

    init {
        Physics.checkIsLoaded()
        pxManager = Physics.Px.CreateControllerManager(world.pxScene)
    }

    override fun doCreateController(props: CharacterProperties): JsCharacterController {
        val desc = PxCapsuleControllerDesc()
        desc.height = props.height
        desc.radius = props.radius
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.material = props.material.pxMaterial
        val pxCharacter = pxManager.createController(desc)

        desc.destroy()

        return JsCharacterController(pxCharacter, this, world)
    }

    override fun release() {
        super.release()
        pxManager.release()
    }
}